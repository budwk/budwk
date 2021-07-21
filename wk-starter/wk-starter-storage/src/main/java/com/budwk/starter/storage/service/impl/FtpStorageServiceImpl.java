package com.budwk.starter.storage.service.impl;

import cn.hutool.core.img.ImgUtil;
import com.budwk.starter.common.exception.FileStorageException;
import com.budwk.starter.storage.StorageServer;
import com.budwk.starter.storage.model.FileStorageInfo;
import com.budwk.starter.storage.service.IStorageService;
import com.budwk.starter.storage.utils.StorageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.nutz.boot.starter.ftp.FtpService;
import org.nutz.img.Images;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.util.NutMap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * ftp存储
 *
 * @author caoshi
 * @date 14点08分2021年4月22日
 */
@IocBean
@Slf4j
public class FtpStorageServiceImpl implements IStorageService {

    @Inject
    private FtpService ftpService;

    @Inject
    private PropertiesProxy conf;

    @Override
    public NutMap upload(InputStream ins, String filename, String storagePath) throws FileStorageException {
        try {
            storagePath = StorageUtil.prefixSeparator(storagePath);
            int size = ins.available();
            boolean res = ftpService.upload(conf.get(StorageServer.FTP_STORAGE_PATH) + storagePath, filename, ins);
            if (log.isDebugEnabled()) {
                log.debug("FTP上传 {},结果：{}", storagePath, res ? "成功" : "失败");
            }
            if (res) {
                FileStorageInfo info = new FileStorageInfo();
                info.setPath(storagePath);
                info.setType(Files.getSuffixName(filename));
                info.setSize(size);
                info.setFileName(filename);
                info.setUrl(storagePath + filename);
                return info;
            } else {
                throw new FileStorageException("FTP链接异常，文件上传失败");
            }
        } catch (Exception e) {
            throw new FileStorageException(e.getMessage());
        }
    }

    @Override
    public byte[] download(String path, String filename) throws FileStorageException {
        try {
            return Streams.readBytesAndClose(downloadInputStream(path, filename));
        } catch (Exception e) {
            throw new FileStorageException(e.getMessage());
        }
    }

    @Override
    public void download(String path, String filename, OutputStream os) throws FileStorageException {
        try (InputStream is = downloadInputStream(path, filename)) {
            Streams.write(os, is);
        } catch (Exception e) {
            throw new FileStorageException(e.getMessage());
        }
    }

    @Override
    public InputStream downloadStream(String path, String filename) throws FileStorageException {
        try {
            return downloadInputStream(path, filename);
        } catch (Exception e) {
            throw new FileStorageException(e.getMessage());
        }
    }

    @Override
    public boolean delete(String path, String filename) throws FileStorageException {
        try {
            return ftpService.delete(conf.get(StorageServer.FTP_STORAGE_PATH) + path + "/" + filename);
        } catch (Exception e) {
            throw new FileStorageException(e.getMessage());
        }
    }

    @Override
    public NutMap uploadImageWatermark(InputStream srcImg, String srcFilename, String storagePath, InputStream watermark, int pos, int margin, float alpha) throws FileStorageException {
        try (ByteArrayOutputStream dest = new ByteArrayOutputStream();
             BufferedInputStream bufIns = new BufferedInputStream(srcImg)) {
            bufIns.mark(srcImg.available() + 1);
            //生成水印图
            BufferedImage watermarkImg = Images.addWatermark(Streams.readBytes(bufIns), Streams.readBytes(watermark), alpha, pos, margin);
            Images.write(watermarkImg, Files.getSuffixName(srcFilename), dest);
            bufIns.reset();

            String watermarkName = Files.getMajorName(srcFilename) + "_watermark" + Files.getSuffix(srcFilename);
            //上传水印图
            NutMap mark = this.upload(Streams.wrap(dest.toByteArray()), watermarkName, storagePath);
            //上传原图
            NutMap original = this.upload(bufIns, srcFilename, storagePath);

            NutMap info = new NutMap();
            info.put("watermark", mark);
            info.put("original", original);
            return info;
        } catch (Exception e) {
            throw new FileStorageException(e.getMessage());
        }
    }

    @Override
    public NutMap uploadImagePressText(InputStream srcImg, String srcFilename, String storagePath, String pressText, Color color, Font font, int x, int y, float alpha) throws FileStorageException {
        try (ByteArrayOutputStream dest = new ByteArrayOutputStream();
             BufferedInputStream bufIns = new BufferedInputStream(srcImg)) {
            bufIns.mark(srcImg.available() + 1);
            //生成水印图
            ImgUtil.pressText(bufIns, dest, pressText, color, font, x, y, alpha);
            bufIns.reset();
            //上传水印图
            String watermarkName = Files.getMajorName(srcFilename) + "_watermark" + Files.getSuffix(srcFilename);
            NutMap mark = this.upload(Streams.wrap(dest.toByteArray()), watermarkName, storagePath);
            //上传原图
            NutMap original = this.upload(bufIns, srcFilename, storagePath);
            NutMap info = new NutMap();
            info.put("watermark", mark);
            info.put("original", original);
            return info;
        } catch (Exception e) {
            throw new FileStorageException(e.getMessage());
        }
    }

    @Override
    public NutMap uploadImageScale(InputStream srcImg, String srcFilename, String storagePath, int width, int height) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             BufferedInputStream bufIns = new BufferedInputStream(srcImg)) {
            bufIns.mark(srcImg.available() + 1);
            //生成缩略图
            BufferedImage scale = Images.zoomScale(Images.read(Streams.readBytes(bufIns)), width, height);
            Images.write(scale, Files.getSuffixName(srcFilename), bos);
            bufIns.reset();
            //上传原图
            NutMap srcInfo = this.upload(bufIns, srcFilename, storagePath);
            //上传缩略图
            String thumbFileName = Files.getMajorName(srcFilename) + "_thumbnail" + Files.getSuffix(srcFilename);
            NutMap thumbInfo = this.upload(Streams.wrap(bos.toByteArray()), thumbFileName, storagePath);
            NutMap info = new NutMap();
            info.put("thumbnail", thumbInfo);
            info.put("original", srcInfo);
            return info;
        } catch (Exception e) {
            throw new FileStorageException(e.getMessage());
        }
    }

    private InputStream downloadInputStream(String path, String filename) throws Exception {
        FTPClient ftp = null;
        String filepath = conf.get(StorageServer.FTP_STORAGE_PATH) + path + "/" + filename;
        try {
            String fileNameHasPath = new String(filepath.getBytes("UTF-8"), "ISO-8859-1");
            ftp = ftpService.connect();
            if (null == ftp) {
                throw new FileStorageException("FTP链接异常，文件下载中断");
            }
            InputStream is = ftp.retrieveFileStream(fileNameHasPath);
            if (null == is || ftp.getReplyCode() == 550) {
                throw new FileStorageException("文件不存在 " + ftp.getReplyString());
            }
            return is;
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.logout();
                    ftp.disconnect();
                } catch (IOException ex) {
                    log.error("close ftp client error");
                }
            }
        }
    }
}
