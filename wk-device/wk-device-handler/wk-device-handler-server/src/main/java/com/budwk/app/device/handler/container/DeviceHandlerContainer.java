package com.budwk.app.device.handler.container;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.app.device.enums.CommandStatus;
import com.budwk.app.device.handler.common.codec.*;
import com.budwk.app.device.handler.common.codec.context.DeviceEventContext;
import com.budwk.app.device.handler.common.codec.context.EncodeContext;
import com.budwk.app.device.handler.common.codec.impl.DecodeCmdRespResult;
import com.budwk.app.device.handler.common.codec.impl.DecodeReportResult;
import com.budwk.app.device.handler.common.codec.impl.DefaultDeviceOperator;
import com.budwk.app.device.handler.common.codec.impl.GatewayDeviceOperatorImpl;
import com.budwk.app.device.handler.common.codec.result.DecodeResult;
import com.budwk.app.device.handler.common.codec.result.EncodeResult;
import com.budwk.app.device.handler.common.device.CommandInfo;
import com.budwk.app.device.handler.common.device.MeterBillingInfo;
import com.budwk.app.device.handler.common.device.MeterChargeInfo;
import com.budwk.app.device.handler.common.device.ProductInfo;
import com.budwk.app.device.handler.common.enums.TransportType;
import com.budwk.app.device.handler.common.message.DeviceMessage;
import com.budwk.app.device.handler.common.message.DeviceResponseMessage;
import com.budwk.app.device.handler.common.message.codec.EncodedMessage;
import com.budwk.app.device.handler.common.utils.ByteConvertUtil;
import com.budwk.app.device.handler.impl.DefaultDecodeContext;
import com.budwk.app.device.handler.impl.DefaultEncodeContext;
import com.budwk.app.device.handler.impl.RedissionCacheStore;
import com.budwk.app.device.handler.loader.DeviceHandlerLoader;
import com.budwk.app.device.handler.support.DeviceOperatorFlushSupport;
import com.budwk.app.device.handler.support.ProductInfoSupport;
import com.budwk.app.device.handler.task.DelayTaskHelper;
import com.budwk.app.device.message.MessageTransfer;
import com.budwk.app.device.message.MqMessage;
import com.budwk.app.device.message.MqTopic;
import com.budwk.app.device.objects.dto.CommandInfoDTO;
import com.budwk.app.device.providers.IDeviceCommandProvider;
import com.budwk.app.device.providers.IDeviceHandlerProvider;
import com.budwk.app.device.storage.objects.dto.DeviceRawDataDTO;
import com.budwk.app.device.storage.storages.DeviceRawDataStorage;
import lombok.extern.slf4j.Slf4j;
import org.nutz.castor.Castors;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 设备解析器容器
 *
 * @author wizzer.cn
 */
@IocBean(create = "init")
@Slf4j
public class DeviceHandlerContainer {
    @Inject
    @Reference(check = false)
    private IDeviceHandlerProvider deviceHandlerProvider;
    @Inject
    @Reference(check = false)
    private IDeviceCommandProvider deviceCommandProvider;
    @Inject
    private RedissonClient redissonClient;
    @Inject
    private DelayTaskHelper delayTaskHelper;
    @Inject
    private MessageTransfer messageTransfer;
    @Inject
    private DeviceHandlerLoader deviceHandlerLoader;
    @Inject
    private DeviceRawDataStorage deviceRawDataStorage;
    @Inject
    private DeviceRegistry deviceRegistry;
    @Inject
    private ProductInfoSupport productInfoSupport;
    @Inject
    private DeviceEventContext deviceEventContext;
    @Inject
    private DeviceOperatorFlushSupport deviceOperatorFlushSupport;
    /**
     * 存放会话ID-设备映射
     */
    private static final Map<String, DeviceOperator> sessionDevice = new ConcurrentHashMap<>();

    /**
     * 存放设备ID-会话信息映射
     */
    private RMapCache<String, NutMap> deviceSessionCache;

