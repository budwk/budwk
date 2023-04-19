package com.budwk.app.device.storage.objects.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wizzer.cn
 */
@Data
public class DeviceAttributeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    //标识符
    private String code;
    //长度
    private Integer dataLen;
    //数据类型
    private Integer dataType;
}
