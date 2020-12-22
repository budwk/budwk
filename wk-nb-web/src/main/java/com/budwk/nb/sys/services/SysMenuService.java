package com.budwk.nb.sys.services;

import com.budwk.nb.sys.models.Sys_menu;
import com.budwk.nb.base.service.BaseService;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * @author wizzer(wizzer.cn) on 2016/12/22.
 */
public interface SysMenuService extends BaseService<Sys_menu> {
    /**
     * 保存菜单
     *
     * @param menu  菜单对象
     * @param pid   父节点ID
     * @param datas 数据权限
     */
    void save(Sys_menu menu, String pid, List<NutMap> datas);

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

    /**
     * 获取左侧菜单
     *
     * @param href 请求路径
     * @return
     */
    Sys_menu getLeftMenu(String href);

    /**
     * 获取左侧菜单路径
     *
     * @param list 路径列表
     * @return
     */
    Sys_menu getLeftPathMenu(List<String> list);

    /**
     * 清空缓存
     */
    void clearCache();
}