    /**
     * 缓存
     */
    private RMapCache<String, Object> cacheMap;

    public void init() {
        // 初始化缓存
        cacheMap = redissonClient.getMapCache("protocol_container");
        deviceSessionCache = redissonClient.getMapCache("device_addr");
    }

    /**
     * 发送待下发指令
     *
     * @param deviceOperator
     */
    private void sendCacheCommand(DeviceOperator deviceOperator) {
        log.debug("获取待下发指令");
        if (null == deviceOperator) {
            return;
        }
        // 获取待下发指令
        CommandInfo deviceCommand = getWaitCommand(deviceOperator.getDeviceId());
        if (null == deviceCommand) {
            // 下发一个结束指令
            delayTaskHelper.delayRun(() -> {
                noMoreCommand(deviceOperator);
            }, 5);
            return;
        }
        // 构造指令
        this.buildCommand(deviceCommand);
    }

    private void noMoreCommand(DeviceOperator deviceOperator) {
        CommandInfo waitCommand = getWaitCommand(deviceOperator.getDeviceId());
        if (null != waitCommand) {
            this.buildCommand(waitCommand);
            return;
        }
        // 优化以下代码
        CommandInfo end = new CommandInfo();
        end.setDeviceId(deviceOperator.getDeviceId());
        end.setCommandCode("END");
        if (deviceOperator instanceof MeterOperator) {
            MeterBillingInfo billingInfo = ((MeterOperator) deviceOperator).getMeterBillingInfo();
            if (null != billingInfo) {
                MeterChargeInfo chargeInfo = billingInfo.getChargeInfo();
                if (null != chargeInfo) {
                    if (0 == chargeInfo.getSettleMode()) {//预付费
                        end.setCommandCode("BALANCE_SYNC");//余额同步
                    }
                }
            }
        }
        end.setCommandId("END");
        this.buildCommand(end);
    }

    private void processCmdResp(DeviceOperator deviceOperator, DecodeCmdRespResult cmdRespResult) {
        if (null == deviceOperator) {
            return;
        }
        String deviceId = deviceOperator.getDeviceId();
        String messageId = cmdRespResult.getCommandId();
        if (Strings.isBlank(messageId)) {
            // 这个就当做是指令id
            messageId = (String) cacheMap.get("cmd:send:" + deviceId);
        }
        List<DeviceMessage> messages = cmdRespResult.getMessages()
                .stream().filter(m -> !(m instanceof DeviceResponseMessage)).collect(Collectors.toList());

        // 如果指令响应中存在业务处理需要的数据，那么也就推送到业务处理去
        if (Lang.isNotEmpty(messages)) {
            // 将解析后的数据推送到业务处理模块
            MqMessage<DecodeReportResult> processorMessage =
                    new MqMessage<>(MqTopic.SERVICE_PROCESSOR, new DecodeReportResult(deviceId, messages));
            processorMessage.setSender("protocol");
            if (null != deviceOperator.getProduct()) {
                processorMessage.addHeader("protocol", deviceOperator.getProduct().getProtocolType());
            }
            if (deviceOperator instanceof GatewayDeviceOperatorImpl) {
                // 是集中器
                processorMessage.addHeader("collector", "true");
                processorMessage.addHeader("collectorId", deviceOperator.getDeviceId());
            }

            // 将解析后的数据推送到业务处理去
            messageTransfer.publish(processorMessage);
        }
        // 为空或者是最后一条自动发送的结束指令，则不处理
        if (Strings.isBlank(messageId) || messageId.contains("END")) {
            return;
        }
        try {
            deviceCommandProvider.markFinished(messageId,
                    cmdRespResult.isSuccess() ? CommandStatus.FINISHED : CommandStatus.FAILED,
                    System.currentTimeMillis(), Json.toJson(cmdRespResult.getMessages(), JsonFormat.tidy()));
        } finally {
            cacheMap.remove("cmd:send:" + deviceId);
            // 获取指令并构造指令
            CommandInfo deviceCommand = getWaitCommand(deviceId);
            if (null != deviceCommand) {
                this.buildCommand(deviceCommand);
            } else {
                this.noMoreCommand(deviceOperator);
            }
        }
    }

