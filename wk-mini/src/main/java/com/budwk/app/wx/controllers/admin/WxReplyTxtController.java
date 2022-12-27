package com.budwk.app.wx.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.wx.models.Wx_reply_txt;
import com.budwk.app.wx.services.WxReplyTxtService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
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
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/6
 */
@IocBean
@At("/wx/admin/reply/txt")
@Ok("json")
@ApiDefinition(tag = "自动回复_文本管理")
@SLog(tag = "自动回复_文本管理")
@Slf4j
public class WxReplyTxtController {

    @Inject
    private WxReplyTxtService wxReplyTxtService;

    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @SaCheckLogin
    @ApiOperation(description = "分页查询")
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
        return Result.data(wxReplyTxtService.listPage(pageNo, pageSize, cnd));
    }

    @At("/create")
    @POST
    @Ok("json")
    @SaCheckPermission("wx.reply.txt.create")
    @SLog("新增文本:${replyTxt.title}")
    @ApiOperation(description = "新增文本")
    @ApiFormParams(
            implementation = Wx_reply_txt.class
    )
    @ApiResponses
    public Result<?> create(@Param("..") Wx_reply_txt replyTxt, HttpServletRequest req) {
        replyTxt.setCreatedBy(SecurityUtil.getUserId());
        wxReplyTxtService.insert(replyTxt);
        return Result.success();
    }

    @At("/update")
    @POST
    @Ok("json")
    @SaCheckPermission("wx.reply.txt.update")
    @SLog("修改文本:${replyTxt.title}")
    @ApiOperation(description = "修改文本")
    @ApiFormParams(
            implementation = Wx_reply_txt.class
    )
    @ApiResponses
    public Result<?> update(@Param("..") Wx_reply_txt replyTxt, HttpServletRequest req) {
        replyTxt.setUpdatedBy(SecurityUtil.getUserId());
        wxReplyTxtService.updateIgnoreNull(replyTxt);
        return Result.success();
    }

    @At("/get/{id}")
    @GET
    @Ok("json")
    @SaCheckLogin
    @ApiOperation(description = "获取文本信息")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> get(String id, HttpServletRequest req) {
        return Result.data(wxReplyTxtService.fetch(id));
    }

    @At("/delete_more")
    @Ok("json")
    @POST
    @SaCheckPermission("wx.reply.txt.delete")
    @SLog(tag = "批量删除文本")
    @ApiOperation(description = "批量删除文本")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "ids", example = "a,b", description = "文本ID数组", required = true, check = true),
                    @ApiFormParam(name = "titles", example = "a,b", description = "文本标题数组", required = true)
            }
    )
    @ApiResponses
    public Result<?> deleteMore(@Param("ids") String[] ids, @Param("titles") String titles, HttpServletRequest req) {
        wxReplyTxtService.delete(ids);
        req.setAttribute("_slog_msg", String.format("文本标题:%s", titles));
        return Result.success();
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @SaCheckPermission("wx.reply.txt.delete")
    @SLog(tag = "删除文本")
    @ApiOperation(description = "删除文本")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> delete(String id, HttpServletRequest req) {
        Wx_reply_txt replyTxt = wxReplyTxtService.fetch(id);
        if (replyTxt == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        wxReplyTxtService.delete(id);
        req.setAttribute("_slog_msg", String.format("文本标题:%s", replyTxt.getTitle()));
        return Result.success();
    }
}
