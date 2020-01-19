package com.budwk.nb.web.controllers.platform.sys;

import com.budwk.nb.sys.models.Sys_dict;
import com.budwk.nb.sys.services.SysDictService;
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
 * @author wizzer(wizzer@qq.com) on 2019/11/26
 */
@IocBean
@At("/api/{version}/platform/sys/dict")
@Ok("json")
@ApiVersion("1.0.0")
public class SysDictController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysDictService sysDictService;

    /**
     * @api {get} /api/1.0.0/platform/sys/dict/child 获取子级数据
     * @apiName child
     * @apiGroup SYS_DICT
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
        List<Sys_dict> list = new ArrayList<>();
        List<NutMap> treeList = new ArrayList<>();
        Cnd cnd = Cnd.NEW();
        if (Strings.isBlank(pid)) {
            cnd.and("parentId", "=", "").or("parentId", "is", null);
        } else {
            cnd.and("parentId", "=", pid);
        }
        cnd.asc("location").asc("path");
        list = sysDictService.query(cnd);
        for (Sys_dict dict : list) {
            if (sysDictService.count(Cnd.where("parentId", "=", dict.getId())) > 0) {
                dict.setHasChildren(true);
            }
            NutMap map = Lang.obj2nutmap(dict);
            map.addv("expanded", false);
            map.addv("children", new ArrayList<>());
            treeList.add(map);
        }
        return Result.success().addData(treeList);
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/dict/tree 获取单位树数据
     * @apiName tree
     * @apiGroup SYS_DICT
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
            List<Sys_dict> list = new ArrayList<>();
            List<NutMap> treeList = new ArrayList<>();
            if (Strings.isBlank(pid)) {
                NutMap root = NutMap.NEW().addv("value", "root").addv("label", Mvcs.getMessage(req, "sys.config.dict.form.noselect")).addv("leaf", true);
                treeList.add(root);
            }
            Cnd cnd = Cnd.NEW();
            if (Strings.isBlank(pid)) {
                cnd.and("parentId", "=", "").or("parentId", "is", null);
            } else {
                cnd.and("parentId", "=", pid);
            }
            cnd.asc("location").asc("path");
            list = sysDictService.query(cnd);
            for (Sys_dict dict : list) {
                NutMap map = NutMap.NEW().addv("value", dict.getId()).addv("label", dict.getName());
                if (dict.isHasChildren()) {
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
     * @api {get} /api/1.0.0/platform/sys/dict/create 新增字典
     * @apiName create
     * @apiGroup SYS_DICT
     * @apiPermission sys.config.dict.create
     * @apiVersion 1.0.0
     * @apiParam {Object} dict   字典
     * @apiParam {String} parentId 父ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  数据
     */
    @At("/create")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.config.dict.create")
    @SLog(tag = "新增字典", msg = "字典名称:${unit.name}")
    public Object create(@Param("..") Sys_dict dict, @Param("parentId") String parentId, HttpServletRequest req) {
        try {
            if ("root".equals(parentId) || parentId == null) {
                parentId = "";
            }
            dict.setCreatedBy(StringUtil.getPlatformUid());
            dict.setUpdatedBy(StringUtil.getPlatformUid());
            sysDictService.save(dict, parentId);
            sysDictService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {delete} /api/1.0.0/platform/sys/dict/delete/:id 删除字典
     * @apiName delete
     * @apiGroup SYS_DICT
     * @apiPermission sys.config.dict.delete
     * @apiVersion 1.0.0
     * @apiParam {String} id ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/delete/?")
    @Ok("json")
    @DELETE
    @RequiresPermissions("sys.config.dict.delete")
    @SLog(tag = "删除字典")
    public Object delete(String id, HttpServletRequest req) {
        try {
            Sys_dict dict = sysDictService.fetch(id);
            if (dict == null) {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.error.noData"));
                return Result.error("system.error.noData");
            }
            req.setAttribute("_slog_msg", "字典名称:" + dict.getName());
            sysDictService.deleteAndChild(dict);
            sysDictService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/dict/get/:id 获取信息
     * @apiName get
     * @apiGroup SYS_DICT
     * @apiPermission sys.config.dict
     * @apiVersion 1.0.0
     * @apiParam {String} id ID
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/get/?")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.config.dict")
    public Object getData(String id, HttpServletRequest req) {
        try {
            Sys_dict dict = sysDictService.fetch(id);
            if (dict == null) {
                return Result.error("system.error.noData");
            }
            return Result.success().addData(dict);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/dict/update 修改信息
     * @apiName update
     * @apiGroup SYS_DICT
     * @apiPermission sys.config.dict.update
     * @apiVersion 1.0.0
     * @apiParam {Object} dict 表单对象
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At
    @Ok("json")
    @POST
    @RequiresPermissions("sys.config.dict.update")
    @SLog(tag = "修改字典", msg = "单位名称:${dict.name}")
    public Object update(@Param("..") Sys_dict dict, HttpServletRequest req) {
        try {
            dict.setUpdatedBy(StringUtil.getPlatformUid());
            sysDictService.updateIgnoreNull(dict);
            sysDictService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {get} /api/1.0.0/platform/sys/dict/get_sort_tree 获取树数据
     * @apiName get_sort_tree
     * @apiGroup SYS_DICT
     * @apiPermission sys.config.dict
     * @apiVersion 1.0.0
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/get_sort_tree")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.config.dict")
    public Object getSortTree(HttpServletRequest req) {
        try {
            List<Sys_dict> list = sysDictService.query(Cnd.NEW().asc("location").asc("path"));
            NutMap nutMap = NutMap.NEW();
            for (Sys_dict dict : list) {
                List<Sys_dict> list1 = nutMap.getList(dict.getParentId(), Sys_dict.class);
                if (list1 == null) {
                    list1 = new ArrayList<>();
                }
                list1.add(dict);
                nutMap.put(Strings.sNull(dict.getParentId()), list1);
            }
            return Result.success().addData(getTree(nutMap, ""));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    private List<NutMap> getTree(NutMap nutMap, String pid) {
        List<NutMap> treeList = new ArrayList<>();
        List<Sys_dict> subList = nutMap.getList(pid, Sys_dict.class);
        for (Sys_dict dict : subList) {
            NutMap map = Lang.obj2nutmap(dict);
            map.put("label", dict.getName());
            if (dict.isHasChildren() || (nutMap.get(dict.getId()) != null)) {
                map.put("children", getTree(nutMap, dict.getId()));
            }
            treeList.add(map);
        }
        return treeList;
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/dict/sort 执行排序
     * @apiName sort
     * @apiGroup SYS_DICT
     * @apiPermission sys.config.dict.update
     * @apiVersion 1.0.0
     * @apiParam ids 节点ids
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     */
    @At("/sort")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.config.dict.update")
    public Object sortDo(@Param("ids") String ids, HttpServletRequest req) {
        try {
            String[] unitIds = StringUtils.split(ids, ",");
            int i = 0;
            sysDictService.update(Chain.make("location", 0), Cnd.NEW());
            for (String id : unitIds) {
                if (!Strings.isBlank(id)) {
                    sysDictService.update(Chain.make("location", i), Cnd.where("id", "=", id));
                    i++;
                }
            }
            sysDictService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    /**
     * @api {post} /api/1.0.0/platform/sys/dict/disabled 启用禁用
     * @apiName disabled
     * @apiGroup SYS_DICT
     * @apiPermission sys.config.dict.update
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
    @RequiresPermissions("sys.config.dict.update")
    @SLog(tag = "启用禁用字典")
    public Object changeDisabled(@Param("id") String id, @Param("path") String path, @Param("disabled") boolean disabled, HttpServletRequest req) {
        try {
            sysDictService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
            if (disabled) {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.off"));
            } else {
                req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.on"));
            }
            sysDictService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }
}