    private CommandInfo getWaitCommand(String deviceId) {
        return deviceRegistry.getDeviceCommand(deviceId);
    }


    /**
     * 构造指令
     *
     * @param commandInfo 指令信息
     */
    private void buildCommand(CommandInfo commandInfo) {
        try {
            if (null == commandInfo) {
                return;
            }
            // 存下发送的指令ID，防止重复下发
            String key = "cmd:send:" + commandInfo.getDeviceId();
            if (Strings.isBlank(commandInfo.getCommandId())) {
                commandInfo.setCommandId(commandInfo.getDeviceId() + "_" + commandInfo.getCommandCode());
            }
            cacheMap.put(key, commandInfo.getCommandId(), 120, TimeUnit.SECONDS);
            // 获取设备会话，如果未找到说明设备不在线，那就不继续发了
            String deviceId = commandInfo.getDeviceId();
            NutMap session = deviceSessionCache.get(deviceId);
            if (null == session) {
                log.warn("未找到设备会话");
                return;
            }
            DeviceRawDataDTO rawData = new DeviceRawDataDTO();
            rawData.setType("D");
            rawData.setDeviceId(deviceId);
            rawData.setParsedData(Json.toJson(commandInfo, JsonFormat.tidy()));
            rawData.setStartTime(System.currentTimeMillis());
            // 从缓存中获取从网关带过来的会话信息
            String sessionId = session.getString("sessionId");
            String address = session.getString("address");
            String handlerCode = session.getString("handlerCode");
            String transportType = session.getString("transportType");

            // 根据网络协议获取编解码类
            MessageCodec messageCodec = getMessageCodec(handlerCode, TransportType.valueOf(transportType.toUpperCase()));
            // 获取设备信息如果已经上报过数据，那么这里可以直接从缓存获取到
            DeviceOperator device = Strings.isNotBlank(sessionId) ? sessionDevice.get(sessionId) : null;
            if (null == device) {
                device = deviceRegistry.getDeviceOperator("id", deviceId);
            }
            // 构造编码上下文
            EncodeContext encodeCtx = new DefaultEncodeContext(commandInfo, device) {
                @Override
                public CacheStore getCacheStore(String id) {
                    // 使用 Redisson的缓存
                    return new RedissionCacheStore(id, redissonClient);
                }
            };
            // 编码指令数据
            EncodeResult encode = messageCodec.encode(encodeCtx);
            if (null != encode) {
                encode.getMessage().forEach(message -> {
                    // 如果需要发送那就发送指令到 网关
                    if (!encode.isSend()) {
                        MqMessage<EncodedMessage> request = new MqMessage(address, message);
                        request.addHeader("sessionId", sessionId);
                        request.addHeader("commandId", commandInfo.getCommandId());
                        request.addHeader("deviceId", deviceId);
                        request.setReplyAddress(MqTopic.DEVICE_CMD_RESP);
                        messageTransfer.publish(request);
                    }
                    // 存储下发的指令原始数据
                    rawData.setOriginData(message.payloadAsString());
                    rawData.setEndTime(System.currentTimeMillis());
                    deviceRawDataStorage.save(rawData);
                });
            }
        } catch (Exception e) {
            log.warn("构造指令失败", e);
        }
    }

    /**
     * 监听网关来的数据
     */
    private void listenData() {
        messageTransfer.subscribe("DEVICE_DATA_UP", MqTopic.DEVICE_DATA_UP, this::handleDataUp);
    }

