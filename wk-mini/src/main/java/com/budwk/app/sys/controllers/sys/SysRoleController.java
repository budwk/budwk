package com.budwk.app.sys.controllers.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.budwk.app.sys.enums.SysUnitType;
import com.budwk.app.sys.models.Sys_group;
import com.budwk.app.sys.models.Sys_menu;
import com.budwk.app.sys.models.Sys_role;
import com.budwk.app.sys.models.Sys_unit;
import com.budwk.app.sys.services.*;
import com.budwk.starter.common.constant.GlobalConstant;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean
@At("/platform/sys/role")
@SLog(tag = "角色管理")
@ApiDefinition(tag = "角色管理")
@Slf4j
public class SysRoleController {
    @Inject
    private SysRoleService sysRoleService;
    @Inject
    private SysUserService sysUserService;
    @Inject
    private SysUnitService sysUnitService;
    @Inject
    private SysGroupService sysGroupService;
    @Inject
    private SysAppService sysAppService;
    @Inject
    private SysPostService sysPostService;
    @Inject
    private SysMenuService sysMenuService;

    @At("/unit")
    @Ok("json")
    @GET
    @SaCheckPermission("sys.manage.role")
    @ApiOperation(name = "获取公司单位数据")
    @ApiImplicitParams
    @ApiResponses
    public Result<?> unit(HttpServletRequest req) {
        List<Sys_unit> list = new ArrayList<>();
        if (StpUtil.hasRole(GlobalConstant.DEFAULT_SYSADMIN_ROLECODE)) {
            Cnd cnd = Cnd.NEW();
            cnd.and(Cnd.exps("type", "=", SysUnitType.GROUP.value()).or("type", "=", SysUnitType.COMPANY.value()));
            cnd.asc("path");
            list = sysUnitService.query(cnd);
        } else {
            //如果非超级管理员,则查询所在单位的所属分公司/总公司
            String unitPath = sysUnitService.getMasterCompanyPath(SecurityUtil.getUnitId());
            Cnd cnd = Cnd.NEW();
            cnd.and("path", "like", unitPath + "%");
            cnd.and(Cnd.exps("type", "=", SysUnitType.GROUP.value()).or("type", "=", SysUnitType.COMPANY.value()));
            cnd.asc("path");
            list = sysUnitService.query(cnd);
        }
        return Result.data(list);
    }

    @At("/group")
    @Ok("json")
    @GET
    @SaCheckPermission("sys.manage.role")
    @ApiOperation(name = "获取角色组")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "unitId", description = "单位ID", required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> group(@Param("unitId") String unitId, HttpServletRequest req) {
        return Result.data(sysGroupService.query(Cnd.where("unitId", "=", unitId).asc("createdAt"), "roles"));
    }

    @At("/post")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取职务列表")
    @ApiImplicitParams
    @ApiResponses
    public Result<?> post(HttpServletRequest req) {
        return Result.data(sysPostService.query());
    }

    @At("/app")
    @Ok("json")
    @GET
    @SaCheckPermission("sys.manage.role")
    @ApiOperation(name = "获取APP列表")
    @ApiImplicitParams
    @ApiResponses
    public Result<?> app(HttpServletRequest req) {
        //需要带icon图片所以不用listAll
        if (StpUtil.hasRole(GlobalConstant.DEFAULT_SYSADMIN_ROLECODE)) {
            return Result.data(sysAppService.query(Cnd.NEW().asc("location")));
        } else {
            return Result.data(sysRoleService.getAppList(SecurityUtil.getUserId()));
        }
    }

