package com.budwk.app.wx.controllers.admin;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.wx.commons.service.WxService;
import com.budwk.app.wx.models.Wx_tpl_list;
import com.budwk.app.wx.services.WxConfigService;
import com.budwk.app.wx.services.WxTplListService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.page.PageUtil;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.*;
import org.nutz.weixin.spi.WxApi2;
import org.nutz.weixin.spi.WxResp;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/8
 */
@IocBean
@At("/wx/admin/tpl/list")
@Ok("json")
@ApiDefinition(tag = "模板消息_模板列表")
@SLog(tag = "模板消息_模板列表")
@Slf4j
public class WxTplListController {

    @Inject
    private WxTplListService wxTplListService;
    @Inject
    private WxConfigService wxConfigService;
    @Inject
    private WxService wxService;

    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @SaCheckLogin
    @ApiOperation(description = "分页查询模板")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "wxid", example = "", description = "微信ID"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Result<?> list(@Param("wxid") String wxid, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(wxid)) {
            cnd.and("wxid", "=", wxid);
        }
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.data(wxTplListService.listPage(pageNo, pageSize, cnd));
    }

    @At("/get_all_template/{wxid}")
    @Ok("json")
    @GET
    @SLog("同步模板")
    @SaCheckPermission("wx.tpl.list.get")
    @ApiOperation(description = "同步模板")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "wxid", description = "wxid", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> getAllTemplate(String wxid, HttpServletRequest req) {
        WxApi2 wxApi2 = wxService.getWxApi2(wxid);
        WxResp wxResp = wxApi2.get_all_private_template();
        if (wxResp.errcode() == 0) {
            wxTplListService.clear(Cnd.where("wxid", "=", wxid));
            List<Wx_tpl_list> lists = wxResp.getList("template_list", Wx_tpl_list.class);
            for (Wx_tpl_list o : lists) {
                o.setWxid(wxid);
                o.setCreatedBy(SecurityUtil.getUserId());
                try {
                    wxTplListService.insert(o);
                } catch (Exception e) {
                }
            }
            return Result.success();
        } else {
            return Result.error(wxResp.errmsg());
        }
    }
}
