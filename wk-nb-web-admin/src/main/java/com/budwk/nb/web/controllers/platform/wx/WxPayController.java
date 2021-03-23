package com.budwk.nb.web.controllers.platform.wx;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.base.page.Pagination;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.wx.models.Wx_pay;
import com.budwk.nb.wx.services.WxPayCertService;
import com.budwk.nb.wx.services.WxPayService;
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
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean
@At("/api/{version}/platform/wx/conf/pay")
@Ok("json")
@ApiVersion
@OpenAPIDefinition(tags = {@Tag(name = "微信_微信支付配置")}, servers = {@Server(url = "/")})
public class WxPayController {
    private static final Log log = Logs.get();

    @Inject
    @Reference(check = false)
    private WxPayService wxPayService;

    @Inject
    @Reference(check = false)
    private WxPayCertService wxPayCertService;

    @At("/create")
    @POST
    @Ok("json")
    @RequiresPermissions("wx.conf.pay.create")
    @SLog(tag = "添加微信支付", msg = "分类名称:${wxPay.name}")
    @Operation(
            tags = "微信_微信支付配置", summary = "添加微信支付",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.pay.create")
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
            implementation = Wx_pay.class
    )
    public Object create(@Param("..") Wx_pay wxPay, HttpServletRequest req) {
        try {
            wxPay.setCreatedBy(StringUtil.getPlatformUid());
            wxPay.setUpdatedBy(StringUtil.getPlatformUid());
            wxPayService.insert(wxPay);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }


    @At("/update")
    @POST
    @Ok("json")
    @RequiresPermissions("wx.conf.pay.update")
    @SLog(tag = "修改微信支付", msg = "分类名称:${config.appname}")
    @Operation(
            tags = "微信_微信支付配置", summary = "修改微信支付",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.pay.update")
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
            implementation = Wx_pay.class
    )
    public Object update(@Param("..") Wx_pay wxPay, HttpServletRequest req) {
        try {
            wxPay.setUpdatedBy(StringUtil.getPlatformUid());
            String v3key = Strings.sNull(wxPay.getV3key());
            if (v3key.contains("***")) {// 包含**不入库
                wxPay.setV3key(null);
            }
            wxPayService.updateIgnoreNull(wxPay);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/get/{id}")
    @GET
    @Ok("json:{ignoreNull:false}")
    @RequiresAuthentication
    @Operation(
            tags = "微信_微信支付配置", summary = "获取微信支付",
            security = {
                    @SecurityRequirement(name = "登陆认证")
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
    public Object get(String id, HttpServletRequest req) {
        try {
            Wx_pay wxPay = wxPayService.fetch(id);
            String v3key = Strings.sNull(wxPay.getV3key());
            if (v3key.length() > 10) {
                wxPay.setV3key(v3key.substring(0, 10) + "*****");
            }
            return Result.success().addData(wxPay);
        } catch (Exception e) {
            return Result.error();
        }
    }


    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @RequiresPermissions("wx.conf.pay.delete")
    @SLog(tag = "删除微信支付")
    @Operation(
            tags = "微信_微信支付配置", summary = "删除微信支付",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.pay.delete")
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
            Wx_pay wxPay = wxPayService.fetch(id);
            if (wxPay == null) {
                return Result.error("system.error.noData");
            }
            wxPayService.delete(id);
            req.setAttribute("_slog_msg", String.format("%s", wxPay.getName()));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/list")
    @POST
    @Ok("json:full")
    @RequiresPermissions("wx.conf.pay")
    @Operation(
            tags = "微信_微信支付配置", summary = "分页查询微信支付",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.pay")
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
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Object list(@Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            Pagination pagination = wxPayService.listPage(pageNo, pageSize, cnd);
            List<Wx_pay> list = pagination.getList(Wx_pay.class);
            list.forEach(
                    wxPay -> {
                        String v3key = Strings.sNull(wxPay.getV3key());
                        if (v3key.length() > 10) {
                            wxPay.setV3key(v3key.substring(0, 10) + "*****");
                        }
                    }
            );
            pagination.setList(list);
            return Result.success().addData(pagination);
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }

    @At("/query")
    @GET
    @Ok("json:{actived:'code|msg|data|id|name|mchid',ignoreNull:true}")
    @RequiresAuthentication
    @Operation(
            tags = "微信_微信支付配置", summary = "获取微信支付信息",
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
    public Object query(HttpServletRequest req) {
        try {
            return Result.success().addData(wxPayService.query());
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/cert/list")
    @POST
    @Ok("json:{locked:'algorithm|nonce|associated_data|ciphertext|certificate',ignoreNull:true}")
    @RequiresPermissions("wx.conf.pay")
    @Operation(
            tags = "微信_微信支付配置", summary = "分页查询平台证书",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.pay")
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
                    @ApiFormParam(name = "mchid", example = "", description = "商户号"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Object certList(@Param("mchid") String mchid, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            cnd.and("mchid", "=", mchid);
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(wxPayCertService.listPage(pageNo, pageSize, cnd));
        } catch (Exception e) {
            log.error(e);
            return Result.error();
        }
    }
}
