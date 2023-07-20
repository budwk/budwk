package com.budwk.app.device.gateway.tcp;

import com.budwk.app.device.enums.ProtocolType;
import com.budwk.app.device.gateway.DeviceGateway;
import com.budwk.app.device.gateway.config.DeviceGatewayConfiguration;
import com.budwk.app.device.gateway.tcp.client.TcpClient;
import com.budwk.app.device.gateway.tcp.server.TcpServer;
import com.budwk.app.device.gateway.tcp.server.VertxTcpServer;
import com.budwk.app.device.handler.common.message.codec.EncodedMessage;
import com.budwk.app.device.handler.common.message.codec.TcpMessage;
import com.budwk.app.device.message.MessageTransfer;
import com.budwk.app.device.message.MqMessage;
import com.budwk.app.device.message.MqTopic;
import com.budwk.starter.rocketmq.enums.ConsumeMode;
import com.budwk.starter.rocketmq.enums.MessageModel;
import io.netty.util.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.castor.Castors;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wizzer.cn
 */
@Slf4j
public class TcpDeviceGateway implements DeviceGateway {


    private String instanceId;
    private final MessageTransfer messageTransfer;
    private final TcpServer tcpServer;
    private final DeviceGatewayConfiguration configuration;

    private final Map<String, TcpClient> clientStorage = new ConcurrentHashMap<>();


    public TcpDeviceGateway(DeviceGatewayConfiguration configuration, MessageTransfer messageTransfer) {
        this.configuration = configuration;
        this.messageTransfer = messageTransfer;
        this.tcpServer = new VertxTcpServer(configuration.getProperties());
    }

    @Override
    public ProtocolType getProtocolType() {
        return ProtocolType.TCP;
    }


    @Override
    public void start() {
        // 启动服务
        tcpServer.start()
                .handleConnection(tcpClient -> {
                    log.debug("客户端 {} 已连接", tcpClient.getId());
                    clientStorage.put(tcpClient.getId(), tcpClient);
                    tcpClient
                            .onMessage(bytes -> {
                                MqMessage<EncodedMessage> mqMessage =
                                        new MqMessage<>(MqTopic.DEVICE_DATA_UP, newTcpMessage(bytes));
                                mqMessage.setSender(getInstanceId());
                                mqMessage.setReplyAddress(getInstanceAddress());
                                mqMessage.addHeader("sessionId", tcpClient.getId());
                                messageTransfer.publish(mqMessage);
                            })
                            .onClose(unused -> {
                                clientStorage.remove(tcpClient.getId());
                            })
                            .onError(throwable -> {
                                clientStorage.remove(tcpClient.getId());
                                log.error("客户端 {} 处理数据出错", tcpClient.getId(), throwable);
                            });
                });
        startCmdListener();
    }

    @Override
    public void stop() {
        if (isAlive()) {

        }
    }

    @Override
    public boolean isAlive() {
        return tcpServer != null;
    }

    private TcpMessage newTcpMessage(byte[] bytes) {
        TcpMessage tcpMessage = new TcpMessage(bytes, configuration.getHandlerCode());
        tcpMessage.setPayloadType(Strings.sBlank(configuration.getProperties().get("payloadType"), "hex"));
        return tcpMessage;
    }

    private void startCmdListener() {
        //tcp是长链接,连上哪个实例不确定,所以采用 广播模式 消费
        messageTransfer.subscribe(this.configuration.getId(), getInstanceAddress(), "*", MessageModel.BROADCASTING, ConsumeMode.CONCURRENTLY,
                mqMessage -> {
                    Object body = mqMessage.getBody();
                    EncodedMessage message = Castors.me().castTo(body, EncodedMessage.class);
                    byte[] bytes = message.getPayload();
                    TcpClient client = clientStorage.get(mqMessage.getHeader("sessionId"));
                    NutMap result = NutMap.NEW()
                            .setv("result", 0)
                            .setv("deviceId", mqMessage.getHeader("deviceId"))
                            .setv("commandId", mqMessage.getHeader("commandId"));
                    log.debug("发送指令：{}", mqMessage.getHeader("commandId"));
                    if (null != client && null != bytes) {
                        client.send(bytes).whenComplete((unused, throwable) -> {
                            if (null == throwable) {
                                replyCmdSendResult(mqMessage.getReplyAddress(), result, mqMessage.getHeaders());
                            } else {
                                replyCmdSendResult(mqMessage.getReplyAddress(), result.setv("result", -1).setv("msg", "发送数据到设备失败"), mqMessage.getHeaders());
                            }
                        });
                    } else {
                        result.setv("result", -1).setv("msg", "未找到设备会话信息");
                        replyCmdSendResult(mqMessage.getReplyAddress(), result, mqMessage.getHeaders());
                    }
                });
    }

    private void replyCmdSendResult(String replyAddress, NutMap result, Map<String, String> headers) {
        if (Strings.isBlank(replyAddress)) {
            return;
        }
        MqMessage<NutMap> replyMqMessage = new MqMessage<>(replyAddress, result);
        replyMqMessage.setSender(getInstanceId());
        replyMqMessage.getHeaders().putAll(headers);
        messageTransfer.publish(replyMqMessage);
    }

    private String getInstanceAddress() {
        return String.format(this.configuration.getId() + ":%s.%s", MqTopic.DEVICE_CMD_DOWN, getInstanceId());
    }

    public String getInstanceId() {
        if (Strings.isNotBlank(instanceId)) {
            return instanceId;
        }
        instanceId = Strings.sBlank(this.configuration.getInstanceId());
        if (Strings.isBlank(instanceId)) {
            String id = configuration.getId() + "." + NetUtil.LOCALHOST + ManagementFactory.getRuntimeMXBean().getName();
            instanceId = Integer.toHexString(id.hashCode());
        }
        return instanceId;
    }
}
