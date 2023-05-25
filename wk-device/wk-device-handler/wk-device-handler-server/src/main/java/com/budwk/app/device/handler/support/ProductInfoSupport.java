package com.budwk.app.device.handler.support;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.app.device.cache.ProductCaching;
import com.budwk.app.device.enums.IotPlatform;
import com.budwk.app.device.handler.common.device.ProductInfo;
import com.budwk.app.device.models.Device_product;
import com.budwk.app.device.objects.cache.ProductInfoCache;
import com.budwk.app.device.providers.IDeviceInfoProvider;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import java.util.Locale;

/**
 * @author zyang
 * @author wizzer.cn
 */
@IocBean
@Slf4j
public class ProductInfoSupport {
    @Inject
    private ProductCaching productCaching;
    @Inject
    protected PropertiesProxy conf;
    @Inject
    @Reference(check = false)
    private IDeviceInfoProvider deviceInfoProvider;


    public ProductInfo getProduct(String productId) {
        ProductInfoCache product = productCaching.getProductCache(productId);
        if (null == product) {
            Device_product product1 = deviceInfoProvider.getProduct(productId);
            //获取平台接入配置信息
            IotPlatform iotPlatform = product1.getIotPlatform();
            NutMap fileConfig = conf.make(NutMap.class, "platform." + iotPlatform.value().toLowerCase(Locale.ROOT));
            product = productCaching.cache(deviceInfoProvider.getProduct(productId), fileConfig);
        }
        ProductInfo productInfo = new ProductInfo();
        productInfo.setId(product.getId());
        productInfo.setName(product.getName());
        productInfo.setTypeId(product.getType());
        productInfo.setSubTypeId(product.getSubType());
        productInfo.setDeviceType(product.getDeviceType().value());
        productInfo.setHandlerCode(product.getHandlerCode());
        productInfo.setPlatform(product.getPlatform().value());
        productInfo.setPayMode(null == product.getPayMode() ? null : product.getPayMode().value());
        productInfo.setSettleMode(null == product.getSettleMode() ? null : product.getSettleMode().value());
        productInfo.setProtocolType(null == product.getProtocolType() ? null : product.getProtocolType().value());
        productInfo.setFileConfig(product.getFileConfig());
        productInfo.setAuthConfig(product.getAuthConfig());
        return productInfo;
    }
}
