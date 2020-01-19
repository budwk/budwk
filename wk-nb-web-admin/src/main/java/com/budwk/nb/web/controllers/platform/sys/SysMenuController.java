package com.budwk.nb.web.controllers.platform.sys;

import com.budwk.nb.sys.models.Sys_menu;
import com.budwk.nb.sys.services.SysMenuService;
import com.budwk.nb.sys.services.SysRoleService;
import com.budwk.nb.sys.services.SysUserService;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.commons.base.Result;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
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
 * @author wizzer(wizzer@qq.com) on 2019/11/22
 */
@IocBean
@At("/api/{version}/platform/sys/menu")
@Ok("json")
@ApiVersion("1.0.0")
public class SysMenuController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysMenuService sysMenuService;
    @Inject
    @Reference
    private SysUserService sysUserService;
    @Inject
    @Reference
    private SysRoleService sysRoleService;

    /**
     * @api {get} /api/1.0.0/platform/sys/menu/child 获取子级数据
     * @apiName child
     * @apiGroup SYS_MENU
     * @apiPermission 登陆用户
     * @apiVersion 1.0.0
     * @apiParam {String} pid   父级ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  数据
     */
    @At("/child")
    @Ok("json")
    @GET
    @RequiresAuthentication
    public Object getChild(@Param("pid") String pid, HttpServletRequest req) {
        List<Sys_menu> list = new ArrayList<>();
        List<NutMap> treeList = new ArrayList<>();
        Cnd cnd = Cnd.NEW();
        if (Strings.isBlank(pid)) {
            cnd.and(Cnd.exps("parentId", "=", "").or("parentId", "is", null));
        } else {
            cnd.and("parentId", "=", pid);
        }
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
        return Result.success().addData(treeList);
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/menu/tree 获取树数据
     * @apiName tree
     * @apiGroup SYS_MENU
     * @apiPermission 登陆用户
     * @apiVersion 1.0.0
     * @apiParam {String} pid   父级ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  数据
     */
    @At("/tree")
    @Ok("json")
    @GET
    @RequiresAuthentication
    public Object getTree(@Param("pid") String pid, HttpServletRequest req) {
        try {
            List<NutMap> treeList = new ArrayList<>();
            if (Strings.isBlank(pid)) {
                NutMap root = NutMap.NEW().addv("value", "root").addv("label", Mvcs.getMessage(req, "sys.manage.menu.form.noselect")).addv("leaf", true);
                treeList.add(root);
            }
            Cnd cnd = Cnd.NEW();
            if (Strings.isBlank(pid)) {
                cnd.and(Cnd.exps("parentId", "=", "").or("parentId", "is", null));
            } else {
                cnd.and("parentId", "=", pid);
            }
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
            return Result.success().addData(treeList);
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/menu/disabled 启用禁用菜单
     * @apiName disabled
     * @apiGroup SYS_MENU
     * @apiPermission sys.manage.menu.update
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
    @RequiresPermissions("sys.manage.menu.update")
    @SLog(tag = "修改菜单")
    public Object changeDisabled(@Param("id") String id, @Param("path") String path, @Param("disabled") boolean disabled, HttpServletRequest req) {
        try {
            if (Strings.sNull(path).startsWith("0001")) {
                return Result.error("system.error.disabled.notAllow");
            }
            sysMenuService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
            if (disabled) {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.off"));
            } else {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.on"));
            }
            sysMenuService.clearCache();
            sysUserService.clearCache();
            sysRoleService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/menu/create 新增菜单
     * @apiName create
     * @apiGroup SYS_MENU
     * @apiPermission sys.manage.menu.create
     * @apiVersion 1.0.0
     * @apiParam {Object} menu   菜单
     * @apiParam {Object} buttons  权限
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  数据
     */
    @At("/create")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.menu.create")
    @SLog(tag = "新增菜单")
    public Object create(@Param("..") NutMap nutMap, HttpServletRequest req) {
        try {
            Sys_menu sysMenu = nutMap.getAs("menu", Sys_menu.class);
            List<NutMap> buttons = Json.fromJsonAsList(NutMap.class, nutMap.getString("buttons"));
            int num = sysMenuService.count(Cnd.where("permission", "=", sysMenu.getPermission().trim()));
            if (num > 0) {
                return Result.error("sys.manage.menu.form.permission.hasExist");
            }
            for (NutMap map : buttons) {
                num = sysMenuService.count(Cnd.where("permission", "=", map.getString("permission", "").trim()));
                if (num > 0) {
                    return Result.error("sys.manage.menu.form.permission.hasExist");
                }
            }
            String parentId = sysMenu.getParentId();
            if ("root".equals(sysMenu.getParentId())) {
                parentId = "";
            }
            sysMenu.setHasChildren(false);
            sysMenu.setCreatedBy(StringUtil.getPlatformUid());
            sysMenu.setUpdatedBy(StringUtil.getPlatformUid());
            sysMenuService.save(sysMenu, Strings.sNull(parentId), buttons);
            req.setAttribute("_slog_msg", "菜单名称:" + sysMenu.getName());
            sysMenuService.clearCache();
            sysUserService.clearCache();
            sysRoleService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {delete} /api/1.0.0/platform/sys/menu/delete/:id 删除菜单
     * @apiName delete
     * @apiGroup SYS_MENU
     * @apiPermission sys.manage.menu.delete
     * @apiVersion 1.0.0
     * @apiParam {String} id 单位ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/delete/?")
    @Ok("json")
    @DELETE
    @RequiresPermissions("sys.manage.menu.delete")
    @SLog(tag = "删除菜单")
    public Object delete(String id, HttpServletRequest req) {
        try {
            Sys_menu menu = sysMenuService.fetch(id);
            if (menu == null) {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.error.noData"));
                return Result.error("system.error.noData");
            }
            req.setAttribute("_slog_msg", "菜单名称:" + menu.getName());
            if (Strings.sNull(menu.getPath()).startsWith("0001")) {
                return Result.error("system.error.delete.notAllow");
            }
            sysMenuService.deleteAndChild(menu);
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/menu/get_sort_tree 获取树数据
     * @apiName get_sort_tree
     * @apiGroup SYS_MENU
     * @apiPermission sys.manage.menu
     * @apiVersion 1.0.0
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/get_sort_tree")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.menu")
    public Object getSortTree(HttpServletRequest req) {
        try {
            List<Sys_menu> list = sysMenuService.query(Cnd.NEW().asc("location").asc("path"));
            NutMap nutMap = NutMap.NEW();
            for (Sys_menu menu : list) {
                List<Sys_menu> list1 = nutMap.getList(menu.getParentId(), Sys_menu.class);
                if (list1 == null) {
                    list1 = new ArrayList<>();
                }
                list1.add(menu);
                nutMap.put(Strings.sNull(menu.getParentId()), list1);
            }
            return Result.success().addData(getTree(nutMap, ""));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
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

    /**
     * @api {post} /api/1.0.0/platform/sys/menu/sort 执行排序
     * @apiName sort
     * @apiGroup SYS_MENU
     * @apiPermission sys.manage.menu.update
     * @apiVersion 1.0.0
     * @apiParam ids 节点ids
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/sort")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.menu.update")
    public Object sortDo(@Param("ids") String ids, HttpServletRequest req) {
        try {
            String[] unitIds = StringUtils.split(ids, ",");
            int i = 0;
            sysMenuService.update(Chain.make("location", 0), Cnd.NEW());
            for (String id : unitIds) {
                if (!Strings.isBlank(id)) {
                    sysMenuService.update(Chain.make("location", i), Cnd.where("id", "=", id));
                    i++;
                }
            }
            sysMenuService.clearCache();
            sysUserService.clearCache();
            sysRoleService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/menu/get_menu/:id 获取菜单信息
     * @apiName get_menu
     * @apiGroup SYS_MENU
     * @apiPermission sys.manage.menu.update
     * @apiVersion 1.0.0
     * @apiParam {String} id 菜单ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/get_menu/?")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.menu.update")
    public Object getMenu(String id, HttpServletRequest req) {
        try {
            Sys_menu menu = sysMenuService.fetch(id);
            NutMap map = Lang.obj2nutmap(menu);
            map.put("parentName", Mvcs.getMessage(req, "system.commons.txt.nothing"));
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
            return Result.success().addData(map);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/menu/update_menu 修改菜单
     * @apiName update_menu
     * @apiGroup SYS_MENU
     * @apiPermission sys.manage.menu.update
     * @apiVersion 1.0.0
     * @apiParam {Object} menu   菜单
     * @apiParam {Object} buttons  权限
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  数据
     */
    @At("/update_menu")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.menu.update")
    @SLog(tag = "修改菜单")
    public Object updateMenu(@Param("..") NutMap nutMap, HttpServletRequest req) {
        try {
            Sys_menu sysMenu = nutMap.getAs("menu", Sys_menu.class);
            List<NutMap> buttons = Json.fromJsonAsList(NutMap.class, nutMap.getString("buttons"));
            //如果权限标识不是自己的,并且被其他记录占用
            int num = sysMenuService.count(Cnd.where("permission", "=", sysMenu.getPermission().trim()).and("id", "<>", sysMenu.getId()));
            if (num > 0) {
                return Result.error("sys.manage.menu.form.permission.hasExist");
            }
            for (NutMap map : buttons) {
                num = sysMenuService.count(Cnd.where("permission", "=", map.getString("permission", "").trim()).and("id", "<>", map.getString("key", "")));
                if (num > 0) {
                    return Result.error("sys.manage.menu.form.permission.hasExist");
                }
            }
            sysMenu.setHasChildren(false);
            sysMenu.setUpdatedBy(StringUtil.getPlatformUid());
            sysMenuService.edit(sysMenu, sysMenu.getParentId(), buttons);
            req.setAttribute("_slog_msg", sysMenu.getName());
            sysMenuService.clearCache();
            sysUserService.clearCache();
            sysRoleService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/menu/get_data/:id 获取权限信息
     * @apiName get_data
     * @apiGroup SYS_MENU
     * @apiPermission sys.manage.menu
     * @apiVersion 1.0.0
     * @apiParam {String} id 菜单权限ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/get_data/?")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.menu")
    public Object getData(String id, HttpServletRequest req) {
        try {
            return Result.success().addData(sysMenuService.fetch(id));
        } catch (Exception e) {
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/menu/update_data 修改菜单权限
     * @apiName update_data
     * @apiGroup SYS_MENU
     * @apiPermission sys.manage.menu.update
     * @apiVersion 1.0.0
     * @apiParam {Object} menu   菜单对象
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  数据
     */
    @At("/update_data")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.menu.update")
    @SLog(tag = "修改菜单权限")
    public Object updateData(@Param("..") Sys_menu menu, HttpServletRequest req) {
        try {
            int num = sysMenuService.count(Cnd.where("permission", "=", menu.getPermission().trim()).and("id", "<>", menu.getId()));
            if (num > 0) {
                return Result.error("sys.manage.menu.form.permission.hasExist");
            }
            sysMenuService.updateIgnoreNull(menu);
            req.setAttribute("_slog_msg", menu.getName());
            sysMenuService.clearCache();
            sysUserService.clearCache();
            sysRoleService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

}
