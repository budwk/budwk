package com.budwk.app.wx.services;

import com.budwk.app.wx.models.Wx_menu;
import com.budwk.starter.database.service.BaseService;

/**
 * @author wizzer@qq.com
 */
public interface WxMenuService extends BaseService<Wx_menu> {
    /**
     * 保存微信菜单
     *
     * @param menu 菜单对象
     * @param pid  父节点ID
     */
    void save(Wx_menu menu, String pid);

    /**
     * 删除微信菜单
     *
     * @param menu 菜单对象
     */
    void deleteAndChild(Wx_menu menu);
}