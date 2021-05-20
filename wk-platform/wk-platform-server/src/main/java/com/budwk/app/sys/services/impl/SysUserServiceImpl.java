package com.budwk.app.sys.services.impl;

import cn.hutool.core.util.RandomUtil;
import com.budwk.app.sys.models.Sys_app;
import com.budwk.app.sys.models.Sys_menu;
import com.budwk.app.sys.models.Sys_role;
import com.budwk.app.sys.models.Sys_user;
import com.budwk.app.sys.services.*;
import com.budwk.starter.common.constant.RedisConstant;
import com.budwk.starter.common.exception.BaseException;
import com.budwk.starter.common.utils.PwdUtil;
import com.budwk.starter.database.service.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
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
import org.nutz.lang.random.R;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemove;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
@CacheDefaults(cacheName = RedisConstant.WKCACHE + "sys_user", isHash = true, cacheLiveTime = RedisConstant.WKCACHE_TIMEOUT)
@Slf4j
public class SysUserServiceImpl extends BaseServiceImpl<Sys_user> implements SysUserService {
    public SysUserServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private SysRoleService sysRoleService;

    @Inject
    private SysMenuService sysMenuService;

    @Inject
    private SysAppService sysAppService;

    @Inject
    private SysUnitService sysUnitService;

