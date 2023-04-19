package com.budwk.app.device.storage.objects.dto;

import com.budwk.app.device.enums.DataType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wizzer.cn
 */
@Data
public class DeviceAttributeDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    //参数名
    private String name;
    //标识符
    private String code;
    //长度
    private Integer dataLen;
    //数据类型
    private DataType dataType;
    //小数位
    private Integer scale;
    //单位
    private String unit;
}
