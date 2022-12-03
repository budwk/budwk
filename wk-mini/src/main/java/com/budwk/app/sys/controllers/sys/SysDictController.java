package com.budwk.app.sys.controllers.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.sys.models.Sys_dict;
import com.budwk.app.sys.services.SysDictService;
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
@At("/platform/sys/dict")
@SLog(tag = "数据字典")
@ApiDefinition(tag = "数据字典")
@Slf4j
public class SysDictController {
    @Inject
    private SysDictService sysDictService;

    @At
    @Ok("json")
    @GET
    @ApiOperation(name = "Vue3树形列表查询")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "name", example = "", description = "单位名称")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckPermission("sys.config.dict")
    public Result<?> list(@Param("name") String name) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(name)) {
            cnd.and("name", "like", "%" + name + "%");
        }
        cnd.asc("location");
        cnd.asc("path");
        return Result.success().addData(sysDictService.query(cnd));
    }

    @At("/child")
    @Ok("json")
    @GET
    @SaCheckPermission("sys.config.dict")
    @ApiOperation(name = "Vue2获取列表树型数据")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "pid", description = "父级ID")
            }
    )
    @ApiResponses
    public Result<?> getChild(@Param("pid") String pid, HttpServletRequest req) {
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
        return Result.data(treeList);
    }

    @At("/tree")
    @Ok("json")
    @GET
    @SaCheckPermission("sys.config.dict")
    @ApiOperation(name = "获取待选择树型数据")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "pid", description = "父级ID")
            }
    )
    @ApiResponses
    public Result<?> getTree(@Param("pid") String pid, HttpServletRequest req) {
        List<Sys_dict> list = new ArrayList<>();
        List<NutMap> treeList = new ArrayList<>();
        if (Strings.isBlank(pid)) {
            NutMap root = NutMap.NEW().addv("value", "root").addv("label", "默认顶级").addv("leaf", true);
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
        return Result.data(treeList);
    }

    @At("/create")
    @Ok("json")
    @POST
    @SaCheckPermission("sys.config.dict.create")
    @SLog(value = "新增字典项:${dict.name}")
    @ApiOperation(name = "新增字典项")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "parentId", description = "父级ID")
            },
            implementation = Sys_dict.class
    )
    @ApiResponses
    public Result<?> create(@Param("..") Sys_dict dict, @Param("parentId") String parentId, HttpServletRequest req) {
        if ("root".equals(parentId) || parentId == null) {
            parentId = "";
        }
        dict.setCreatedBy(SecurityUtil.getUserId());
        sysDictService.save(dict, parentId);
        sysDictService.cacheClear();
        return Result.success();
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @SaCheckPermission("sys.config.dict.delete")
    @SLog(value = "删除字典项:")
    @ApiOperation(name = "删除字典项")
    @ApiImplicitParams(
            value = {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> delete(String id, HttpServletRequest req) {
        Sys_dict dict = sysDictService.fetch(id);
        if (dict == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        req.setAttribute("_slog_msg", dict.getName());
        sysDictService.deleteAndChild(dict);
        sysDictService.cacheClear();
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取字典项")
    @ApiImplicitParams(
            value = {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> getData(String id, HttpServletRequest req) {
        Sys_dict dict = sysDictService.fetch(id);
        if (dict == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(dict);
    }

    @At
    @Ok("json")
    @POST
    @SaCheckPermission("sys.config.dict.update")
    @SLog(value = "修改字典项:${dict.name}")
    @ApiOperation(name = "修改字典项")
    @ApiFormParams(
            implementation = Sys_dict.class
    )
    @ApiResponses
    public Result<?> update(@Param("..") Sys_dict dict, HttpServletRequest req) {
        dict.setUpdatedBy(SecurityUtil.getUserId());
        sysDictService.updateIgnoreNull(dict);
        sysDictService.cacheClear();
        return Result.success();
    }

    @At("/get_sort_tree")
    @Ok("json")
    @GET
    @SaCheckPermission("sys.config.dict")
    @ApiOperation(name = "获取待排序数据")
    @ApiImplicitParams
    @ApiResponses
    public Result<?> getSortTree(HttpServletRequest req) {
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
        return Result.data(getTree(nutMap, ""));
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

    @At("/sort")
    @Ok("json")
    @POST
    @SaCheckPermission("sys.config.dict.update")
    @ApiOperation(name = "保存排序数据")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "ids", description = "ids数组")
            }
    )
    @ApiResponses
    public Result<?> sortDo(@Param("ids") String ids, HttpServletRequest req) {
        String[] unitIds = StringUtils.split(ids, ",");
        int i = 0;
        sysDictService.update(Chain.make("location", 0), Cnd.NEW());
        for (String id : unitIds) {
            if (!Strings.isBlank(id)) {
                sysDictService.update(Chain.make("location", i), Cnd.where("id", "=", id));
                i++;
            }
        }
        sysDictService.cacheClear();
        return Result.success();
    }

    @At("/disabled")
    @Ok("json")
    @POST
    @SaCheckPermission("sys.config.dict.update")
    @SLog(value = "启用禁用:${id}-")
    @ApiOperation(name = "启用禁用")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "id", description = "主键ID", required = true),
                    @ApiFormParam(name = "disabled", description = "disabled=true禁用", required = true)
            }
    )
    @ApiResponses
    public Result<?> changeDisabled(@Param("id") String id, @Param("disabled") boolean disabled, HttpServletRequest req) {
        int res = sysDictService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
        if (res > 0) {
            if (disabled) {
                req.setAttribute("_slog_msg", "禁用");
            } else {
                req.setAttribute("_slog_msg", "启用");
            }
            sysDictService.cacheClear();
            return Result.success();
        }
        return Result.error();
    }
}
