package com.budwk.nb.web.controllers.platform.wx;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.DateUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.web.commons.base.Globals;
import com.budwk.nb.web.commons.ext.wx.WxService;
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
import org.nutz.boot.starter.ftp.FtpService;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
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
import org.nutz.weixin.spi.WxApi2;
import org.nutz.weixin.spi.WxResp;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/6
 */
@IocBean
@At("/api/{version}/platform/wx/file/upload")
@Ok("json")
@ApiVersion
@OpenAPIDefinition(tags = {@Tag(name = "微信_文件上传")}, servers = @Server(url = "/"))
public class WxFileUploadController {
    private static final Log log = Logs.get();
    @Inject
    @Reference(check = false)
    private WxConfigService wxConfigService;
    @Inject
    private WxService wxService;
    @Inject
    private FtpService ftpService;
    @Inject
    private PropertiesProxy conf;
    @Inject("java:$conf.get('budwk.upload.type')")
    private String UploadType;
    private final static String UPLOAD_TYPE_FTP = "ftp";

    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:imageUpload"})
    @POST
    @At("/upload_image/{wxid}")
    @Ok("json")
    @RequiresAuthentication
    @SuppressWarnings("deprecation")
    @Operation(
            tags = "微信_文件上传", summary = "上传图片到微信平台",
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
                                    "    \"code\":0,\n" +
                                    "    \"msg\":\"上传成功\",\n" +
                                    "    \"data\":{\n" +
                                    "        \"file_type\":\".png\",\n" +
                                    "        \"file_name\":\"xxx\",\n" +
                                    "        \"file_size\":1024,\n" +
                                    "        \"file_url\":\"/upload/file/20200220/xxx.doc\"\n" +
                                    "    }\n" +
                                    "}"), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "Filedata", description = "文件对象名")
            },
            mediaType = "multipart/form-data"
    )
    public Object uploadImage(String wxid, @Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
        try {
            if (err != null && err.getAdaptorErr() != null) {
                return Result.error("system.error.upload.file");
            } else if (tf == null) {
                return Result.error("system.error.upload.empty");
            } else {
                WxApi2 wxApi2 = wxService.getWxApi2(wxid);
                WxResp resp = wxApi2.uploadimg(tf.getFile());
                if (resp.errcode() != 0) {
                    return Result.error();
                }
                return Result.success("system.error.upload.success", NutMap.NEW().addv("file_type", tf.getSubmittedFileName().substring(tf.getSubmittedFileName().lastIndexOf(".") + 1))
                        .addv("file_name", tf.getSubmittedFileName()).addv("file_size", tf.getSize()).addv("file_url", Strings.sNull(resp.get("url")).replace("http://", "https://")));
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
    @At("/upload_thumb/{wxid}")
    @Ok("json")
    @RequiresAuthentication
    @SuppressWarnings("deprecation")
    //AdaptorErrorContext必须是最后一个参数
    @Operation(
            tags = "微信_文件上传", summary = "上传缩略图到微信",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "wxid", description = "微信ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
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
                            .addv("picurl", url)
                            .addv("wx_picurl", Strings.sNull(resp.get("url")).replace("http://", "https://")));
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
    @At("/upload_image_material/{wxid}")
    @Ok("json")
    @RequiresAuthentication
    @SuppressWarnings("deprecation")
    //AdaptorErrorContext必须是最后一个参数
    @Operation(
            tags = "微信_文件上传", summary = "上传媒体图片到微信",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "wxid", description = "微信ID", in = ParameterIn.PATH),
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
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
    public Object uploadImageMaterial(String wxid, @Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
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
                        .addv("picurl", url)
                        .addv("wx_picurl", Strings.sNull(resp.get("url")).replace("http://", "https://")));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return Result.error("system.error.upload.filetype");
        }
    }
}