    /**
     * 解析上报数据
     *
     * @param mqMessage
     */
    private void handleDataUp(MqMessage<?> mqMessage) {
        log.debug("收到消息{}", mqMessage.toString());
        Object body = mqMessage.getBody();
        EncodedMessage message = Castors.me().castTo(body, EncodedMessage.class);
        DeviceRawDataDTO rawData = new DeviceRawDataDTO();
        rawData.setType("U");
        rawData.setOriginData(ByteConvertUtil.bytesToHex(message.getPayload()));
        rawData.setStartTime(System.currentTimeMillis());
        // 加载编解码类
        MessageCodec messageCodec = getMessageCodec(message.getHandlerCode(), message.getTransportType());
        try {
            String sessionId = mqMessage.getHeader("sessionId");
            // 从缓存中尝试获取设备
            DeviceOperator deviceOperator = Strings.isNotBlank(sessionId) ? sessionDevice.get(sessionId) : null;
            // 构造解码上下文
            DefaultDecodeContext decodeContext = new DefaultDecodeContext(deviceRegistry, message) {
                /**
                 * 提供方法可以让解析包中直接发送消息给设备
                 * @param replyMessage 需要发送的消息
                 */
                @Override
                public void send(EncodedMessage replyMessage) {
                    if (null == replyMessage) {
                        return;
                    }
                    DeviceRawDataDTO rawData = new DeviceRawDataDTO();
                    DeviceOperator device = this.getDevice();
                    rawData.setDeviceId(null != device ? device.getDeviceId() : null);
                    rawData.setType("U");
                    rawData.setOriginData(replyMessage.payloadAsString());
                    rawData.setStartTime(System.currentTimeMillis());
                    if (!replyMessage.isSend()) {
                        MqMessage<EncodedMessage> reply = new MqMessage(mqMessage.getReplyAddress(), replyMessage);
                        reply.setReplyAddress(MqTopic.DEVICE_CMD_RESP);
                        reply.getHeaders().putAll(mqMessage.getHeaders());
                        messageTransfer.publish(reply);
                    }
                    rawData.setEndTime(System.currentTimeMillis());
                    deviceRawDataStorage.save(rawData);
                }

                /**
                 * 提供缓存实现
                 * @param id 缓存存储的id
                 * @return
                 */
                @Override
                public CacheStore getCacheStore(String id) {
                    return new RedissionCacheStore(id, redissonClient);
                }
            };
            // deviceOperator 重新赋值
            if (null != decodeContext.getDevice()) {
                deviceOperator = decodeContext.getDevice();
            } else {
                if (null != deviceOperator) {
                    decodeContext.setDevice(deviceOperator);
                }
            }
            // 解析数据
            DecodeResult result = messageCodec.decode(decodeContext);
            if (null == result) {
                log.warn("数据解析失败 {}", message.getHandlerCode());
                // 存储上报的原始数据
                rawData.setDeviceId(null);
                rawData.setEndTime(System.currentTimeMillis());
                deviceRawDataStorage.save(rawData);
                return;
            }
            if (null != decodeContext.getDevice()) {
                if (Strings.isNotBlank(sessionId)) {
                    sessionDevice.put(sessionId, decodeContext.getDevice());
                }
                // 同时更新状态为在线以及最近通讯时间
                deviceRegistry.updateDeviceOnline(decodeContext.getDevice().getDeviceId());
            }
            // 如处理数据上报
            if (result instanceof DecodeReportResult) {
                DecodeReportResult reportResult = (DecodeReportResult) result;
                String deviceId = reportResult.getDeviceId();
                // 保存一下信息，用于发送指令时的处理
                deviceSessionCache.put(deviceId,
                        NutMap.NEW()
                                .setv("address", mqMessage.getReplyAddress())
                                .setv("sessionId", mqMessage.getHeader("sessionId"))
                                .setv("transportType", message.getTransportType().name())
                                .setv("handlerCode", message.getHandlerCode()), 600, TimeUnit.SECONDS);
                // 存储上报的原始数据
                rawData.setDeviceId(deviceId);
                rawData.setParsedData(Json.toJson(reportResult.getMessages(), JsonFormat.tidy()));
                rawData.setEndTime(System.currentTimeMillis());
                deviceRawDataStorage.save(rawData);
                if (Lang.isNotEmpty(reportResult.getMessages())) {
                    // 将解析后的数据推送到业务处理模块
                    MqMessage<DecodeReportResult> processorMessage = new MqMessage<>(MqTopic.SERVICE_PROCESSOR, reportResult);
                    processorMessage.setSender("handlerCode");
                    processorMessage.addHeader("handlerCode", message.getHandlerCode());
                    if (deviceOperator instanceof GatewayDeviceOperatorImpl) {
                        // 是集中器
                        processorMessage.addHeader("collector", "true");
                        processorMessage.addHeader("collectorId", deviceOperator.getDeviceId());
                    }
                    // 将解析后的数据推送到业务处理去
                    messageTransfer.publish(processorMessage);
                }
                this.sendCacheCommand(decodeContext.getDevice());
            }
            // 处理指令回复的数据
            else {
                DecodeCmdRespResult cmdRespResult = (DecodeCmdRespResult) result;
                this.processCmdResp(decodeContext.getDevice(), cmdRespResult);
                // 存储上报的原始数据
                rawData.setDeviceId(null != decodeContext.getDevice() ? decodeContext.getDevice().getDeviceId() : null);
                rawData.setParsedData(Json.toJson(cmdRespResult.getMessages(), JsonFormat.tidy()));
                rawData.setEndTime(System.currentTimeMillis());
                deviceRawDataStorage.save(rawData);
            }
            // 刷新设备信息
            deviceOperatorFlushSupport.flush(decodeContext.getDevice());
        } catch (Exception e) {
            log.warn("解析数据失败", e);
        }
    }

