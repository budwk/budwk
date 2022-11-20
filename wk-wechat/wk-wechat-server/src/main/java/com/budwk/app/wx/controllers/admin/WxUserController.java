package com.budwk.app.wx.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.wx.commons.service.WxService;
import com.budwk.app.wx.models.Wx_user;
import com.budwk.app.wx.services.WxConfigService;
import com.budwk.app.wx.services.WxUserService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.page.PageUtil;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.log.annotation.SLog;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.*;
import org.nutz.mvc.annotation.*;
import org.nutz.weixin.spi.WxApi2;
import org.nutz.weixin.spi.WxResp;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/5
 */
@IocBean
@At("/admin/user")
@Ok("json")
@ApiDefinition(tag = "用户管理")
@SLog(tag = "用户管理")
@Slf4j
public class WxUserController {

    @Inject
    private WxConfigService wxConfigService;

    @Inject
    private WxUserService wxUserService;

    @Inject
    private WxService wxService;

    @At("/list")
    @POST
    @Ok("json:full")
    @SaCheckLogin
    @ApiOperation(description = "分页查询")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "wxid", example = "", description = "微信ID"),
                    @ApiFormParam(name = "openid", example = "", description = "微信openid"),
                    @ApiFormParam(name = "nickname", example = "", description = "微信昵称"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Result<?> list(@Param("wxid") String wxid, @Param("openid") String openid, @Param("nickname") String nickname, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        if (!Strings.isBlank(wxid)) {
            cnd.and("wxid", "=", wxid);
        }
        if (Strings.isNotBlank(openid)) {
            cnd.and("openid", "=", Strings.trim(openid));
        }
        if (Strings.isNotBlank(nickname)) {
            cnd.and("nickname", "like", "%" + Strings.trim(nickname) + "%");
        }
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.data(wxUserService.listPage(pageNo, pageSize, cnd));
    }

    @At("/down/{wxid}")
    @GET
    @Ok("json")
    @SaCheckPermission("wx.user.list.sync")
    @SLog("同步微信会员:${wxid}")
    @ApiOperation(description = "同步微信会员")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "wxid", description = "wxid", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> down(String wxid, HttpServletRequest req) {
        WxApi2 wxApi2 = wxService.getWxApi2(wxid);
        wxApi2.user_get(new Each<String>() {
            public void invoke(int index, String _ele, int length)
                    throws ExitLoop, ContinueLoop, LoopException {
                WxResp resp = wxApi2.user_info(_ele, "zh_CN");
                Wx_user usr = Json.fromJson(Wx_user.class, Json.toJson(resp.user()));
                usr.setCreatedAt(System.currentTimeMillis());
                usr.setNickname(EmojiParser.parseToAliases(usr.getNickname(), EmojiParser.FitzpatrickAction.REMOVE));
                usr.setSubscribeAt(resp.user().getSubscribe_time());
                usr.setWxid(wxid);
                Wx_user dbUser = wxUserService.fetch(Cnd.where("wxid", "=", wxid).and("openid", "=", usr.getOpenid()));
                if (dbUser == null) {
                    wxUserService.insert(usr);
                } else {
                    usr.setId(dbUser.getId());
                    wxUserService.updateIgnoreNull(usr);
                }
            }
        });
        return Result.success();
    }
}
