package com.budwk.nb.web.controllers.platform.pub;

import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.utils.DateUtil;
import com.budwk.nb.web.commons.base.Globals;
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
 * Created by wizzer.cn on 2019/12/19
 */
@IocBean
@At("/api/{version}/platform/pub/file/upload")
@Ok("json")
@ApiVersion("1.0.0")
public class PubFileUploadController {
    private static final Log log = Logs.get();
    @Inject
    private FtpService ftpService;
    @Inject
    private PropertiesProxy conf;
    @Inject("java:$conf.get('system.upload.type')")
    private String UploadType;

    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:fileUpload"})
    @POST
    @At
    @Ok("json")
    @RequiresAuthentication
    //AdaptorErrorContext必须是最后一个参数
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
                if ("ftp".equals(UploadType)) {
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
    //AdaptorErrorContext必须是最后一个参数
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
                if ("ftp".equals(UploadType)) {
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
    //AdaptorErrorContext必须是最后一个参数
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
                if ("ftp".equals(UploadType)) {
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
