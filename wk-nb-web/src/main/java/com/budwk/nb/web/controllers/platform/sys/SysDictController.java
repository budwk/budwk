package com.budwk.nb.web.controllers.platform.sys;

import com.budwk.nb.base.annotation.SLog;
import com.budwk.nb.base.result.Result;
import com.budwk.nb.base.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.sys.models.Sys_dict;
import com.budwk.nb.sys.services.SysDictService;
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
 * @author wizzer(wizzer.cn) on 2019/11/26
 */
@IocBean
@At("/api/{version}/platform/sys/dict")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "系统_字典管理")}, servers = @Server(url = "/"))
public class SysDictController {
    private static final Log log = Logs.get();
    @Inject
    private SysDictService sysDictService;

    @At("/child")
    @Ok("json")
    @GET
    @RequiresAuthentication
    @Operation(
            tags = "系统_字典管理", summary = "获取字典子级",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "pid", description = "父级ID", in = ParameterIn.QUERY),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
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

    @At("/tree")
    @Ok("json")
    @GET
    @RequiresAuthentication
    @Operation(
            tags = "系统_字典管理", summary = "获取字典子级树",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "pid", description = "父级ID", in = ParameterIn.QUERY),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
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

    @At("/create")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.config.dict.create")
    @SLog(tag = "新增字典", msg = "字典名称:${unit.name}")
    @Operation(
            tags = "系统_字典管理", summary = "新增字典",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.config.dict.create")
            },
            requestBody = @RequestBody(content = @Content()),
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "parentId", description = "父级ID")
            },
            implementation = Sys_dict.class
    )
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

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @RequiresPermissions("sys.config.dict.delete")
    @SLog(tag = "删除字典")
    @Operation(
            tags = "系统_字典管理", summary = "删除字典",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.config.dict.delete")
            },
            parameters = {
                    @Parameter(name = "id", description = "主键ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
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

    @At("/get/{id}")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.config.dict")
    @Operation(
            tags = "系统_字典管理", summary = "获取字典内容",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.config.dict")
            },
            parameters = {
                    @Parameter(name = "id", description = "主键ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
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

    @At
    @Ok("json")
    @POST
    @RequiresPermissions("sys.config.dict.update")
    @SLog(tag = "修改字典", msg = "单位名称:${dict.name}")
    @Operation(
            tags = "系统_字典管理", summary = "修改字典内容",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.config.dict.update")
            },
            requestBody = @RequestBody(content = @Content()),
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Sys_dict.class
    )
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

    @At("/get_sort_tree")
    @Ok("json")
    @GET
    @RequiresPermissions("sys.config.dict")
    @Operation(
            tags = "系统_字典管理", summary = "获取待排序字典树",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.config.dict")
            },
            requestBody = @RequestBody(content = @Content()),
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
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

    @At("/sort")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.config.dict.update")
    @Operation(
            tags = "系统_字典管理", summary = "提交字典排序数据",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.config.dict.update")
            },
            requestBody = @RequestBody(content = @Content()),
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "ids", description = "字典ID数组")
            }
    )
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

    @At("/disabled")
    @Ok("json")
    @POST
    @RequiresPermissions("sys.config.dict.update")
    @SLog(tag = "启用禁用字典")
    @Operation(
            tags = "系统_字典管理", summary = "启用禁用字典",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "sys.config.dict.update")
            },
            requestBody = @RequestBody(content = @Content()),
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "id", description = "字典主键", required = true),
                    @ApiFormParam(name = "code", description = "字典PATH", required = true),
                    @ApiFormParam(name = "disabled", description = "启用禁用", required = true, example = "true", type = "boolean")
            }
    )
    public Object changeDisabled(@Param("id") String id, @Param("path") String path, @Param("disabled") boolean disabled, HttpServletRequest req) {
        try {
            int res = sysDictService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
            if (res > 0) {
                if (disabled) {
                    req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.off"));
                } else {
                    req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.on"));
                }
                sysDictService.clearCache();
                return Result.success();
            }
            return Result.error(500512, "system.fail");
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }
}
