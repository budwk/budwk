package com.budwk.nb.web.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.base.page.Pagination;
import com.budwk.nb.commons.constants.PlatformConstant;
import com.budwk.nb.commons.constants.RedisConstant;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.sys.models.Sys_menu;
import com.budwk.nb.sys.models.Sys_unit;
import com.budwk.nb.sys.models.Sys_user;
import com.budwk.nb.sys.services.SysLangLocalService;
import com.budwk.nb.sys.services.SysUnitService;
import com.budwk.nb.sys.services.SysUserService;
import com.budwk.nb.web.commons.base.Globals;
import com.budwk.nb.web.commons.utils.ShiroUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.integration.jedis.RedisService;
import org.nutz.integration.json4excel.J4E;
import org.nutz.integration.json4excel.J4EColumn;
import org.nutz.integration.json4excel.J4EConf;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author wizzer(wizzer @ qq.com) on 2019/11/21
 */
@IocBean
@At("/api/{version}/platform/sys/user")
@Ok("json")
@ApiVersion("1.0.0")
public class SysUserController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysUserService sysUserService;
    @Inject
    @Reference
    private SysUnitService sysUnitService;
    @Inject
    @Reference
    private SysLangLocalService sysLangLocalService;
    @Inject
    @Reference
    private ShiroUtil shiroUtil;
    @Inject
    private RedisService redisService;

    /**
     * @api {get} /api/1.0.0/platform/sys/user/logon_user_info 获取当前用户菜单及角色
     * @apiName logon_user_info
     * @apiGroup SYS_USER
     * @apiPermission 登陆用户
     * @apiVersion 1.0.0
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  数据对象
     * @apiSuccess {Object} data.user  用户数据
     * @apiSuccess {Object} data.menus  用户菜单
     * @apiSuccess {Object} data.roles  用户角色
     */
    @At("/logon_user_info")
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @GET
    @RequiresAuthentication
    public Object getLogonUserInfo(HttpSession session) {
        try {
            Subject subject = SecurityUtils.getSubject();
            if (subject != null) {
                Sys_user user = (Sys_user) subject.getPrincipal();
                if (user != null) {
                    return Result.success().addData(
                            NutMap.NEW()
                                    .addv("lang", sysLangLocalService.getLocal())
                                    .addv("conf", Globals.MyConfig)
                                    .addv("user", user)
                                    .addv("token", Strings.sNull(session.getAttribute("userToken")))
                                    .addv("menus", sysUserService.getMenusAndButtons(user.getId()))
                                    .addv("roles", sysUserService.getRoles(user)));
                }
            }
            return Result.error(500211, "system.login.norole");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.error();
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/user/set_user_theme 设置用户布局及样式
     * @apiName set_user_theme
     * @apiGroup SYS_USER
     * @apiPermission 登陆用户
     * @apiVersion 1.0.0
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/set_user_theme")
    @Ok("json")
    @POST
    @RequiresAuthentication
    public Object setUserThemeConfig(@Param("themeConfig") String themeConfig) {
        try {
            if ("superadmin".equals(StringUtil.getPlatformLoginname()) && Globals.MyConfig.getBoolean("AppDemoEnv")) {
                return Result.error("system.error.demo");
            }
            sysUserService.update(Chain.make("themeConfig", themeConfig), Cnd.where("id", "=", StringUtil.getPlatformUid()));
            Subject subject = SecurityUtils.getSubject();
            Sys_user user = (Sys_user) subject.getPrincipal();
            // 替换当前用户session里的值
            if (user != null) {
                user.setThemeConfig(themeConfig);
            }
            return Result.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.error();
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/user/set_user_avatar 设置用户头像
     * @apiName set_user_avatar
     * @apiGroup SYS_USER
     * @apiPermission 登陆用户
     * @apiVersion 1.0.0
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/set_user_avatar")
    @Ok("json")
    @POST
    @RequiresAuthentication
    public Object setUserAvatar(@Param("avatar") String avatar) {
        try {
            if ("superadmin".equals(StringUtil.getPlatformLoginname()) && Globals.MyConfig.getBoolean("AppDemoEnv")) {
                return Result.error("system.error.demo");
            }
            sysUserService.update(Chain.make("avatar", avatar), Cnd.where("id", "=", StringUtil.getPlatformUid()));
            Subject subject = SecurityUtils.getSubject();
            Sys_user user = (Sys_user) subject.getPrincipal();
            // 替换当前用户session里的值
            if (user != null) {
                user.setAvatar(avatar);
            }
            return Result.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.error();
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/user/change_user_info 修改当前用户信息
     * @apiName change_user_info
     * @apiGroup SYS_USER
     * @apiPermission 登陆用户
     * @apiVersion 1.0.0
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/change_user_info")
    @Ok("json")
    @RequiresAuthentication
    public Object doChangeInfo(@Param("username") String username, @Param("email") String email, @Param("mobile") String mobile, @Param("avatar") String avatar, HttpServletRequest req) {
        try {
            if ("superadmin".equals(StringUtil.getPlatformLoginname()) && Globals.MyConfig.getBoolean("AppDemoEnv")) {
                return Result.error("system.error.demo");
            }
            Subject subject = SecurityUtils.getSubject();
            Sys_user user = (Sys_user) subject.getPrincipal();
            if (user != null) {
                if (Strings.isNotBlank(email)) {
                    if (sysUserService.count(Cnd.where("email", "=", email).and("id", "<>", user.getId())) > 0) {
                        return Result.error().addMsg("sys.user.error.emailExisted");
                    }
                }
                if (Strings.isNotBlank(mobile)) {
                    if (sysUserService.count(Cnd.where("mobile", "=", mobile).and("id", "<>", user.getId())) > 0) {
                        return Result.error().addMsg("sys.user.error.mobileExisted");
                    }
                }
                sysUserService.update(Chain.make("username", username)
                                .add("avatar", avatar)
                                .add("email", email)
                                .add("mobile", mobile)
                        , Cnd.where("id", "=", user.getId()));
                user.setUsername(username);
                user.setAvatar(avatar);
                user.setEmail(email);
                user.setMobile(mobile);
                sysUserService.deleteCache(user.getId());
            }
            return Result.success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.error();
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/user/change_user_pwd 修改当前用户密码
     * @apiName change_user_pwd
     * @apiGroup SYS_USER
     * @apiPermission 登陆用户
     * @apiVersion 1.0.0
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/change_user_pwd")
    @Ok("json")
    @RequiresAuthentication
    public Object doChangePassword(@Param("oldPassword") String oldPassword, @Param("newPassword") String newPassword, HttpServletRequest req) {
        try {
            if ("superadmin".equals(StringUtil.getPlatformLoginname()) && Globals.MyConfig.getBoolean("AppDemoEnv")) {
                return Result.error("system.error.demo");
            }
            Subject subject = SecurityUtils.getSubject();
            Sys_user user = (Sys_user) subject.getPrincipal();
            String old = new Sha256Hash(oldPassword, user.getSalt(), 1024).toHex();
            if (old.equals(user.getPassword())) {
                String salt = R.UU32();
                String hashedPasswordBase64 = new Sha256Hash(newPassword, ByteSource.Util.bytes(salt), 1024).toHex();
                // 替换当前用户session里的值
                user.setSalt(salt);
                // 替换当前用户session里的值
                user.setPassword(hashedPasswordBase64);
                sysUserService.update(Chain.make("salt", salt)
                                .add("password", hashedPasswordBase64)
                        , Cnd.where("id", "=", user.getId()));
                sysUserService.deleteCache(user.getId());
                return Result.success();
            } else {
                return Result.error().addMsg("sys.user.change.pwd.oldPwdError");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.error();
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/user/info_by_id/:userId 查询用户信息
     * @apiName info_by_id
     * @apiGroup SYS_USER
     * @apiPermission 登陆用户
     * @apiVersion 1.0.0
     * @apiParam {String} userId   用户ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  用户数据
     */
    @At("/info_by_id/?")
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @GET
    @RequiresAuthentication
    public Object getInfoByUserId(String userId) {
        try {
            Sys_user sysUser = sysUserService.fetch(userId);
            if (sysUser == null) {
                return Result.error().addMsg("sys.user.not.exist");
            }
            return Result.success().addData(sysUser);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.error();
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/user/info_by_loginname/:loginname 查询用户信息
     * @apiName info_by_loginname
     * @apiGroup SYS_USER
     * @apiPermission 登陆用户
     * @apiVersion 1.0.0
     * @apiParam {String} loginname 登陆账号
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  用户数据
     */
    @At("/info_by_loginname/?")
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @GET
    @RequiresAuthentication
    public Object getInfoByLoginname(String loginname) {
        try {
            Sys_user sysUser = sysUserService.fetch(Cnd.where("loginname", "=", loginname));
            if (sysUser == null) {
                return Result.error().addMsg("sys.user.not.exist");
            }
            return Result.success().addData(sysUser);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.error();
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/user/list 用户分页查询
     * @apiName list
     * @apiGroup SYS_USER
     * @apiPermission sys.manage.user
     * @apiVersion 1.0.0
     * @apiParam {String} unitid   单位ID
     * @apiParam {String} loginname  用户名
     * @apiParam {String} username  姓名或昵称
     * @apiParam {String} mobile   手机号
     * @apiParam {String} email    Email
     * @apiParam {String} pageNo   页码
     * @apiParam {String} pageSize   页大小
     * @apiParam {String} pageOrderName   排序字段
     * @apiParam {String} pageOrderBy   排序方式
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  分页数据
     */
    @At
    @POST
    @Ok("json:full")
    @RequiresPermissions("sys.manage.user")
    public Object list(@Param("unitid") String unitid, @Param("loginname") String loginname, @Param("username") String username, @Param("mobile") String mobile, @Param("email") String email, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (shiroUtil.hasRole(PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME)) {
                if (Strings.isNotBlank(unitid) && !"root".equals(unitid)) {
                    cnd.and("unitid", "=", unitid);
                }
            } else {
                Sys_user user = (Sys_user) shiroUtil.getPrincipal();
                if (Strings.isNotBlank(unitid) && !"root".equals(unitid)) {
                    Sys_unit unit = sysUnitService.fetch(unitid);
                    if (unit == null || !unit.getPath().startsWith(user.getUnit().getPath())) {
                        //防止有人越级访问
                        return Result.error("system.error.invalid");
                    } else {
                        cnd.and("unitid", "=", unitid);
                    }
                } else {
                    cnd.and("unitid", "=", user.getUnitid());
                }
            }
            if (Strings.isNotBlank(username)) {
                cnd.and(Cnd.likeEX("username", username));
            }
            if (Strings.isNotBlank(loginname)) {
                cnd.and(Cnd.likeEX("loginname", loginname));
            }
            if (Strings.isNotBlank(email)) {
                cnd.and(Cnd.likeEX("email", email));
            }
            if (Strings.isNotBlank(mobile)) {
                cnd.and(Cnd.likeEX("mobile", mobile));
            }
            cnd.and("delFlag", "=", false);
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            Pagination pagination = sysUserService.listPageLinks(pageNo, pageSize, cnd, "^(unit|createdByUser|updatedByUser)$");
            for (Sys_user user : pagination.getList(Sys_user.class)) {
                user.setUserOnline(getUserOnlineStatus(user.getId()));
            }
            return Result.success().addData(pagination);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    private boolean getUserOnlineStatus(String userId) {
        try {
            Set<String> set = redisService.keys(RedisConstant.REDIS_KEY_LOGIN_ADMIN_SESSION + userId + ":*");
            return set.size() > 0;
        } catch (Exception e) {
            log.error(e);
        }
        return false;
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/user/get_unit_tree 获取单位树数据
     * @apiName get_unit_tree
     * @apiGroup SYS_USER
     * @apiPermission sys.manage.user
     * @apiVersion 1.0.0
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/get_unit_tree")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.user")
    public Object getUnitTree(HttpServletRequest req) {
        try {
            Cnd cnd = Cnd.NEW();
            if (!shiroUtil.hasRole(PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME)) {
                Sys_user user = (Sys_user) shiroUtil.getPrincipal();
                if (user != null) {
                    cnd.and("path", "like", user.getUnit().getPath() + "%");
                } else {
                    cnd.and("1", "<>", 1);
                }
            }
            cnd.asc("location").asc("path");
            List<Sys_unit> list = sysUnitService.query(cnd);
            NutMap nutMap = NutMap.NEW();
            for (Sys_unit unit : list) {
                List<Sys_unit> list1 = nutMap.getList(unit.getParentId(), Sys_unit.class);
                if (list1 == null) {
                    list1 = new ArrayList<>();
                }
                list1.add(unit);
                nutMap.put(Strings.sNull(unit.getParentId()), list1);
            }
            return Result.success().addData(getTree(nutMap, "", req));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    private List<NutMap> getTree(NutMap nutMap, String pid, HttpServletRequest req) {
        List<NutMap> treeList = new ArrayList<>();
        if (Strings.isBlank(pid)) {
            NutMap root = NutMap.NEW().addv("id", "root").addv("value", "root").addv("label", Mvcs.getMessage(req, "sys.manage.user.form.alluser")).addv("leaf", true);
            treeList.add(root);
        }
        List<Sys_unit> subList = nutMap.getList(pid, Sys_unit.class);
        for (Sys_unit unit : subList) {
            NutMap map = Lang.obj2nutmap(unit);
            map.put("label", unit.getName());
            if (unit.isHasChildren() || (nutMap.get(unit.getId()) != null)) {
                map.put("children", getTree(nutMap, unit.getId(), req));
            }
            treeList.add(map);
        }
        return treeList;
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/user/disabled 用户启用禁用
     * @apiName disabled
     * @apiGroup SYS_USER
     * @apiPermission sys.manage.user.update
     * @apiVersion 1.0.0
     * @apiParam {String} id   ID
     * @apiParam {String} disabled   true/false
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  数据
     */
    @At("/disabled")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.user.update")
    @SLog(tag = "启用禁用用户")
    public Object changeDisabled(@Param("id") String id, @Param("disabled") boolean disabled, HttpServletRequest req) {
        try {
            int userCount = sysUserService.count(Cnd.where("delFlag", "=", false).and("disabled", "=", false));
            if (userCount == 1) {
                Sys_user user = sysUserService.fetch(id);
                if (user != null && PlatformConstant.PLATFORM_DEFAULT_SUPERADMIN_NAME.equals(user.getLoginname()) && disabled) {
                    // 只有一个启用用户时,超级管理员不可禁用
                    return Result.error("sys.manage.user.superadmin.disable");
                }
            }
            sysUserService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
            if (disabled) {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.off"));
            } else {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.on"));
            }
            sysUserService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/user/create 新增用户
     * @apiName create
     * @apiGroup SYS_USER
     * @apiPermission sys.manage.user.create
     * @apiVersion 1.0.0
     * @apiParam {Object} user   用户表单
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  数据
     */
    @At
    @Ok("json")
    @RequiresPermissions("sys.manage.user.create")
    @SLog(tag = "新增用户")
    public Object create(@Param("..") Sys_user user, HttpServletRequest req) {
        try {
            if (Strings.isNotBlank(user.getLoginname())) {
                int num = sysUserService.count(Cnd.where("loginname", "=", Strings.trim(user.getLoginname())));
                if (num > 0) {
                    return Result.error("sys.manage.user.error.loginname");
                }
            }
            if (Strings.isNotBlank(user.getMobile())) {
                int num = sysUserService.count(Cnd.where("mobile", "=", Strings.trim(user.getMobile())));
                if (num > 0) {
                    return Result.error("sys.manage.user.error.mobile");
                }
            }
            if (Strings.isNotBlank(user.getEmail())) {
                int num = sysUserService.count(Cnd.where("email", "=", Strings.trim(user.getEmail())));
                if (num > 0) {
                    return Result.error("sys.manage.user.error.email");
                }
            }
            String salt = R.UU32();
            user.setSalt(salt);
            user.setPassword(new Sha256Hash(user.getPassword(), ByteSource.Util.bytes(salt), 1024).toHex());
            user.setLoginCount(0);
            user.setCreatedBy(StringUtil.getPlatformUid());
            user.setUpdatedBy(StringUtil.getPlatformUid());
            sysUserService.insert(user);
            sysUserService.clearCache();
            req.setAttribute("_slog_msg", String.format("%s(%s)", user.getLoginname(), user.getUsername()));
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/user/get_menu/:id 获取用户的权限
     * @apiName get_menu
     * @apiGroup SYS_USER
     * @apiPermission sys.manage.user
     * @apiVersion 1.0.0
     * @apiParam {String} id ID
     * @apiParam {String} pid 父节点ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/get_menu/?")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.user")
    public Object getMenu(String id, @Param("pid") String pid) {
        try {
            List<Sys_menu> list = sysUserService.getRoleMenus(id, pid);
            List<NutMap> treeList = new ArrayList<>();
            for (Sys_menu unit : list) {
                if (!unit.isHasChildren() && sysUserService.hasChildren(id, unit.getId())) {
                    unit.setHasChildren(true);
                }
                NutMap map = Lang.obj2nutmap(unit);
                map.addv("expanded", false);
                map.addv("children", new ArrayList<>());
                treeList.add(map);
            }
            return Result.success().addData(treeList);
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/user/reset_pwd/:id 重置用户密码
     * @apiName reset_pwd
     * @apiGroup SYS_USER
     * @apiPermission sys.manage.user.update
     * @apiVersion 1.0.0
     * @apiParam {String} id ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/reset_pwd/?")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.user.update")
    @SLog(tag = "重置用户密码")
    public Object resetPwd(String id, HttpServletRequest req) {
        try {
            Sys_user user = sysUserService.fetch(id);
            String salt = R.UU32();
            String pwd = R.captchaNumber(6);
            String hashedPasswordBase64 = new Sha256Hash(pwd, ByteSource.Util.bytes(salt), 1024).toHex();
            sysUserService.update(Chain.make("salt", salt)
                    .add("password", hashedPasswordBase64)
                    .add("updatedBy", StringUtil.getPlatformUid())
                    .add("updatedAt", Times.now().getTime()), Cnd.where("id", "=", id));
            sysUserService.deleteCache(user.getId());
            req.setAttribute("_slog_msg", String.format("%s(%s)", user.getLoginname(), user.getUsername()));
            return Result.success().addData(pwd);
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/user/get/:id 获取用户信息
     * @apiName get
     * @apiGroup SYS_USER
     * @apiPermission sys.manage.user
     * @apiVersion 1.0.0
     * @apiParam {String} id 用户ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/get/?")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.user")
    public Object getData(String id, HttpServletRequest req) {
        try {
            Sys_user user = sysUserService.fetch(id);
            if (user == null) {
                return Result.error("system.error.noData");
            }
            return Result.success().addData(user);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/user/update 修改用户信息
     * @apiName update
     * @apiGroup SYS_USER
     * @apiPermission sys.manage.user.update
     * @apiVersion 1.0.0
     * @apiParam {Object} user 用户表单
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.user.update")
    @SLog(tag = "修改用户")
    public Object update(@Param("..") Sys_user user, HttpServletRequest req) {
        try {
            user.setUpdatedBy(StringUtil.getPlatformUid());
            sysUserService.updateIgnoreNull(user);
            sysUserService.deleteCache(user.getId());
            req.setAttribute("_slog_msg", String.format("%s(%s)", user.getLoginname(), user.getUsername()));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/user/delete/:id 删除用户
     * @apiName delete
     * @apiGroup SYS_USER
     * @apiPermission sys.manage.user.delete
     * @apiVersion 1.0.0
     * @apiParam {String} id 用户ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/delete/?")
    @Ok("json")
    @DELETE
    @RequiresPermissions("sys.manage.user.delete")
    @SLog(tag = "删除用户")
    public Object delete(String userId, HttpServletRequest req) {
        try {
            Sys_user user = sysUserService.fetch(userId);
            if (user == null) {
                return Result.error("system.error.noData");
            }
            if (PlatformConstant.PLATFORM_DEFAULT_SUPERADMIN_NAME.equals(user.getLoginname())) {
                // 超级管理员不可删除
                return Result.error("sys.manage.user.superadmin.delete");
            }
            sysUserService.deleteById(userId);
            sysUserService.deleteCache(user.getId());
            req.setAttribute("_slog_msg", String.format("%s(%s)", user.getLoginname(), user.getUsername()));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/user/delete_more 批量删除用户
     * @apiName delete_more
     * @apiGroup SYS_USER
     * @apiPermission sys.manage.user.delete
     * @apiVersion 1.0.0
     * @apiParam {String} id 用户ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/delete_more")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.user.delete")
    @SLog(tag = "删除用户")
    public Object deleteMore(@Param("ids") String[] ids, @Param("names") String names, HttpServletRequest req) {
        try {
            if (ids == null) {
                return Result.error("system.error.invalid");
            }
            Sys_user user = sysUserService.fetch(Cnd.where("loginname", "=", PlatformConstant.PLATFORM_DEFAULT_SUPERADMIN_NAME));
            for (String s : ids) {
                if (s.equals(user.getId())) {
                    // 超级管理员不可删除,但有多个用户时可以禁用
                    return Result.error("sys.manage.user.superadmin.delete");
                }
            }
            int userCount = sysUserService.count(Cnd.where("delFlag", "=", false));
            if (userCount == ids.length) {
                // 用户不可以全部删除
                return Result.error("sys.manage.user.not.deleteAll");
            }
            sysUserService.deleteByIds(ids);
            sysUserService.clearCache();
            req.setAttribute("_slog_msg", names);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/user/export 导出用户数据
     * @apiName export
     * @apiGroup SYS_USER
     * @apiPermission sys.manage.user
     * @apiVersion 1.0.0
     * @apiParam {String} unitid   单位ID
     * @apiParam {String} loginname  用户名
     * @apiParam {String} username  姓名或昵称
     * @apiParam {String} mobile   手机号
     * @apiParam {String} email    Email
     * @apiParam {String} pageOrderName   排序字段
     * @apiParam {String} pageOrderBy   排序方式
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  多语言字符串
     */
    @At
    @Ok("void")
    @RequiresPermissions("sys.manage.user")
    public void export(@Param("unitid") String unitid, @Param("loginname") String loginname, @Param("username") String username, @Param("mobile") String mobile, @Param("email") String email, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy, HttpServletRequest req, HttpServletResponse response) {
        try {
            J4EConf j4eConf = J4EConf.from(Sys_user.class);
            j4eConf.setUse2007(true);
            List<J4EColumn> jcols = j4eConf.getColumns();
            for (J4EColumn j4eColumn : jcols) {
                if ("updatedBy".equals(j4eColumn.getFieldName()) || "updatedAt".equals(j4eColumn.getFieldName()) || "createdBy".equals(j4eColumn.getFieldName()) || "createdAt".equals(j4eColumn.getFieldName()) || "delFlag".equals(j4eColumn.getFieldName())) {
                    j4eColumn.setIgnore(true);
                }
            }
            Cnd cnd = Cnd.NEW();
            if (shiroUtil.hasRole(PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME)) {
                if (Strings.isNotBlank(unitid) && !"root".equals(unitid)) {
                    cnd.and("unitid", "=", unitid);
                }
            } else {
                Sys_user user = (Sys_user) shiroUtil.getPrincipal();
                if (Strings.isNotBlank(unitid) && !"root".equals(unitid)) {
                    Sys_unit unit = sysUnitService.fetch(unitid);
                    if (unit == null || !unit.getPath().startsWith(user.getUnit().getPath())) {
                        //防止有人越级访问
                        throw Lang.makeThrow(Mvcs.getMessage(req, "system.error.invalid"));
                    } else {
                        cnd.and("unitid", "=", unitid);
                    }
                } else {
                    cnd.and("unitid", "=", user.getUnitid());
                }
            }
            if (Strings.isNotBlank(username)) {
                cnd.and(Cnd.likeEX("username", username));
            }
            if (Strings.isNotBlank(loginname)) {
                cnd.and(Cnd.likeEX("loginname", loginname));
            }
            if (Strings.isNotBlank(email)) {
                cnd.and(Cnd.likeEX("email", email));
            }
            if (Strings.isNotBlank(mobile)) {
                cnd.and(Cnd.likeEX("mobile", mobile));
            }
            cnd.and("delFlag", "=", false);
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            OutputStream out = response.getOutputStream();
            response.setHeader("content-type", "application/shlnd.ms-excel;charset=utf-8");
            response.setHeader("content-disposition", "attachment; filename=" + new String("sys_user".getBytes(), "ISO-8859-1") + ".xlsx");
            J4E.toExcel(out, sysUserService.query(cnd), j4eConf);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
