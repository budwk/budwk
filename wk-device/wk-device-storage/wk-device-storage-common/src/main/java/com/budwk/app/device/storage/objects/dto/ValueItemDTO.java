package com.budwk.app.device.storage.objects.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ValueItemDTO<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1L;

    public ValueItemDTO(String code, T value) {
        this.code = code;
        this.value = value;
    }

    public ValueItemDTO(String code, String name, T value) {
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
