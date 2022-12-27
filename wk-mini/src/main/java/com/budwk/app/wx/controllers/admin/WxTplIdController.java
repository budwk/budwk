package com.budwk.app.wx.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.wx.commons.service.WxService;
import com.budwk.app.wx.models.Wx_tpl_id;
import com.budwk.app.wx.services.WxConfigService;
import com.budwk.app.wx.services.WxTplIdService;
import com.budwk.starter.common.openapi.annotation.*;
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
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.weixin.spi.WxApi2;
import org.nutz.weixin.spi.WxResp;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/7
 */
@IocBean
@At("/wechat/admin/tpl/id")
@Ok("json")
@ApiDefinition(tag = "模板消息_模板编号")
@SLog(tag = "模板消息_模板编号")
@Slf4j
public class WxTplIdController {

    @Inject
    private WxTplIdService wxTplIdService;
    @Inject
    private WxConfigService wxConfigService;
    @Inject
    private WxService wxService;

    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @SaCheckLogin
    @ApiOperation(description = "分页查询模板编号")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "wxid", example = "", description = "微信ID"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses
    public Result<?> list(@Param("wxid") String wxid, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(wxid)) {
            cnd.and("wxid", "=", wxid);
        }
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.data(wxTplIdService.listPage(pageNo, pageSize, cnd));
    }

    @At("/create")
    @POST
    @Ok("json")
    @SaCheckPermission("wx.tpl.id.create")
    @SLog("新增模板:${wxTplId.template_id}")
    @ApiOperation(description = "新增模板")
    @ApiFormParams(
            implementation = Wx_tpl_id.class
    )
    @ApiResponses
    public Result<?> create(@Param("..") Wx_tpl_id wxTplId, HttpServletRequest req) {
        WxApi2 wxApi2 = wxService.getWxApi2(wxTplId.getWxid());
        WxResp wxResp = wxApi2.template_api_add_template(wxTplId.getId());
        if (wxResp.errcode() == 0) {
            wxTplId.setCreatedBy(SecurityUtil.getUserId());
            wxTplId.setTemplate_id(wxResp.template_id());
            wxTplIdService.insert(wxTplId);
            return Result.success();
        }
        return Result.error(wxResp.errmsg());
    }

    @At("/delete_more")
    @Ok("json")
    @POST
    @SaCheckPermission("wx.tpl.id.delete")
    @SLog(tag = "批量删除模板")
    @ApiOperation(description = "批量删除模板")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "wxid", example = "", description = "微信ID", required = true),
                    @ApiFormParam(name = "ids", example = "a,b", description = "ID数组", required = true, check = true),
                    @ApiFormParam(name = "template_ids", example = "a,b", description = "模板编号数组", required = true)
            }
    )
    @ApiResponses
    public Result<?> deleteMore(@Param("wxid") String wxid, @Param("ids") String[] ids, @Param("template_ids") String template_ids, HttpServletRequest req) {
        WxApi2 wxApi2 = wxService.getWxApi2(wxid);
        for (String id : ids) {
            Wx_tpl_id wxTplId = wxTplIdService.fetch(Cnd.where("id", "=", id).and("wxid", "=", wxid));
            WxResp wxResp = wxApi2.template_api_del_template(wxTplId.getTemplate_id());
            if (wxResp.errcode() == 0) {
                wxTplIdService.clear(Cnd.where("id", "=", id).and("wxid", "=", wxid));
            } else {
                return Result.error(wxResp.errmsg());
            }
        }
        wxTplIdService.delete(ids);
        req.setAttribute("_slog_msg", String.format("模板编号:%s", template_ids));
        return Result.success();
    }

    @At("/delete")
    @Ok("json")
    @POST
    @SaCheckPermission("wx.tpl.id.delete")
    @SLog(tag = "删除模板")
    @ApiOperation(description = "删除模板")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "wxid", example = "", description = "微信ID", required = true),
                    @ApiFormParam(name = "id", example = "", description = "模板库ID", required = true)
            }
    )
    @ApiResponses
    public Result<?> delete(@Param("wxid") String wxid, @Param("id") String id, HttpServletRequest req) {
        Wx_tpl_id wxTplId = wxTplIdService.fetch(Cnd.where("id", "=", id).and("wxid", "=", wxid));
        if (wxTplId == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        WxApi2 wxApi2 = wxService.getWxApi2(wxid);
        WxResp wxResp = wxApi2.template_api_del_template(wxTplId.getTemplate_id());
        if (wxResp.errcode() == 0) {
            wxTplIdService.clear(Cnd.where("id", "=", id).and("wxid", "=", wxTplId.getWxid()));
            req.setAttribute("_slog_msg", String.format("模板编号:%s", wxTplId.getTemplate_id()));
            return Result.success();
        } else {
            return Result.error(wxResp.errmsg());
        }
    }
}
