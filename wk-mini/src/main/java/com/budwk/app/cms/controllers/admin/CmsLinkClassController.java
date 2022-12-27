package com.budwk.app.cms.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.cms.models.Cms_link_class;
import com.budwk.app.cms.services.CmsLinkClassService;
import com.budwk.app.cms.services.CmsLinkService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.page.PageUtil;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@At("/cms/admin/links/class")
@ApiDefinition(tag = "CMS链接分类")
@SLog(tag = "CMS链接分类")
@Slf4j
public class CmsLinkClassController {

    @Inject
    private CmsLinkClassService cmsLinkClassService;

    @Inject
    private CmsLinkService cmsLinkService;

    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @SaCheckPermission("cms.links.class")
    @ApiOperation(name = "分页查询链接分类")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses(
            implementation = Cms_link_class.class
    )
    public Result<?> list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.data(cmsLinkClassService.listPage(pageNo, pageSize, cnd));
    }

    @At("/create")
    @POST
    @Ok("json")
    @SaCheckPermission("cms.links.class.create")
    @SLog(value = "新增链接分类:${linkClass.name}")
    @ApiOperation(name = "新增链接分类")
    @ApiFormParams(
            implementation = Cms_link_class.class
    )
    @ApiResponses
    public Result<?> create(@Param("..") Cms_link_class linkClass, HttpServletRequest req) {
        int codeCount = cmsLinkClassService.count(Cnd.where("code", "=", Strings.sNull(linkClass.getCode())));
        if (codeCount > 0) {
            return Result.error(ResultCode.FAILURE);
        }
        linkClass.setCreatedBy(SecurityUtil.getUserId());
        linkClass.setUpdatedBy(SecurityUtil.getUserId());
        cmsLinkClassService.insert(linkClass);
        return Result.success();
    }

    @At("/update")
    @POST
    @Ok("json")
    @SaCheckPermission("cms.links.class.update")
    @SLog(value = "修改链接分类:${linkClass.name}")
    @ApiOperation(name = "修改链接分类")
    @ApiFormParams(
            implementation = Cms_link_class.class
    )
    @ApiResponses
    public Result<?> update(@Param("..") Cms_link_class linkClass, HttpServletRequest req) {
        Cms_link_class dbClass = cmsLinkClassService.fetch(linkClass.getId());
        if (!Strings.sNull(linkClass.getCode()).equals(dbClass.getCode())) {
            int codeCount = cmsLinkClassService.count(Cnd.where("code", "=", Strings.sNull(linkClass.getCode())));
            if (codeCount > 0) {
                return Result.error(ResultCode.SERVER_ERROR);
            }
        }
        linkClass.setUpdatedBy(SecurityUtil.getUserId());
        cmsLinkClassService.updateIgnoreNull(linkClass);
        return Result.success();
    }

    @At("/get/{id}")
    @GET
    @Ok("json")
    @SaCheckLogin
    @ApiOperation(name = "获取链接分类信息")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH, check = true, required = true)
            }
    )
    @ApiResponse
    public Result<?> get(String id, HttpServletRequest req) {
        return Result.data(cmsLinkClassService.fetch(id));
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @SaCheckPermission("cms.links.class.delete")
    @SLog(value = "删除链接分类")
    @ApiOperation(name = "删除链接分类")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH, check = true, required = true)
            }
    )
    @ApiResponses
    public Result<?> delete(String id, HttpServletRequest req) {
        Cms_link_class linkClass = cmsLinkClassService.fetch(id);
        if (linkClass == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        cmsLinkClassService.delete(id);
        cmsLinkService.clear(Cnd.where("classId", "=", id));
        req.setAttribute("_slog_msg", String.format("%s", linkClass.getName()));
        return Result.success();
    }
}
