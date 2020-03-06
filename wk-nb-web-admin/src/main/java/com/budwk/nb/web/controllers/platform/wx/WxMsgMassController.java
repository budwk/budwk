package com.budwk.nb.web.controllers.platform.wx;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.annotation.SLog;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.DateUtil;
import com.budwk.nb.commons.utils.PageUtil;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.web.commons.base.Globals;
import com.budwk.nb.web.commons.ext.wx.WxService;
import com.budwk.nb.wx.models.Wx_mass;
import com.budwk.nb.wx.models.Wx_mass_news;
import com.budwk.nb.wx.models.Wx_mass_send;
import com.budwk.nb.wx.services.WxConfigService;
import com.budwk.nb.wx.services.WxMassNewsService;
import com.budwk.nb.wx.services.WxMassSendService;
import com.budwk.nb.wx.services.WxMassService;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.boot.starter.ftp.FtpService;
import org.nutz.dao.Cnd;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;
import org.nutz.weixin.bean.WxMassArticle;
import org.nutz.weixin.bean.WxOutMsg;
import org.nutz.weixin.spi.WxApi2;
import org.nutz.weixin.spi.WxResp;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/6
 */
@IocBean
@At("/api/{version}/platform/wx/msg/mass")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "微信_群发消息")}, servers = {@Server(url = "/")})
public class WxMsgMassController {

    private static final Log log = Logs.get();

    @Inject
    @Reference(check = false)
    private WxConfigService wxConfigService;
    @Inject
    @Reference(check = false)
    private WxService wxService;
    @Inject
    @Reference(check = false)
    private WxMassService wxMassService;
    @Inject
    @Reference(check = false)
    private WxMassSendService wxMassSendService;
    @Inject
    @Reference(check = false)
    private WxMassNewsService wxMassNewsService;
    @Inject
    private FtpService ftpService;
    @Inject
    private PropertiesProxy conf;
    @Inject("java:$conf.get('budwk.upload.type')")
    private String UploadType;
    private final static String UPLOAD_TYPE_FTP = "ftp";

