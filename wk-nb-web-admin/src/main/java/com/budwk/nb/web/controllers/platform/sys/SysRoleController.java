package com.budwk.nb.web.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.constants.PlatformConstant;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.sys.models.Sys_menu;
import com.budwk.nb.sys.models.Sys_role;
import com.budwk.nb.sys.models.Sys_unit;
import com.budwk.nb.sys.models.Sys_user;
import com.budwk.nb.sys.services.SysMenuService;
import com.budwk.nb.sys.services.SysRoleService;
import com.budwk.nb.sys.services.SysUnitService;
import com.budwk.nb.sys.services.SysUserService;
import com.budwk.nb.web.commons.utils.ShiroUtil;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * @author wizzer(wizzer @ qq.com) on 2019/11/27
 */
@IocBean
@At("/api/{version}/platform/sys/role")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "系统_角色管理")}, servers = @Server(url = "/"))
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

    @At
    @POST
    @Ok("json:full")
    @RequiresPermissions("sys.manage.role")
    @Operation(
            tags = "系统_角色管理", summary = "分页查询角色",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.role")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "name", example = "", description = "角色名称"),
                    @ApiFormParam(name = "code", example = "", description = "角色代码"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
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

    @At("/get_all_menu")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.role")
    @Operation(
            tags = "系统_角色管理", summary = "获取所有权限菜单",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.role")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
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

    @At("/get_unit_tree")
    @Ok("json")
    @GET
    @RequiresAuthentication
    @Operation(
            tags = "系统_角色管理", summary = "获取单位树",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "pid", description = "父级ID", required = false)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
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

    @At("/create")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.role.create")
    @SLog(tag = "添加角色", msg = "角色名称:${role.name}")
    @Operation(
            tags = "系统_角色管理", summary = "新增角色",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.role.create")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "menuIds", description = "权限菜单ID数组", required = false)
            },
            implementation = Sys_role.class
    )
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

    @At("/disabled")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.role.update")
    @SLog(tag = "启用禁用角色")
    @Operation(
            tags = "系统_角色管理", summary = "启用禁用角色",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.role.update")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "id", description = "主键", required = true),
                    @ApiFormParam(name = "code", description = "角色代码", required = true),
                    @ApiFormParam(name = "disabled", description = "启用禁用", required = true, example = "true", type = "boolean")
            }
    )
    public Object changeDisabled(@Param("id") String id, @Param("code") String code, @Param("disabled") boolean disabled, HttpServletRequest req) {
        try {
            if (PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME.equals(code)) {
                return Result.error("system.error.disabled.notAllow");
            }
            int res = sysRoleService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
            if (res > 0) {
                if (disabled) {
                    req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.off"));
                } else {
                    req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.on"));
                }
                sysUserService.clearCache();
                sysRoleService.clearCache();
                return Result.success();
            }
            return Result.error(500512, "system.fail");
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }


    @At("/delete/{roleId}")
    @Ok("json")
    @DELETE
    @RequiresPermissions("sys.manage.role.delete")
    @SLog(tag = "删除角色", msg = "角色名称:${args[1].getAttribute('name')}")
    @Operation(
            tags = "系统_角色管理", summary = "删除角色",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.role.delete")
            },
            parameters = {
                    @Parameter(name = "roleId", description = "角色ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
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


    @At("/get_menu/{id}")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.role")
    @Operation(
            tags = "系统_角色管理", summary = "获取角色拥有的权限菜单",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.role")
            },
            parameters = {
                    @Parameter(name = "id", description = "角色ID", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "pid", description = "菜单ID", in = ParameterIn.QUERY)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
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

    @At("/get_do_menu/{roleId}")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.role")
    @Operation(
            tags = "系统_角色管理", summary = "获取角色待分配权限菜单",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.role")
            },
            parameters = {
                    @Parameter(name = "roleId", description = "角色ID", in = ParameterIn.PATH, required = true)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
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


    @At("/do_menu")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.role.menu")
    @SLog(tag = "分配角色权限")
    @Operation(
            tags = "系统_角色管理", summary = "分配角色权限",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.role.menu")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "menuIds", description = "权限菜单ID数组"),
                    @ApiFormParam(name = "roleId", description = "角色ID", required = true)
            }
    )
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


    @At("/user_search")
    @Ok("json:full")
    @POST
    @RequiresPermissions("sys.manage.role")
    @Operation(
            tags = "系统_角色管理", summary = "查询角色下用户列表",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.role")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "query", description = "用户名或姓名"),
                    @ApiFormParam(name = "roleId", description = "角色ID", required = true)
            }
    )
    public Object userSearch(@Param("query") String keyword, @Param("roleId") String roleId) {
        try {
            Sys_user user = (Sys_user) shiroUtil.getPrincipal();
            return Result.success().addData(sysRoleService.userSearch(roleId, keyword, shiroUtil.hasRole(PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME), user.getUnit()));
        } catch (Exception e) {
            return Result.error();
        }
    }


    @At("/user_list")
    @Ok("json:full")
    @POST
    @RequiresPermissions("sys.manage.role")
    @Operation(
            tags = "系统_角色管理", summary = "分页查询角色待分配用户",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.role")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "roleId", example = "", description = "角色ID"),
                    @ApiFormParam(name = "searchName", example = "", description = "查询字段名"),
                    @ApiFormParam(name = "searchKeyword", example = "", description = "查询字段值"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
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

    @At("/user_add")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.role.user")
    @SLog(tag = "关联用户到角色")
    @Operation(
            tags = "系统_角色管理", summary = "关联用户到角色",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.role.user")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "users", example = "", description = "用户ID数组"),
                    @ApiFormParam(name = "loginnames", example = "", description = "用户名数组"),
                    @ApiFormParam(name = "roleId", example = "", description = "角色ID", required = true)
            }
    )
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

    @At("/user_remove")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.role.user")
    @SLog(tag = "从角色移除用户")
    @Operation(
            tags = "系统_角色管理", summary = "从角色移除用户",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.role.user")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "users", example = "", description = "用户ID数组"),
                    @ApiFormParam(name = "loginnames", example = "", description = "用户名数组"),
                    @ApiFormParam(name = "roleId", example = "", description = "角色ID", required = true)
            }
    )
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

    @At("/get/{id}")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.role")
    @Operation(
            tags = "系统_角色管理", summary = "查询角色信息",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.role")
            },
            parameters = {
                    @Parameter(name = "id", description = "角色ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
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


    @At
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.role.update")
    @SLog(tag = "修改角色", msg = "角色名称:${role.name}")
    @Operation(
            tags = "系统_角色管理", summary = "修改角色信息",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.role.update")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Sys_role.class
    )
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