    @At("/create")
    @Ok("json")
    @POST
    @SaCheckPermission("sys.manage.role.create")
    @SLog(value = "新建${type=='role'?'角色':'角色组'}:${name}")
    @ApiOperation(name = "新建角色/角色组")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "type", description = "分类", example = "role=角色/group=角色组", required = true, check = true),
                    @ApiFormParam(name = "name", description = "角色名称/角色组名",required = true,check = true),
                    @ApiFormParam(name = "code", description = "角色代码"),
                    @ApiFormParam(name = "disabled", description = "启用状态"),
                    @ApiFormParam(name = "groupId", description = "所属角色组ID"),
                    @ApiFormParam(name = "unitId", description = "所属单位ID", required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> create(@Param("type") String type, @Param("name") String name,
                            @Param("code") String code,
                            @Param("disabled") boolean disabled,
                            @Param("groupId") String groupId,
                            @Param("unitId") String unitId,
                            HttpServletRequest req) {
        if ("role".equals(type)) {
            int num = sysRoleService.count(Cnd.where("code", "=", code.trim()));
            if (num > 0) {
                return Result.error("角色代码已存在");
            }
            Sys_role role = new Sys_role();
            role.setGroupId(groupId);
            role.setUnitId(unitId);
            role.setCode(code);
            role.setDisabled(disabled);
            role.setCreatedBy(SecurityUtil.getUserId());
            role.setName(name);
            sysRoleService.insert(role);
        }
        if ("group".equals(type)) {
            Sys_group group = new Sys_group();
            group.setUnitId(unitId);
            group.setCreatedBy(SecurityUtil.getUserId());
            group.setName(name);
            group.setUnitPath(sysUnitService.fetch(unitId).getPath());
            sysGroupService.insert(group);
        }
        return Result.success();
    }

    @At("/update")
    @Ok("json")
    @POST
    @SaCheckPermission("sys.manage.role.update")
    @SLog(value = "修改${type=='role'?'角色':'角色组'}:${name}")
    @ApiOperation(name = "修改角色/角色组")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "type", description = "分类", example = "role=角色/group=角色组", required = true, check = true),
                    @ApiFormParam(name = "name", description = "角色名称/角色组名"),
                    @ApiFormParam(name = "code", description = "角色代码"),
                    @ApiFormParam(name = "disabled", description = "启用状态"),
                    @ApiFormParam(name = "groupId", description = "所属角色组ID"),
                    @ApiFormParam(name = "unitId", description = "所属单位ID"),
                    @ApiFormParam(name = "id", description = "主键ID", required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> update(@Param("type") String type, @Param("name") String name,
                            @Param("code") String code,
                            @Param("disabled") boolean disabled,
                            @Param("groupId") String groupId,
                            @Param("unitId") String unitId,
                            @Param("id") String id,
                            HttpServletRequest req) {
        if ("role".equals(type)) {
            int num = sysRoleService.count(Cnd.where("code", "=", code.trim()).and("id", "<>", id));
            if (num > 0) {
                return Result.error("角色代码已存在");
            }
            Sys_role role = new Sys_role();
            role.setGroupId(groupId);
            role.setUnitId(unitId);
            role.setCode(code);
            role.setDisabled(disabled);
            role.setCreatedBy(SecurityUtil.getUserId());
            role.setName(name);
            role.setId(id);
            sysRoleService.updateIgnoreNull(role);
            //修改角色code及状态,需要清除用户权限缓存
            sysUserService.cacheClear();
        }
        if ("group".equals(type)) {
            Sys_group group = new Sys_group();
            group.setUnitId(unitId);
            group.setCreatedBy(SecurityUtil.getUserId());
            group.setName(name);
            group.setId(id);
            group.setUnitPath(sysUnitService.fetch(unitId).getPath());
            sysGroupService.updateIgnoreNull(group);
        }
        return Result.success();
    }

    @At("/delete")
    @Ok("json")
    @POST
    @SaCheckPermission("sys.manage.role.delete")
    @SLog(value = "删除${type=='role'?'角色':'角色组'}:${name}")
    @ApiOperation(name = "删除角色/角色组")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "type", description = "分类", example = "role=角色/group=角色组", required = true, check = true),
                    @ApiFormParam(name = "name", description = "角色名称/角色组名"),
                    @ApiFormParam(name = "id", description = "主键ID", required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> delete(@Param("type") String type, @Param("name") String name,
                            @Param("id") String id,
                            HttpServletRequest req) {
        if ("role".equals(type)) {
            Sys_role role = sysRoleService.fetch(id);
            if (role == null) {
                return Result.error(ResultCode.NULL_DATA_ERROR);
            }
            if (GlobalConstant.DEFAULT_SYSADMIN_ROLECODE.equals(role.getCode())) {
                return Result.error("超级管理员角色 不可被删除");
            }
            if ("public".equals(role.getCode())) {
                return Result.error("公共角色 不可被删除");
            }
            sysRoleService.clearRole(id);
        }
        if ("group".equals(type)) {
            Sys_group group = sysGroupService.fetch(id);
            if (group == null) {
                return Result.error(ResultCode.NULL_DATA_ERROR);
            }
            if ("SYSTEM".equals(group.getId())) {
                return Result.error("系统管理组 不可被删除");
            }
            sysGroupService.clearGroup(id);
        }
        return Result.success();
    }

    @At("/user")
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @POST
    @ApiOperation(name = "获取角色用户列表")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "roleId", example = "", description = "角色ID"),
                    @ApiFormParam(name = "username", example = "", description = "参数Key"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckPermission("sys.manage.role")
    public Result<?> user(@Param("roleId") String roleId, @Param("username") String username, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        return Result.data(sysRoleService.getUserListPage(roleId, username, pageNo, pageSize, pageOrderName, pageOrderBy));
    }

    @At("/select_user")
    @Ok("json:{locked:'^(password|salt)$',ignoreNull:false}")
    @POST
    @ApiOperation(name = "获取待分配用户列表(排除已分配)")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "roleId", example = "", description = "角色ID",required = true,check = true),
                    @ApiFormParam(name = "unitId", example = "", description = "单位ID",required = true,check = true),
                    @ApiFormParam(name = "username", example = "", description = "参数Key"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckPermission("sys.manage.role")
    public Result<?> getSelectUser(@Param("roleId") String roleId, @Param("unitId") String unitId, @Param("username") String username, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        return Result.data(sysRoleService.getSelUserListPage(roleId, username, StpUtil.hasRole("sysadmin"), unitId, pageNo, pageSize, pageOrderName, pageOrderBy));
    }

    @At("/link_user")
    @Ok("json")
    @POST
    @SLog(value = "关联用户到角色${roleCode}:${names}")
    @ApiOperation(name = "关联用户到角色")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "roleId", example = "", description = "角色ID"),
                    @ApiFormParam(name = "roleCode", example = "", description = "角色代码"),
                    @ApiFormParam(name = "ids", example = "", description = "用户ID数组"),
                    @ApiFormParam(name = "names", example = "descending", description = "用户名数组")
            }
    )
    @ApiResponses
    @SaCheckPermission("sys.manage.role.user")
    public Result<?> doLinkUser(@Param("roleId") String roleId, @Param("roleCode") String roleCode, @Param("ids") String ids, @Param("names") String names) {
        sysRoleService.doLinkUser(roleId, SecurityUtil.getUnitId(), Strings.splitIgnoreBlank(ids));
        return Result.success();
    }

    @At("/unlink_user")
    @Ok("json")
    @POST
    @SLog(value = "从角色移除用户${roleCode}:${name}")
    @ApiOperation(name = "从角色移除用户")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "roleId", example = "", description = "角色ID"),
                    @ApiFormParam(name = "roleCode", example = "", description = "角色代码"),
                    @ApiFormParam(name = "id", example = "", description = "参数Key"),
                    @ApiFormParam(name = "name", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses
    @SaCheckPermission("sys.manage.role.user")
    public Result<?> doUnLinkUser(@Param("roleId") String roleId, @Param("roleCode") String roleCode, @Param("id") String id, @Param("name") String name) {
        sysRoleService.doLinkUser(roleId, id);
        return Result.success();
    }

    @At("/get_menus")
    @Ok("json:{locked:'^(createdBy|createdAt|updatedBy|updatedAt)$'}")
    @GET
    @ApiOperation(name = "Vue3获取菜单及权限")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "roleId", example = "", description = "角色ID"),
                    @ApiImplicitParam(name = "appId", example = "", description = "应用ID")
            }
    )
    @ApiResponses(
            {
                    @ApiResponse(name = "menuTree", description = "所有菜单及权限数组"),
                    @ApiResponse(name = "menuIds", description = "角色已分配的权限ID数组")
            }
    )
    @SaCheckPermission("sys.manage.role")
    public Result<?> getMenuS(@Param("roleId") String roleId, @Param("appId") String appId) {
        // 角色已拥有的权限
        List<Sys_menu> hasList = sysRoleService.getMenusAndDatas(roleId, appId);
        List<Sys_menu> list;
        if (StpUtil.hasRole(GlobalConstant.DEFAULT_SYSADMIN_ROLECODE)) {
            // 超级管理员加载所有权限
            list = sysMenuService.query(Cnd.where("appId", "=", appId).asc("location").asc("path"));
        } else {
            // 非超级管理员角色加载用户自己拥有的权限
            list = sysUserService.getMenusAndDatas(SecurityUtil.getUserId(), appId);
        }
        List<String> menuIds = new ArrayList<>();
        for (Sys_menu menu : hasList) {
            menuIds.add(menu.getId());
        }
        return Result.data(NutMap.NEW().addv("menuList", list).addv("menuIds", menuIds));
    }

    @At("/get_do_menu")
    @Ok("json")
    @GET
    @ApiOperation(name = "Vue2获取菜单及权限")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "roleId", example = "", description = "角色ID"),
                    @ApiImplicitParam(name = "appId", example = "", description = "应用ID")
            }
    )
    @ApiResponses(
            {
                    @ApiResponse(name = "menuTree", description = "所有菜单及权限数组"),
                    @ApiResponse(name = "menuIds", description = "角色已分配的权限ID数组")
            }
    )
    @SaCheckPermission("sys.manage.role")
    public Result<?> getDoMenu(@Param("roleId") String roleId, @Param("appId") String appId) {
        // 角色已拥有的权限
        List<Sys_menu> hasList = sysRoleService.getMenusAndDatas(roleId, appId);
        List<Sys_menu> list;
        if (StpUtil.hasRole(GlobalConstant.DEFAULT_SYSADMIN_ROLECODE)) {
            // 超级管理员加载所有权限
            list = sysMenuService.query(Cnd.where("appId", "=", appId).asc("location").asc("path"));
        } else {
            // 非超级管理员角色加载用户自己拥有的权限
            list = sysUserService.getMenusAndDatas(SecurityUtil.getUserId(), appId);
        }
        NutMap menuMap = NutMap.NEW();
        for (Sys_menu menu : list) {
            List<Sys_menu> list1 = menuMap.getList(menu.getParentId(), Sys_menu.class);
            if (list1 == null) {
                list1 = new ArrayList<>();
            }
            list1.add(menu);
            menuMap.put(menu.getParentId(), list1);
        }
        List<String> menuIds = new ArrayList<>();
        for (Sys_menu menu : hasList) {
            menuIds.add(menu.getId());
        }
        return Result.data(NutMap.NEW().addv("menuTree", getTree(menuMap, "")).addv("menuIds", menuIds));
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

    @At("/do_menu")
    @Ok("json")
    @POST
    @SLog(value = "为${roleCode}角色分配权限:${appId}")
    @ApiOperation(name = "为角色分配权限")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "roleId", example = "", description = "角色ID", required = true, check = true),
                    @ApiFormParam(name = "roleCode", example = "", description = "角色代码", required = true, check = true),
                    @ApiFormParam(name = "appId", example = "", description = "应用ID", required = true, check = true),
                    @ApiFormParam(name = "menuIds", example = "", description = "菜单权限ID数组")
            }
    )
    @ApiResponses
    @SaCheckPermission("sys.manage.role.menu")
    public Result<?> doMenu(@Param("roleId") String roleId, @Param("roleCode") String roleCode, @Param("appId") String appId, @Param("menuIds") String menuIds) {
        String[] ids = Strings.splitIgnoreBlank(menuIds);
        if (GlobalConstant.DEFAULT_SYSADMIN_ROLECODE.equals(roleCode) && GlobalConstant.DEFAULT_PLATFORM_APPID.equals(appId) && ids.length == 0) {
            return Result.error("超级管理员角色权限不可为空,可能会造成无法登录系统");
        }
        sysRoleService.saveMenu(roleId, appId, ids);
        return Result.success();
    }
}
