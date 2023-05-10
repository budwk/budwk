package com.budwk.app.device.cache;

import com.budwk.app.device.models.Device_product;
import com.budwk.app.device.objects.cache.ProductInfoCache;
import org.nutz.lang.util.NutMap;

import java.util.Map;

/**
 * 产品缓存
 * @author wizzer.cn
 */
public interface ProductCaching {
    ProductInfoCache cache(Device_product product, NutMap fileConfig);

    boolean update(Device_product product,NutMap fileConfig);

    ProductInfoCache getProductCache(String productId);

    boolean deleteProductCache(String productId);
}
