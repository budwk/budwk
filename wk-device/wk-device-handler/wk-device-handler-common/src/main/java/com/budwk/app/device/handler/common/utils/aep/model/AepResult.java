package com.budwk.app.device.handler.common.utils.aep.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author wizzer.cn
 */
@Getter
@Setter
public class AepResult implements Serializable {
    private static final long serialVersionUID = -6449044373142363303L;

    public AepResult(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


    private int code;

    private String msg;

    private Object data;


    @Override
    public String toString() {
        return "AepResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static AepResult success(String msg, Object data) {
        return new AepResult(0, msg, data);
    }

}
