package com.budwk.app.device.cache.impl;

import com.budwk.app.device.cache.ProductCaching;
import com.budwk.app.device.cache.constants.CacheConstant;
import com.budwk.app.device.models.Device_product;
import com.budwk.app.device.objects.cache.ProductInfoCache;
import com.budwk.starter.redisjson.RedisJsonService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.NutMap;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wizzer.cn
 */
@IocBean
public class ProductCachingImpl implements ProductCaching {

    @Inject
    private RedisJsonService redisJsonService;

    @Override
    public ProductInfoCache cache(Device_product product, NutMap fileConfig) {
        ProductInfoCache cache = new ProductInfoCache();
        cache.setId(product.getId());
        cache.setName(product.getName());
        cache.setType(product.getTypeId());
        cache.setSubType(product.getSubTypeId());
        cache.setDeviceType(product.getDeviceType());
        cache.setHandlerCode(product.getHandlerCode());
        cache.setPayMode(product.getPayMode());
        cache.setSettleMode(product.getSettleMode());
        cache.setProtocolType(product.getProtocolType());
        cache.setPlatform(product.getIotPlatform());
        if (null != fileConfig) {
            redisJsonService.setObject(CacheConstant.PRODUCT_CACHE_KEY.concat(product.getId()).concat(".fileConfig"), fileConfig);
        }
        if (null != product.getAuthJson()) {
            redisJsonService.setObject(CacheConstant.PRODUCT_CACHE_KEY.concat(product.getId()).concat(".authConfig"), Json.fromJson(NutMap.class, product.getAuthJson()));
        }
        redisJsonService.setObject(CacheConstant.PRODUCT_CACHE_KEY.concat(product.getId()), cache);
        return cache;
    }

    @Override
    public boolean update(Device_product product, NutMap fileConfig) {
        if (null == product && null == fileConfig) {
            return false;
        }
        if (null != product) {
            String productId = product.getId();
            Mirror<Device_product> mir = Mirror.me(product);
            for (Field field : mir.getFields()) {
                if (null != mir.getValue(product, field.getName())) {
                    String ok = redisJsonService.updateField(productId, field.getName(), mir.getValue(product, field.getName()));
                    if (!ok.equals(CacheConstant.SUCCESS)) {
                        return false;
                    }
                }
            }
            if (null != fileConfig) {
                redisJsonService.setObject(CacheConstant.PRODUCT_CACHE_KEY.concat(productId).concat(".fileConfig"), fileConfig);
            }
            if (null != product.getAuthJson()) {
                redisJsonService.setObject(CacheConstant.PRODUCT_CACHE_KEY.concat(product.getId()).concat(".authConfig"), Json.fromJson(Map.class, product.getAuthJson()));
            }
        }
        return true;
    }

    @Override
    public ProductInfoCache getProductCache(String productId) {
        ProductInfoCache object = redisJsonService.getObject(CacheConstant.PRODUCT_CACHE_KEY.concat(productId), ProductInfoCache.class);
        if (null != object) {
            NutMap fileConfig = redisJsonService.getObject(CacheConstant.PRODUCT_CACHE_KEY.concat(productId).concat(".fileConfig"), NutMap.class);
            NutMap authConfig = redisJsonService.getObject(CacheConstant.PRODUCT_CACHE_KEY.concat(productId).concat(".authConfig"), NutMap.class);
            object.setFileConfig(fileConfig);
            object.setAuthConfig(authConfig);
        }
        return object;
    }

    @Override
    public boolean deleteProductCache(String productId) {
        redisJsonService.delObject(productId);
        return true;
    }
}
