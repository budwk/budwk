package com.budwk.nb.web.controllers.platform.sys;

import com.budwk.nb.sys.models.Sys_unit;
import com.budwk.nb.sys.models.Sys_user;
import com.budwk.nb.sys.services.SysUnitService;
import com.budwk.nb.web.commons.slog.annotation.SLog;
import com.budwk.nb.web.commons.utils.ShiroUtil;
import com.budwk.nb.web.commons.utils.StringUtil;
import com.budwk.nb.framework.base.Result;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
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
 * Created by wizzer.cn on 2019/11/21
 */
@IocBean
@At("/api/{version}/platform/sys/unit")
@Ok("json")
@ApiVersion("1.0.0")
public class SysUnitController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysUnitService sysUnitService;
    @Inject
    private ShiroUtil shiroUtil;

    /**
     * @api {get} /api/1.0.0/platform/sys/unit/child 获取单位子级数据
     * @apiName child
     * @apiGroup SYS_UNIT
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
        List<Sys_unit> list = new ArrayList<>();
        List<NutMap> treeList = new ArrayList<>();
        if (shiroUtil.hasRole("sysadmin")) {
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
            NutMap map = Lang.obj2nutmap(unit);
            map.addv("expanded", false);
            map.addv("children", new ArrayList<>());
            treeList.add(map);
        }
        return Result.success().addData(treeList);
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/unit/tree 获取单位树数据
     * @apiName tree
     * @apiGroup SYS_UNIT
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
            List<Sys_unit> list = new ArrayList<>();
            List<NutMap> treeList = new ArrayList<>();
            if (Strings.isBlank(pid)) {
                NutMap root = NutMap.NEW().addv("value", "root").addv("label", Mvcs.getMessage(req, "sys.manage.unit.form.noselect")).addv("leaf", true);
                treeList.add(root);
            }
            if (shiroUtil.hasRole("sysadmin")) {
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
     * @api {get} /api/1.0.0/platform/sys/unit/create 新增单位
     * @apiName create
     * @apiGroup SYS_UNIT
     * @apiPermission sys.manage.unit.create
     * @apiVersion 1.0.0
     * @apiParam {Object} unit   Sys_unit
     * @apiParam {String} parentId 父单位ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  数据
     */
    @At("/create")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.unit.create")
    @SLog(tag = "新增单位", msg = "单位名称:${unit.name}")
    public Object create(@Param("..") Sys_unit unit, @Param("parentId") String parentId, HttpServletRequest req) {
        try {
            if ("root".equals(parentId) || parentId == null) {
                parentId = "";
            }
            unit.setCreatedBy(StringUtil.getPlatformUid());
            unit.setUpdatedBy(StringUtil.getPlatformUid());
            sysUnitService.save(unit, parentId);
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {delete} /api/1.0.0/platform/sys/unit/delete/:id 删除单位
     * @apiName delete
     * @apiGroup SYS_UNIT
     * @apiPermission sys.manage.unit.delete
     * @apiVersion 1.0.0
     * @apiParam {String} id 单位ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/delete/?")
    @Ok("json")
    @DELETE
    @RequiresPermissions("sys.manage.unit.delete")
    @SLog(tag = "删除单位")
    public Object delete(String id, HttpServletRequest req) {
        try {
            Sys_unit unit = sysUnitService.fetch(id);
            if (unit == null) {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.error.noData"));
                return Result.error("system.error.noData");
            }
            req.setAttribute("_slog_msg", "单位名称:" + unit.getName());
            if (Strings.sNull(unit.getPath()).startsWith("0001")) {
                return Result.error("system.error.delete.notAllow");
            }
            sysUnitService.deleteAndChild(unit);
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/unit/get/:id 获取单位信息
     * @apiName get
     * @apiGroup SYS_UNIT
     * @apiPermission sys.manage.unit
     * @apiVersion 1.0.0
     * @apiParam {String} id 单位ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/get/?")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.unit")
    public Object getData(String id, HttpServletRequest req) {
        try {
            Sys_unit unit = sysUnitService.fetch(id);
            if (unit == null) {
                return Result.error("system.error.noData");
            }
            return Result.success().addData(unit);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/unit/update 修改单位信息
     * @apiName update
     * @apiGroup SYS_UNIT
     * @apiPermission sys.manage.unit.update
     * @apiVersion 1.0.0
     * @apiParam {String} id 单位ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.unit.update")
    @SLog(tag = "修改单位", msg = "单位名称:${unit.name}")
    public Object update(@Param("..") Sys_unit unit, HttpServletRequest req) {
        try {
            unit.setUpdatedBy(StringUtil.getPlatformUid());
            sysUnitService.updateIgnoreNull(unit);
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/unit/get_sort_tree 获取树数据
     * @apiName get_sort_tree
     * @apiGroup SYS_UNIT
     * @apiPermission sys.manage.unit
     * @apiVersion 1.0.0
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/get_sort_tree")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.unit")
    public Object getSortTree(HttpServletRequest req) {
        try {
            Cnd cnd = Cnd.NEW();
            if (!shiroUtil.hasRole("sysadmin")) {
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
            return Result.success().addData(getTree(nutMap, ""));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    private List<NutMap> getTree(NutMap nutMap, String pid) {
        List<NutMap> treeList = new ArrayList<>();
        List<Sys_unit> subList = nutMap.getList(pid, Sys_unit.class);
        for (Sys_unit unit : subList) {
            NutMap map = Lang.obj2nutmap(unit);
            map.put("label", unit.getName());
            if (unit.isHasChildren() || (nutMap.get(unit.getId()) != null)) {
                map.put("children", getTree(nutMap, unit.getId()));
            }
            treeList.add(map);
        }
        return treeList;
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/unit/sort 执行排序
     * @apiName sort
     * @apiGroup SYS_UNIT
     * @apiPermission sys.manage.unit.update
     * @apiVersion 1.0.0
     * @apiParam ids 节点ids
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/sort")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.unit.update")
    public Object sortDo(@Param("ids") String ids, HttpServletRequest req) {
        try {
            String[] unitIds = StringUtils.split(ids, ",");
            int i = 0;
            if (shiroUtil.hasRole("sysadmin")) {
                sysUnitService.update(Chain.make("location", 0), Cnd.NEW());
            }
            for (String id : unitIds) {
                if (!Strings.isBlank(id)) {
                    sysUnitService.update(Chain.make("location", i), Cnd.where("id", "=", id));
                    i++;
                }
            }
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }
}
