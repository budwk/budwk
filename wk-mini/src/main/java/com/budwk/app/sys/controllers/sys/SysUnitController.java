package com.budwk.app.sys.controllers.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.budwk.app.sys.models.Sys_unit;
import com.budwk.app.sys.models.Sys_unit_user;
import com.budwk.app.sys.services.SysUnitService;
import com.budwk.app.sys.services.SysUnitUserService;
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
@At("/platform/sys/unit")
@SLog(tag = "单位管理")
@ApiDefinition(tag = "单位管理")
@Slf4j
public class SysUnitController {
    @Inject
    private SysUnitService sysUnitService;
    @Inject
    private SysUserService sysUserService;
    @Inject
    private SysUnitUserService sysUnitUserService;

    @At
    @Ok("json")
    @GET
    @ApiOperation(name = "Vue3树形列表查询")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "name", example = "", description = "单位名称"),
                    @ApiImplicitParam(name = "leaderName", example = "", description = "部门负责人")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckPermission("sys.manage.unit")
    public Result<?> list(
            @Param("name") String name,
            @Param("leaderName") String leaderName) {
        Cnd cnd = Cnd.NEW();
        // 非管理员显示所属单位及下级单位
        if (!StpUtil.hasRole(GlobalConstant.DEFAULT_SYSADMIN_ROLECODE)) {
            Sys_unit unit = sysUnitService.fetch(SecurityUtil.getUnitId());
            cnd.and("path", "like", unit.getPath() + "%");
        }
        if (Strings.isNotBlank(name)) {
            cnd.and("name", "like", "%" + name + "%");
        }
        if (Strings.isNotBlank(leaderName)) {
            cnd.and("leaderName", "like", "%" + name + "%");
        }
        cnd.asc("location");
        cnd.asc("path");
        return Result.success().addData(sysUnitService.query(cnd));
    }


    @At("/child")
    @Ok("json")
    @GET
    @SaCheckPermission("sys.manage.unit")
    @ApiOperation(name = "Vue2获取列表树型数据")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "pid", description = "父级ID")
            }
    )
    @ApiResponses
    public Result<?> getChild(@Param("pid") String pid, HttpServletRequest req) {
        List<Sys_unit> list = new ArrayList<>();
        List<NutMap> treeList = new ArrayList<>();
        if (StpUtil.hasRole(GlobalConstant.DEFAULT_SYSADMIN_ROLECODE)) {
            Cnd cnd = Cnd.NEW();
            if (Strings.isBlank(pid)) {
                cnd.and("parentId", "=", "").or("parentId", "is", null);
            } else {
                cnd.and("parentId", "=", pid);
            }
            cnd.asc("location").asc("path");
            list = sysUnitService.query(cnd);
        } else {
            //如果非超级管理员,则查询所在单位的所属分公司
            if (Strings.isBlank(pid)) {
                list = sysUnitService.query(Cnd.where("id", "=", sysUnitService.getMasterCompanyId(SecurityUtil.getUnitId())).asc("location").asc("path"));
            } else {
                Cnd cnd = Cnd.NEW();
                cnd.and("parentId", "=", pid);
                cnd.asc("location").asc("path");
                list = sysUnitService.query(cnd);
            }
        }
        for (Sys_unit unit : list) {
            NutMap map = Lang.obj2nutmap(unit);
            map.addv("expanded", false);
            map.addv("level1", Strings.isBlank(pid));
            map.addv("children", new ArrayList<>());
            map.addv("userNumber", sysUserService.count(Cnd.where("unitId", "=", unit.getId())));
            map.addv("allNumber", sysUserService.count(Cnd.where("unitPath", "like", unit.getPath() + "%")));
            treeList.add(map);
        }
        return Result.data(treeList);
    }


    @At("/create")
    @Ok("json")
    @POST
    @SaCheckPermission("sys.manage.unit.create")
    @SLog(value = "新建单位:${unit.name}")
    @ApiOperation(name = "新建单位")
    @ApiFormParams(
            implementation = Sys_unit.class
    )
    @ApiResponses
    public Result<?> create(@Param("..") Sys_unit unit, HttpServletRequest req) {
        unit.setCreatedBy(SecurityUtil.getUserId());
        sysUnitService.save(unit);
        return Result.success();
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @SaCheckPermission("sys.manage.unit.delete")
    @SLog(value = "删除单位:")
    @ApiOperation(name = "删除单位")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> delete(String id, HttpServletRequest req) {
        Sys_unit unit = sysUnitService.fetch(id);
        if (unit == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        req.setAttribute("_slog_msg", unit.getName());
        if (Strings.sNull(unit.getPath()).equals("0001")) {
            return Result.error("禁止删除");
        }
        sysUnitService.deleteAndChild(unit);
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json:{locked:'^(password|salt|mobile|email)$'}")
    @GET
    @SaCheckPermission("sys.manage.unit")
    @ApiOperation(name = "获取单位信息")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> getData(String id, HttpServletRequest req) {
        Sys_unit unit = sysUnitService.fetch(id);
        if (unit == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        List<Sys_unit_user> unitUserList = sysUnitUserService.query(Cnd.where("unitId", "=", id), "user");
        return Result.data(NutMap.NEW().addv("unit", unit).addv("unitUserList", unitUserList));
    }

    @At
    @Ok("json")
    @POST
    @SaCheckPermission("sys.manage.unit.update")
    @SLog(value = "修改单位:${unit.name}")
    @ApiOperation(name = "修改单位")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "leader", description = "本单位领导"),
                    @ApiFormParam(name = "higher", description = "上级主管领导"),
                    @ApiFormParam(name = "assigner", description = "上级分管领导")
            },
            implementation = Sys_unit.class
    )
    @ApiResponses
    public Result<?> update(@Param("..") Sys_unit unit, @Param("leader") String leader,
                            @Param("higher") String higher, @Param("assigner") String assigner, HttpServletRequest req) {
        unit.setUpdatedBy(SecurityUtil.getUserId());
        sysUnitService.update(unit, Strings.splitIgnoreBlank(leader), Strings.splitIgnoreBlank(higher), Strings.splitIgnoreBlank(assigner));
        return Result.success();
    }

    @At("/get_sort_tree")
    @Ok("json")
    @GET
    @SaCheckPermission("sys.manage.unit")
    @ApiOperation(name = "Vue2获取待排序数据")
    @ApiImplicitParams
    @ApiResponses
    public Result<?> getSortTree(HttpServletRequest req) {
        Cnd cnd = Cnd.NEW();
        if (!StpUtil.hasRole(GlobalConstant.DEFAULT_SYSADMIN_ROLECODE)) {
            Sys_unit unit = sysUnitService.fetch(SecurityUtil.getUnitId());
            cnd.and("path", "like", unit.getPath() + "%");
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
        return Result.data(getTree(nutMap, ""));
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

    @At("/sort")
    @Ok("json")
    @POST
    @SaCheckPermission("sys.manage.unit.update")
    @ApiOperation(name = "保存排序数据")
    @ApiFormParams({
            @ApiFormParam(name = "ids", description = "ids数组")
    })
    @ApiResponses
    public Result<?> sortDo(@Param("ids") String ids, HttpServletRequest req) {
        String[] unitIds = StringUtils.split(ids, ",");
        int i = 0;
        if (StpUtil.hasRole(GlobalConstant.DEFAULT_SYSADMIN_ROLECODE)) {
            sysUnitService.update(Chain.make("location", 0), Cnd.NEW());
        }
        for (String id : unitIds) {
            if (!Strings.isBlank(id)) {
                sysUnitService.update(Chain.make("location", i), Cnd.where("id", "=", id));
                i++;
            }
        }
        return Result.success();
    }

    @At("/search_user")
    @Ok("json")
    @POST
    @SaCheckPermission("sys.manage.unit")
    @ApiOperation(name = "查询单位用户")
    @ApiFormParams({
            @ApiFormParam(name = "query", description = "查询关键词"),
            @ApiFormParam(name = "unitId", description = "单位ID")
    })
    @ApiResponses
    public Result<?> searchUser(@Param("query") String query, @Param("unitId") String unitId, HttpServletRequest req) {
        return Result.data(sysUnitService.searchUser(query, unitId));
    }
}
