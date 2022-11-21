package com.budwk.app.wx.controllers.wx;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.budwk.app.wx.services.WxTplLogService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.page.PageUtil;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.log.annotation.SLog;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/8
 */
@IocBean
@At("/admin/tpl/log")
@Ok("json")
@ApiDefinition(tag = "模板消息_发送日志")
@SLog(tag = "模板消息_发送日志")
@Slf4j
public class WxTplLogController {

    @Inject
    private WxTplLogService wxTplLogService;

    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @SaCheckLogin
    @ApiOperation(description = "分页查询")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "wxid", example = "", description = "微信ID"),
                    @ApiFormParam(name = "openid", example = "", description = "openid"),
                    @ApiFormParam(name = "nickname", example = "", description = "微信昵称"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses
    public Result<?> list(@Param("wxid") String wxid, @Param("openid") String openid, @Param("nickname") String nickname, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(wxid)) {
            cnd.and("wxid", "=", wxid);
        }
        if (Strings.isNotBlank(wxid)) {
            cnd.and("openid", "=", openid);
        }
        if (Strings.isNotBlank(nickname)) {
            cnd.and("nickname", "like", "%" + nickname + "%");
        }
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.data(wxTplLogService.listPage(pageNo, pageSize, cnd));
    }
}
