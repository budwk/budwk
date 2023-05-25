package com.budwk.app.device.services.impl;

import com.budwk.app.device.models.Device_product;
import com.budwk.app.device.services.DeviceProductService;
import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.database.service.BaseServiceImpl;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemove;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

/**
 * @author wizzer.cn
 */
@IocBean(args = {"refer:dao"})
@CacheDefaults(cacheName = RedisConstant.WKCACHE + "device_product", isHash = false, cacheLiveTime = RedisConstant.WKCACHE_TIMEOUT)
public class DeviceProductServiceImpl extends BaseServiceImpl<Device_product> implements DeviceProductService {
    public DeviceProductServiceImpl(Dao dao) {
        super(dao);
    }

    @Override
    public void createProduct(Device_product deviceProduct){
        this.insert(deviceProduct);
        //todo 产品菜单初始化、设备字段初始化等
        this.deleteCache(deviceProduct.getId());
    }

    @Override
    public void updateProduct(Device_product deviceProduct){
        this.updateIgnoreNull(deviceProduct);
        this.deleteCache(deviceProduct.getId());
    }

    @Override
    @CacheResult(cacheKey = "${productId}_getWithCache")
    public Device_product getProductWithCache(String productId) {
        if (Strings.isBlank(productId)) {
            return null;
        }
        return this.fetch(productId);
    }

    @Override
    @CacheRemove(cacheKey = "${productId}_*")
    public void deleteCache(String productId) {

    }

    @Override
    @CacheRemoveAll
    public void clearCache() {

    }
}