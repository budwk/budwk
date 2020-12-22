package com.budwk.nb.web.controllers.platform.pub;

import com.budwk.nb.base.result.Result;
import com.budwk.nb.base.utils.DateUtil;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.web.commons.base.Globals;
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
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author wizzer(wizzer.cn) on 2019/12/19
 */
@IocBean
@At("/api/{version}/platform/pub/file/upload")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "系统_公共_文件上传")}, servers = @Server(url = "/"))
public class PubFileUploadController {
    private static final Log log = Logs.get();
    @Inject
    private FtpService ftpService;
    @Inject
    private PropertiesProxy conf;
    @Inject("java:$conf.get('budwk.upload.type')")
    private String UploadType;
    private final static String UPLOAD_TYPE_FTP = "ftp";


    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:fileUpload"})
    @POST
    @At
    @Ok("json")
    @RequiresAuthentication
    /**
     * AdaptorErrorContext必须是最后一个参数
     */
    @Operation(
            tags = "系统_公共_文件上传", summary = "上传文件",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            requestBody = @RequestBody(content = @Content()),
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(example = "{\n" +
                                    "    \"code\":0,\n" +
                                    "    \"msg\":\"上传成功\",\n" +
                                    "    \"data\":{\n" +
                                    "        \"file_type\":\".doc\",\n" +
                                    "        \"file_name\":\"xxx\",\n" +
                                    "        \"file_size\":1024,\n" +
                                    "        \"file_url\":\"/upload/file/20200220/xxx.doc\"\n" +
                                    "    }\n" +
                                    "}"), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "Filedata",description = "文件对象名")
            },
            mediaType = "multipart/form-data"
    )
    public Object file(@Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
        try {
            if (err != null && err.getAdaptorErr() != null) {
                return Result.error("system.error.upload.file");
            } else if (tf == null) {
                return Result.error("system.error.upload.empty");
            } else {
                String suffixName = tf.getSubmittedFileName().substring(tf.getSubmittedFileName().lastIndexOf(".")).toLowerCase();
                String filePath = Globals.AppUploadBase + "/file/" + DateUtil.format(new Date(), "yyyyMMdd") + "/";
                String fileName = R.UU32() + suffixName;
                String url = filePath + fileName;
                if (UPLOAD_TYPE_FTP.equals(UploadType)) {
                    if (ftpService.upload(filePath, fileName, tf.getInputStream())) {
                        return Result.success("system.error.upload.success", NutMap.NEW().addv("file_type", suffixName).addv("file_name", tf.getSubmittedFileName()).addv("file_size", tf.getSize()).addv("file_url", url));
                    } else {
                        return Result.error("system.error.upload.ftp");
                    }
                } else {
                    String staticPath = conf.get("jetty.staticPath", "/files");
                    Files.write(staticPath + url, tf.getInputStream());
                    return Result.success("system.error.upload.success", NutMap.NEW().addv("file_type", suffixName).addv("file_name", tf.getSubmittedFileName()).addv("file_size", tf.getSize()).addv("file_url", url));
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

    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:videoUpload"})
    @POST
    @At
    @Ok("json")
    @RequiresAuthentication
    /**
     * AdaptorErrorContext必须是最后一个参数
     */
    @Operation(
            tags = "系统_公共_文件上传", summary = "上传视频",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            requestBody = @RequestBody(content = @Content()),
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(example = "{\n" +
                                    "    \"code\":0,\n" +
                                    "    \"msg\":\"上传成功\",\n" +
                                    "    \"data\":{\n" +
                                    "        \"file_type\":\".mp4\",\n" +
                                    "        \"file_name\":\"xxx\",\n" +
                                    "        \"file_size\":1024,\n" +
                                    "        \"file_url\":\"/upload/video/20200220/xxx.mp4\"\n" +
                                    "    }\n" +
                                    "}"), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "Filedata",description = "文件对象名")
            },
            mediaType = "multipart/form-data"
    )
    public Object video(@Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
        try {
            if (err != null && err.getAdaptorErr() != null) {
                return Result.error("system.error.upload.file");
            } else if (tf == null) {
                return Result.error("system.error.upload.empty");
            } else {
                String suffixName = tf.getSubmittedFileName().substring(tf.getSubmittedFileName().lastIndexOf(".")).toLowerCase();
                String filePath = Globals.AppUploadBase + "/video/" + DateUtil.format(new Date(), "yyyyMMdd") + "/";
                String fileName = R.UU32() + suffixName;
                String url = filePath + fileName;
                if (UPLOAD_TYPE_FTP.equals(UploadType)) {
                    if (ftpService.upload(filePath, fileName, tf.getInputStream())) {
                        return Result.success("system.error.upload.success", NutMap.NEW().addv("file_type", suffixName).addv("file_name", tf.getSubmittedFileName()).addv("file_size", tf.getSize()).addv("file_url", url));
                    } else {
                        return Result.error("system.error.upload.ftp");
                    }
                } else {
                    String staticPath = conf.get("jetty.staticPath", "/files");
                    Files.write(staticPath + url, tf.getInputStream());
                    return Result.success("system.error.upload.success", NutMap.NEW().addv("file_type", suffixName).addv("file_name", tf.getSubmittedFileName()).addv("file_size", tf.getSize()).addv("file_url", url));
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
    @At
    @Ok("json")
    @RequiresAuthentication
    /**
     * AdaptorErrorContext必须是最后一个参数
     */
    @Operation(
            tags = "系统_公共_文件上传", summary = "上传图片",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            requestBody = @RequestBody(content = @Content()),
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
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
                                    "        \"file_url\":\"/upload/image/20200220/xxx.png\"\n" +
                                    "    }\n" +
                                    "}"), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "Filedata",description = "文件对象名")
            },
            mediaType = "multipart/form-data"
    )
    public Object image(@Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
        try {
            if (err != null && err.getAdaptorErr() != null) {
                return Result.error("system.error.upload.file");
            } else if (tf == null) {
                return Result.error("system.error.upload.empty");
            } else {
                String suffixName = tf.getSubmittedFileName().substring(tf.getSubmittedFileName().lastIndexOf(".")).toLowerCase();
                String filePath = Globals.AppUploadBase + "/image/" + DateUtil.format(new Date(), "yyyyMMdd") + "/";
                String fileName = R.UU32() + suffixName;
                String url = filePath + fileName;
                if (UPLOAD_TYPE_FTP.equals(UploadType)) {
                    if (ftpService.upload(filePath, fileName, tf.getInputStream())) {
                        return Result.success("system.error.upload.success", NutMap.NEW().addv("file_type", suffixName).addv("file_name", tf.getSubmittedFileName()).addv("file_size", tf.getSize()).addv("file_url", url));
                    } else {
                        return Result.error("system.error.upload.ftp");
                    }
                } else {
                    String staticPath = conf.get("jetty.staticPath", "/files");
                    Files.write(staticPath + url, tf.getInputStream());
                    return Result.success("system.error.upload.success", NutMap.NEW().addv("file_type", suffixName).addv("file_name", tf.getSubmittedFileName()).addv("file_size", tf.getSize()).addv("file_url", url));
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

}
