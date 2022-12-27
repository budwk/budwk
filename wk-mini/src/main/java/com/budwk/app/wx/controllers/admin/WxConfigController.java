package com.budwk.app.wx.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.wx.models.Wx_config;
import com.budwk.app.wx.services.WxConfigService;
import com.budwk.starter.common.constant.RedisConstant;
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
import org.nutz.integration.jedis.pubsub.PubSubService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/4
 */
@IocBean
@At("/wx/admin/conf/account")
@Ok("json")
@ApiDefinition(tag = "微信账号配置")
@SLog(tag = "微信账号配置")
@Slf4j
public class WxConfigController {

    @Inject
    private WxConfigService wxConfigService;

    @Inject
    private PubSubService pubSubService;

    @At("/list_account")
    @GET
    @Ok("json:{actived:'code|msg|data|id|appname|appid',ignoreNull:true}")
    @SaCheckLogin
    @ApiOperation(description = "获取账号列表")
    @ApiImplicitParams
    @ApiResponses
    public Result<?> listAccount(HttpServletRequest req) {
        return Result.data(wxConfigService.query());
    }

    @At("/create")
    @POST
    @Ok("json")
    @SaCheckPermission("wx.conf.account.create")
    @SLog("添加账号:${config.appname}")
    @ApiOperation(description = "添加账号")
    @ApiFormParams(
            implementation = Wx_config.class
    )
    @ApiResponses
    public Result<?> create(@Param("..") Wx_config config, HttpServletRequest req) {
        int count = wxConfigService.count(Cnd.where("id", "=", Strings.sNull(config.getId())));
        if (count > 0) {
            return Result.error(ResultCode.HAVE_DATA_ERROR);
        }
        config.setCreatedBy(SecurityUtil.getUserId());
        wxConfigService.insert(config);
        return Result.success();
    }


    @At("/update")
    @POST
    @Ok("json")
    @SaCheckPermission("wx.conf.account.update")
    @SLog("修改账号:${config.appname}")
    @ApiOperation(description = "修改账号")
    @ApiFormParams(
            implementation = Wx_config.class
    )
    @ApiResponses
    public Result<?> update(@Param("..") Wx_config config, HttpServletRequest req) {
        config.setUpdatedBy(SecurityUtil.getUserId());
        String appsecret = Strings.sNull(config.getAppsecret());
        if (appsecret.contains("***")) {// 包含**不入库
            config.setAppsecret(null);
        }
        String encodingAESKey = Strings.sNull(config.getEncodingAESKey());
        if (encodingAESKey.contains("***")) {// 包含**不入库
            config.setEncodingAESKey(null);
        }
        wxConfigService.updateIgnoreNull(config);
        pubSubService.fire(RedisConstant.PRE + ":wxpubsub","clear");
        return Result.success();
    }

    @At("/get/{id}")
    @GET
    @Ok("json")
    @SaCheckLogin
    @ApiOperation(description = "获取账号")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "账号主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> get(String id, HttpServletRequest req) {
        Wx_config wxConfig = wxConfigService.fetch(id);
        String appsecret = Strings.sNull(wxConfig.getAppsecret());
        if (appsecret.length() > 10) {// 包含**不入库
            wxConfig.setAppsecret(appsecret.substring(0, 10) + "*****");
        }
        String encodingAESKey = Strings.sNull(wxConfig.getEncodingAESKey());
        if (encodingAESKey.length() > 10) {// 包含**不入库
            wxConfig.setEncodingAESKey(encodingAESKey.substring(0, 10) + "*****");
        }
        return Result.data(wxConfig);

    }


    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @SaCheckPermission("wx.conf.account.delete")
    @SLog(tag = "删除账号:")
    @ApiOperation(description = "删除账号")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "账号主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> delete(String id, HttpServletRequest req) {
        Wx_config config = wxConfigService.fetch(id);
        if (config == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        wxConfigService.delete(id);
        pubSubService.fire(RedisConstant.PRE + ":wxpubsub","clear");
        req.setAttribute("_slog_msg", String.format("%s", config.getAppname()));
        return Result.success();
    }

    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @SaCheckPermission("wx.conf.account")
    @SaCheckLogin
    @ApiOperation(description = "账号分页查询")
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
        return Result.data(pagination);
    }

}
