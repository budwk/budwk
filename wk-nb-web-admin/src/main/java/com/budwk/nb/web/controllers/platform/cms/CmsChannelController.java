package com.budwk.nb.web.controllers.platform.cms;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.cms.enums.CmsChannelType;
import com.budwk.nb.cms.models.Cms_channel;
import com.budwk.nb.cms.services.CmsChannelService;
import com.budwk.nb.cms.services.CmsSiteService;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
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
 * @author wizzer(wizzer.cn)
 * @date 2020/2/10
 */
@IocBean
@At("/api/{version}/platform/cms/channel")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "CMS_栏目管理")}, servers = {@Server(url = "/")})
public class CmsChannelController {
    private static final Log log = Logs.get();
    @Inject
    @Reference(check = false)
    private CmsChannelService cmsChannelService;
    @Inject
    @Reference(check = false)
    private CmsSiteService cmsSiteService;

    @At("/list_site")
    @GET
    @Ok("json:{actived:'code|msg|data|id|site_name',ignoreNull:true}")
    @RequiresAuthentication
    @Operation(
            tags = "CMS_栏目管理", summary = "获取站点列表",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
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
                                    "      \"id\": \"site\",\n" +
                                    "      \"site_name\": \"演示站点\"\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}"), mediaType = "application/json"))
            }
    )
    public Object listSite(HttpServletRequest req) {
        try {
            return Result.success().addData(cmsSiteService.query());
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/get_type")
    @Ok("json")
    @GET
    @RequiresAuthentication
    @Operation(
            tags = "CMS_栏目管理", summary = "获取栏目类型",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object getType() {
        try {
            return Result.success().addData(CmsChannelType.values());
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At("/child/{siteId}")
    @GET
    @Ok("json")
    @RequiresAuthentication
    @Operation(
            tags = "CMS_栏目管理", summary = "获取子级栏目树表数据",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "siteId", description = "站点ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
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
                    @ApiFormParam(name = "pid", example = "", description = "父级ID")
            }
    )
    public Object child(String siteId, @Param("pid") String pid, HttpServletRequest req) {
        try {
            List<Cms_channel> list = new ArrayList<>();
            List<NutMap> treeList = new ArrayList<>();
            Cnd cnd = Cnd.NEW();
            if (Strings.isBlank(pid)) {
                cnd.and(Cnd.exps("parentId", "=", "").or("parentId", "is", null));
            } else {
                cnd.and("parentId", "=", pid);
            }
            cnd.and("siteid", "=", siteId);
            cnd.asc("location").asc("path");
            list = cmsChannelService.query(cnd);
            for (Cms_channel channel : list) {
                if (cmsChannelService.count(Cnd.where("parentId", "=", channel.getId())) > 0) {
                    channel.setHasChildren(true);
                }
                NutMap map = Lang.obj2nutmap(channel);
                map.addv("expanded", false);
                map.addv("children", new ArrayList<>());
                treeList.add(map);
            }
            return Result.success().addData(treeList);
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/tree/{siteId}")
    @GET
    @Ok("json")
    @RequiresAuthentication
    @Operation(
            tags = "CMS_栏目管理", summary = "获取子级栏目树数据",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "siteId", description = "站点ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
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
                    @ApiFormParam(name = "pid", example = "", description = "父级ID")
            }
    )
    public Object tree(String siteId, @Param("pid") String pid, HttpServletRequest req) {
        try {
            List<NutMap> treeList = new ArrayList<>();
            if (Strings.isBlank(pid)) {
                NutMap root = NutMap.NEW().addv("value", "root").addv("label", Mvcs.getMessage(req, "system.commons.txt.parentRoot")).addv("leaf", true);
                treeList.add(root);
            }
            Cnd cnd = Cnd.NEW();
            if (Strings.isBlank(pid)) {
                cnd.and(Cnd.exps("parentId", "=", "").or("parentId", "is", null));
            } else {
                cnd.and("parentId", "=", pid);
            }
            cnd.and("siteid", "=", siteId);
            cnd.asc("location").asc("path");
            List<Cms_channel> list = cmsChannelService.query(cnd);
            for (Cms_channel menu : list) {
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

    @At("/create")
    @POST
    @Ok("json")
    @RequiresPermissions("cms.content.channel.create")
    @SLog(tag = "新建栏目", msg = "栏目名称:${channel.name}")
    @Operation(
            tags = "CMS_栏目管理", summary = "新建栏目",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.content.channel.create")
            },
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
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
                    @ApiFormParam(name = "parentId", example = "", description = "父级ID")
            },
            implementation = Cms_channel.class
    )
    public Object create(@Param("..") Cms_channel channel, @Param(value = "parentId", df = "") String parentId, HttpServletRequest req) {
        try {
            if ("root".equals(parentId)) {
                parentId = "";
            }
            int codeCount = cmsChannelService.count(Cnd.where("code", "=", Strings.sNull(channel.getCode())));
            if (codeCount > 0) {
                return Result.error("cms.content.channel.form.code.exist");
            }
            channel.setCreatedBy(StringUtil.getPlatformUid());
            channel.setUpdatedBy(StringUtil.getPlatformUid());
            cmsChannelService.save(channel, parentId);
            cmsChannelService.clearCache();
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/get/{id}")
    @GET
    @Ok("json")
    @RequiresPermissions("cms.content.channel")
    @Operation(
            tags = "CMS_栏目管理", summary = "获取栏目信息",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.content.channel")
            },
            parameters = {
                    @Parameter(name = "id", description = "栏目ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object get(String id, HttpServletRequest req) {
        try {
            Cms_channel channel = cmsChannelService.fetch(id);
            NutMap map = Lang.obj2nutmap(channel);
            map.put("parentName", "-");
            map.put("siteName", "-");
            if (Strings.isNotBlank(channel.getParentId())) {
                map.put("parentName", cmsChannelService.fetch(channel.getParentId()).getName());
            }
            if (Strings.isNotBlank(channel.getSiteid())) {
                map.put("siteName", cmsSiteService.fetch(channel.getSiteid()).getSite_name());
            }
            return Result.success().addData(map);
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/update")
    @POST
    @Ok("json")
    @RequiresPermissions("cms.content.channel.update")
    @SLog(tag = "修改栏目", msg = "栏目名称:${channel.name}")
    @Operation(
            tags = "CMS_栏目管理", summary = "修改栏目",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.content.channel.update")
            },
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Cms_channel.class
    )
    public Object update(@Param("..") Cms_channel channel, HttpServletRequest req) {
        try {
            Cms_channel dbChannel = cmsChannelService.fetch(channel.getId());
            if (!Strings.sNull(channel.getCode()).equals(dbChannel.getCode())) {
                int codeCount = cmsChannelService.count(Cnd.where("code", "=", Strings.sNull(channel.getCode())));
                if (codeCount > 0) {
                    return Result.error("cms.content.channel.form.code.exist");
                }
            }
            channel.setUpdatedBy(StringUtil.getPlatformUid());
            cmsChannelService.updateIgnoreNull(channel);
            cmsChannelService.clearCache();
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/delete/{id}")
    @DELETE
    @Ok("json")
    @RequiresPermissions("cms.content.channel.delete")
    @SLog(tag = "删除栏目")
    @Operation(
            tags = "CMS_栏目管理", summary = "删除栏目",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.content.channel.delete")
            },
            parameters = {
                    @Parameter(name = "id", description = "栏目ID", in = ParameterIn.PATH),
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
            Cms_channel channel = cmsChannelService.fetch(id);
            req.setAttribute("_slog_msg", String.format("栏目名称:%s", channel.getName()));
            cmsChannelService.deleteAndChild(channel);
            cmsChannelService.clearCache();
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/disabled")
    @Ok("json")
    @POST
    @RequiresPermissions("cms.content.channel.update")
    @SLog(tag = "启用禁用栏目")
    @Operation(
            tags = "CMS_栏目管理", summary = "启用禁用栏目",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.content.channel.update")
            },
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
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
                    @ApiFormParam(name = "disabled", description = "启用禁用", required = true, example = "true", type = "boolean")
            }
    )
    public Object changeDisabled(@Param("id") String id, @Param("disabled") boolean disabled, HttpServletRequest req) {
        try {
            int res = cmsChannelService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
            if (res > 0) {
                if (disabled) {
                    req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.off"));
                } else {
                    req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.on"));
                }
                cmsChannelService.clearCache();
                return Result.success();
            }
            return Result.error(500512, "system.fail");
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At("/get_sort_tree/{siteid}")
    @Ok("json")
    @GET
    @RequiresPermissions("cms.content.channel")
    @Operation(
            tags = "CMS_栏目管理", summary = "获取待排序栏目树",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.content.channel")
            },
            parameters = {
                    @Parameter(name = "siteid", description = "站点ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object getSortTree(String siteid, HttpServletRequest req) {
        try {
            List<Cms_channel> list = cmsChannelService.query(Cnd.where("siteid", "=", siteid).asc("location").asc("path"));
            NutMap nutMap = NutMap.NEW();
            for (Cms_channel channel : list) {
                List<Cms_channel> list1 = nutMap.getList(channel.getParentId(), Cms_channel.class);
                if (list1 == null) {
                    list1 = new ArrayList<>();
                }
                list1.add(channel);
                nutMap.put(Strings.sNull(channel.getParentId()), list1);
            }
            return Result.success().addData(getTree(nutMap, ""));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    private List<NutMap> getTree(NutMap nutMap, String pid) {
        List<NutMap> treeList = new ArrayList<>();
        List<Cms_channel> subList = nutMap.getList(pid, Cms_channel.class);
        for (Cms_channel channel : subList) {
            NutMap map = Lang.obj2nutmap(channel);
            map.put("label", channel.getName());
            if (channel.isHasChildren() || (nutMap.get(channel.getId()) != null)) {
                map.put("children", getTree(nutMap, channel.getId()));
            }
            treeList.add(map);
        }
        return treeList;
    }

    @At("/sort/{siteid}")
    @Ok("json")
    @POST
    @RequiresPermissions("cms.content.channel")
    @Operation(
            tags = "CMS_栏目管理", summary = "提交栏目排序数据",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.content.channel")
            },
            requestBody = @RequestBody(content = @Content()),
            parameters = {
                    @Parameter(name = "siteid", description = "站点ID", in = ParameterIn.PATH),
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
                    @ApiFormParam(name = "ids", description = "ID数组")
            }
    )
    public Object sortDo(String siteid, @Param("ids") String ids, HttpServletRequest req) {
        try {
            String[] unitIds = StringUtils.split(ids, ",");
            int i = 0;
            cmsChannelService.update(Chain.make("location", 0), Cnd.NEW());
            for (String id : unitIds) {
                if (!Strings.isBlank(id)) {
                    cmsChannelService.update(Chain.make("location", i), Cnd.where("id", "=", id).and("siteid", "=", siteid));
                    i++;
                }
            }
            cmsChannelService.clearCache();
            return Result.success();
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

}
