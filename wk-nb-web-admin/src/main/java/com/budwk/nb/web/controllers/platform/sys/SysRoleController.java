package com.budwk.nb.web.controllers.platform.sys;

import com.budwk.nb.commons.constants.PlatformConstant;
import com.budwk.nb.sys.models.Sys_menu;
import com.budwk.nb.sys.models.Sys_role;
import com.budwk.nb.sys.models.Sys_unit;
import com.budwk.nb.sys.models.Sys_user;
import com.budwk.nb.sys.services.SysMenuService;
import com.budwk.nb.sys.services.SysRoleService;
import com.budwk.nb.sys.services.SysUnitService;
import com.budwk.nb.sys.services.SysUserService;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.commons.base.Result;
import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.web.commons.utils.ShiroUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer(wizzer@qq.com) on 2019/11/27
 */
@IocBean
@At("/api/{version}/platform/sys/role")
@Ok("json")
@ApiVersion("1.0.0")
public class SysRoleController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysUserService sysUserService;
    @Inject
    @Reference
    private SysMenuService sysMenuService;
    @Inject
    @Reference
    private SysUnitService sysUnitService;
    @Inject
    @Reference
    private SysRoleService sysRoleService;
    @Inject
    private ShiroUtil shiroUtil;

    /**
     * @api {post} /api/1.0.0/platform/sys/role/list 角色分页查询
     * @apiName list
     * @apiGroup SYS_ROLE
     * @apiPermission sys.manage.role
     * @apiVersion 1.0.0
     * @apiParam {String} name   角色名称
     * @apiParam {String} pageNo   页码
     * @apiParam {String} pageSize   页大小
     * @apiParam {String} pageOrderName   排序字段
     * @apiParam {String} pageOrderBy   排序方式
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  多语言字符串
     */
    @At
    @POST
    @Ok("json:full")
    @RequiresPermissions("sys.manage.role")
    public Object list(@Param("name") String name, @Param("code") String code, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (Strings.isNotBlank(name)) {
                cnd.and(Cnd.likeEX("name", name));
            }
            if (Strings.isNotBlank(code)) {
                cnd.and(Cnd.likeEX("code", code));
            }
            if (!shiroUtil.hasRole(PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME)) {
                cnd.and("unitid", "=", StringUtil.getPlatformUserUnitid());
            }
            cnd.and("delFlag", "=", false);
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(sysRoleService.listPageLinks(pageNo, pageSize, cnd, "^(createdByUser|unit)$"));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/role/get_all_menu 获取权限菜单
     * @apiName get_all_menu
     * @apiGroup SYS_ROLE
     * @apiPermission sys.manage.role
     * @apiVersion 1.0.0
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  树形数据
     */
    @At("/get_all_menu")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.role")
    public Object getAllMenu(HttpServletRequest req) {
        try {
            List<Sys_menu> list;
            if (shiroUtil.hasRole(PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME)) {
                list = sysMenuService.query(Cnd.orderBy().asc("location").asc("path"));
            } else {
                list = sysUserService.getMenusAndButtons(StringUtil.getPlatformUid());
            }
            NutMap menuMap = NutMap.NEW();
            for (Sys_menu unit : list) {
                List<Sys_menu> list1 = menuMap.getList(unit.getParentId(), Sys_menu.class);
                if (list1 == null) {
                    list1 = new ArrayList<>();
                }
                list1.add(unit);
                menuMap.put(unit.getParentId(), list1);
            }
            return Result.success().addData(getTree(menuMap, ""));
        } catch (Exception e) {
            return Result.error();
        }
    }

    private List<NutMap> getTree(NutMap menuMap, String pid) {
        List<NutMap> treeList = new ArrayList<>();
        List<Sys_menu> subList = menuMap.getList(pid, Sys_menu.class);
        for (Sys_menu menu : subList) {
            NutMap map = Lang.obj2nutmap(menu);
            map.put("label", menu.getName());
            if (menu.isHasChildren() || (menuMap.get(menu.getId()) != null)) {
                map.put("children", getTree(menuMap, menu.getId()));
            }
            treeList.add(map);
        }
        return treeList;
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/role/get_unit_tree 获取单位树数据
     * @apiName get_unit_tree
     * @apiGroup SYS_ROLE
     * @apiPermission 登陆用户
     * @apiVersion 1.0.0
     * @apiParam {String} pid   父级ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  数据
     */
    @At("/get_unit_tree")
    @Ok("json")
    @GET
    @RequiresAuthentication
    public Object getUnitTree(@Param("pid") String pid, HttpServletRequest req) {
        try {
            List<Sys_unit> list = new ArrayList<>();
            List<NutMap> treeList = new ArrayList<>();
            if (Strings.isBlank(pid) && shiroUtil.hasRole(PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME)) {
                NutMap sys = NutMap.NEW().addv("value", "system").addv("label", Mvcs.getMessage(req, "sys.manage.role.form.systemrole")).addv("leaf", true);
                treeList.add(sys);
            }
            if (shiroUtil.hasRole(PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME)) {
                Cnd cnd = Cnd.NEW();
                if (Strings.isBlank(pid)) {
                    cnd.and("parentId", "=", "").or("parentId", "is", null);
                } else {
                    cnd.and("parentId", "=", pid);
                }
                cnd.asc("location").asc("path");
                list = sysUnitService.query(cnd);
            } else {
                Sys_user user = (Sys_user) shiroUtil.getPrincipal();
                if (user != null) {
                    if (Strings.isBlank(pid)) {
                        list = sysUnitService.query(Cnd.where("id", "=", user.getUnitid()).asc("path"));
                    } else {
                        Cnd cnd = Cnd.NEW();
                        cnd.and("parentId", "=", pid);
                        cnd.asc("location").asc("path");
                        list = sysUnitService.query(cnd);
                    }
                }
            }
            for (Sys_unit unit : list) {
                NutMap map = NutMap.NEW().addv("value", unit.getId()).addv("label", unit.getName());
                if (unit.isHasChildren()) {
                    map.addv("children", new ArrayList<>());
                    map.addv("leaf", false);
                } else {
                    map.addv("leaf", true);
                }
                treeList.add(map);
            }
            return Result.success().addData(treeList);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/role/create 新增角色
     * @apiName create
     * @apiGroup SYS_ROLE
     * @apiPermission sys.manage.role.create
     * @apiVersion 1.0.0
     * @apiParam {String} menuIds   权限菜单ID
     * @apiParam {Object} role   角色对象表单
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  数据
     */
    @At("/create")
    @Ok("json")
    @RequiresPermissions("sys.manage.role.create")
    @SLog(tag = "添加角色", msg = "角色名称:${role.name}")
    public Object addDo(@Param("menuIds") String menuIds, @Param("..") Sys_role role, HttpServletRequest req) {
        try {
            int num = sysRoleService.count(Cnd.where("code", "=", role.getCode().trim()));
            if (num > 0) {
                return Result.error("sys.manage.role.form.code.hasExist");
            }
            String[] ids = StringUtils.split(menuIds, ",");
            if ("system".equals(role.getUnitid())) {
                role.setUnitid("");
            }
            role.setCreatedBy(StringUtil.getPlatformUid());
            role.setUpdatedBy(StringUtil.getPlatformUid());
            Sys_role r = sysRoleService.insert(role);
            if (r != null) {
                // 清除缓存操作在方法内部实现
                sysRoleService.saveMenu(ids, r.getId());
            }
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/role/disabled 启用禁用角色
     * @apiName disabled
     * @apiGroup SYS_ROLE
     * @apiPermission sys.manage.role.update
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
    @RequiresPermissions("sys.manage.role.update")
    @SLog(tag = "启用禁用角色")
    public Object changeDisabled(@Param("id") String id, @Param("code") String code, @Param("disabled") boolean disabled, HttpServletRequest req) {
        try {
            if (PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME.equals(code)) {
                return Result.error("system.error.disabled.notAllow");
            }
            sysRoleService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
            if (disabled) {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.on"));
            } else {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.off"));
            }
            sysUserService.clearCache();
            sysRoleService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {delete} /api/1.0.0/platform/sys/role/delete/:id 删除角色
     * @apiName delete
     * @apiGroup SYS_ROLE
     * @apiPermission sys.manage.role.delete
     * @apiVersion 1.0.0
     * @apiParam {String} id 角色ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/delete/?")
    @Ok("json")
    @DELETE
    @RequiresPermissions("sys.manage.role.delete")
    @SLog(tag = "删除角色", msg = "角色名称:${args[1].getAttribute('name')}")
    public Object delete(String roleId, HttpServletRequest req) {
        try {
            Sys_role role = sysRoleService.fetch(roleId);
            if (role == null) {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.error.noData"));
                return Result.error("system.error.noData");
            }
            if (PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME.equals(role.getCode()) || PlatformConstant.PLATFORM_ROLE_PUBLIC_NAME.equals(role.getCode())) {
                return Result.error("system.error.delete.notAllow");
            }
            sysRoleService.del(roleId);
            sysRoleService.clearCache();
            sysUserService.clearCache();
            req.setAttribute("name", role.getName());
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/role/get_menu/:id 获取角色的权限
     * @apiName get_menu
     * @apiGroup SYS_ROLE
     * @apiPermission sys.manage.role
     * @apiVersion 1.0.0
     * @apiParam {String} id 角色ID
     * @apiParam {String} pid 父节点ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/get_menu/?")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.role")
    public Object getMenu(String id, @Param("pid") String pid) {
        try {
            List<Sys_menu> list = sysRoleService.getRoleMenus(id, pid);
            List<NutMap> treeList = new ArrayList<>();
            for (Sys_menu unit : list) {
                if (!unit.isHasChildren() && sysRoleService.hasChildren(id, unit.getId())) {
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
     * @api {get} /api/1.0.0/platform/sys/role/get_do_menu/:id 获取可分配的菜单
     * @apiName get_do_menu
     * @apiGroup SYS_ROLE
     * @apiPermission sys.manage.role
     * @apiVersion 1.0.0
     * @apiParam {String} id 角色ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/get_do_menu/?")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.role")
    public Object getDoMenu(String roleId, HttpServletRequest req) {
        try {
            // 角色已拥有的权限
            List<Sys_menu> hasList = sysRoleService.getMenusAndButtons(roleId);
            List<Sys_menu> list;
            if (shiroUtil.hasRole(PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME)) {
                // 超级管理员加载所有权限
                list = sysMenuService.query(Cnd.orderBy().asc("location").asc("path"));
            } else {
                // 非超级管理员角色加载用户自己拥有的权限
                list = sysUserService.getMenusAndButtons(StringUtil.getPlatformUid());
            }
            NutMap menuMap = NutMap.NEW();
            for (Sys_menu unit : list) {
                List<Sys_menu> list1 = menuMap.getList(unit.getParentId(), Sys_menu.class);
                if (list1 == null) {
                    list1 = new ArrayList<>();
                }
                list1.add(unit);
                menuMap.put(unit.getParentId(), list1);
            }
            List<String> cmenu = new ArrayList<>();
            for (Sys_menu menu : hasList) {
                cmenu.add(menu.getId());
            }
            return Result.success().addData(NutMap.NEW().addv("menu", getTree(menuMap, "")).addv("cmenu", cmenu));
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/role/do_menu 为角色分配权限
     * @apiName do_menu
     * @apiGroup SYS_ROLE
     * @apiPermission sys.manage.role.menu
     * @apiVersion 1.0.0
     * @apiParam {String} menuIds 权限字符串数组
     * @apiParam {String} roleId 角色ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/do_menu")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.role.menu")
    @SLog(tag = "分配角色权限")
    public Object doMenu(@Param("menuIds") String menuIds, @Param("roleId") String roleId, HttpServletRequest req) {
        try {
            Sys_role role = sysRoleService.fetch(roleId);
            if (role == null) {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.error.noData"));
                return Result.error("system.error.noData");
            }
            String[] ids = StringUtils.split(menuIds, ",");
            sysRoleService.saveMenu(ids, roleId);
            req.setAttribute("_slog_msg", role.getName());
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/role/user_search 查询可分配的用户
     * @apiName user_search
     * @apiGroup SYS_ROLE
     * @apiPermission sys.manage.role
     * @apiVersion 1.0.0
     * @apiParam {String} query 查询关键词
     * @apiParam {String} roleId 角色ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/user_search")
    @Ok("json:full")
    @POST
    @RequiresPermissions("sys.manage.role")
    public Object userSearch(@Param("query") String keyword, @Param("roleId") String roleId) {
        try {
            Sys_user user = (Sys_user) shiroUtil.getPrincipal();
            return Result.success().addData(sysRoleService.userSearch(roleId, keyword, shiroUtil.hasRole(PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME), user.getUnit()));
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/role/user_list 查询已分配的用户
     * @apiName user_list
     * @apiGroup SYS_ROLE
     * @apiPermission sys.manage.role
     * @apiVersion 1.0.0
     * @apiParam {String} searchName 查询字段
     * @apiParam {String} searchKeyword 查询关键词
     * @apiParam {String} roleId 角色ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/user_list")
    @Ok("json:full")
    @RequiresPermissions("sys.manage.role")
    public Object userList(@Param("roleId") String roleId, @Param("searchName") String searchName, @Param("searchKeyword") String searchKeyword,
                           @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Sql sql = Sqls.create("SELECT a.*,c.name as unitname FROM sys_user a,sys_user_role b,sys_unit c WHERE a.unitid=c.id and a.id=b.userId and b.roleId=@roleId $s $o");
            sql.params().set("roleId", roleId);
            if (Strings.isNotBlank(searchName) && Strings.isNotBlank(searchKeyword)) {
                sql.vars().set("s", " and a." + searchName + " like '%" + searchKeyword + "%'");
            }
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                sql.vars().set("o", " order by a." + pageOrderName + " " + PageUtil.getOrder(pageOrderBy));

            }
            return Result.success().addData(sysUserService.listPage(pageNo, pageSize, sql));
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/role/user_add 关联用户到角色
     * @apiName user_add
     * @apiGroup SYS_ROLE
     * @apiPermission sys.manage.role.user
     * @apiVersion 1.0.0
     * @apiParam {String} users 用户ID数组
     * @apiParam {String} loginnames 用户名数组
     * @apiParam {String} roleId 角色ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/user_add")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.role.user")
    @SLog(tag = "关联用户到角色")
    public Object userAdd(@Param("users") String users, @Param("loginnames") String loginnames, @Param("roleId") String roleId, HttpServletRequest req) {
        try {
            String[] ids = StringUtils.split(users, ",");
            Sys_role role = sysRoleService.fetch(roleId);
            if (role == null) {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.error.noData"));
                return Result.error("system.error.noData");
            }
            for (String s : ids) {
                sysRoleService.insert("sys_user_role", Chain.make("roleId", roleId).add("userId", s));
            }
            sysRoleService.clearCache();
            sysUserService.clearCache();
            req.setAttribute("_slog_msg", String.format("角色名称:%s,关联用户:%s", role.getName(), loginnames));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/role/user_remove 从角色移除用户
     * @apiName user_remove
     * @apiGroup SYS_ROLE
     * @apiPermission sys.manage.role.user
     * @apiVersion 1.0.0
     * @apiParam {String} users 用户ID数组
     * @apiParam {String} loginnames 用户名数组
     * @apiParam {String} roleId 角色ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/user_remove")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.role.user")
    @SLog(tag = "从角色移除用户")
    public Object userRemove(@Param("users") String users, @Param("loginnames") String loginnames, @Param("roleId") String roleId, HttpServletRequest req) {
        try {
            Sys_role role = sysRoleService.fetch(roleId);
            if (role == null) {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.error.noData"));
                return Result.error("system.error.noData");
            }
            String superadminId = sysUserService.fetch(Cnd.where("loginname", "=", PlatformConstant.PLATFORM_DEFAULT_SUPERADMIN_NAME)).getId();
            String sysadminRoleid = sysRoleService.fetch(Cnd.where("code", "=", PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME)).getId();
            String[] ids = StringUtils.split(users, ",");
            if (Lang.contains(ids, superadminId) && roleId.equals(sysadminRoleid)) {
                return Result.error("sys.manage.role.do.delsuperadmin");
            }
            sysRoleService.clear("sys_user_role", Cnd.where("userId", "in", ids).and("roleId", "=", roleId));
            sysRoleService.clearCache();
            sysUserService.clearCache();
            req.setAttribute("_slog_msg", String.format("角色名称:%s,移除用户:%s", role.getName(), loginnames));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/role/get/:id 获取信息
     * @apiName get
     * @apiGroup SYS_ROLE
     * @apiPermission sys.manage.role
     * @apiVersion 1.0.0
     * @apiParam {String} id ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/get/?")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.role")
    public Object getData(String id, HttpServletRequest req) {
        try {
            Sys_role role = sysRoleService.fetch(id);
            if (role == null) {
                return Result.error("system.error.noData");
            }
            return Result.success().addData(role);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/role/update 修改信息
     * @apiName update
     * @apiGroup SYS_ROLE
     * @apiPermission sys.manage.role.update
     * @apiVersion 1.0.0
     * @apiParam {Object} role 表单对象
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.role.update")
    @SLog(tag = "修改角色", msg = "角色名称:${role.name}")
    public Object update(@Param("..") Sys_role role, HttpServletRequest req) {
        try {
            Sys_role oldRole = sysRoleService.fetch(role.getId());
            if (oldRole != null && !Strings.sBlank(oldRole.getCode()).equalsIgnoreCase(role.getCode())) {
                int num = sysRoleService.count(Cnd.where("code", "=", role.getCode().trim()));
                if (num > 0) {
                    return Result.error("sys.manage.role.form.code.hasExist");
                }
            }
            if ("system".equals(role.getUnitid())) {
                role.setUnitid("");
            }
            role.setUpdatedBy(StringUtil.getPlatformUid());
            sysRoleService.updateIgnoreNull(role);
            sysRoleService.clearCache();
            sysUserService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }
}
