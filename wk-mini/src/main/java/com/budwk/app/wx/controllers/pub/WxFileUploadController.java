package com.budwk.app.wx.controllers.pub;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.date.DateUtil;
import com.budwk.app.sys.services.SysConfigService;
import com.budwk.app.wx.commons.service.WxService;
import com.budwk.app.wx.services.WxConfigService;
import com.budwk.starter.common.openapi.annotation.*;
import com.budwk.starter.common.openapi.enums.ParamIn;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.security.utils.SecurityUtil;
import com.budwk.starter.storage.StorageServer;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
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
@At("/pub/file/upload")
@Ok("json")
@ApiDefinition(tag = "微信文件上传")
public class WxFileUploadController {
    private static final Log log = Logs.get();
    @Inject
    private WxConfigService wxConfigService;
    @Inject
    private WxService wxService;
    @Inject
    private StorageServer storageServer;
    @Inject
    private PropertiesProxy conf;
    @Inject
    private SysConfigService sysConfigService;

    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:imageUpload"})
    @POST
    @At("/upload_image/{wxid}")
    @Ok("json")
    @SaCheckLogin
    @SuppressWarnings("deprecation")
    @ApiOperation(description = "上传图片")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "wxid", description = "wxid", in = ParamIn.PATH)
            }
    )
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "Filedata", description = "文件对象名")
            },
            mediaType = "multipart/form-data"
    )
    @ApiResponses
    public Result<?> uploadImage(String wxid, @Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
        try {
            if (err != null && err.getAdaptorErr() != null) {
                return Result.error("文件上传出错");
            } else if (tf == null) {
                return Result.error("文件不可为空");
            } else {
                WxApi2 wxApi2 = wxService.getWxApi2(wxid);
                WxResp resp = wxApi2.uploadimg(tf.getFile());
                if (resp.errcode() != 0) {
                    return Result.error();
                }
                return Result.data(NutMap.NEW().addv("file_type", tf.getSubmittedFileName().substring(tf.getSubmittedFileName().lastIndexOf(".") + 1))
                        .addv("file_name", tf.getSubmittedFileName()).addv("file_size", tf.getSize()).addv("file_url", Strings.sNull(resp.get("url")).replace("http://", "https://")));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return Result.error("文件上传出错");
        }
    }


    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:imageUpload"})
    @POST
    @At("/upload_thumb/{wxid}")
    @Ok("json")
    @SaCheckLogin
    @SuppressWarnings("deprecation")
    //AdaptorErrorContext必须是最后一个参数
    @ApiOperation(description = "上传缩略图")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "wxid", description = "wxid", in = ParamIn.PATH)
            }
    )
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "Filedata", description = "文件对象名")
            },
            mediaType = "multipart/form-data"
    )
    @ApiResponses
    public Result<?> uploadThumb(String wxid, @Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
        try {
            if (err != null && err.getAdaptorErr() != null) {
                return Result.error("文件上传出错");
            } else if (tf == null) {
                return Result.error("文件不可为空");
            } else {

                WxApi2 wxApi2 = wxService.getWxApi2(wxid);
                WxResp resp = wxApi2.media_upload("thumb", tf.getFile());
                if (resp.errcode() != 0) {
                    return Result.error(resp.errmsg());
                }
                String suffixName = tf.getSubmittedFileName().substring(tf.getSubmittedFileName().lastIndexOf(".")).toLowerCase();
                String filePath = sysConfigService.getString(SecurityUtil.getAppId(), "AppUploadBase") + "/image/" + DateUtil.format(new Date(), "yyyyMMdd") + "/";
                String fileName = R.UU32() + suffixName;
                String url = filePath + fileName;
                storageServer.upload(tf.getInputStream(), fileName, filePath);
                return Result.success(NutMap.NEW().addv("id", resp.get("thumb_media_id"))
                        .addv("picurl", url)
                        .addv("wx_picurl", Strings.sNull(resp.get("url")).replace("http://", "https://")));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return Result.error("文件上传出错");
        }
    }


    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:imageUpload"})
    @POST
    @At("/upload_image_material/{wxid}")
    @Ok("json")
    @SaCheckLogin
    @SuppressWarnings("deprecation")
    //AdaptorErrorContext必须是最后一个参数
    @ApiOperation(description = "上传媒体图片")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "wxid", description = "wxid", in = ParamIn.PATH)
            }
    )
    @ApiFormParams(
            value = {
                    @ApiFormParam(name = "Filedata", description = "文件对象名")
            },
            mediaType = "multipart/form-data"
    )
    @ApiResponses
    public Result<?> uploadImageMaterial(String wxid, @Param("Filedata") TempFile tf, HttpServletRequest req, AdaptorErrorContext err) {
        try {
            if (err != null && err.getAdaptorErr() != null) {
                return Result.error("文件上传出错");
            } else if (tf == null) {
                return Result.error("文件不可为空");
            } else {
                WxApi2 wxApi2 = wxService.getWxApi2(wxid);
                WxResp resp = wxApi2.add_material("image", tf.getFile());
                if (resp.errcode() != 0) {
                    return Result.error(resp.errmsg());
                }
                String suffixName = tf.getSubmittedFileName().substring(tf.getSubmittedFileName().lastIndexOf(".") + 1).toLowerCase();
                String filePath = sysConfigService.getString(SecurityUtil.getAppId(), "AppUploadBase") + "/image/" + DateUtil.format(new Date(), "yyyyMMdd") + "/";
                String fileName = R.UU32() + suffixName;
                String url = filePath + fileName;
                storageServer.upload(tf.getInputStream(), fileName, filePath);
                return Result.success(NutMap.NEW().addv("id", resp.get("media_id"))
                        .addv("picurl", url)
                        .addv("wx_picurl", Strings.sNull(resp.get("url")).replace("http://", "https://")));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return Result.error("文件上传出错");
        }
    }
}
