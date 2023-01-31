package com.budwk.app.cms.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.cms.models.Cms_article;
import com.budwk.app.cms.models.Cms_channel;
import com.budwk.app.cms.services.CmsArticleService;
import com.budwk.app.cms.services.CmsChannelService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.page.PageUtil;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
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
@At("/cms/admin/article")
@SLog(tag = "CMS文章管理")
@ApiDefinition(tag = "CMS文章管理")
@Slf4j
public class CmsArticleController {

    @Inject
    private CmsChannelService cmsChannelService;

    @Inject
    private CmsArticleService cmsArticleService;

    @At("/get_channel_list/{siteId}")
    @Ok("json")
    @GET
    @ApiOperation(name = "Vue3获取栏目树数据")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "siteId", example = "", in = ParamIn.PATH, description = "所属站点", required = true, check = true)
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckLogin
    public Result<?> list(String siteId) {
        Cnd cnd = Cnd.NEW();
        cnd.and("siteId", "=", siteId);
        cnd.asc("location");
        cnd.asc("path");
        return Result.success().addData(cmsChannelService.query(cnd));
    }

    @At("/get_channel_tree/{siteId}")
    @Ok("json")
    @GET
    @SaCheckLogin
    @ApiOperation(name = "Vue2获取栏目树数据")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "siteId", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> getChannelTree(String siteId, HttpServletRequest req) {
        List<Cms_channel> list = cmsChannelService.query(Cnd.where("siteId", "=", siteId).asc("location").asc("path"));
        NutMap nutMap = NutMap.NEW();
        for (Cms_channel channel : list) {
            List<Cms_channel> list1 = nutMap.getList(channel.getParentId(), Cms_channel.class);
            if (list1 == null) {
                list1 = new ArrayList<>();
            }
            list1.add(channel);
            nutMap.put(Strings.sNull(channel.getParentId()), list1);
        }
        return Result.data(getTree(nutMap, "", req));
    }

    private List<NutMap> getTree(NutMap nutMap, String pid, HttpServletRequest req) {
        List<NutMap> treeList = new ArrayList<>();
        if (Strings.isBlank(pid)) {
            NutMap root = NutMap.NEW().addv("id", "root").addv("value", "root").addv("label", "全部栏目").addv("leaf", true);
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
    @Ok("json:{locked:'^(password|salt)$',ignoreNull:false}")
    @SaCheckLogin
    @ApiOperation(name = "文章列表")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "siteId", description = "站点ID", required = true),
                    @ApiFormParam(name = "channelId", description = "栏目ID"),
                    @ApiFormParam(name = "title", description = "标题"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses(implementation = Cms_article.class)
    public Result<?> list(@Param("siteId") String siteId, @Param("channelId") String channelId, @Param("title") String title, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        cnd.and("siteId", "=", siteId);
        if (!Strings.isBlank(channelId) && !"root".equals(channelId)) {
            cnd.and("channelId", "=", channelId);
        }
        if (!Strings.isBlank(title)) {
            cnd.and("title", "like", "%" + title + "%");
        }
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.data(cmsArticleService.listPage(pageNo, pageSize, cnd, "^(id|siteId|title|author|disabled|publishAt|endAt|location|viewNum)$"));

    }

    @At("/create")
    @POST
    @Ok("json")
    @SaCheckPermission("cms.content.article.create")
    @SLog(value = "新增文章:${article.title}")
    @AdaptBy(type = WhaleAdaptor.class)
    @ApiOperation(name = "新增文章")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "time_param", example = "[]", description = "发布起始时间"),
            },
            implementation = Cms_article.class
    )
    @ApiResponses
    public Result<?> create(@Param("..") Cms_article article, @Param("time_param") long[] time, HttpServletRequest req) {
        article.setStatus(0);
        article.setCreatedBy(SecurityUtil.getUserId());
        article.setUpdatedBy(SecurityUtil.getUserId());
        cmsArticleService.insert(article);
        cmsArticleService.cacheClear();
        return Result.success();
    }

    @At("/disabled")
    @Ok("json")
    @POST
    @SaCheckPermission("cms.content.article.update")
    @SLog(value = "启用禁用文章：")
    @ApiOperation(name = "启用禁用文章")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "id", description = "主键", required = true),
                    @ApiFormParam(name = "disabled", description = "启用禁用", required = true, example = "true", type = "boolean")
            }
    )
    @ApiResponses
    public Result<?> changeDisabled(@Param("id") String id, @Param("disabled") boolean disabled, HttpServletRequest req) {
        Cms_article article = cmsArticleService.fetch(id);
        int res = cmsArticleService.update(Chain.make("disabled", disabled), Cnd.where("id", "=", id));
        if (res > 0) {
            if (disabled) {
                req.setAttribute("_slog_msg", String.format("%s 禁用", article.getTitle()));
            } else {
                req.setAttribute("_slog_msg", String.format("%s 启用", article.getTitle()));
            }
            cmsArticleService.cacheClear();
            return Result.success();
        }
        return Result.error(ResultCode.SERVER_ERROR);
    }

    @At("/delete_more")
    @Ok("json")
    @POST
    @SaCheckPermission("cms.content.article.delete")
    @SLog(value = "批量删除文章:")
    @ApiOperation(name = "批量删除文章")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "ids", example = "a,b", description = "用户ID数组", required = true),
                    @ApiFormParam(name = "names", example = "a,b", description = "文章标题数组", required = true)
            }
    )
    @ApiResponses
    public Result<?> deleteMore(@Param("ids") String[] ids, @Param("titles") String titles, HttpServletRequest req) {
        if (ids == null) {
            return Result.error(ResultCode.PARAM_ERROR);
        }
        cmsArticleService.delete(ids);
        cmsArticleService.cacheClear();
        req.setAttribute("_slog_msg", titles);
        return Result.success();
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @SaCheckPermission("cms.content.article.delete")
    @SLog(value = "删除文章:")
    @ApiOperation(name = "删除文章")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", in = ParamIn.PATH, description = "ID", check = true, required = true)
            }
    )
    @ApiResponses
    public Result<?> delete(String id, HttpServletRequest req) {
        Cms_article article = cmsArticleService.fetch(id);
        if (article == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        cmsArticleService.delete(id);
        cmsArticleService.cacheClear();
        req.setAttribute("_slog_msg", String.format("%s", article.getTitle()));
        return Result.success();
    }

    @At("/get/{id}")
    @Ok("json")
    @GET
    @SaCheckLogin
    @ApiOperation(name = "查看文章")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "文章ID", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses(
            implementation = Cms_article.class
    )
    public Result<?> getData(String id, HttpServletRequest req) {
        Cms_article article = cmsArticleService.fetch(id);
        if (article == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        return Result.data(article);
    }


    @At("/update")
    @POST
    @Ok("json")
    @SaCheckPermission("cms.content.article.update")
    @SLog(value = "修改文章:${article.title}")
    @AdaptBy(type = WhaleAdaptor.class)
    @ApiOperation(name = "修改文章")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "time_param", example = "[]", description = "发布起始时间"),
            },
            implementation = Cms_article.class
    )
    @ApiResponses
    public Result<?> update(@Param("..") Cms_article article, @Param("time_param") long[] time, HttpServletRequest req) {
        article.setStatus(0);
        article.setUpdatedBy(SecurityUtil.getUserId());
        cmsArticleService.updateIgnoreNull(article);
        cmsArticleService.cacheClear();
        return Result.success();
    }

}