    @At("/list")
    @POST
    @Ok("json:full")
    @RequiresAuthentication
    @Operation(
            tags = "微信_群发消息", summary = "分页查询群发信息",
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
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Object list(@Param("wxid") String wxid, @Param("openid") String openid, @Param("nickname") String nickname, @Param("content") String content, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (!Strings.isBlank(wxid)) {
                cnd.and("wxid", "=", wxid);
            }
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(wxMassService.listPageLinks(pageNo, pageSize, cnd, "massSend"));
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/news_list")
    @POST
    @Ok("json:{locked:'content'}")
    @RequiresAuthentication
    @Operation(
            tags = "微信_群发消息", summary = "分页查询图文素材",
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
                    @ApiFormParam(name = "pageNo", example = "1", description = "页码", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageSize", example = "10", description = "页大小", type = "integer", format = "int32"),
                    @ApiFormParam(name = "pageOrderName", example = "createdAt", description = "排序字段"),
                    @ApiFormParam(name = "pageOrderBy", example = "descending", description = "排序方式")
            }
    )
    public Object newsList(@Param("wxid") String wxid, @Param("openid") String openid, @Param("nickname") String nickname, @Param("content") String content, @Param("pageNo") int pageNo, @Param("pageSize") int pageSize, @Param("pageOrderName") String pageOrderName, @Param("pageOrderBy") String pageOrderBy) {
        try {
            Cnd cnd = Cnd.NEW();
            if (!Strings.isBlank(wxid)) {
                cnd.and("wxid", "=", wxid);
            }
            if (Strings.isNotBlank(pageOrderName) && Strings.isNotBlank(pageOrderBy)) {
                cnd.orderBy(pageOrderName, PageUtil.getOrder(pageOrderBy));
            }
            return Result.success().addData(wxMassNewsService.listPage(pageNo, pageSize, cnd));
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/news_detail/{id}")
    @GET
    @Ok("json")
    @RequiresPermissions("wx.msg.mass")
    @Operation(
            tags = "微信_群发消息", summary = "查看图文素材内容",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "id", description = "图文ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object newsDetail(String id, HttpServletRequest req) {
        try {
            return Result.success().addData(wxMassNewsService.fetch(id));
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/news_create")
    @POST
    @Ok("json")
    @RequiresPermissions("wx.msg.mass.news.create")
    @SLog(tag = "添加图文", msg = "图文标题:${news.title}")
    @Operation(
            tags = "微信_群发消息", summary = "添加图文",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.msg.mass.news.create")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            implementation = Wx_mass_news.class
    )
    public Object newsCreate(@Param("..") Wx_mass_news news, HttpServletRequest req) {
        try {
            news.setCreatedBy(StringUtil.getPlatformUid());
            news.setUpdatedBy(StringUtil.getPlatformUid());
            wxMassNewsService.insert(news);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @At("/news_delete/{id}")
    @Ok("json")
    @RequiresPermissions("wx.msg.mass.news.delete")
    @SLog(tag = "删除图文")
    @Operation(
            tags = "微信_群发消息", summary = "删除图文",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.msg.mass.news.delete")
            },
            parameters = {
                    @Parameter(name = "id", description = "图文ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object deleteNews(String id, HttpServletRequest req) {
        try {
            Wx_mass_news news = wxMassNewsService.fetch(id);
            if (news == null) {
                return Result.error("system.error.noData");
            }
            req.setAttribute("_slog_msg", news.getTitle());
            wxMassNewsService.delete(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error();
        }
    }

    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:imageUpload"})
    @POST
    @At("/upload_thumb/{wxid}")
    @Ok("json")
    @RequiresPermissions("wx.msg.mass")
    @SuppressWarnings("deprecation")
    //AdaptorErrorContext必须是最后一个参数
    @Operation(
            tags = "微信_群发消息", summary = "上传缩略图到微信",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.msg.mass")
            },
            parameters = {
                    @Parameter(name = "wxid", description = "微信ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(example = "{\n" +
                                    "    \"code\":0,\n" +
                                    "    \"msg\":\"上传成功\",\n" +
                                    "    \"data\":{\n" +
                                    "        \"thumb_media_id\":\"xxxxx\",\n" +
                                    "        \"picurl\":\"https://\"\n" +
                                    "    }\n" +
                                    "}"), mediaType = "application/json"))
            }
    )
    public Object uploadThumb(String wxid, @Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
        try {
            if (err != null && err.getAdaptorErr() != null) {
                return Result.error("system.error.upload.file");
            } else if (tf == null) {
                return Result.error("system.error.upload.empty");
            } else {

                WxApi2 wxApi2 = wxService.getWxApi2(wxid);
                WxResp resp = wxApi2.media_upload("thumb", tf.getFile());
                if (resp.errcode() != 0) {
                    return Result.error(resp.errmsg());
                }
                String suffixName = tf.getSubmittedFileName().substring(tf.getSubmittedFileName().lastIndexOf(".")).toLowerCase();
                String filePath = Globals.AppUploadBase + "/image/" + DateUtil.format(new Date(), "yyyyMMdd") + "/";
                String fileName = R.UU32() + suffixName;
                String url = filePath + fileName;
                if (conf.getBoolean("ftp.enabled")) {
                    if (ftpService.upload(filePath, fileName, tf.getInputStream())) {
                        return Result.success("system.error.upload.success", NutMap.NEW().addv("id", resp.get("thumb_media_id"))
                                .addv("picurl", url));
                    } else {
                        return Result.error("system.error.upload.ftp");
                    }
                } else {
                    String staticPath = conf.get("jetty.staticPath", "/files");
                    Files.write(staticPath + url, tf.getInputStream());
                    return Result.success("system.error.upload.success", NutMap.NEW().addv("id", resp.get("thumb_media_id"))
                            .addv("picurl", url));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return Result.error("system.error.upload.filetype");
        }
    }

    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:imageUpload"})
    @POST
    @At("/upload_image/{wxid}")
    @Ok("json")
    @RequiresPermissions("wx.msg.mass")
    @SuppressWarnings("deprecation")
    //AdaptorErrorContext必须是最后一个参数
    @Operation(
            tags = "微信_群发消息", summary = "上传媒体图片到微信",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.msg.mass")
            },
            parameters = {
                    @Parameter(name = "wxid", description = "微信ID", in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(example = "{\n" +
                                    "    \"code\":0,\n" +
                                    "    \"msg\":\"上传成功\",\n" +
                                    "    \"data\":{\n" +
                                    "        \"media_id\":\"xxxxx\",\n" +
                                    "        \"picurl\":\"https://\"\n" +
                                    "    }\n" +
                                    "}"), mediaType = "application/json"))
            }
    )
    public Object uploadImage(String wxid, @Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
        try {
            if (err != null && err.getAdaptorErr() != null) {
                return Result.error("system.error.upload.file");
            } else if (tf == null) {
                return Result.error("system.error.upload.empty");
            } else {
                WxApi2 wxApi2 = wxService.getWxApi2(wxid);
                WxResp resp = wxApi2.add_material("image", tf.getFile());
                if (resp.errcode() != 0) {
                    return Result.error(resp.errmsg());
                }
                String suffixName = tf.getSubmittedFileName().substring(tf.getSubmittedFileName().lastIndexOf(".") + 1).toLowerCase();
                String filePath = Globals.AppUploadBase + "/image/" + DateUtil.format(new Date(), "yyyyMMdd");
                String fileName = R.UU32() + suffixName;
                String url = filePath + fileName;
                if (conf.getBoolean("ftp.enabled")) {
                    ftpService.upload(filePath, fileName, tf.getInputStream());
                } else {
                    String staticPath = conf.get("jetty.staticPath", "/files");
                    Files.write(staticPath + url, tf.getInputStream());
                }
                return Result.success("system.error.upload.success", NutMap.NEW().addv("id", resp.get("media_id"))
                        .addv("picurl", url));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return Result.error("system.error.upload.filetype");
        }
    }

    @At("/push")
    @Ok("json")
    @RequiresPermissions("wx.msg.mass.push")
    @SLog(tag = "群发消息", msg = "群发名称:${mass.name}")
    @Operation(
            tags = "微信_群发消息", summary = "群发消息",
            security = {
                    @SecurityRequirement(name = "登陆认证"),
                    @SecurityRequirement(name = "wx.msg.mass.push")
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
                    @ApiFormParam(name = "openids", description = "openid 数组(不大于10000个)"),
                    @ApiFormParam(name = "newsids", description = "图文素材ID数组(不大于10个)")
            },
            implementation = Wx_mass.class
    )
    public Object sendDo(@Param("..") Wx_mass mass, @Param("receivers") String openids, @Param("newsids") String newsids, HttpServletRequest req) {
        try {
            WxApi2 wxApi2 = wxService.getWxApi2(mass.getWxid());
            WxOutMsg outMsg = new WxOutMsg();
            if ("news".equals(mass.getType())) {
                String[] ids = StringUtils.split(newsids, ",");
                int i = 0;
                for (String id : ids) {
                    wxMassNewsService.update(org.nutz.dao.Chain.make("location", i), Cnd.where("id", "=", id));
                    i++;
                }
                List<Wx_mass_news> newsList = wxMassNewsService.query(Cnd.where("id", "in", ids).asc("location"));
                List<WxMassArticle> articles = Json.fromJsonAsList(WxMassArticle.class, Json.toJson(newsList));
                WxResp resp = wxApi2.uploadnews(articles);
                log.debug(resp);
                String media_id = resp.media_id();
                mass.setMedia_id(media_id);
                outMsg.setMedia_id(media_id);
                outMsg.setMsgType("mpnews");
            }
            if ("text".equals(mass.getType())) {
                outMsg.setContent(mass.getContent());
                outMsg.setMsgType("text");
            }
            if ("image".equals(mass.getType())) {
                outMsg.setMedia_id(mass.getMedia_id());
                outMsg.setMsgType("image");
            }
            WxResp resp;
            if ("all".equals(mass.getScope())) {
                resp = wxApi2.mass_sendall(true, null, outMsg);
            } else {
                String[] ids = StringUtils.split(openids, ",");
                resp = wxApi2.mass_send(Arrays.asList(ids), outMsg);
            }
            log.debug(resp);
            int status = resp.errcode() == 0 ? 1 : 2;
            String errmsg = resp.getString("errmsg");
            if (status != 1) {
                return Result.error(errmsg);
            }
            mass.setStatus(resp.errcode() == 0 ? 1 : 2);
            Wx_mass wxMass = wxMassService.insert(mass);
            Wx_mass_send send = new Wx_mass_send();
            send.setWxid(wxMass.getWxid());
            send.setMassId(wxMass.getId());
            send.setErrCode(String.valueOf(resp.errcode()));
            send.setMsgId(resp.getString("msg_id"));
            if (!"all".equals(mass.getScope())) {
                send.setReceivers(openids);
            }
            send.setErrMsg(errmsg);
            send.setStatus(status);
            wxMassSendService.insert(send);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }
    }
}
