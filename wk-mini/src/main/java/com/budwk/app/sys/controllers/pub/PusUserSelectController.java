package com.budwk.app.sys.controllers.pub;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.budwk.app.sys.models.Sys_unit;
import com.budwk.app.sys.services.SysPostService;
import com.budwk.app.sys.services.SysUnitService;
import com.budwk.app.sys.services.SysUserService;
import com.budwk.starter.common.constant.GlobalConstant;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.page.PageUtil;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author wizzer.cn
 */
@IocBean
@At("/platform/pub/user")
@SLog(tag = "用户选择")
@ApiDefinition(tag = "用户选择")
@Slf4j
public class PusUserSelectController {
    @Inject
    private SysUnitService sysUnitService;
    @Inject
    private SysUserService sysUserService;
    @Inject
    private SysPostService sysPostService;

    @At("/unitlist")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取单位列表")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "name", example = "", description = "用户名")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckLogin
    public Result<?> getUnitList(@Param("name") String name) {
        Cnd cnd = Cnd.NEW();
        // 非管理员显示所属单位及下级单位
        if (!StpUtil.hasRole(GlobalConstant.DEFAULT_SYSADMIN_ROLECODE)) {
            Sys_unit unit = sysUnitService.fetch(SecurityUtil.getUnitId());
            cnd.and("path", "like", unit.getPath() + "%");
        }
        if (Strings.isNotBlank(name)) {
            cnd.and("name", "like", "%" + name + "%");
        }
        cnd.asc("location");
        cnd.asc("path");
        return Result.success().addData(sysUnitService.query(cnd));
    }

    @At("/post")
    @Ok("json")
    @GET
    @ApiOperation(name = "获取职务列表")
    @ApiImplicitParams
    @ApiResponses
    @SaCheckLogin
    public Result<?> post(HttpServletRequest req) {
        return Result.data(sysPostService.query());
    }

    @At("/list")
    @Ok("json:{locked:'^(password|salt|loginIp|pwdResetAt|themeConfig|updatedByUser|needChangePwd|createdAt|createdBy|updatedAt|updatedBy)$',ignoreNull:false}")
    @POST
    @ApiOperation(name = "获取用户列表")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "unitPath", example = "", description = "单位PATH"),
                    @ApiFormParam(name = "postId", example = "", description = "职务ID"),
                    @ApiFormParam(name = "users", example = "", description = "已选用户名(英文,分隔)"),
                    @ApiFormParam(name = "keyword", example = "", description = "用户姓名/用户名"),
                    @ApiFormParam(name = "mobile", example = "", description = "手机号码"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses(
            implementation = Pagination.class
    )
    @SaCheckLogin
    public Result<?> list(@Param("users") String users, @Param("mobile") String mobile, @Param("unitPath") String unitPath, @Param("postId") String postId, @Param("keyword") String keyword, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(unitPath)) {
            cnd.and("unitPath", "like", unitPath + "%");
        }
        // 非管理员显示所属单位及下级单位
        if (!StpUtil.hasRole(GlobalConstant.DEFAULT_SYSADMIN_ROLECODE)) {
            Sys_unit unit = sysUnitService.fetch(SecurityUtil.getUnitId());
            cnd.and("unitPath", "like", unit.getPath() + "%");
        }
        if (Strings.isNotBlank(postId)) {
            cnd.and("postId", "=", postId);
        }
        if (Strings.isNotBlank(users)) {
            String[] users_ = Strings.splitIgnoreBlank(users, ",");
            cnd.and("loginname", "not in ", Arrays.asList(users_));
        }
        if (Strings.isNotBlank(keyword)) {
            cnd.and(Cnd.exps("loginname", "like", "%" + keyword + "%").or("username", "like", "%" + keyword + "%"));
        }
        if (Strings.isNotBlank(mobile)) {
            cnd.and("mobile", "like", "%" + mobile + "%");
        }
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.data(sysUserService.listPageLinks(pageNo, pageSize, cnd, "^(unit)$"));
    }
}
