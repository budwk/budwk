package com.budwk.app.wx.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.wx.models.Wx_mina;
import com.budwk.app.wx.services.WxMinaService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.page.PageUtil;
import com.budwk.starter.common.page.Pagination;
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
import java.util.List;

@IocBean
@At("/admin/conf/mina")
@Ok("json")
@ApiDefinition(tag = "微信小程序配置")
@SLog(tag = "微信小程序配置")
@Slf4j
public class WxMinaController {

    @Inject
    private WxMinaService wxMinaService;

    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @SaCheckLogin
    @ApiOperation(description = "分页查询")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses
    public Result<?> list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        Pagination pagination = wxMinaService.listPage(pageNo, pageSize, cnd);
        List<Wx_mina> list = pagination.getList(Wx_mina.class);
        list.forEach(
                wxMina -> {
                    String appSecret = Strings.sNull(wxMina.getAppsecret());
                    if (appSecret.length() > 10) {
                        wxMina.setAppsecret(appSecret.substring(0, 10) + "*****");
                    }
                }
        );
        pagination.setList(list);
        return Result.data(pagination);
    }

    @At("/create")
    @POST
    @Ok("json")
    @SaCheckPermission("wx.conf.mina.create")
    @SLog("新增小程序:${wxMina.appname}")
    @ApiOperation(description = "新增小程序")
    @ApiFormParams(
            implementation = Wx_mina.class
    )
    @ApiResponses
    public Result<?> create(@Param("..") Wx_mina wxMina, HttpServletRequest req) {
        wxMina.setCreatedBy(SecurityUtil.getUserId());
        wxMinaService.insert(wxMina);
        return Result.success();
    }

    @At("/update")
    @POST
    @Ok("json")
    @SaCheckPermission("wx.conf.mina.update")
    @SLog("修改小程序:${wxMina.appname}")
    @ApiOperation(description = "修改小程序")
    @ApiFormParams(
            implementation = Wx_mina.class
    )
    @ApiResponses
    public Result<?> update(@Param("..") Wx_mina wxMina, HttpServletRequest req) {
        wxMina.setUpdatedBy(SecurityUtil.getUserId());
        String appSecret = Strings.sNull(wxMina.getAppsecret());
        if (appSecret.contains("***")) {// 包含**不入库
            wxMina.setAppsecret(null);
        }
        wxMinaService.updateIgnoreNull(wxMina);
        return Result.success();
    }

    @At("/get/{id}")
    @GET
    @Ok("json")
    @SaCheckLogin
    @ApiOperation(description = "获取小程序")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> get(String id, HttpServletRequest req) {
        Wx_mina wxMina = wxMinaService.fetch(id);
        String appSecret = Strings.sNull(wxMina.getAppsecret());
        if (appSecret.length() > 10) {
            wxMina.setAppsecret(appSecret.substring(0, 10) + "*****");
        }
        return Result.data(wxMina);
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @SaCheckPermission("wx.conf.mina.delete")
    @ApiOperation(description = "删除小程序:")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> delete(String id, HttpServletRequest req) {
        Wx_mina wxMina = wxMinaService.fetch(id);
        if (wxMina == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        wxMinaService.delete(id);
        req.setAttribute("_slog_msg", wxMina.getAppname());
        return Result.success();
    }
}
