package com.budwk.starter.storage;

import com.budwk.starter.common.exception.FileStorageException;
import com.budwk.starter.storage.service.IStorageService;
import com.budwk.starter.storage.service.impl.FtpStorageServiceImpl;
import com.budwk.starter.storage.service.impl.LocalStorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;

@IocBean(create = "init")
@Slf4j
public class StorageServer {

    protected static final String PRE = "storage.";

    @PropDoc(value = "文件存储服务是否开启", defaultValue = "")
    public static final String FILE_STORAGE_ENABLE = PRE + "enable";

    @PropDoc(value = "文件存储方式", defaultValue = "")
    public static final String FILE_STORAGE_PROVIDER = PRE + "provider";

    @PropDoc(value = "本地文件存储位置", defaultValue = "")
    public static final String LOCAL_STORAGE_PATH = PRE + "local.path";

    @PropDoc(value = "ftp文件存储位置", defaultValue = "")
    public static final String FTP_STORAGE_PATH = PRE + "ftp.path";

    @PropDoc(value = "天翼云OSS认证ID", defaultValue = "")
    public static final String CTYUN_OSS_ACCESS_ID = PRE + "ctyun.oss.access-id";

    @PropDoc(value = "天翼云OSS密钥", defaultValue = "")
    public static final String CTYUN_OSS_SECRET_KEY = PRE + "ctyun.oss.secret";

    @PropDoc(value = "天翼云OSS存储节点", defaultValue = "")
    public static final String CTYUN_OSS_ENDPOINT = PRE + "ctyun.oss.endpoint";

    @PropDoc(value = "天翼云OSS存储桶名称", defaultValue = "")
    public static final String CTYUN_OSS_BUCKET = PRE + "ctyun.oss.bucket";

    @PropDoc(value = "水印位置x轴偏移量", defaultValue = "")
    public static final String WATERMARK_X_OFFSET = PRE + "image.watermark.x-offset";

    @PropDoc(value = "水印位置y轴偏移量", defaultValue = "")
    public static final String WATERMARK_Y_OFFSET = PRE + "image.watermark.y-offset";

    @PropDoc(value = "水印透明度", defaultValue = "")
    public static final String WATERMARK_OPACITY = PRE + "image.watermark.opacity";

    private IStorageService iStorageService;

    @Inject
    protected PropertiesProxy conf;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    public void init() {
        if (conf.getBoolean(FILE_STORAGE_ENABLE, false)) {
            String storageType = conf.get(FILE_STORAGE_PROVIDER, "local");
            if (storageType.equalsIgnoreCase("local")) {
                iStorageService = ioc.get(LocalStorageServiceImpl.class);
            } else if (storageType.equalsIgnoreCase("ftp")) {
                iStorageService = ioc.get(FtpStorageServiceImpl.class);
            }
        }
    }

    /**
     * 文件上传
     * @param ins      文件流
     * @param filename 文件完整名称
     * @param path     上传路径
     * @return
     * @throws FileStorageException
     */
    public NutMap upload(InputStream ins, String filename, String path) throws FileStorageException {
        if (!conf.getBoolean(FILE_STORAGE_ENABLE, false)) {
            throw new FileStorageException("文件存储服务未启用");
        }
        return iStorageService.upload(ins, filename, path);
    }

    /**
     * 文件下载
     * @param path     文件路径 ,使用第三方云存储path为存储桶名称
     * @param filename 文件完整名称
     * @return 文件字节数组
     * @throws FileStorageException
     */
    public byte[] download(String path, String filename) throws FileStorageException {
        if (!conf.getBoolean(FILE_STORAGE_ENABLE, false)) {
            throw new FileStorageException("文件存储服务未启用");
        }
        return iStorageService.download(path, filename);
    }

    /**
     * 文件下载
     * @param path     文件路径 ,使用第三方云存储path为存储桶名称
     * @param filename 文件完整名称
     * @throws FileStorageException
     */
    public void download(String path, String filename, OutputStream os) throws FileStorageException {
        if (!conf.getBoolean(FILE_STORAGE_ENABLE, false)) {
            throw new FileStorageException("文件存储服务未启用");
        }
        iStorageService.download(path, filename, os);
    }

    /**
     * 文件下载
     * <p><b style=color:red>注意</b>，它并不会关闭返回流
     * @param path     文件路径 ,使用第三方云存储path为存储桶名称
     * @param filename 文件完整名称
     * @throws FileStorageException
     */
    public InputStream downloadStream(String path, String filename) throws FileStorageException {
        if (!conf.getBoolean(FILE_STORAGE_ENABLE, false)) {
            throw new FileStorageException("文件存储服务未启用");
        }
        return iStorageService.downloadStream(path, filename);
    }

    /**
     * 删除文件
     * @param path     文件路径 ,使用第三方云存储则为存储桶名称
     * @param filename 文件完整名称
     * @return true/false
     * @throws FileStorageException
     */
    public boolean delete(String path, String filename) throws FileStorageException {
        if (!conf.getBoolean(FILE_STORAGE_ENABLE, false)) {
            throw new FileStorageException("文件存储服务未启用");
        }
        return iStorageService.delete(path, filename);
    }

    /**
     * 上传原图并加水印上传
     * @param srcImg        原图
     * @param srcFilename   文件名
     * @param storagePath   存储路径
     * @param pressImg      水印图
     * @param pos           位置 1-9
     * @param margin        相对偏移
     * @param alpha         透明度 0.0-1.0
     * @return {watermark,original}
     * @throws FileStorageException
     */
    public NutMap uploadImageWatermark(InputStream srcImg, String srcFilename, String storagePath, InputStream pressImg, int pos, int margin, float alpha)throws FileStorageException {
        if (!conf.getBoolean(FILE_STORAGE_ENABLE, false)) {
            throw new FileStorageException("文件存储服务未启用");
        }
        return iStorageService.uploadImageWatermark(srcImg, srcFilename, storagePath, pressImg, pos, margin, alpha);
    }

    /**
     * 上传原图并加文字水印上传
     * @param srcImg        原图
     * @param srcFilename   原图名称
     * @param storagePath   存储路径
     * @param pressText     文字水印内容
     * @param font          字体
     * @param x             修正值。 默认在中间，偏移量相对于中间偏移
     * @param y             修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha         透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @return {watermark,original}
     * @throws FileStorageException
     */
    public NutMap uploadImagePressText(InputStream srcImg, String srcFilename,String storagePath,String pressText,Color color,Font font,int x,int y,float alpha)throws FileStorageException{
        if (!conf.getBoolean(FILE_STORAGE_ENABLE, false)) {
            throw new FileStorageException("文件存储服务未启用");
        }
        return iStorageService.uploadImagePressText(srcImg,srcFilename,storagePath,pressText,color,font,x,y,alpha);
    }

    /**
     * 上传原图及等比生成缩略图
     * @param srcImg 原图
     * @param srcFilename 原图名称
     * @param storagePath 存储路径
     * @param width 缩略宽度
     * @param height 缩略高度
     * @return {thumbnail,original}
     * @throws FileStorageException
     */
    public NutMap uploadImageScale(InputStream srcImg, String srcFilename, String storagePath, int width, int height)throws FileStorageException{
        if (!conf.getBoolean(FILE_STORAGE_ENABLE, false)) {
            throw new FileStorageException("文件存储服务未启用");
        }
        return iStorageService.uploadImageScale(srcImg,srcFilename,storagePath,width,height);
    }


}
