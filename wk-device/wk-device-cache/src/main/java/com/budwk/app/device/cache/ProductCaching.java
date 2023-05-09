package com.budwk.app.device.cache;

import com.budwk.app.device.models.Device_product;
import com.budwk.app.device.objects.cache.ProductInfoCache;

/**
 * 产品缓存
 * @author wizzer.cn
 */
public interface ProductCaching {
    ProductInfoCache cache(Device_product product);

    boolean update(Device_product product);

    ProductInfoCache get(String productId);

    boolean delete(String productId);
}
