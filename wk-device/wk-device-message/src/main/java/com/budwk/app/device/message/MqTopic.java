package com.budwk.app.device.message;

/**
 * @author wizzer.cn
 */
public class MqTopic {
    // 消息上行队列topic
    public static final String DEVICE_DATA_UP = "device.data.up";
    // 指令下行队列topic
    public static final String DEVICE_CMD_DOWN = "device.cmd.down";
    // 指令触发
    public static final String DEVICE_CMD_TRIGGER = "device.cmd.trigger";
    // 指令回复topic
    public static final String DEVICE_CMD_RESP = "device.cmd.resp";
    // 业务处理器topic
    public static final String SERVICE_PROCESSOR = "device.processor";
    // 设备告警topic
    public static final String SERVICE_NOTIFY_ALARM = "device.notify.alarm";
    // 解析器修改topic
    public static final String SERVICE_HANDLER_EVENT = "device.handler.event";
    // 解析器重载事件topic
    public static final String SERVICE_HANDLER_RELOAD = "device.handler.reload";
}
