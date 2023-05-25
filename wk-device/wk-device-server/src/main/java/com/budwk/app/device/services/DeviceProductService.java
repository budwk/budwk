package com.budwk.app.device.services;

import com.budwk.app.device.models.Device_product;
import com.budwk.starter.database.service.BaseService;

/**
 * @author wizzer.cn
 */
public interface DeviceProductService extends BaseService<Device_product> {
    void createProduct(Device_product deviceProduct);
    void updateProduct(Device_product deviceProduct);
    Device_product getProductWithCache(String productId);
    void deleteCache(String productId);
    void clearCache();
}