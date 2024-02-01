package com.budwk.app.device.objects.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SubscribeDTO implements Serializable {

    private String productId;
    private String deviceId;
    private String url;
    private Type type;


    public enum Type {
        /**
         * 数据上报
         */
        DATA_REPORT,
        /**
         * 事件上报
         */
        EVENT_REPORT,
        /**
         * 指令响应
         */
        COMMAND_RESPONSE,
        /**
         * 预警通知
         */
        WARNING_NOTIFY;

        public static Type form(String name) {
            try {
                return valueOf(name);
            } catch (Exception ignore) {
                return null;
            }
        }
    }
}
