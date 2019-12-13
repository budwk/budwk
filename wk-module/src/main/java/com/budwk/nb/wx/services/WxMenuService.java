package com.budwk.nb.wx.services;

import com.budwk.nb.wx.models.Wx_menu;
import com.budwk.nb.commons.base.service.BaseService;

public interface WxMenuService extends BaseService<Wx_menu> {
    void save(Wx_menu menu, String pid);

    void deleteAndChild(Wx_menu menu);
}
