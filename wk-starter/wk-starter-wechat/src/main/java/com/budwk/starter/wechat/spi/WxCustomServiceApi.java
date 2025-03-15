package com.budwk.starter.wechat.spi;

import java.io.File;
import java.util.List;

import com.budwk.starter.wechat.bean.WxKfAccount;

/**
 * http://mp.weixin.qq.com/wiki/18/749901f4e123170fb8a4d447ae6040ba.html
 */
public interface WxCustomServiceApi {

    List<WxKfAccount> getkflist();
    
    List<WxKfAccount> getonlinekflist();
    
    WxResp kfaccount_add(String kf_account, String nickname, String password);
    
    WxResp kfaccount_update(String kf_account, String nickname, String password);
    
    WxResp kfaccount_uploadheadimg(String kf_account, File f);
    
    WxResp kfaccount_del(String kf_account);
}
