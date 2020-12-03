package com.budwk.nb.sys.services.impl;

import com.budwk.nb.commons.base.service.BaseServiceImpl;
import com.budwk.nb.sys.models.Sys_menu;
import com.budwk.nb.sys.models.Sys_role;
import com.budwk.nb.sys.models.Sys_user;
import com.budwk.nb.sys.services.SysMenuService;
import com.budwk.nb.sys.services.SysRoleService;
import com.budwk.nb.sys.services.SysUserService;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemove;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

import java.util.ArrayList;
import java.util.List;

import static com.budwk.nb.commons.constants.RedisConstant.PLATFORM_REDIS_WKCACHE_PREFIX;

/**
 * @author wizzer(wizzer.cn) on 2016/12/22.
 */
@IocBean(args = {"refer:dao"})
@CacheDefaults(cacheName = PLATFORM_REDIS_WKCACHE_PREFIX + "sys_user",isHash = true)
public class SysUserServiceImpl extends BaseServiceImpl<Sys_user> implements SysUserService {
    public SysUserServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private SysMenuService sysMenuService;
    @Inject
    private SysRoleService sysRoleService;

    @Override
    @CacheResult(cacheKey = "${user.id}_getRoles")
    public List<NutMap> getRoles(Sys_user user) {
        this.dao().fetchLinks(user, "roles");
        List<NutMap> roleNameList = new ArrayList<>();
        for (Sys_role role : user.getRoles()) {
            if (!role.isDisabled()) {
                roleNameList.add(NutMap.NEW().addv("code", role.getCode())
                        .addv("name", role.getName()));
            }
        }
        return roleNameList;
    }

    /**
     * 查询用户角色code列表
     *
     * @param user
     * @return
     */
    @Override
    @CacheResult(cacheKey = "${args[0].id}_getRoleCodeList")
    public List<String> getRoleCodeList(Sys_user user) {
        dao().fetchLinks(user, "roles");
        List<String> roleNameList = new ArrayList<String>();
        for (Sys_role role : user.getRoles()) {
            if (!role.isDisabled()) {
                roleNameList.add(role.getCode());
            }
        }
        return roleNameList;
    }

    /**
     * 查询用户菜单和按钮权限
     *
     * @param userId
     * @return
     */
    @Override
    @CacheResult(cacheKey = "${userId}_getMenusAndButtons")
    public List<Sys_menu> getMenusAndButtons(String userId) {
        Sql sql = Sqls.create("select distinct a.* from sys_menu a,sys_role_menu b where a.id=b.menuId and " +
                " b.roleId in(select c.roleId from sys_user_role c,sys_role d where c.roleId=d.id and c.userId=@userId and d.disabled=@f) and a.disabled=@f order by a.location ASC,a.path asc");
        sql.params().set("userId", userId);
        sql.params().set("f", false);
        return sysMenuService.listEntity(sql);
    }

    /**
     * @param userId
     * @param pid
     * @return
     */
    @Override
    public List<Sys_menu> getRoleMenus(String userId, String pid) {
        Sql sql = Sqls.create("select distinct a.* from sys_menu a,sys_role_menu b where a.id=b.menuId and " +
                "$m and b.roleId in(select c.roleId from sys_user_role c,sys_role d where c.roleId=d.id and c.userId=@userId and d.disabled=@f) and a.disabled=@f order by a.location ASC,a.path asc");
        sql.params().set("userId", userId);
        sql.params().set("f", false);
        if (Strings.isNotBlank(pid)) {
            sql.vars().set("m", "a.parentId='" + pid + "'");
        } else {
            sql.vars().set("m", "(a.parentId='' or a.parentId is null)");
        }
        return sysMenuService.listEntity(sql);

    }

    /**
     * @param userId
     * @param pid
     * @return
     */
    @Override
    public boolean hasChildren(String userId, String pid) {
        Sql sql = Sqls.create("select count(*) from sys_menu a,sys_role_menu b where a.id=b.menuId and " +
                "$m and b.roleId in(select c.roleId from sys_user_role c,sys_role d where c.roleId=d.id and c.userId=@userId and d.disabled=@f) and a.disabled=@f order by a.location ASC,a.path asc");
        sql.params().set("userId", userId);
        sql.params().set("f", false);
        if (Strings.isNotBlank(pid)) {
            sql.vars().set("m", "a.parentId='" + pid + "'");
        } else {
            sql.vars().set("m", "(a.parentId='' or a.parentId is null)");
        }
        return sysMenuService.count(sql) > 0;
    }

    /**
     * 删除一个用户
     *
     * @param userId
     */
    @Override
    @Aop(TransAop.READ_COMMITTED)
    public void deleteById(String userId) {
        dao().clear("sys_user_unit", Cnd.where("userId", "=", userId));
        dao().clear("sys_user_role", Cnd.where("userId", "=", userId));
        dao().clear("sys_user", Cnd.where("id", "=", userId));
    }

    /**
     * 批量删除用户
     *
     * @param userIds
     */
    @Override
    @Aop(TransAop.READ_COMMITTED)
    public void deleteByIds(String[] userIds) {
        dao().clear("sys_user_unit", Cnd.where("userId", "in", userIds));
        dao().clear("sys_user_role", Cnd.where("userId", "in", userIds));
        dao().clear("sys_user", Cnd.where("id", "in", userIds));
    }

    /**
     * 获取用户可分配角色列表
     *
     * @param kw
     * @param sysadmin
     * @param userId
     * @return
     */
    @Override
    public List<Sys_role> getUserCanRoleList(String kw, boolean sysadmin, String userId) {
        if (sysadmin) {
            Cnd cnd = Cnd.NEW();
            if (Strings.isNotBlank(kw)) {
                cnd.and("name", "like", "%" + kw + "%");
            }
            return sysRoleService.query(cnd);
        } else {
            Sql sql = Sqls.create("SELECT a.* FROM sys_role a,sys_user_role b WHERE a.id=b.roleId AND b.userId=@userId $s");
            sql.setParam("userId", userId);
            if (Strings.isNotBlank(kw)) {
                sql.vars().set("s", " AND a.name like '%" + kw + "%'");
            }
            return sysRoleService.listEntity(sql);
        }
    }

    /**
     * 创建用户及角色关系
     *
     * @param roleIds
     * @param userId
     */
    @Override
    public void insertUserRole(String[] roleIds, String userId) {
        dao().clear("sys_user_role", Cnd.where("userId", "=", userId));
        for (String roleId : roleIds) {
            dao().insert("sys_user_role", Chain.from(NutMap.NEW().addv("roleId", roleId).addv("userId", userId)));
        }
    }

    /**
     * 查询用户分配的角色
     *
     * @param userId
     * @return
     */
    public List<NutMap> getUserRoles(String userId) {
        return this.listMap(Sqls.create("select roleId from sys_user_role where userId=@userId").setParam("userId", userId));
    }

    @Override
    @CacheRemove(cacheKey = "${userId}_*")
    @Async
    public void deleteCache(String userId) {

    }

    @Override
    @CacheRemoveAll
    @Async
    public void clearCache() {

    }
}