    /**
     * 监听指令，从其他地方调用的创建指令都会到这里来
     */
    private void listenCmd() {
        messageTransfer.subscribe("DEVICE_CMD_SEND", MqTopic.DEVICE_CMD_TRIGGER, this::handleCmd);
    }

    /**
     * 处理待下发指令
     *
     * @param mqMessage
     * @param <T>
     */
    private <T extends Serializable> void handleCmd(MqMessage<T> mqMessage) {
        log.debug("收到指令{}", mqMessage.toString());
        CommandInfoDTO commandInfoDTO = (CommandInfoDTO) mqMessage.getBody();
        if (null == commandInfoDTO) {
            return;
        }
        String key = "cmd:send:" + commandInfoDTO.getDeviceId();
        Object o = cacheMap.get(key);
        if (null != o) {
            log.warn("正在下发其他指令 {}", o);
            return;
        }
        CommandInfo cmdInfo = new CommandInfo();
        cmdInfo.setCommandId(commandInfoDTO.getId());
        cmdInfo.setCommandCode(commandInfoDTO.getCommandCode());
        cmdInfo.setDeviceId(commandInfoDTO.getDeviceId());
        cmdInfo.setParams(commandInfoDTO.getParams());
        if (Strings.isBlank(cmdInfo.getCommandId())) {
            cmdInfo.setCommandId(cmdInfo.getDeviceId() + "_" + cmdInfo.getCommandCode() + "_" + System.currentTimeMillis());
        }
        // 构造指令
        this.buildCommand(cmdInfo);
    }

    /**
     * 监听指令发送结果。网关回复的结果会到这里
     */
    private void listenCmdResp() {
        messageTransfer.subscribe("DEVICE_DATA_RESP", MqTopic.DEVICE_CMD_RESP,
                this::handleCmdResp);
    }

