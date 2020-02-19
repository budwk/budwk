package com.budwk.nb.web.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.constants.PlatformConstant;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.sys.models.Sys_unit;
import com.budwk.nb.sys.models.Sys_user;
import com.budwk.nb.sys.services.SysUnitService;
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
 * @author wizzer(wizzer @ qq.com) on 2019/11/21
 */
@IocBean
@At("/api/{version}/platform/sys/unit")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "系统_单位管理")}, servers = {@Server(url = "/")})
public class SysUnitController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysUnitService sysUnitService;
    @Inject
    private ShiroUtil shiroUtil;

    @At("/child")
    @Ok("json")
    @GET
    @RequiresAuthentication
    @Operation(
            tags = "系统_单位管理", summary = "获取子级单位",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "pid", description = "父级ID", required = false, in = ParameterIn.QUERY)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(example = "{\n" +
                                    "  \"code\": 0,\n" +
                                    "  \"msg\": \"操作成功\",\n" +
                                    "  \"data\": [\n" +
                                    "    {\n" +
                                    "      \"id\": \"5fb661b395ab42bea45a56ba73245a64\",\n" +
                                    "      \"parentId\": \"\",\n" +
                                    "      \"path\": \"0001\",\n" +
                                    "      \"name\": \"系统管理\",\n" +
                                    "      \"aliasName\": \"System\",\n" +
                                    "      \"address\": \"银河-太阳系-地球\",\n" +
                                    "      \"telephone\": \"\",\n" +
                                    "      \"email\": \"wizzer@qq.com\",\n" +
                                    "      \"website\": \"https://wizzer.cn\",\n" +
                                    "      \"location\": 0,\n" +
                                    "      \"hasChildren\": false,\n" +
                                    "      \"createdBy\": \"\",\n" +
                                    "      \"createdAt\": 1573701338314,\n" +
                                    "      \"updatedBy\": \"\",\n" +
                                    "      \"updatedAt\": 1573701338314,\n" +
                                    "      \"delFlag\": false,\n" +
                                    "      \"expanded\": false,\n" +
                                    "      \"children\": []\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}"), mediaType = "application/json"))
            }
    )
    public Object getChild(@Param("pid") String pid, HttpServletRequest req) {
        List<Sys_unit> list = new ArrayList<>();
        List<NutMap> treeList = new ArrayList<>();
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
            NutMap map = Lang.obj2nutmap(unit);
            map.addv("expanded", false);
            map.addv("children", new ArrayList<>());
            treeList.add(map);
        }
        return Result.success().addData(treeList);
    }


    @At("/tree")
    @Ok("json")
    @GET
    @RequiresAuthentication
    @Operation(
            tags = "系统_单位管理", summary = "获取单位树形数据",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "pid", description = "父级ID", required = false, in = ParameterIn.QUERY)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(example = "{\n" +
                                    "  \"code\": 0,\n" +
                                    "  \"msg\": \"操作成功\",\n" +
                                    "  \"data\": [\n" +
                                    "    {\n" +
                                    "      \"value\": \"root\",\n" +
                                    "      \"label\": \"不选择单位\",\n" +
                                    "      \"leaf\": true\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "      \"value\": \"5fb661b395ab42bea45a56ba73245a64\",\n" +
                                    "      \"label\": \"系统管理\",\n" +
                                    "      \"leaf\": true\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}"), mediaType = "application/json"))
            }
    )
    public Object getTree(@Param("pid") String pid, HttpServletRequest req) {
        try {
            List<Sys_unit> list = new ArrayList<>();
            List<NutMap> treeList = new ArrayList<>();
            if (Strings.isBlank(pid)) {
                NutMap root = NutMap.NEW().addv("value", "root").addv("label", Mvcs.getMessage(req, "sys.manage.unit.form.noselect")).addv("leaf", true);
                treeList.add(root);
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
    @RequiresPermissions("sys.manage.unit.create")
    @SLog(tag = "新增单位", msg = "单位名称:${unit.name}")
    @Operation(
            tags = "系统_单位管理", summary = "新增单位",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.unit.create")
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
                    @ApiFormParam(name = "parentId",description = "父级ID")
            },
            implementation = Sys_unit.class
    )
    public Object create(@Param("..") Sys_unit unit, @Param("parentId") String parentId, HttpServletRequest req) {
        try {
            if ("root".equals(parentId) || parentId == null) {
                parentId = "";
            }
            if (!shiroUtil.hasRole(PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME) && Strings.isBlank(parentId)) {
                return Result.error("system.error.invalid");
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

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @RequiresPermissions("sys.manage.unit.delete")
    @SLog(tag = "删除单位")
    @Operation(
            tags = "系统_单位管理", summary = "删除单位",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.unit.delete")
            },
            parameters = {
                    @Parameter(name = "id", description = "单位ID", required = true, in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
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

    @At("/get/{id}")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.unit")
    @Operation(
            tags = "系统_单位管理", summary = "查询单位",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.unit")
            },
            parameters = {
                    @Parameter(name = "id", description = "单位ID", required = true, in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(example = "{\n" +
                                    "  \"code\": 0,\n" +
                                    "  \"msg\": \"操作成功\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"id\": \"5fb661b395ab42bea45a56ba73245a64\",\n" +
                                    "    \"parentId\": \"\",\n" +
                                    "    \"path\": \"0001\",\n" +
                                    "    \"name\": \"系统管理\",\n" +
                                    "    \"aliasName\": \"System\",\n" +
                                    "    \"address\": \"银河-太阳系-地球\",\n" +
                                    "    \"telephone\": \"\",\n" +
                                    "    \"email\": \"wizzer@qq.com\",\n" +
                                    "    \"website\": \"https://wizzer.cn\",\n" +
                                    "    \"location\": 0,\n" +
                                    "    \"hasChildren\": false,\n" +
                                    "    \"createdBy\": \"\",\n" +
                                    "    \"createdAt\": 1573701338314,\n" +
                                    "    \"updatedBy\": \"\",\n" +
                                    "    \"updatedAt\": 1573701338314,\n" +
                                    "    \"delFlag\": false\n" +
                                    "  }\n" +
                                    "}"), mediaType = "application/json"))
            }
    )
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

    @At
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.unit.update")
    @SLog(tag = "修改单位", msg = "单位名称:${unit.name}")
    @Operation(
            tags = "系统_单位管理", summary = "修改单位",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.unit.update")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Sys_unit.class
    )
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


    @At("/get_sort_tree")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.manage.unit")
    @Operation(
            tags = "系统_单位管理", summary = "获取排序所用数据",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.unit")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(example = "{\n" +
                                    "  \"code\": 0,\n" +
                                    "  \"msg\": \"操作成功\",\n" +
                                    "  \"data\": [\n" +
                                    "    {\n" +
                                    "      \"id\": \"5fb661b395ab42bea45a56ba73245a64\",\n" +
                                    "      \"parentId\": \"\",\n" +
                                    "      \"path\": \"0001\",\n" +
                                    "      \"name\": \"系统管理\",\n" +
                                    "      \"aliasName\": \"System\",\n" +
                                    "      \"address\": \"银河-太阳系-地球\",\n" +
                                    "      \"telephone\": \"\",\n" +
                                    "      \"email\": \"wizzer@qq.com\",\n" +
                                    "      \"website\": \"https://wizzer.cn\",\n" +
                                    "      \"location\": 0,\n" +
                                    "      \"hasChildren\": false,\n" +
                                    "      \"createdBy\": \"\",\n" +
                                    "      \"createdAt\": 1573701338314,\n" +
                                    "      \"updatedBy\": \"\",\n" +
                                    "      \"updatedAt\": 1573701338314,\n" +
                                    "      \"delFlag\": false,\n" +
                                    "      \"label\": \"系统管理\"\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "      \"id\": \"2ffbe81b95ce40049159576fe89ab775\",\n" +
                                    "      \"parentId\": \"\",\n" +
                                    "      \"path\": \"0002\",\n" +
                                    "      \"name\": \"萌发开源\",\n" +
                                    "      \"website\": \"https://budwk.com\",\n" +
                                    "      \"location\": 0,\n" +
                                    "      \"hasChildren\": true,\n" +
                                    "      \"createdBy\": \"5ff0e61b22254ce1ab11eff1f68b391b\",\n" +
                                    "      \"createdAt\": 1579140419646,\n" +
                                    "      \"updatedBy\": \"5ff0e61b22254ce1ab11eff1f68b391b\",\n" +
                                    "      \"updatedAt\": 1579140447604,\n" +
                                    "      \"delFlag\": false,\n" +
                                    "      \"label\": \"萌发开源\",\n" +
                                    "      \"children\": [\n" +
                                    "        {\n" +
                                    "          \"id\": \"2acaef467bdf46d6ad98df5d0500e70f\",\n" +
                                    "          \"parentId\": \"2ffbe81b95ce40049159576fe89ab775\",\n" +
                                    "          \"path\": \"00020001\",\n" +
                                    "          \"name\": \"大鲨鱼科技有限公司\",\n" +
                                    "          \"note\": \"\",\n" +
                                    "          \"location\": 0,\n" +
                                    "          \"hasChildren\": false,\n" +
                                    "          \"createdBy\": \"5ff0e61b22254ce1ab11eff1f68b391b\",\n" +
                                    "          \"createdAt\": 1579140435410,\n" +
                                    "          \"updatedBy\": \"5ff0e61b22254ce1ab11eff1f68b391b\",\n" +
                                    "          \"updatedAt\": 1579140435410,\n" +
                                    "          \"delFlag\": false,\n" +
                                    "          \"label\": \"大鲨鱼科技有限公司\"\n" +
                                    "        }\n" +
                                    "      ]\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}"), mediaType = "application/json"))
            }
    )
    public Object getSortTree(HttpServletRequest req) {
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

    @At("/sort")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.manage.unit.update")
    @Operation(
            tags = "系统_单位管理", summary = "提交排序后的数据",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.manage.unit.update")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    // 当表单参数不是类对象时,使用 @ApiFormParams 定义,不在 @RequestBody
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "ids", example = "a,b", description = "单位ID数组", required = true)
            }
    )
    public Object sortDo(@Param("ids") String ids, HttpServletRequest req) {
        try {
            String[] unitIds = StringUtils.split(ids, ",");
            int i = 0;
            if (shiroUtil.hasRole(PlatformConstant.PLATFORM_ROLE_SYSADMIN_NAME)) {
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
