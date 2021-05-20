package com.budwk.starter.security;

import java.util.List;

/**
 * @author wizzer@qq.com
 */
public interface IAuthProvider {
    /**
     * 获取用户权限列表
     *
     * @param userId 用户ID
     * @return
     */
    List<String> getPermissionList(String userId);

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return
     */
    List<String> getRoleList(String userId);
}