    /**
     * 处理结果
     *
     * @param mqMessage
     * @param <T>
     */
    private <T extends Serializable> void handleCmdResp(MqMessage<T> mqMessage) {
        //
        String deviceId = mqMessage.getHeader("deviceId");
        String commandId = mqMessage.getHeader("commandId");
        if (Strings.isBlank(commandId)) {
            return;
        }
        log.debug("设备 {} 指令 {} 发送到设备结果 {}", deviceId, commandId, mqMessage.getBody());
        // 如果需要指令的中间状态，那么就放开下面的注释
//            if (Strings.isBlank(deviceId) || Strings.isBlank(commandId)) {
//                return;
//            }
//        deviceCommandProvider.markSend(commandId, System.currentTimeMillis());
    }

    /**
     * 监听协议包的重加载
     */
    private void listenProtocolReload() {
        messageTransfer.subscribe("DEVICE_HANDLER_RELOAD", MqTopic.SERVICE_HANDLER_RELOAD,
                mqMessage -> {
                    log.debug("重加载协议 {}", mqMessage.getBody());
                    String protocol = Strings.sBlank(mqMessage.getBody());
                    deviceHandlerLoader.reload(protocol);
                });
    }

    /**
     * 监听业务事件
     */
    private void listenEvent() {
        messageTransfer.subscribe("DEVICE_HANDLER_EVENT", MqTopic.SERVICE_HANDLER_EVENT,
                mqMessage -> {
                    log.debug("监听事件 {}", mqMessage.getBody());
                    NutMap map = Lang.obj2nutmap(mqMessage.getBody());
                    String event = map.getString("event", "");
                    String handlerCode = map.getString("handlerCode");
                    DeviceOperator operator = null;
                    String deviceId = map.getString("deviceId");
                    String productId = map.getString("productId");
                    if (Strings.isNotBlank(deviceId)) {
                        operator = deviceRegistry.getDeviceOperator(deviceId);
                        if (Strings.isBlank(handlerCode)) {
                            handlerCode = operator.getProduct().getHandlerCode();
                        }
                    }
                    switch (event) {
                        // 设备注册事件
                        case "device-register":
                            Handler protocol1 = deviceHandlerLoader.loadHandler(handlerCode);
                            if (null != protocol1 && null != operator) {
                                protocol1.onDeviceRegistered(deviceEventContext, operator);
                            }
                            break;
                        // 设备注销事件
                        case "device-unregister":
                            protocol1 = deviceHandlerLoader.loadHandler(handlerCode);
                            if (null != protocol1) {
                                if (null == operator) {
                                    operator = new DefaultDeviceOperator() {
                                        @Override
                                        public ProductInfo getProduct() {
                                            return Strings.isBlank(productId) ? null : productInfoSupport.getProduct(productId);
                                        }
                                    };
                                    operator.setProperty("deviceId", deviceId);
                                    operator.setProperty("iotDevId", map.getString("iotDevId"));
                                }
                                protocol1.onDeviceUnRegistered(deviceEventContext, operator);
                            }
                            break;
                    }
                });
    }

    /**
     * 获取编解码类
     *
     * @param handlerCode   协议标识
     * @param transportType 网络类型
     * @return
     */
    private MessageCodec getMessageCodec(String handlerCode, TransportType transportType) {
        if (Strings.isBlank(handlerCode) || null == transportType) {
            throw new RuntimeException("编解码信息错误");
        }
        MessageCodec messageCodec;
        Handler handlers = deviceHandlerLoader.loadHandler(handlerCode);
        if (null == handlers) {
            log.warn("未找到协议：{}", handlerCode);
            throw new RuntimeException("未找到可用的协议：" + handlerCode);
        }
        messageCodec = handlers.getMessageCodec(transportType);
        if (null == messageCodec) {
            log.warn("未找到可用的编解码方法：{} {}", handlerCode, transportType);
            throw new RuntimeException("未找到可用的编解码插件：" + handlerCode + " " + transportType);
        }
        return messageCodec;
    }


    public void start() throws Exception {
        listenProtocolReload();
        listenEvent();
        listenData();
        listenCmd();
        listenCmdResp();
    }

    public void stop() throws Exception {

    }
}
