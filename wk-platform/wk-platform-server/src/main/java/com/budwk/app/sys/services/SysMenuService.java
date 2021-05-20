package com.budwk.app.sys.services;

import com.budwk.app.sys.models.Sys_menu;
import com.budwk.starter.database.service.BaseService;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * @author wizzer@qq.com
 */
public interface SysMenuService extends BaseService<Sys_menu> {

    /**
     * 保存菜单
     *
     * @param appId 应用ID
     * @param menu  菜单对象
     * @param pid   父节点ID
     * @param datas 数据权限
     */
    void save(String appId, Sys_menu menu, String pid, List<NutMap> datas);

    /**
     * 编辑菜单
     *
     * @param menu  菜单对象
     * @param pid   父节点ID
     * @param datas 数据权限
     */
    void edit(Sys_menu menu, String pid, List<NutMap> datas);

    /**
     * 级联删除菜单
     *
     * @param menu 菜单对象
     */
    void deleteAndChild(Sys_menu menu);
}
