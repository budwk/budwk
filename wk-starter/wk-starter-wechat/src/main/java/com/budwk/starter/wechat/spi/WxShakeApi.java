package com.budwk.starter.wechat.spi;

/**
 * 摇一摇API
 * @author wendal
 *
 */
public interface WxShakeApi {

    WxResp applyId(int quantity, String apply_reason, String comment, int poi_id);
    
    WxResp applyStatus(String id);
    
    WxResp update(int device_id, String comment);
    
    WxResp update(String uuid, int major, int minor, String comment);
    
    WxResp bindLocation(int deviceId, int poi_id);
    
    WxResp bindLocation(String uuid, int major, int minor, int poi_id);

    WxResp search(int device_id);
    WxResp search(String uuid, int major, int minor);
    WxResp search(int begin, int count);
    WxResp search(int apply_id, int begin, int count);
    
    WxResp getShakeInfo(String ticket, int need_poi);
}
