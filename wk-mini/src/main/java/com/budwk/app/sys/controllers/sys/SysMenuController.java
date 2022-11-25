package com.budwk.app.sys.controllers.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.sys.models.Sys_menu;
import com.budwk.app.sys.services.SysAppService;
import com.budwk.app.sys.services.SysMenuService;
import com.budwk.app.sys.services.SysRoleService;
import com.budwk.app.sys.services.SysUserService;
import com.budwk.starter.common.constant.GlobalConstant;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
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
@At("/platform/sys/menu")
@SLog(tag = "菜单管理")
@ApiDefinition(tag = "菜单管理")
@Slf4j
public class SysMenuController {
    @Inject
    private SysMenuService sysMenuService;
    @Inject
    private SysAppService sysAppService;
    @Inject
    private SysUserService sysUserService;
    @Inject
    private SysRoleService sysRoleService;

    @At
    @Ok("json")
    @GET
    @ApiOperation(name = "获取配置数据")
    @SaCheckPermission("sys.manage.menu")
    public Result<?> data() {
        NutMap map = NutMap.NEW();
        map.addv("apps", sysAppService.listAll());
        return Result.data(map);
    }

    @At
    @Ok("json")
    @GET
    @ApiOperation(name = "Vue3树形列表查询")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "name", example = "", description = "菜单名称"),
                    @ApiImplicitParam(name = "href", example = "", description = "菜单路径"),
                    @ApiImplicitParam(name = "appId", example = "", description = "所属应用", required = true, check = true)
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckPermission("sys.manage.menu")
    public Result<?> list(@Param("name") String name, @Param("appId") String appId, @Param("href") String href) {
        Cnd cnd = Cnd.NEW();
        cnd.and("appId", "=", appId);
        if (Strings.isNotBlank(name)) {
            cnd.and("name", "like", "%" + name + "%");
        }
        if (Strings.isNotBlank(href)) {
            cnd.and("href", "like", "%" + href + "%");
        }
        cnd.asc("location");
        cnd.asc("path");
        return Result.success().addData(sysMenuService.query(cnd));
    }

    @At("/child")
    @Ok("json")
    @GET
    @SaCheckPermission("sys.manage.menu")
    @ApiOperation(name = "Vue2获取列表树型数据")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "pid", description = "父级ID"),
                    @ApiImplicitParam(name = "appId", description = "应用ID", required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> getChild(@Param("pid") String pid, @Param("appId") String appId, HttpServletRequest req) {
        List<Sys_menu> list = new ArrayList<>();
        List<NutMap> treeList = new ArrayList<>();
        Cnd cnd = Cnd.NEW();
        if (Strings.isBlank(pid)) {
            cnd.and(Cnd.exps("parentId", "=", "").or("parentId", "is", null));
        } else {
            cnd.and("parentId", "=", pid);
        }
        cnd.and("appId", "=", appId);
        cnd.asc("location").asc("path");
        list = sysMenuService.query(cnd);
        for (Sys_menu menu : list) {
            if (sysMenuService.count(Cnd.where("parentId", "=", menu.getId())) > 0) {
                menu.setHasChildren(true);
            }
            NutMap map = Lang.obj2nutmap(menu);
            map.addv("expanded", false);
            map.addv("children", new ArrayList<>());
            treeList.add(map);
        }
        return Result.data(treeList);
    }

    @At("/tree")
    @Ok("json")
    @GET
    @SaCheckPermission("sys.manage.menu")
    @ApiOperation(name = "获取待选择树型数据")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "pid", description = "父级ID"),
                    @ApiImplicitParam(name = "appId", description = "应用ID", required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> getTree(@Param("pid") String pid, @Param("appId") String appId, HttpServletRequest req) {
        List<NutMap> treeList = new ArrayList<>();
        if (Strings.isBlank(pid)) {
            NutMap root = NutMap.NEW().addv("value", "root").addv("label", "默认顶级").addv("leaf", true);
            treeList.add(root);
        }
        Cnd cnd = Cnd.NEW();
        if (Strings.isBlank(pid)) {
            cnd.and(Cnd.exps("parentId", "=", "").or("parentId", "is", null));
        } else {
            cnd.and("parentId", "=", pid);
        }
        cnd.and("appId", "=", appId);
        cnd.and("type", "=", "menu");
        cnd.asc("location").asc("path");
        List<Sys_menu> list = sysMenuService.query(cnd);
        for (Sys_menu menu : list) {
            NutMap map = NutMap.NEW().addv("value", menu.getId()).addv("label", menu.getName());
            if (menu.isHasChildren()) {
                map.addv("children", new ArrayList<>());
                map.addv("leaf", false);
            } else {
                map.addv("leaf", true);
            }
            treeList.add(map);
        }
        return Result.data(treeList);
    }

    @At("/create")
    @Ok("json")
    @POST
    @SaCheckPermission("sys.manage.menu.create")
    @SLog(value = "创建菜单,菜单名称:")
    @ApiOperation(name = "创建菜单")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "menu", description = "Sys_menu对象", type = "object"),
                    @ApiFormParam(name = "buttons", description = "权限按钮"),
                    @ApiFormParam(name = "appId", description = "应用ID", check = true, required = true)
            }
    )
    @ApiResponses
    public Result<?> create(@Param("..") NutMap nutMap, HttpServletRequest req) {
        Sys_menu sysMenu = nutMap.getAs("menu", Sys_menu.class);
        String appId = nutMap.getString("appId");
        List<NutMap> buttons = Json.fromJsonAsList(NutMap.class, nutMap.getString("buttons"));
        int num = sysMenuService.count(Cnd.where("permission", "=", sysMenu.getPermission().trim()));
        if (num > 0) {
            return Result.error("权限标识已存在");
        }
        for (NutMap map : buttons) {
            num = sysMenuService.count(Cnd.where("permission", "=", map.getString("permission", "").trim()));
            if (num > 0) {
                return Result.error("权限标识已存在");
            }
        }
        String parentId = sysMenu.getParentId();
        if ("root".equals(sysMenu.getParentId())) {
            parentId = "";
        }
        sysMenu.setHasChildren(false);
        sysMenu.setCreatedBy(SecurityUtil.getUserId());
        sysMenuService.save(appId, sysMenu, Strings.sNull(parentId), buttons);
        req.setAttribute("_slog_msg", sysMenu.getName());
        sysUserService.cacheClear();
        return Result.success();
    }

    @At("/disabled")
    @Ok("json")
    @POST
    @SaCheckPermission("sys.manage.menu.update")
    @SLog(value = "启用禁用菜单,菜单${id}:")
    @ApiOperation(name = "创建菜单")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "id", description = "主键ID"),
                    @ApiFormParam(name = "path", description = "PATH路径"),
                    @ApiFormParam(name = "disabled", description = "true=禁用")
            }
    )
    @ApiResponses
    public Result<?> changeDisabled(@Param("id") String id, @Param("path") String path, @Param("disabled") boolean disabled, HttpServletRequest req) {
        if (Strings.sNull(path).startsWith("0001")) {
            return Result.error("系统菜单禁止操作");
        }
        int res = sysMenuService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
        if (res > 0) {
            if (disabled) {
                req.setAttribute("_slog_msg", "禁用");
            } else {
                req.setAttribute("_slog_msg", "启用");
            }
            sysUserService.cacheClear();
            return Result.success();
        }
        return Result.error();
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @SaCheckPermission("sys.manage.menu.delete")
    @SLog(value = "删除菜单,菜单名称:")
    @ApiOperation(name = "删除菜单")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH),
            }
    )
    @ApiResponses
    public Result<?> delete(String id, HttpServletRequest req) {
        Sys_menu menu = sysMenuService.fetch(id);
        if (menu == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        req.setAttribute("_slog_msg", menu.getName());
        if (!GlobalConstant.DEFAULT_SYSADMIN_LOGINNAME.equals(SecurityUtil.getUserLoginname()) && Strings.sNull(menu.getPath()).startsWith("0001")) {
            return Result.error("系统菜单禁止操作");
        }
        sysMenuService.deleteAndChild(menu);
        return Result.success();
    }

    @At("/get_sort_tree")
    @Ok("json")
    @GET
    @SaCheckPermission("sys.manage.menu")
    @ApiOperation(name = "获取待排序数据")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "appId", description = "应用ID", required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> getSortTree(@Param("appId") String appId, HttpServletRequest req) {
        List<Sys_menu> list = sysMenuService.query(Cnd.where("appId", "=", appId).asc("location").asc("path"));
        NutMap nutMap = NutMap.NEW();
        for (Sys_menu menu : list) {
            List<Sys_menu> list1 = nutMap.getList(menu.getParentId(), Sys_menu.class);
            if (list1 == null) {
                list1 = new ArrayList<>();
            }
            list1.add(menu);
            nutMap.put(Strings.sNull(menu.getParentId()), list1);
        }
        return Result.data(getTree(nutMap, ""));
    }

    private List<NutMap> getTree(NutMap nutMap, String pid) {
        List<NutMap> treeList = new ArrayList<>();
        List<Sys_menu> subList = nutMap.getList(pid, Sys_menu.class);
        for (Sys_menu menu : subList) {
            NutMap map = Lang.obj2nutmap(menu);
            map.put("label", menu.getName());
            if (menu.isHasChildren() || (nutMap.get(menu.getId()) != null)) {
                map.put("children", getTree(nutMap, menu.getId()));
            }
            treeList.add(map);
        }
        return treeList;
    }

    @At("/sort")
    @Ok("json")
    @POST
    @SaCheckPermission("sys.manage.menu.update")
    @SLog(value = "菜单排序")
    @ApiOperation(name = "菜单排序")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "ids", description = "ID数组"),
                    @ApiFormParam(name = "appId", description = "应用ID", required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> sortDo(@Param("ids") String ids, @Param("appId") String appId, HttpServletRequest req) {
        String[] unitIds = StringUtils.split(ids, ",");
        int i = 0;
        sysMenuService.update(Chain.make("location", 0), Cnd.where("appId", "=", appId));
        for (String id : unitIds) {
            if (!Strings.isBlank(id)) {
                sysMenuService.update(Chain.make("location", i), Cnd.where("id", "=", id));
                i++;
            }
        }
        sysUserService.cacheClear();
        return Result.success();
    }

    @At("/get_menu/{id}")
    @Ok("json")
    @GET
    @SaCheckPermission("sys.manage.menu")
    @ApiOperation(name = "获取菜单数据")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> getMenu(String id, HttpServletRequest req) {

        Sys_menu menu = sysMenuService.fetch(id);
        NutMap map = Lang.obj2nutmap(menu);
        map.put("parentName", "无");
        map.put("children", "false");
        if (Strings.isNotBlank(menu.getParentId())) {
            map.put("parentName", sysMenuService.fetch(menu.getParentId()).getName());
        }
        List<Sys_menu> list = sysMenuService.query(Cnd.where("parentId", "=", id).and("type", "=", "data").asc("location").asc("path"));
        List<NutMap> buttons = new ArrayList<>();
        if (list != null && list.size() > 0) {
            map.put("children", "true");
            for (Sys_menu m : list) {
                buttons.add(NutMap.NEW().addv("key", m.getId()).addv("name", m.getName()).addv("permission", m.getPermission()));
            }
        }
        map.put("buttons", buttons);
        return Result.data(map);
    }

    @At("/update_menu")
    @Ok("json")
    @POST
    @SaCheckPermission("sys.manage.menu.update")
    @SLog(value = "修改菜单,菜单名称:")
    @ApiOperation(name = "修改菜单")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "menu", description = "菜单对象", type = "object", required = true),
                    @ApiFormParam(name = "buttons", description = "菜单权限数组", required = true)
            }
    )
    @ApiResponses
    public Result<?> updateMenu(@Param("..") NutMap nutMap, HttpServletRequest req) {

        Sys_menu sysMenu = nutMap.getAs("menu", Sys_menu.class);
        List<NutMap> buttons = Json.fromJsonAsList(NutMap.class, nutMap.getString("buttons"));
        //如果权限标识不是自己的,并且被其他记录占用
        int num = sysMenuService.count(Cnd.where("permission", "=", sysMenu.getPermission().trim()).and("id", "<>", sysMenu.getId()));
        if (num > 0) {
            return Result.error("权限标识已存在");
        }
        for (NutMap map : buttons) {
            num = sysMenuService.count(Cnd.where("permission", "=", map.getString("permission", "").trim()).and("id", "<>", map.getString("key", "")));
            if (num > 0) {
                return Result.error("权限标识已存在");
            }
        }
        sysMenu.setHasChildren(false);
        sysMenu.setUpdatedBy(SecurityUtil.getUserId());
        sysMenuService.edit(sysMenu, sysMenu.getParentId(), buttons);
        req.setAttribute("_slog_msg", sysMenu.getName());
        sysUserService.cacheClear();
        return Result.success();
    }


    @At("/get_data/{id}")
    @Ok("json")
    @GET
    @SaCheckPermission("sys.manage.menu")
    @ApiOperation(name = "获取权限数据")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> getData(String id, HttpServletRequest req) {
        return Result.data(sysMenuService.fetch(id));
    }

    @At("/update_data")
    @Ok("json")
    @POST
    @SaCheckPermission("sys.manage.menu.update")
    @SLog(value = "修改权限,权限名称:")
    @ApiOperation(name = "修改权限")
    @ApiFormParams(
            implementation = Sys_menu.class
    )
    @ApiResponses
    public Result<?> updateData(@Param("..") Sys_menu menu, HttpServletRequest req) {
        int num = sysMenuService.count(Cnd.where("permission", "=", menu.getPermission().trim()).and("id", "<>", menu.getId()));
        if (num > 0) {
            return Result.error("权限标识已存在");
        }
        sysMenuService.updateIgnoreNull(menu);
        req.setAttribute("_slog_msg", menu.getName());
        sysUserService.cacheClear();
        return Result.success();
    }
}
