package com.budwk.app.device.objects.cache;

import com.budwk.app.device.enums.IotPlatform;
import com.budwk.app.device.enums.PayMode;
import com.budwk.app.device.enums.ProtocolType;
import com.budwk.app.device.enums.SettleMode;
import lombok.Data;
import org.nutz.lang.util.NutMap;

import java.io.Serializable;
import java.util.Map;

/**
 * @author wizzer.cn
 */
@Data
public class ProductInfoCache implements Serializable {
    private static final long serialVersionUID = -6680588310568121435L;
    private String id;
    private String name;
    private String type;
    private String secondType;
    private String bizCode;
    private PayMode payMode;
    private SettleMode settleMode;
    private ProtocolType protocolType;
    private String handlerCode;
    private IotPlatform platform;
    /**
     * 产品的配置文件配置信息
     */
    private NutMap fileConfig;
    /**
     * 产品的认证配置信息
     */
    private NutMap authConfig;
}
