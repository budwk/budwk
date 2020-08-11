package com.budwk.nb.sys.services;

import com.budwk.nb.sys.models.Sys_menu;
import com.budwk.nb.sys.models.Sys_role;
import com.budwk.nb.sys.models.Sys_user;
import com.budwk.nb.commons.base.service.BaseService;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * @author wizzer(wizzer@qq.com) on 2016/12/22.
 */
public interface SysUserService extends BaseService<Sys_user> {
    /**
     * 查询用户的角色
     *
     * @param user 用户对象
     * @return
     */
    List<NutMap> getRoles(Sys_user user);
    /**
     * 查询用户的角色
     *
     * @param user 用户对象
     * @return
     */
    List<String> getRoleCodeList(Sys_user user);

    /**
     * 通过用户ID获取菜单及权限
     *
     * @param userId 用户ID
     * @return
     */
    List<Sys_menu> getMenusAndButtons(String userId);

    /**
     * 通过用户ID和菜单父ID获取下级权限菜单
     *
     * @param userId 用户ID
     * @param pid 菜单父ID
     * @return
     */
    List<Sys_menu> getRoleMenus(String userId, String pid);

    /**
     * 判断用户是否有下级数据权限
     *
     * @param userId 用户ID
     * @param pid 菜单父ID
     * @return
     */
    boolean hasChildren(String userId, String pid);
    /**
     * 通过用户ID删除用户
     *
     * @param userId 用户ID
     */
    void deleteById(String userId);

    /**
     * 批量删除用户
     *
     * @param userIds 用户ID数组
     */
    void deleteByIds(String[] userIds);

    /**
     * 获取用户可分配角色列表
     *
     * @param kw
     * @param sysadmin
     * @param userId
     * @return
     */

    List<Sys_role> getUserCanRoleList(String kw, boolean sysadmin, String userId);

    /**
     * 创建用户及角色关系
     *
     * @param roleIds
     * @param userId
     */
    void insertUserRole(String[] roleIds, String userId);

    /**
     * 查询用户分配的角色
     *
     * @param userId
     * @return
     */
    List<NutMap> getUserRoles(String userId);
    /**
     * 清除一个用户的缓存
     *
     * @param userId 用户ID
     */
    void deleteCache(String userId);

    /**
     * 清空缓存
     */
    void clearCache();
}
