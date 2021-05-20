package com.budwk.starter.storage.service.impl;

import cn.hutool.core.img.ImgUtil;
import com.budwk.starter.common.exception.FileStorageException;
import com.budwk.starter.storage.StorageServer;
import com.budwk.starter.storage.model.FileStorageInfo;
import com.budwk.starter.storage.service.IStorageService;
import com.budwk.starter.storage.utils.StorageUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.img.Images;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 本地存储实现
 *
 * @author caoshi
 * @date 14点08分2021年4月22日
 */
@IocBean
@Slf4j
public class LocalStorageServiceImpl implements IStorageService {

    @Inject
    private PropertiesProxy conf;

    @Override
    public NutMap upload(InputStream in, String filename, String storagePath) throws FileStorageException {
        try {
            if (Strings.isBlank(conf.get(StorageServer.LOCAL_STORAGE_PATH))) {
                throw new FileStorageException("未设置文件存储路径");
            }
            long size = in.available();
            storagePath=StorageUtil.prefixSeparator(storagePath);
            String fileUrl=conf.get(StorageServer.LOCAL_STORAGE_PATH) +  storagePath + "/" + filename;
            Files.write(fileUrl, in);
            FileStorageInfo info = new FileStorageInfo();
            info.setPath(storagePath);
            info.setType(Files.getSuffixName(filename));
            info.setSize(size);
            info.setFileName(filename);
            info.setUrl(storagePath + filename);
            return info;
        } catch (Exception e) {
            throw new FileStorageException(e.getMessage());
        }
    }

    @Override
    public byte[] download(String path, String filename) throws FileStorageException {
        try {
            return Files.readBytes(path +"/"+filename);
        } catch (Exception e) {
            throw new FileStorageException(e.getMessage());
        }
    }

    @Override
    public void download(String path, String filename, OutputStream os) throws FileStorageException {
        try (InputStream is = Files.findFileAsStream(path +"/"+filename)) {
            Streams.write(os, is);
        } catch (Exception e) {
            throw new FileStorageException(e.getMessage());
        }
    }

    @Override
    public InputStream downloadStream(String path, String filename) throws FileStorageException {
        try {
            return Files.findFileAsStream(path +"/"+filename);
        } catch (Exception e) {
            throw new FileStorageException(e.getMessage());
        }
    }

    @Override
    public boolean delete(String path, String filename) throws FileStorageException {
        try {
            return Files.deleteFile(new File(path+"/"+filename));
        } catch (Exception e) {
            throw new FileStorageException(e.getMessage());
        }
    }

    @Override
    public NutMap uploadImageWatermark(InputStream srcImg, String srcFilename, String storagePath, InputStream watermark, int pos, int margin, float alpha) throws FileStorageException {
        try(ByteArrayOutputStream dest = new ByteArrayOutputStream();
            BufferedInputStream bufIns = new BufferedInputStream(srcImg)) {
            bufIns.mark(srcImg.available() + 1);
            //生成水印图
            BufferedImage watermarkImg = Images.addWatermark(Streams.readBytes(bufIns), Streams.readBytes(watermark), alpha, pos, margin);
            Images.write(watermarkImg,Files.getSuffixName(srcFilename),dest);
            bufIns.reset();

            String watermarkName=Files.getMajorName(srcFilename)+"_watermark"+Files.getSuffix(srcFilename);
            //上传水印图
            NutMap mark = this.upload(Streams.wrap(dest.toByteArray()), watermarkName, storagePath);
            //上传原图
            NutMap original = this.upload(bufIns, srcFilename, storagePath);

            NutMap info=new NutMap();
            info.put("watermark", mark);
            info.put("original", original);
            return info;
        }catch (Exception e){
            throw new FileStorageException(e.getMessage());
        }
    }

    @Override
    public NutMap uploadImagePressText(InputStream srcImg, String srcFilename, String storagePath, String pressText,Color color, Font font, int x, int y, float alpha) throws FileStorageException {
        try(ByteArrayOutputStream dest = new ByteArrayOutputStream();
            BufferedInputStream bufIns = new BufferedInputStream(srcImg)) {
            bufIns.mark(srcImg.available() + 1);
            //生成水印图
            ImgUtil.pressText(bufIns,dest,pressText,color,font,x,y,alpha);
            bufIns.reset();
            //上传水印图
            String watermarkName=Files.getMajorName(srcFilename)+"_watermark"+Files.getSuffix(srcFilename);
            NutMap mark = this.upload(Streams.wrap(dest.toByteArray()), watermarkName, storagePath);
            //上传原图
            NutMap original = this.upload(bufIns, srcFilename, storagePath);
            NutMap info=new NutMap();
            info.put("watermark", mark);
            info.put("original", original);
            return info;
        }catch (Exception e){
            throw new FileStorageException(e.getMessage());
        }
    }

    @Override
    public NutMap uploadImageScale(InputStream srcImg, String srcFilename, String storagePath, int width, int height) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             BufferedInputStream bufIns = new BufferedInputStream(srcImg)){
            bufIns.mark(srcImg.available() + 1);
            //生成缩略图
            BufferedImage scale = Images.zoomScale(Images.read(Streams.readBytes(bufIns)), width, height);
            Images.write(scale,Files.getSuffixName(srcFilename),bos);
            bufIns.reset();
            //上传原图
            NutMap srcInfo = this.upload(bufIns, srcFilename, storagePath);
            //上传缩略图
            String thumbFileName=Files.getMajorName(srcFilename)+"_thumbnail"+Files.getSuffix(srcFilename);
            NutMap thumbInfo = this.upload(Streams.wrap(bos.toByteArray()), thumbFileName, storagePath);
            NutMap info=new NutMap();
            info.put("thumbnail", thumbInfo);
            info.put("original", srcInfo);
            return info;
        }catch (Exception e){
            throw new FileStorageException(e.getMessage());
        }
    }


}
