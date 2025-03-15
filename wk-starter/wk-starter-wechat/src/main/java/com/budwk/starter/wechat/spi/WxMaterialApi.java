package com.budwk.starter.wechat.spi;

import java.io.File;
import java.util.List;

import org.nutz.resource.NutResource;
import com.budwk.starter.wechat.bean.WxArticle;
import com.budwk.starter.wechat.bean.WxMassArticle;

/**
 * http://mp.weixin.qq.com/wiki/10/10ea5a44870f53d79449290dfd43d006.html
 * @author wendal
 *
 */
public interface WxMaterialApi {

    WxResp add_news(WxMassArticle...news);

    WxResp uploadimg(File f);

    WxResp uploadnews(List<WxMassArticle> articles) ;
    
    WxResp add_material(String type, File f);
    
    WxResp add_video(File f, String title, String introduction);
    
    NutResource get_material(String media_id);
    
    List<WxArticle> get_material_news(String media_id);
    
    WxResp get_material_video(String media_id);
    
    WxResp del_material(String media_id);
    
    WxResp update_material(String media_id, int index, WxArticle article);
    
    WxResp get_materialcount();
    
    WxResp batchget_material(String type, int offset, int count);
}
