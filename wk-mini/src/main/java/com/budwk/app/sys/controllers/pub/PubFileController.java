package com.budwk.app.sys.controllers.pub;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.date.DateUtil;
import com.budwk.app.sys.services.SysConfigService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.log.annotation.SLog;
import com.budwk.starter.security.utils.SecurityUtil;
import com.budwk.starter.storage.StorageServer;
import lombok.extern.slf4j.Slf4j;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * @author wizzer@qq.com
 */
@IocBean
@At("/platform/pub/file")
@SLog(tag = "文件服务")
@ApiDefinition(tag = "文件服务")
@Slf4j
public class PubFileController {
    @Inject
    private StorageServer storageServer;
    @Inject
    private SysConfigService sysConfigService;

    private static final String[] IMAGE_EXTENSION = {"bmp", "gif", "jpg", "jpeg", "png"};

    private static final String[] VIDEO_EXTENSION = {"swf", "flv", "mp4", "avi", "rmvb", "asf", "rm", "rmvb", "wmv", "avi", "mpg"};

    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:fileUpload"})
    @POST
    @At("/upload/file")
    @Ok("json")
    @ApiOperation(name = "上传文件")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "Filedata", example = "", description = "文件表单对象名"),
            },
            mediaType = "multipart/form-data"
    )
    @ApiResponses(
            {
                    @ApiResponse(name = "path", description = "文件夹路径"),
                    @ApiResponse(name = "url", description = "文件路径"),
                    @ApiResponse(name = "type", description = "文件后缀名"),
                    @ApiResponse(name = "size", description = "文件大小", type = "long"),
                    @ApiResponse(name = "filename", description = "文件名"),
            }
    )
    @SaCheckLogin
    public Result<?> file(@Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
        if (err != null && err.getAdaptorErr() != null) {
            return Result.error(log.isDebugEnabled() ? err.getAdaptorErr().getMessage() : "文件服务异常");
        } else if (tf == null) {
            return Result.error("文件不可为空");
        } else {
            String suffixName = tf.getSubmittedFileName().substring(tf.getSubmittedFileName().lastIndexOf(".")).toLowerCase();
            String filePath = sysConfigService.getString(SecurityUtil.getAppId(), "AppUploadBase") + "/file/" + DateUtil.format(new Date(), "yyyyMMdd") + "/";
            String fileName = R.UU32() + suffixName;
            try {
                return Result.data(storageServer.upload(tf.getInputStream(), fileName, filePath));
            } catch (IOException e) {
                return Result.error(log.isDebugEnabled() ? e.getMessage() : "文件服务异常");
            }
        }
    }

    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:imageUpload"})
    @POST
    @At("/upload/image")
    @Ok("json")
    @ApiOperation(name = "上传图片")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "Filedata", example = "", description = "文件表单对象名"),
            },
            mediaType = "multipart/form-data"
    )
    @ApiResponses(
            {
                    @ApiResponse(name = "path", description = "文件夹路径"),
                    @ApiResponse(name = "url", description = "文件路径"),
                    @ApiResponse(name = "type", description = "文件后缀名"),
                    @ApiResponse(name = "size", description = "文件大小", type = "long"),
                    @ApiResponse(name = "filename", description = "文件名"),
            }
    )
    @SaCheckLogin
    public Result<?> image(@Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
        if (err != null && err.getAdaptorErr() != null) {
            return Result.error(log.isDebugEnabled() ? err.getAdaptorErr().getMessage() : "文件服务异常");
        } else if (tf == null) {
            return Result.error("图片不可为空");
        } else {
            String suffixName = tf.getSubmittedFileName().substring(tf.getSubmittedFileName().lastIndexOf(".")).toLowerCase();
            String filePath = sysConfigService.getString(SecurityUtil.getAppId(), "AppUploadBase") + "/image/" + DateUtil.format(new Date(), "yyyyMMdd") + "/";
            String fileName = R.UU32() + suffixName;
            try {
                return Result.data(storageServer.upload(tf.getInputStream(), fileName, filePath));
            } catch (IOException e) {
                return Result.error(log.isDebugEnabled() ? e.getMessage() : "文件服务异常");
            }
        }
    }

    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:blobUpload"})
    @POST
    @At("/upload/blob")
    @Ok("json")
    @ApiOperation(name = "上传Blob文件")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "Filedata", example = "", description = "文件表单对象名"),
            },
            mediaType = "multipart/form-data"
    )
    @ApiResponses(
            {
                    @ApiResponse(name = "path", description = "文件夹路径"),
                    @ApiResponse(name = "url", description = "文件路径"),
                    @ApiResponse(name = "type", description = "文件后缀名"),
                    @ApiResponse(name = "size", description = "文件大小", type = "long"),
                    @ApiResponse(name = "filename", description = "文件名"),
            }
    )
    @SaCheckLogin
    public Result<?> blob(@Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
        if (err != null && err.getAdaptorErr() != null) {
            return Result.error(log.isDebugEnabled() ? err.getAdaptorErr().getMessage() : "文件服务异常");
        } else if (tf == null) {
            return Result.error("文件不可为空");
        } else {
            String suffix = ".blob";
            String type = "file";
            if (Strings.isNotBlank(tf.getContentType()) && Strings.sNull(tf.getContentType()).contains("/")) {
                suffix = tf.getContentType().substring(tf.getContentType().lastIndexOf("/")+1).toLowerCase();
            }
            if (Arrays.asList(IMAGE_EXTENSION).contains(suffix)) {
                type = "image";
            }
            if (Arrays.asList(VIDEO_EXTENSION).contains(suffix)) {
                type = "video";
            }
            String filePath = sysConfigService.getString(SecurityUtil.getAppId(), "AppUploadBase") + "/" + type + "/" + DateUtil.format(new Date(), "yyyyMMdd") + "/";
            String fileName = R.UU32() + "." + suffix;
            try {
                return Result.data(storageServer.upload(tf.getInputStream(), fileName, filePath));
            } catch (IOException e) {
                return Result.error(log.isDebugEnabled() ? e.getMessage() : "文件服务异常");
            }
        }
    }

    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:videoUpload"})
    @POST
    @At("/upload/video")
    @Ok("json")
    @ApiOperation(name = "上传视频")
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "Filedata", example = "", description = "文件表单对象名"),
            },
            mediaType = "multipart/form-data"
    )
    @ApiResponses(
            {
                    @ApiResponse(name = "path", description = "文件夹路径"),
                    @ApiResponse(name = "url", description = "文件路径"),
                    @ApiResponse(name = "type", description = "文件后缀名"),
                    @ApiResponse(name = "size", description = "文件大小", type = "long"),
                    @ApiResponse(name = "filename", description = "文件名"),
            }
    )
    @SaCheckLogin
    public Result<?> video(@Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
        if (err != null && err.getAdaptorErr() != null) {
            return Result.error(log.isDebugEnabled() ? err.getAdaptorErr().getMessage() : "文件服务异常");
        } else if (tf == null) {
            return Result.error("视频不可为空");
        } else {
            String suffixName = tf.getSubmittedFileName().substring(tf.getSubmittedFileName().lastIndexOf(".")).toLowerCase();
            String filePath = sysConfigService.getString(SecurityUtil.getAppId(), "AppUploadBase") + "/video/" + DateUtil.format(new Date(), "yyyyMMdd") + "/";
            String fileName = R.UU32() + suffixName;
            try {
                return Result.data(storageServer.upload(tf.getInputStream(), fileName, filePath));
            } catch (IOException e) {
                return Result.error(log.isDebugEnabled() ? e.getMessage() : "文件服务异常");
            }
        }
    }
}
