package com.budwk.app.cms.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.cms.models.Cms_site;
import com.budwk.app.cms.services.CmsSiteService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.page.PageUtil;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * @author wizzer(wizzer.cn)
 * @date 2020/2/10
 */
@IocBean
@At("/cms/admin/site")
@SLog(tag = "CMS站点管理")
@ApiDefinition(tag = "CMS站点管理")
public class CmsSiteController {
    @Inject
    private CmsSiteService cmsSiteService;

    @At
    @POST
    @Ok("json:full")
    @SaCheckPermission("cms.sites.site")
    @ApiOperation(name = "站点列表")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Result<?> list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        cnd.and("delFlag", "=", false);
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        Pagination pagination = cmsSiteService.listPage(pageNo, pageSize, cnd);
        return Result.success(pagination);
    }

    @At
    @POST
    @Ok("json")
    @SaCheckPermission("cms.sites.site.create")
    @SLog(value = "新增站点:${site.name}")
    @ApiOperation(name = "新增站点")
    @ApiFormParams(
            implementation = Cms_site.class
    )
    @ApiResponses
    public Result<?> create(@Param("..") Cms_site site, HttpServletRequest req) {
        site.setCreatedBy(SecurityUtil.getUserId());
        if (cmsSiteService.count(Cnd.where("id", "=", site.getId())) > 0) {
            return Result.error("站点已存在");
        }
        cmsSiteService.insert(site);
        cmsSiteService.cacheClear();
        return Result.success();
    }

    @At
    @POST
    @Ok("json")
    @SaCheckPermission("cms.sites.site.update")
    @SLog(value = "修改站点:${site.name}")
    @ApiOperation(name = "修改站点")
    @ApiFormParams(
            implementation = Cms_site.class
    )
    @ApiResponses
    public Result<?> update(@Param("..") Cms_site site, HttpServletRequest req) {
        site.setUpdatedBy(SecurityUtil.getUserId());
        cmsSiteService.updateIgnoreNull(site);
        cmsSiteService.cacheClear();
        return Result.success();
    }


    @At("/delete/{id}")
    @DELETE
    @Ok("json")
    @SaCheckPermission("cms.sites.site.delete")
    @SLog(value = "删除站点:")
    @ApiOperation(name = "删除站点")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> delete(String id, HttpServletRequest req) {
        Cms_site site = cmsSiteService.fetch(id);
        cmsSiteService.delete(id);
        cmsSiteService.cacheClear();
        req.setAttribute("_slog_msg", site.getSite_name());
        return Result.success();
    }

    @At("/get/{id}")
    @GET
    @Ok("json")
    @SaCheckLogin
    @ApiOperation(name = "获取站点信息")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH, required = true, check = true)
            }
    )
    @ApiResponses
    public Result<?> get(String id, HttpServletRequest req) {
        return Result.success(cmsSiteService.fetch(id));
    }

}
