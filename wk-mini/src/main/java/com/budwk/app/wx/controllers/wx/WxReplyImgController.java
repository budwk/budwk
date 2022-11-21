package com.budwk.app.wx.controllers.wx;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.wx.models.Wx_reply_img;
import com.budwk.app.wx.services.WxReplyImgService;
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
@At("/admin/reply/img")
@Ok("json")
@ApiDefinition(tag = "自动回复_图片管理")
@SLog(tag = "自动回复_图片管理")
@Slf4j
public class WxReplyImgController {

    @Inject
    private WxReplyImgService wxReplyImgService;

    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @SaCheckLogin
    @ApiOperation(description = "分页查询图片")
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
        return Result.data(wxReplyImgService.listPage(pageNo, pageSize, cnd));
    }

    @At("/create")
    @POST
    @Ok("json")
    @SaCheckPermission("wx.reply.img.create")
    @SLog("添加图片:${replyImg.picurl}")
    @ApiOperation(description = "添加图片")
    @ApiFormParams(
            implementation = Wx_reply_img.class
    )
    @ApiResponses
    public Result<?> create(@Param("..") Wx_reply_img replyImg, HttpServletRequest req) {
        replyImg.setCreatedBy(SecurityUtil.getUserId());
        wxReplyImgService.insert(replyImg);
        return Result.success();
    }

    @At("/update")
    @POST
    @Ok("json")
    @SaCheckPermission("wx.reply.img.update")
    @SLog("修改图片:${replyImg.picurl}")
    @ApiOperation(description = "修改图片")
    @ApiFormParams(
            implementation = Wx_reply_img.class
    )
    @ApiResponses
    public Result<?> update(@Param("..") Wx_reply_img replyImg, HttpServletRequest req) {
        replyImg.setUpdatedBy(SecurityUtil.getUserId());
        wxReplyImgService.updateIgnoreNull(replyImg);
        return Result.success();
    }

    @At("/get/{id}")
    @GET
    @Ok("json")
    @SaCheckLogin
    @ApiOperation(description = "获取图片信息")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> get(String id, HttpServletRequest req) {
        return Result.data(wxReplyImgService.fetch(id));
    }

    @At("/delete_more")
    @Ok("json")
    @POST
    @SaCheckPermission("wx.reply.img.delete")
    @SLog(tag = "批量删除图片")
    @ApiOperation(description = "批量删除图片")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "ids", example = "a,b", description = "文本ID数组", required = true, check = true),
                    @ApiFormParam(name = "picurls", example = "a,b", description = "图片链接数组", required = true)
            }
    )
    @ApiResponses
    public Result<?> deleteMore(@Param("ids") String[] ids, @Param("picurls") String picurls, HttpServletRequest req) {
        wxReplyImgService.delete(ids);
        req.setAttribute("_slog_msg", String.format("图片链接:%s", picurls));
        return Result.success();
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @SaCheckPermission("wx.reply.img.delete")
    @SLog(tag = "删除图片")
    @ApiOperation(description = "删除图片")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> delete(String id, HttpServletRequest req) {
        Wx_reply_img replyImg = wxReplyImgService.fetch(id);
        if (replyImg == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        wxReplyImgService.delete(id);
        req.setAttribute("_slog_msg", String.format("图片链接:%s", replyImg.getPicurl()));
        return Result.success();
    }

}
