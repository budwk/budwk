package com.budwk.nb.web.controllers.platform.wx;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.web.commons.ext.wx.WxService;
import com.budwk.nb.wx.models.Wx_user;
import com.budwk.nb.wx.services.WxConfigService;
import com.budwk.nb.wx.services.WxUserService;
import com.vdurmont.emoji.EmojiParser;
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
import org.nutz.json.Json;
import org.nutz.lang.*;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.weixin.spi.WxApi2;
import org.nutz.weixin.spi.WxResp;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/5
 */
@IocBean
@At("/api/{version}/platform/wx/user")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "微信_用户管理")}, servers = {@Server(url = "/")})
public class WxUserController {
    private static final Log log = Logs.get();

    @Inject
    @Reference(check = false)
    private WxConfigService wxConfigService;

    @Inject
    @Reference(check = false)
    private WxUserService wxUserService;

    @Inject
    private WxService wxService;

    @At("/list_account")
    @GET
    @Ok("json:{actived:'code|msg|data|id|appname|appid',ignoreNull:true}")
    @RequiresAuthentication
    @Operation(
            tags = "微信_用户管理", summary = "获取微信账号列表",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(example = "{\n" +
                                    "  \"code\": 0,\n" +
                                    "  \"msg\": \"操作成功\",\n" +
                                    "  \"data\": [\n" +
                                    "    {\n" +
                                    "      \"id\": \"main\",\n" +
                                    "      \"appname\": \"测试公众号\"\n" +
                                    "    }\n" +
                                    "  ]\n" +
                                    "}"), mediaType = "application/json"))
            }
    )
    public Object listAccount(HttpServletRequest req) {
        try {
            return Result.success().addData(wxConfigService.query());
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/list")
    @POST
    @Ok("json:full")
    @RequiresAuthentication
    @Operation(
            tags = "微信_用户管理", summary = "分页查询微信用户信息",
            security = {
                    @SecurityRequirement(name = "登陆认证")
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
                    @ApiFormParam(name = "wxid", example = "", description = "微信ID"),
                    @ApiFormParam(name = "openid", example = "", description = "微信openid"),
                    @ApiFormParam(name = "nickname", example = "", description = "微信昵称"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Object list(@Param("wxid") String wxid, @Param("openid") String openid, @Param("nickname") String nickname, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
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
            return Result.success().addData(wxUserService.listPage(pageNo, pageSize, cnd));
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/down/{wxid}")
    @GET
    @Ok("json")
    @RequiresPermissions("wx.user.list.sync")
    @SLog(tag = "同步微信会员", msg = "微信ID:${wxid}")
    @Operation(
            tags = "微信_用户管理", summary = "同步微信会员",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.user.list.sync")
            },
            parameters = {
                    @Parameter(name = "wxid", description = "微信ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object down(String wxid, HttpServletRequest req) {
        try {
            WxApi2 wxApi2 = wxService.getWxApi2(wxid);
            wxApi2.user_get(new Each<String>() {
                public void invoke(int index, String _ele, int length)
                        throws ExitLoop, ContinueLoop, LoopException {
                    WxResp resp = wxApi2.user_info(_ele, "zh_CN");
                    Wx_user usr = Json.fromJson(Wx_user.class, Json.toJson(resp.user()));
                    usr.setCreatedAt(System.currentTimeMillis());
                    usr.setCreatedBy(StringUtil.getPlatformUid());
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}
