package com.budwk.app.device.objects.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wizzer.cn
 */
@Data
public class DeviceHandlerDTO implements Serializable {
    private static final long serialVersionUID = -8671470623979587727L;
    private String code;
    private String fileUrl;
    private String mainClass;
}
