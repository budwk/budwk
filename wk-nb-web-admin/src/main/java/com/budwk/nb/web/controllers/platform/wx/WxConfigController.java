package com.budwk.nb.web.controllers.platform.wx;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.base.page.Pagination;
import com.budwk.nb.commons.constants.RedisConstant;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.wx.models.Wx_config;
import com.budwk.nb.wx.services.WxConfigService;
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
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/4
 */
@IocBean
@At("/api/{version}/platform/wx/conf/account")
@Ok("json")
@ApiVersion
@OpenAPIDefinition(tags = {@Tag(name = "微信_账号配置")}, servers = {@Server(url = "/")})
public class WxConfigController {
    private static final Log log = Logs.get();

    @Inject
    @Reference(check = false)
    private WxConfigService wxConfigService;

    @Inject
    private PubSubService pubSubService;

    @At("/list_account")
    @GET
    @Ok("json:{actived:'code|msg|data|id|appname|appid',ignoreNull:true}")
    @RequiresAuthentication
    @Operation(
            tags = "微信_账号配置", summary = "获取微信账号列表",
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

    @At("/create")
    @POST
    @Ok("json")
    @RequiresPermissions("wx.conf.account.create")
    @SLog(tag = "添加帐号", msg = "分类名称:${config.appname}")
    @Operation(
            tags = "微信_账号配置", summary = "添加帐号",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.account.create")
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
            implementation = Wx_config.class
    )
    public Object create(@Param("..") Wx_config config, HttpServletRequest req) {
        try {
            int count = wxConfigService.count(Cnd.where("id", "=", Strings.sNull(config.getId())));
            if (count > 0) {
                return Result.error("wx.conf.account.form.id.exist");
            }
            config.setCreatedBy(StringUtil.getPlatformUid());
            config.setUpdatedBy(StringUtil.getPlatformUid());
            wxConfigService.insert(config);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }


    @At("/update")
    @POST
    @Ok("json")
    @RequiresPermissions("wx.conf.account.update")
    @SLog(tag = "修改帐号", msg = "分类名称:${config.appname}")
    @Operation(
            tags = "微信_账号配置", summary = "修改帐号",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.account.update")
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
            implementation = Wx_config.class
    )
    public Object update(@Param("..") Wx_config config, HttpServletRequest req) {
        try {
            config.setUpdatedBy(StringUtil.getPlatformUid());
            String appsecret = Strings.sNull(config.getAppsecret());
            if (appsecret.contains("***")) {// 包含**不入库
                config.setAppsecret(null);
            }
            String encodingAESKey = Strings.sNull(config.getEncodingAESKey());
            if (encodingAESKey.contains("***")) {// 包含**不入库
                config.setEncodingAESKey(null);
            }
            int res = wxConfigService.updateIgnoreNull(config);
            if (res > 0) {
                pubSubService.fire(RedisConstant.REDIS_KEY_ADMIN_PUBSUB, "sys_wx");
            }
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/get/{id}")
    @GET
    @Ok("json")
    @RequiresAuthentication
    @Operation(
            tags = "微信_账号配置", summary = "获取微信帐号信息",
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
            Wx_config wxConfig = wxConfigService.fetch(id);
            String appsecret = Strings.sNull(wxConfig.getAppsecret());
            if (appsecret.length() > 10) {// 包含**不入库
                wxConfig.setAppsecret(appsecret.substring(0, 10) + "*****");
            }
            String encodingAESKey = Strings.sNull(wxConfig.getEncodingAESKey());
            if (encodingAESKey.length() > 10) {// 包含**不入库
                wxConfig.setEncodingAESKey(encodingAESKey.substring(0, 10) + "*****");
            }
            return Result.success().addData(wxConfig);
        } catch (Exception e) {
            return Result.error();
        }
    }


    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @RequiresPermissions("wx.conf.account.delete")
    @SLog(tag = "删除账号")
    @Operation(
            tags = "微信_账号配置", summary = "删除账号",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.account.delete")
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
            Wx_config config = wxConfigService.fetch(id);
            if (config == null) {
                return Result.error("system.error.noData");
            }
            int res = wxConfigService.delete(id);
            if (res > 0) {
                pubSubService.fire(RedisConstant.REDIS_KEY_ADMIN_PUBSUB, "sys_wx");
            }
            req.setAttribute("_slog_msg", String.format("%s", config.getAppname()));
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @RequiresPermissions("wx.conf.account")
    @Operation(
            tags = "微信_账号配置", summary = "分页查询微信公众号",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.conf.account")
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
            Pagination pagination = wxConfigService.listPage(pageNo, pageSize, cnd);
            List<Wx_config> list = pagination.getList(Wx_config.class);
            list.forEach(
                    wxConfig -> {
                        String appsecret = Strings.sNull(wxConfig.getAppsecret());
                        if (appsecret.length() > 10) {// 包含**不入库
                            wxConfig.setAppsecret(appsecret.substring(0, 10) + "*****");
                        }
                        String encodingAESKey = Strings.sNull(wxConfig.getEncodingAESKey());
                        if (encodingAESKey.length() > 10) {// 包含**不入库
                            wxConfig.setEncodingAESKey(encodingAESKey.substring(0, 10) + "*****");
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

}
