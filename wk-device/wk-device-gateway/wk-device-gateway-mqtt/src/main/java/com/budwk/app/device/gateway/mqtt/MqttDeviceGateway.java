package com.budwk.app.device.gateway.mqtt;

import com.budwk.app.device.enums.ProtocolType;
import com.budwk.app.device.gateway.DeviceGateway;
import com.budwk.app.device.gateway.config.DeviceGatewayConfiguration;
import com.budwk.app.device.handler.common.message.codec.EncodedMessage;
import com.budwk.app.device.handler.common.message.codec.MqttMessage;
import com.budwk.app.device.message.MessageTransfer;
import com.budwk.app.device.message.MqMessage;
import com.budwk.app.device.message.MqTopic;
import com.budwk.starter.rocketmq.enums.ConsumeMode;
import com.budwk.starter.rocketmq.enums.MessageModel;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.NetUtil;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import lombok.extern.slf4j.Slf4j;
import org.nutz.castor.Castors;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.lang.management.ManagementFactory;
import java.util.Map;

/**
 * @author wizzer.cn
 */
@Slf4j
public class MqttDeviceGateway implements DeviceGateway {


    private String instanceId;
    private final MessageTransfer messageTransfer;
    private final DeviceGatewayConfiguration configuration;
    private MqttClient client;
    private String topic;

    public MqttDeviceGateway(DeviceGatewayConfiguration configuration, MessageTransfer messageTransfer) {
        this.configuration = configuration;
        this.messageTransfer = messageTransfer;
    }

    @Override
    public ProtocolType getProtocolType() {
        return ProtocolType.MQTT;
    }

    @Override
    public void start() {
        MqttClientOptions options = new MqttClientOptions();
        // specify broker host
        String clentId = Strings.sNull(configuration.getProperties().get("clentId"));
        if (Strings.isBlank(clentId)) {
            clentId = getInstanceId();
        }
        options.setClientId(clentId);
        options.setUsername(Strings.sNull(configuration.getProperties().get("username")));
        options.setPassword(Strings.sNull(configuration.getProperties().get("password")));
        options.setMaxMessageSize(100_000_000);
        topic = Strings.sNull(configuration.getProperties().get("topic"));
        Vertx vertx = Vertx.vertx();
        client = MqttClient.create(vertx, options);
        // 接收到消息的处理
        client.publishHandler(s -> {
            try {
                // 根据实际情况修改
                MqttMessage mqttMessage = new MqttMessage(s.payload().getBytes());
                mqttMessage.setMessageId(s.messageId() + "");
                mqttMessage.setClientId(client.clientId());
                mqttMessage.setPayloadType(Strings.sNull(configuration.getProperties().get("payloadType")));
                mqttMessage.setTopic(s.topicName());
                MqMessage<EncodedMessage> mqMessage =
                        new MqMessage<>(MqTopic.DEVICE_DATA_UP,
                                mqttMessage);
                mqMessage.setSender(getInstanceId());
                mqMessage.setReplyAddress(getInstanceAddress());
                mqMessage.addHeader("sessionId", s.topicName());
                messageTransfer.publish(mqMessage);
                log.debug("Receive message with content: {} from topic {}", mqttMessage.payloadAsString(), s.topicName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // 连接到 MQTT 代理服务器
        client.connect(Integer.parseInt(Strings.sNull(configuration.getProperties().get("port"), "5000")), Strings.sNull(configuration.getProperties().get("host")), ar -> {
            if (ar.succeeded()) {
                log.debug("Connected to MQTT broker");
                // 订阅主题 # = 所有设备消息
                client.subscribe(topic, 0, ar2 -> {
                    if (ar2.succeeded()) {
                        log.debug("Subscribed to topic: {} " + topic);
                    } else {
                        log.debug("Failed to subscribe to topic: " + topic);
                    }
                });

            } else {
                System.err.println("Failed to connect to MQTT broker");
            }
        });
        startCmdListener();

    }

    @Override
    public void stop() {
        if (isAlive()) {
            try {
                client.disconnect();
            } catch (Exception ignore) {

            }
            client = null;
        }
    }

    @Override
    public boolean isAlive() {
        return client != null && client.isConnected();
    }

    private void startCmdListener() {
        messageTransfer.subscribe(this.configuration.getId(), getInstanceAddress(), "*", MessageModel.BROADCASTING, ConsumeMode.CONCURRENTLY,
                mqMessage -> {
                    Object body = mqMessage.getBody();
                    EncodedMessage message = Castors.me().castTo(body, EncodedMessage.class);
                    byte[] bytes = message.getPayload();
                    NutMap result = NutMap.NEW()
                            .setv("result", 0)
                            .setv("deviceId", mqMessage.getHeader("deviceId"))
                            .setv("commandId", mqMessage.getHeader("commandId"));
                    log.debug("发送指令：{}", mqMessage.getHeader("commandId"));
                    if (null != client && null != bytes) {
                        Buffer payload = Buffer.buffer(bytes);
                        client.publish(mqMessage.getHeader("sessionId"), payload, MqttQoS.valueOf(0), false, false,
                                ar -> {
                                    if (ar.succeeded()) {
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
