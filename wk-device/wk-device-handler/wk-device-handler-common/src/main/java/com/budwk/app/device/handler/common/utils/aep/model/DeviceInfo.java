package com.budwk.app.device.handler.common.utils.aep.model;

import lombok.Data;
import org.nutz.lang.util.NutMap;

import java.io.Serializable;

/**
 * @author wizzer.cn
 */
@Data
public class DeviceInfo implements Serializable {
    private static final long serialVersionUID = -7469129229410795362L;
    public DeviceInfo() {
        /**
         * 必须设置自动订阅参数，否则会提示参数验证失败，默认为 自动订阅
         */
        this.setAutoObserver(true);
    }

    /**
     * 设备名称，必填
     */
    private String deviceName;
    /**
     * 设备编号，MQTT,T_Link协议必填
     */
    private String deviceSn;
    /**
     * imei号，LWM2M,NB网关必填
     */
    private String imei;
    /**
     * 操作者，必填
     */
    private String operator;
    /**
     * 产品ID，必填
     */
    private String productId;

    /**
     * other: 选填，LWM2M协议选填参数,其他协议不填：
     * {
     *       autoObserver:0.自动订阅 1.取消自动订阅，选填;
     *       imsi:imsi号,选填;
     *       pskValue:由大小写字母加0-9数字组成的32位字符串,选填
     * }
     */
    private transient boolean autoObserver = true;
    private transient String imsi;
    private transient String pskValue;

    private NutMap other = NutMap.NEW();
}
