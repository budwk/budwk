package com.budwk.nb.wx.services;

import com.budwk.nb.commons.base.service.BaseService;
import com.budwk.nb.wx.models.Wx_menu;

/**
 * @author wizzer(wizzer.cn) on 2019/12/12.
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
