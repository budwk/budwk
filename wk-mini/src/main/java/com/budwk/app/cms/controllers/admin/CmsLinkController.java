package com.budwk.app.cms.controllers.admin;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.cms.models.Cms_link;
import com.budwk.app.cms.services.CmsLinkClassService;
import com.budwk.app.cms.services.CmsLinkService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.page.PageUtil;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/3
 */
@IocBean
@At("/cms/admin/links/link")
@SLog(tag = "CMS链接管理")
@ApiDefinition(tag = "CMS链接管理")
@Slf4j
public class CmsLinkController {

    @Inject
    private CmsLinkClassService cmsLinkClassService;

    @Inject
    private CmsLinkService cmsLinkService;

    @At("/list_class")
    @GET
    @Ok("json:{actived:'code|msg|data|id|name',ignoreNull:true}")
    @SaCheckLogin
    @ApiOperation(name = "获取分类列表")
    @ApiResponses
    public Result<?> listClass(HttpServletRequest req) {
        return Result.data(cmsLinkClassService.query());
    }

    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @SaCheckPermission("cms.links.link")
    @ApiOperation(name = "分页查询链接")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "classId", example = "", description = "分类ID"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses(implementation = Cms_link.class)
    public Result<?> list(@Param("classId") String classId, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(classId)) {
            cnd.and("classId", "=", classId);
        }
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.data(cmsLinkService.listPage(pageNo, pageSize, cnd));
    }

    @At("/create")
    @POST
    @Ok("json")
    @SaCheckPermission("cms.links.link.create")
    @SLog(value = "新增链接:${link.name}")
    @ApiOperation(name = "新增链接")
    @ApiFormParams(
            implementation = Cms_link.class
    )
    @ApiResponses
    public Result<?> create(@Param("..") Cms_link link, HttpServletRequest req) {
        link.setCreatedBy(SecurityUtil.getUserId());
        link.setUpdatedBy(SecurityUtil.getUserId());
        cmsLinkService.insert(link);
        return Result.success();
    }

    @At("/update")
    @POST
    @Ok("json")
    @SaCheckPermission("cms.links.link.update")
    @SLog(value = "修改链接:${link.name}")
    @ApiOperation(name = "修改链接")
    @ApiFormParams(
            implementation = Cms_link.class
    )
    @ApiResponses
    public Result<?> update(@Param("..") Cms_link link, HttpServletRequest req) {
        link.setUpdatedBy(SecurityUtil.getUserId());
        cmsLinkService.updateIgnoreNull(link);
        return Result.success();
    }

    @At("/get/{id}")
    @GET
    @Ok("json")
    @SaCheckLogin
    @ApiOperation(name = "获取链接信息")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses(implementation = Cms_link.class)
    public Result<?> get(String id, HttpServletRequest req) {
        return Result.success(cmsLinkService.fetch(id));
    }

    @At("/delete_more")
    @Ok("json")
    @POST
    @SaCheckPermission("cms.links.link.delete")
    @SLog(value = "批量删除链接")
    @ApiOperation(name = "批量删除链接")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "ids", example = "a,b", description = "链接ID数组", required = true),
                    @ApiFormParam(name = "names", example = "a,b", description = "链接名称数组", required = true)
            }
    )
    @ApiResponses
    public Result<?> deleteMore(@Param("ids") String[] ids, @Param("names") String names, HttpServletRequest req) {
        if (ids == null) {
            return Result.error(ResultCode.PARAM_ERROR);
        }
        cmsLinkService.delete(ids);
        req.setAttribute("_slog_msg", names);
        return Result.success();
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @SaCheckPermission("cms.links.link.delete")
    @SLog(value = "删除链接")
    @ApiOperation(name = "删除链接")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> delete(String id, HttpServletRequest req) {
        Cms_link link = cmsLinkService.fetch(id);
        if (link == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        cmsLinkService.delete(id);
        req.setAttribute("_slog_msg", String.format("%s", link.getName()));
        return Result.success();
    }
}
