package com.budwk.app.wx.controllers.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.budwk.app.wx.models.Wx_reply;
import com.budwk.app.wx.services.*;
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
 * @date 2020/3/7
 */
@IocBean
@At("/wx/admin/reply/conf")
@Ok("json")
@ApiDefinition(tag = "自动回复_事件配置")
@SLog(tag = "自动回复_事件配置")
@Slf4j
public class WxReplyConfController {

    @Inject
    private WxReplyTxtService wxReplyTxtService;
    @Inject
    private WxReplyNewsService wxReplyNewsService;
    @Inject
    private WxReplyImgService wxReplyImgService;
    @Inject
    private WxReplyService wxReplyService;
    @Inject
    private WxConfigService wxConfigService;


    @At("/list")
    @POST
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @SaCheckLogin
    @ApiOperation(description = "分页查询事件")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "wxid", example = "", description = "微信ID"),
                    @ApiFormParam(name = "type", example = "", description = "事件类型"),
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    @ApiResponses
    public Result<?> list(@Param("wxid") String wxid, @Param("type") String type, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(wxid)) {
            cnd.and("wxid", "=", wxid);
        }
        if (Strings.isNotBlank(type)) {
            cnd.and("type", "=", type);
        }
        if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
            cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
        }
        return Result.data(wxReplyService.listPageLinks(pageNo, pageSize, cnd, "^(replyImg|replyTxt|replyNews)$"));
    }

    @At("/create")
    @POST
    @Ok("json")
    @SaCheckPermission("wx.reply.conf.create")
    @SLog("新增事件:${reply.type}")
    @ApiOperation(description = "新增事件")
    @ApiFormParams(
            implementation = Wx_reply.class
    )
    @ApiResponses
    public Result<?> create(@Param("..") Wx_reply reply, HttpServletRequest req) {
        if ("follow".equals(reply.getType())) {
            int c = wxReplyService.count(Cnd.where("type", "=", "follow").and("wxid", "=", reply.getWxid()));
            if (c > 0) {
                return Result.error("关注事件只允许设置一个");
            }
        }
        if ("keyword".equals(reply.getType())) {
            int c = wxReplyService.count(Cnd.where("keyword", "=", reply.getKeyword()).and("wxid", "=", reply.getWxid()));
            if (c > 0) {
                return Result.error("关键词已存在");
            }
        }
        reply.setCreatedBy(SecurityUtil.getUserId());
        wxReplyService.insert(reply);
        return Result.success();
    }

    @At("/update")
    @POST
    @Ok("json")
    @SaCheckPermission("wx.reply.conf.update")
    @SLog("修改事件:${reply.type}")
    @ApiOperation(description = "修改事件")
    @ApiFormParams(
            implementation = Wx_reply.class
    )
    @ApiResponses
    public Result<?> update(@Param("..") Wx_reply reply, HttpServletRequest req) {
        reply.setUpdatedBy(SecurityUtil.getUserId());
        wxReplyService.updateIgnoreNull(reply);
        if ("news".equals(reply.getMsgType())) {
            String[] newsIds = Strings.sBlank(reply.getContent()).split(",");
            int i = 0;
            for (String id : newsIds) {
                wxReplyNewsService.update(org.nutz.dao.Chain.make("location", i), Cnd.where("id", "=", id));
                i++;
            }
        }
        return Result.success();
    }

    @At("/get/{id}")
    @GET
    @Ok("json")
    @SaCheckLogin
    @ApiOperation(description = "获取事件内容")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> get(String id, HttpServletRequest req) {
        Wx_reply reply = wxReplyService.fetch(id);
        if ("txt".equals(reply.getMsgType())) {
            req.setAttribute("txt", wxReplyTxtService.fetch(reply.getContent()));
        } else if ("news".equals(reply.getMsgType())) {
            req.setAttribute("txt", wxReplyTxtService.fetch(reply.getContent()));
        } else if ("image".equals(reply.getMsgType())) {
            req.setAttribute("image", wxReplyImgService.fetch(reply.getContent()));
        }
        return Result.data(reply);
    }

    @At("/delete_more")
    @Ok("json")
    @POST
    @SaCheckPermission("wx.reply.conf.delete")
    @SLog(tag = "批量删除事件")
    @ApiOperation(description = "批量删除事件")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "ids", example = "a,b", description = "事件ID数组", required = true, check = true),
                    @ApiFormParam(name = "types", example = "a,b", description = "事件类型数组", required = true)
            }
    )
    @ApiResponses
    public Result<?> deleteMore(@Param("ids") String[] ids, @Param("types") String types, HttpServletRequest req) {
        wxReplyService.delete(ids);
        req.setAttribute("_slog_msg", String.format("事件类型:%s", types));
        return Result.success();
    }

    @At("/delete/{id}")
    @Ok("json")
    @DELETE
    @SaCheckPermission("wx.reply.conf.delete")
    @SLog(tag = "删除事件")
    @ApiOperation(description = "删除事件")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id", description = "主键ID", in = ParamIn.PATH)
            }
    )
    @ApiResponses
    public Result<?> delete(String id, HttpServletRequest req) {
        Wx_reply replyt = wxReplyService.fetch(id);
        if (replyt == null) {
            return Result.error(ResultCode.NULL_DATA_ERROR);
        }
        wxReplyService.delete(id);
        req.setAttribute("_slog_msg", String.format("事件类型:%s", replyt.getType()));
        return Result.success();
    }

    @At("/list_content")
    @POST
    @Ok("json:full")
    @SaCheckPermission("wx.reply.conf")
    @ApiOperation(description = "获取待选择内容")
    @ApiFormParams(
            {
                    @ApiFormParam(name = "wxid", description = "微信ID"),
                    @ApiFormParam(name = "msgType", description = "消息类型")
            }
    )
    @ApiResponses
    public Result<?> listContent(@Param("wxid") String wxid, @Param("msgType") String msgType) {
        if ("txt".equals(msgType)) {
            return Result.data(wxReplyTxtService.query(Cnd.orderBy().desc("createdAt")));
        } else if ("image".equals(msgType)) {
            return Result.data(wxReplyImgService.query(Cnd.orderBy().desc("createdAt")));
        } else if ("news".equals(msgType)) {
            return Result.data(wxReplyNewsService.query(Cnd.orderBy().desc("createdAt")));
        }
        return Result.error();
    }
}
