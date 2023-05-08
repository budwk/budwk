package com.budwk.app.device.handler.common.utils.aep.model;

import lombok.Data;
import org.nutz.lang.util.NutMap;

import java.io.Serializable;

/**
 * lwm2m协议有profile指令
 * @author wizzer.cn
 */
@Data
public class CmdBody implements Serializable {
    private static final long serialVersionUID = 2048648304686209114L;
    /**
     * 设备ID，必填
     */
    private String deviceId;
    /**
     * 操作者，必填
     */
    private String operator;
    /**
     * 产品ID，必填
     */
    private String productId;
    /**
     * 指令在缓存时长默认60秒，选填
     */
    private int ttl = 60;
    /**
     * 指令内容,必填，格式为Json,参数如下：
     * {
     * 	  serviceId:命令对应的服务ID,
     * 	  method:命令服务下具体的命令名称
     *    paras:指令参数，格式为json,
     * }
     */
    private NutMap command;
}
