package com.budwk.app.device.handler.common.device;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wizzer.cn
 */
@Data
public class ValueItem <T extends Serializable> implements Serializable {
    private static final long serialVersionUID = -1227063281883181989L;

    public ValueItem(String code, T value) {
        this.code = code;
        this.value = value;
    }

    public ValueItem(String code, String name, T value) {
        this.code = code;
        this.name = name;
        this.value = value;
    }

    /**
     * 标识
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 值
     */
    private T value;


}
