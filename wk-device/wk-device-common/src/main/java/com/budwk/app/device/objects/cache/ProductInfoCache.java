package com.budwk.app.device.objects.cache;

import com.budwk.app.device.enums.*;
import lombok.Data;
import org.nutz.lang.util.NutMap;

import java.io.Serializable;

/**
 * @author wizzer.cn
 */
@Data
public class ProductInfoCache implements Serializable {
    private static final long serialVersionUID = -6680588310568121435L;
    private String id;
    private String name;
    private String type;//一级分类ID
    private String subType;//二级分类ID
    private DeviceType deviceType;//设备业务类型
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
