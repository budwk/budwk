package com.budwk.app.sys.services;


import com.budwk.app.sys.models.Sys_app;
import com.budwk.app.sys.models.Sys_menu;
import com.budwk.app.sys.models.Sys_role;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.database.service.BaseService;

import java.util.List;

/**
 * @author wizzer@qq.com
 */
public interface SysRoleService extends BaseService<Sys_role> {

    /**
     * 获取public角色Id
     *
     * @return
     */
    String getPublicId();

    /**
     * 获取角色权限
     *
     * @param role 角色对象
     * @return
     */
    List<String> getPermissionList(Sys_role role);

    /**
     * 获取应用列表
     *
     * @param userId
     * @return
     */
    List<Sys_app> getAppList(String userId);

    /**
     * 删除角色
     *
     * @param roleId
     */
    void clearRole(String roleId);

    /**
     * 获取角色用户列表
     *
     * @param roleId        角色ID
     * @param username      用户姓名
     * @param pageNo        页码
     * @param pageSize      页大小
     * @param pageOrderName 排序字段
     * @param pageOrderBy   排序方式
     * @return
     */
    Pagination getUserListPage(String roleId, String username, int pageNo, int pageSize, String pageOrderName, String pageOrderBy);

    /**
     * 获取待分配用户列表(排除已分配)
     *
     * @param roleId        角色ID
     * @param username      用户姓名
     * @param sysadmin      是否超级管理员
     * @param unitId        当前用户单位ID
     * @param pageNo        页码
     * @param pageSize      页大小
     * @param pageOrderName 排序字段
     * @param pageOrderBy   排序方式
     * @return
     */
    Pagination getSelUserListPage(String roleId, String username, boolean sysadmin, String unitId, int pageNo, int pageSize, String pageOrderName, String pageOrderBy);

    /**
     * 关联用户到角色
     *
     * @param roleId  角色ID
     * @param userId  当前用户ID
     * @param userIds 用户ID数组
     */
    void doLinkUser(String roleId, String userId, String[] userIds);

    /**
     * 从角色移除用户
     *
     * @param roleId 角色ID
     * @param id     用户ID
     */
    void doLinkUser(String roleId, String id);

    /**
     * 通过角色ID获取菜单及数据权限
     *
     * @param roleId 角色ID
     * @param appId  应用ID
     * @return
     */
    List<Sys_menu> getMenusAndDatas(String roleId, String appId);


    /**
     * 保存菜单数据
     *
     * @param roleId  角色ID
     * @param appId   应用ID
     * @param menuIds 菜单数组
     */
    void saveMenu(String roleId, String appId, String[] menuIds);
}