    @Override
    @CacheResult(cacheKey = "${userId}_getPermissionList")
    public List<String> getPermissionList(String userId) {
        Sys_user user = this.fetch(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        this.fetchLinks(user, "roles");
        if (user.getRoles() == null) {
            return new ArrayList<>();
        }
        List<String> permissionList = new ArrayList<String>();
        for (Sys_role role : user.getRoles()) {
            if (!role.isDisabled()) {
                permissionList.addAll(sysRoleService.getPermissionList(role));
            }
        }
        // 追加public公共角色权限
        permissionList.addAll(sysRoleService.getPermissionList(sysRoleService.fetch(Cnd.where("code", "=", "public"))));
        return permissionList;
    }

    @Override
    @CacheResult(cacheKey = "${userId}_getRoleList")
    public List<String> getRoleList(String userId) {
        Sys_user user = this.fetch(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        this.fetchLinks(user, "roles");
        if (user.getRoles() == null) {
            return new ArrayList<>();
        }
        List<String> roleList = new ArrayList<String>();
        for (Sys_role role : user.getRoles()) {
            if (!role.isDisabled()) {
                roleList.add(role.getCode());
            }
        }
        // 追加public公共角色权限
        roleList.add("public");
        return roleList;
    }

    @Override
    @CacheResult(cacheKey = "${userId}_getMenuList")
    public List<Sys_menu> getMenuList(String userId) {
        Sql sql = Sqls.create("select distinct a.* from sys_menu a,sys_role_menu b where a.id=b.menuId and " +
                " (b.roleId=@pubRoleId or b.roleId in(select c.roleId from sys_user_role c,sys_role d where c.roleId=d.id and c.userId=@userId and d.disabled=@f) ) and a.disabled=@f order by a.location ASC,a.path asc");
        sql.params().set("pubRoleId", sysRoleService.getPublicId());
        sql.params().set("userId", userId);
        sql.params().set("f", false);
        return sysMenuService.listEntity(sql);
    }

    @Override
    @CacheResult(cacheKey = "${userId}_${appId}_getMenuList")
    public List<Sys_menu> getMenuList(String userId, String appId) {
        Sql sql = Sqls.create("select distinct a.* from sys_menu a,sys_role_menu b where a.appId=@appId and a.id=b.menuId and " +
                " (b.roleId=@pubRoleId or b.roleId in(select c.roleId from sys_role_user c,sys_role d where c.roleId=d.id and c.userId=@userId and d.disabled=@f) ) and a.disabled=@f order by a.location ASC,a.path asc");
        sql.params().set("pubRoleId", sysRoleService.getPublicId());
        sql.params().set("userId", userId);
        sql.params().set("appId", appId);
        sql.params().set("f", false);
        return sysMenuService.listEntity(sql);
    }

    @Override
    @CacheResult(cacheKey = "${userId}_getAppList")
    public List<Sys_app> getAppList(String userId) {
        Sql sql = Sqls.create("select distinct a.* from sys_app a,sys_role_app b where a.id=b.appId and " +
                " (b.roleId=@pubRoleId or b.roleId in(select c.roleId from sys_role_user c,sys_role d where c.roleId=d.id and c.userId=@userId and d.disabled=@f) ) and a.disabled=@f order by a.location");
        sql.params().set("pubRoleId", sysRoleService.getPublicId());
        sql.params().set("userId", userId);
        sql.params().set("f", false);
        return sysAppService.listEntity(sql);
    }

    @Override
    public List<Sys_menu> getMenusAndDatas(String userId, String appId) {
        Sql sql = Sqls.create("select distinct a.* from sys_menu a,sys_role_menu b where a.id=b.menuId and a.appId=@appId and " +
                " b.roleId in(select c.roleId from sys_role_user c,sys_role d where c.roleId=d.id and c.userId=@userId and d.disabled=@f) and a.disabled=@f order by a.location ASC,a.path asc");
        sql.params().set("userId", userId);
        sql.params().set("appId", appId);
        sql.params().set("f", false);
        return sysMenuService.listEntity(sql);
    }

    @Override
    public void checkLoginname(String loginname) throws BaseException {
        if (this.count(Cnd.where("loginname", "=", loginname)) < 1) {
            throw new BaseException("用户名不存在");
        }
    }

    @Override
    public void checkMobile(String mobile) throws BaseException {
        if (this.count(Cnd.where("mobile", "=", mobile)) < 1) {
            throw new BaseException("手机号不存在");
        }
    }

    @Override
    public Sys_user loginByPassword(String loginname, String passowrd) throws BaseException {
        Sys_user user = this.fetch(Cnd.where("loginname", "=", loginname));
        if (user == null) {
            throw new BaseException("用户不存在");
        }
        if (user.isDisabled()) {
            throw new BaseException("用户被禁用");
        }
        String hashedPassword = PwdUtil.getPassword(passowrd, user.getSalt());
        if (!Strings.sNull(hashedPassword).equalsIgnoreCase(user.getPassword())) {
            throw new BaseException("密码不正确");
        }
        return user;
    }

    @Override
    public Sys_user loginByMobile(String mobile) throws BaseException {
        Sys_user user = this.fetch(Cnd.where("mobile", "=", mobile));
        if (user == null) {
            throw new BaseException("用户不存在");
        }
        if (user.isDisabled()) {
            throw new BaseException("用户被禁用");
        }
        return user;
    }

    // 这里不能用缓存,因为没有 userId 没法用前缀清除,会造成缓存清除不干净
    public Sys_user getUserByLoginname(String loginname) throws BaseException {
        Sys_user user = this.fetch(Cnd.where("loginname", "=", loginname));
        if (user == null) {
            throw new BaseException("用户不存在");
        }
        return user;
    }

    @CacheResult(cacheKey = "${userId}_getUserById")
    public Sys_user getUserById(String userId) throws BaseException {
        Sys_user user = this.fetch(userId);
        if (user == null) {
            throw new BaseException("用户不存在");
        }
        return user;
    }

    public void setPwdByLoginname(String loginname, String password) throws BaseException {
        Sys_user user = this.fetch(Cnd.where("loginname", "=", loginname));
        if (user == null) {
            throw new BaseException("用户不存在");
        }
        String salt = R.UU32();
        this.update(Chain.make("salt", salt).add("password", PwdUtil.getPassword(password, salt)),
                Cnd.where("loginname", "=", loginname));
        this.cacheRemove(user.getId());
    }

    public void setPwdById(String id, String password) throws BaseException {
        Sys_user user = this.fetch(id);
        if (user == null) {
            throw new BaseException("用户不存在");
        }
        String salt = R.UU32();
        this.update(Chain.make("salt", salt).add("password", PwdUtil.getPassword(password, salt)),
                Cnd.where("id", "=", id));
        this.cacheRemove(user.getId());
    }

    public void setThemeConfig(String id, String themeConfig) {
        this.update(Chain.make("themeConfig", themeConfig), Cnd.where("id", "=", id));
        this.cacheRemove(id);// 清一下缓存
    }

    public void setLoginInfo(String userId, String ip) {
        this.update(Chain.make("loginIp", ip).add("loginAt", System.currentTimeMillis()).addSpecial("loginCount", "+1"), Cnd.where("id", "=", userId));
    }

    @Override
    @Aop(TransAop.READ_COMMITTED)
    public void create(Sys_user user, String[] roleIds) {
        String password = user.getPassword();
        if (Strings.isBlank(password)) {
            password = user.getMobile().substring(user.getMobile().length() - 6);
        }
        String salt = R.UU32();
        user.setPassword(PwdUtil.getPassword(password, salt));
        user.setSalt(salt);
        user.setLoginCount(0);
        user.setNeedChangePwd(true);
        user.setCompanyId(sysUnitService.getMasterCompanyId(user.getUnitId()));
        this.insert(user);
        if (roleIds != null) {
            for (String roleId : roleIds) {
                this.dao().insert("sys_role_user", Chain.make("id", R.UU32()).add("userId", user.getId()).add("roleId", roleId));
            }
        }
    }

    @Override
    @Aop(TransAop.READ_COMMITTED)
    public void update(Sys_user user, String[] roleIds) {
        user.setPassword(null);
        user.setSalt(null);
        this.updateIgnoreNull(user);
        this.dao().clear("sys_role_user", Cnd.where("userId", "=", user.getId()));
        if (roleIds != null) {
            for (String roleId : roleIds) {
                this.dao().insert("sys_role_user", Chain.make("id", R.UU32()).add("userId", user.getId()).add("roleId", roleId));
            }
        }
        this.cacheRemove(user.getId());
    }

    @Override
    public String resetPwd(String userId) {
        String salt = R.UU32();
        String password = RandomUtil.randomNumbers(6);
        this.update(Chain.make("password", PwdUtil.getPassword(password, salt))
                .add("salt", salt).add("needChangePwd", true), Cnd.where("id", "=", userId));
        this.cacheRemove(userId);
        return password;
    }

    @Override
    public String resetPwd(String userId, String password,boolean needChangePwd) {
        String salt = R.UU32();
        this.update(Chain.make("password", PwdUtil.getPassword(password, salt))
                .add("salt", salt).add("needChangePwd", needChangePwd), Cnd.where("id", "=", userId));
        this.cacheRemove(userId);
        return password;
    }

    @Override
    @Aop(TransAop.READ_COMMITTED)
    public void deleteUser(String userId) {
        this.dao().clear("sys_role_user", Cnd.where("userId", "=", userId));
        this.dao().clear("sys_unit_user", Cnd.where("userId", "=", userId));
        this.delete(userId);
        this.cacheRemove(userId);
    }

    @Override
    @CacheRemove(cacheKey = "${userId}_*")
    @Async
    public void cacheRemove(String userId) {

    }

    @Override
    @CacheRemoveAll
    @Async
    public void cacheClear() {

    }
}
