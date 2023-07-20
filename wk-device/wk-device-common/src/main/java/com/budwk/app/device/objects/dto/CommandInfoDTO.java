package com.budwk.app.device.objects.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author wizzer.cn
 */
@Data
public class CommandInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    /**
     * 指令的id，与{@link #commandCode}二选一，如果这个值不为null，将会忽略 {@link #commandCode}和{@link #commandName} 的值
     */
    private String commandId;
    /**
     * 设备id，必填项
     */
    private String productId;
    private String deviceId;
    private String deviceNo;
    private String accountNo;
    /**
     * 指令的标识与{@link #commandId}二选一
     */
    private String commandCode;
    /**
     * 指令的名称。如果{@link #commandId}不为null可忽略
     */
    private String commandName;
    /**
     * 指令的参数
     */
    private Map<String, Object> params;
    /**
     * 备注说明
     */
    private String note;
    /**
     * 操作人
     */
    private String operator;

}