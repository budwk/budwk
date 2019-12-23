package com.budwk.nb.api.open.controllers.open;

import com.budwk.nb.api.open.commons.filters.ApiHeaderSignFilter;
import com.budwk.nb.api.open.commons.sign.ApiSignServer;
import com.budwk.nb.commons.base.Result;
import org.nutz.filepool.NutFilePool;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wizzer(wizzer@qq.com) on 2018/4/4.
 */
@IocBean
@At("/open/api/file")
public class ApiFileTestController {
    private final static Log log = Logs.get();
    @Inject("refer:tmpFilePool")
    private NutFilePool tmpFilePool;
    @Inject
    private ApiSignServer apiSignServer;

    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:imageUpload"})
    @POST
    @At("/upload/image_by_sign")
    @Ok("json")
    public Object upload1(@Param("Filedata") TempFile tf, @Param("..") NutMap params, HttpServletRequest req, AdaptorErrorContext err) {
        try {
            log.debug("params::" + Json.toJson(params));
            // Filedata不参与签名
            params.remove("Filedata");
            Result result = apiSignServer.checkSign(params);
            if (result == null) {
                return Result.error("签名出错");
            }
            if (result.getCode() != 0) {
                return result;
            }
            if (err != null && err.getAdaptorErr() != null) {
                return Result.error("文件不合法");
            } else if (tf == null) {
                return Result.error("空文件");
            } else {
                long fileId = tmpFilePool.getFileId(tf.getFile());
                //获取临时文件ID,在表单提交的API里通过此ID获取文件
                String suffix = tf.getSubmittedFileName().substring(tf.getSubmittedFileName().indexOf("."));
                return Result.success("上传成功",
                        NutMap.NEW().addv("file_id", fileId)
                                .addv("file_suffix", suffix));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("系统错误");
        } catch (Throwable e) {
            return Result.error("文件格式错误");
        }
    }

    @Filters({@By(type = ApiHeaderSignFilter.class)})
    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:imageUpload"})
    @POST
    @At("/upload/image_by_header_sign")
    @Ok("json")
    public Object upload2(@Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
        try {
            if (err != null && err.getAdaptorErr() != null) {
                return Result.error("文件不合法");
            } else if (tf == null) {
                return Result.error("空文件");
            } else {
                long fileId = tmpFilePool.getFileId(tf.getFile());
                //获取临时文件ID,在表单提交的API里通过此ID获取文件
                String suffix = tf.getSubmittedFileName().substring(tf.getSubmittedFileName().indexOf("."));
                return Result.success("上传成功",
                        NutMap.NEW().addv("file_id", fileId)
                                .addv("file_suffix", suffix));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("系统错误");
        } catch (Throwable e) {
            return Result.error("文件格式错误");
        }
    }

}
