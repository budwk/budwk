package com.budwk.app.device.handler.common.device;

import lombok.Data;
import org.nutz.lang.util.NutMap;

import java.io.Serializable;

/**
 * 产品信息
 *
 * @author wizzer.cn
 * @author zyang
 */
@Data
public class ProductInfo implements Serializable {
    private static final long serialVersionUID = 1L;


    private String id;
    private String name;
    private String typeId;//一级分类ID
    private String subTypeId;//二级分类ID
    private String deviceType;//设备业务类型
    /**
     * 计费方式：0 无 ，1 表端计费，2 平台计费
     */
    private Integer payMode;
    /**
     * 结算模式 0 无 ，1 后付费，2 预付费
     */
    private Integer settleMode;
    /**
     * 传输协议
     */
    private String protocolType;
    /**
     * 解析协议标识
     */
    private String handlerCode;
    /**
     * 接入平台
     */
    private String platform;
    /**
     * auth配置信息
     */
    private NutMap authConfig;
    /**
     * 配置文件配置信息
     */
    private NutMap fileConfig;
}
