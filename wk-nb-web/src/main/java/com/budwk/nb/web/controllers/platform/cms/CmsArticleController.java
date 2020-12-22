package com.budwk.nb.web.controllers.platform.cms;

import com.budwk.nb.cms.models.Cms_article;
import com.budwk.nb.cms.models.Cms_channel;
import com.budwk.nb.cms.services.CmsArticleService;
import com.budwk.nb.cms.services.CmsChannelService;
import com.budwk.nb.base.annotation.SLog;
import com.budwk.nb.base.result.Result;
import com.budwk.nb.base.utils.PageUtil;
import com.budwk.nb.base.utils.StringUtil;
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
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/2
 */
@IocBean
@At("/api/{version}/platform/cms/article")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "CMS_文章管理")}, servers = {@Server(url = "/")})
public class CmsArticleController {
    private static final Log log = Logs.get();

    @Inject
    private CmsChannelService cmsChannelService;

    @Inject
    private CmsArticleService cmsArticleService;

    @At("/get_channel_tree/{siteid}")
    @Ok("json")
    @GET
    @RequiresAuthentication
    @Operation(
            tags = "CMS_文章管理", summary = "获取栏目树数据",
            security = {
                    @SecurityRequirement(name = "登陆认证")
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
    public Object getChannelTree(String siteid, HttpServletRequest req) {
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
            return Result.success().addData(getTree(nutMap, "", req));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    private List<NutMap> getTree(NutMap nutMap, String pid, HttpServletRequest req) {
        List<NutMap> treeList = new ArrayList<>();
        if (Strings.isBlank(pid)) {
            NutMap root = NutMap.NEW().addv("id", "root").addv("value", "root").addv("label", Mvcs.getMessage(req, "cms.content.article.form.allChannel")).addv("leaf", true);
            treeList.add(root);
        }
        List<Cms_channel> subList = nutMap.getList(pid, Cms_channel.class);
        for (Cms_channel channel : subList) {
            NutMap map = Lang.obj2nutmap(channel);
            map.put("label", channel.getName());
            if (channel.isHasChildren() || (nutMap.get(channel.getId()) != null)) {
                map.put("children", getTree(nutMap, channel.getId(), req));
            }
            treeList.add(map);
        }
        return treeList;
    }

    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @RequiresAuthentication
    @Operation(
            tags = "CMS_文章管理", summary = "分页查询文章",
            security = {
                    @SecurityRequirement(name = "登陆认证")
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
                    @ApiFormParam(name = "siteid", example = "", description = "站点ID", required = true),
                    @ApiFormParam(name = "channelId", example = "", description = "栏目ID"),
                    @ApiFormParam(name = "title", example = "", description = "标题"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Object list(@Param("siteid") String siteid, @Param("channelId") String channelId, @Param("title") String title, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            cnd.and("siteid", "=", siteid);
            if (!Strings.isBlank(channelId) && !"root".equals(channelId)) {
                cnd.and("channelId", "=", channelId);
            }
            if (!Strings.isBlank(title)) {
                cnd.and("title", "like", "%" + title + "%");
            }
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(cmsArticleService.listPage(pageNo, pageSize, cnd, "^(id|siteid|title|author|disabled|publishAt|endAt|location|view_num)$"));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At("/create")
    @POST
    @Ok("json")
    @RequiresPermissions("cms.content.article.create")
    @SLog(tag = "添加文章", msg = "文章标题:${article.title}")
    @AdaptBy(type = WhaleAdaptor.class)
    @Operation(
            tags = "CMS_文章管理", summary = "添加文章",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.content.article.create")
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
                    @ApiFormParam(name = "time_param", example = "[]", description = "发布起始时间"),
            },
            implementation = Cms_article.class
    )
    public Object create(@Param("..") Cms_article article, @Param("time_param") long[] time, HttpServletRequest req) {
        try {
            article.setPublishAt(time[0]);
            article.setEndAt(time[1]);
            article.setStatus(0);
            article.setCreatedBy(StringUtil.getPlatformUid());
            article.setUpdatedBy(StringUtil.getPlatformUid());
            cmsArticleService.insert(article);
            cmsArticleService.clearCache();
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/disabled")
    @Ok("json")
    @POST
    @RequiresPermissions("cms.content.article.update")
    @SLog(tag = "启用禁用文章")
    @Operation(
            tags = "CMS_文章管理", summary = "启用禁用文章",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.content.article.update")
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
                    @ApiFormParam(name = "id", description = "主键", required = true),
                    @ApiFormParam(name = "disabled", description = "启用禁用", required = true, example = "true", type = "boolean")
            }
    )
    public Object changeDisabled(@Param("id") String id, @Param("disabled") boolean disabled, HttpServletRequest req) {
        try {
            int res = cmsArticleService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
            if (res > 0) {
                if (disabled) {
                    req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.off"));
                } else {
                    req.setAttribute("_slog_msg", Mvcs.getMessage(req, "system.commons.txt.disabled.on"));
                }
                cmsArticleService.clearCache();
                return Result.success();
            }
            return Result.error(500512, "system.fail");
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At("/delete_more")
    @Ok("json")
    @POST
    @RequiresPermissions("cms.content.article.delete")
    @SLog(tag = "批量删除文章")
    @Operation(
            tags = "CMS_文章管理", summary = "批量删除文章",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.content.article.delete")
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
                    @ApiFormParam(name = "ids", example = "a,b", description = "用户ID数组", required = true),
                    @ApiFormParam(name = "names", example = "a,b", description = "文章标题数组", required = true)
            }
    )
    public Object deleteMore(@Param("ids") String[] ids, @Param("titles") String titles, HttpServletRequest req) {
        try {
            if (ids == null) {
                return Result.error("system.error.invalid");
            }
            cmsArticleService.delete(ids);
            cmsArticleService.clearCache();
            req.setAttribute("_slog_msg", titles);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @RequiresPermissions("cms.content.article.delete")
    @SLog(tag = "删除文章")
    @Operation(
            tags = "CMS_文章管理", summary = "删除文章",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.content.article.delete")
            },
            parameters = {
                    @Parameter(name = "id", description = "文章ID", in = ParameterIn.PATH),
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
            Cms_article article = cmsArticleService.fetch(id);
            if (article == null) {
                return Result.error("system.error.noData");
            }
            cmsArticleService.delete(id);
            cmsArticleService.clearCache();
            req.setAttribute("_slog_msg", String.format("%s", article.getTitle()));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @RequiresAuthentication
    @Operation(
            tags = "CMS_文章管理", summary = "获取文章",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "id", description = "用户ID", in = ParameterIn.PATH)
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
            Cms_article article = cmsArticleService.fetch(id);
            if (article == null) {
                return Result.error("system.error.noData");
            }
            return Result.success().addData(article);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }


    @At("/update")
    @POST
    @Ok("json")
    @RequiresPermissions("cms.content.article.update")
    @SLog(tag = "修改文章", msg = "文章标题:${article.title}")
    @AdaptBy(type = WhaleAdaptor.class)
    @Operation(
            tags = "CMS_文章管理", summary = "修改文章",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "cms.content.article.update")
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
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "time_param", example = "[]", description = "发布起始时间"),
            },
            implementation = Cms_article.class
    )
    public Object update(@Param("..") Cms_article article, @Param("time_param") long[] time, HttpServletRequest req) {
        try {
            article.setPublishAt(time[0]);
            article.setEndAt(time[1]);
            article.setStatus(0);
            article.setUpdatedBy(StringUtil.getPlatformUid());
            cmsArticleService.updateIgnoreNull(article);
            cmsArticleService.clearCache();
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }
    }

}
