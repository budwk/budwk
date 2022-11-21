package com.budwk.app.wx.controllers.wx;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.wx.models.Wx_pay;
import com.budwk.app.wx.services.WxPayCertService;
import com.budwk.app.wx.services.WxPayService;
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

/**
 * @author wizzer@qq.com
 */
@IocBean
@At("/admin/conf/pay")
@Ok("json")
@ApiDefinition(tag = "微信支付配置")
@SLog(tag = "微信支付配置")
@Slf4j
public class WxPayController {

    @Inject
    private WxPayService wxPayService;

    @Inject
    private WxPayCertService wxPayCertService;

    @At("/create")
    @POST
    @Ok("json")
    @SaCheckPermission("wx.conf.pay.create")
    @SLog("支付名称:${wxPay.name}")
    @ApiOperation(description = "添加微信支付")
    @ApiFormParams(
            implementation = Wx_pay.class
    )
    @ApiResponses
    public Result<?> create(@Param("..") Wx_pay wxPay, HttpServletRequest req) {
        wxPay.setCreatedBy(SecurityUtil.getUserId());
        wxPayService.insert(wxPay);
        return Result.success();
    }


    @At("/update")
    @POST
    @Ok("json")
    @SaCheckPermission("wx.conf.pay.update")
    @SLog("支付名称:${wxPay.name}")
    @ApiOperation(description = "修改微信支付")
    @ApiFormParams(
            implementation = Wx_pay.class
    )
    @ApiResponses
    public Result<?> update(@Param("..") Wx_pay wxPay, HttpServletRequest req) {

        wxPay.setUpdatedBy(SecurityUtil.getUserId());
        String v2key = Strings.sNull(wxPay.getV2key());
        if (v2key.contains("***")) {// 包含**不入库
            wxPay.setV2key(null);
        }
        String v3key = Strings.sNull(wxPay.getV3key());
        if (v3key.contains("***")) {// 包含**不入库
            wxPay.setV3key(null);
        }
        wxPayService.updateIgnoreNull(wxPay);
        return Result.success();
    }

    @At("/get/{id}")
    @GET
    @Ok("json:{ignoreNull:false}")
    @SaCheckLogin
    @ApiOperation(description = "获取微信支付")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> get(String id, HttpServletRequest req) {
        Wx_pay wxPay = wxPayService.fetch(id);
        String v2key = Strings.sNull(wxPay.getV2key());
        if (v2key.length() > 10) {
            wxPay.setV2key(v2key.substring(0, 10) + "*****");
        }
        String v3key = Strings.sNull(wxPay.getV3key());
        if (v3key.length() > 10) {
            wxPay.setV3key(v3key.substring(0, 10) + "*****");
        }
        return Result.data(wxPay);
    }


    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @SaCheckPermission("wx.conf.pay.delete")
    @SLog(tag = "删除微信支付")
    @ApiOperation(description = "删除微信支付")
    @ApiResponses
    public Result<?> delete(String id, HttpServletRequest req) {
        Wx_pay wxPay = wxPayService.fetch(id);
        if (wxPay == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        wxPayService.delete(id);
        req.setAttribute("_slog_msg", String.format("%s", wxPay.getName()));
        return Result.success();
    }

    @At("/list")
    @POST
    @Ok("json:full")
    @SaCheckPermission("wx.conf.pay")
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
        Pagination pagination = wxPayService.listPage(pageNo, pageSize, cnd);
        List<Wx_pay> list = pagination.getList(Wx_pay.class);
        list.forEach(
                wxPay -> {
                    String v2key = Strings.sNull(wxPay.getV2key());
                    if (v2key.length() > 10) {
                        wxPay.setV2key(v2key.substring(0, 10) + "*****");
                    }
                    String v3key = Strings.sNull(wxPay.getV3key());
                    if (v3key.length() > 10) {
                        wxPay.setV3key(v3key.substring(0, 10) + "*****");
                    }
                }
        );
        pagination.setList(list);
        return Result.data(pagination);
    }

    @At("/query")
    @GET
    @Ok("json:{actived:'code|msg|data|id|name|mchid',ignoreNull:true}")
    @SaCheckLogin
    @ApiOperation(description = "获取微信支付信息")
    @ApiImplicitParams
    @ApiResponses
    public Result<?> query(HttpServletRequest req) {
        return Result.data(wxPayService.query());
    }

    @At("/cert/list")
    @POST
    @Ok("json:{locked:'algorithm|nonce|associated_data|ciphertext|certificate',ignoreNull:true}")
    @SaCheckPermission("wx.conf.pay")
    @ApiOperation(description = "分页查询平台证书")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "mchid", example = "", description = "商户号"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses
    public Result<?> certList(@Param("mchid") String mchid, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        cnd.and("mchid", "=", mchid);
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.data(wxPayCertService.listPage(pageNo, pageSize, cnd));
    }
}
