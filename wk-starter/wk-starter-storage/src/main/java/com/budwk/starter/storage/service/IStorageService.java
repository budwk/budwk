package com.budwk.starter.storage.service;

import com.budwk.starter.common.exception.FileStorageException;
import org.nutz.lang.util.NutMap;

import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 存储接口
 * @author caoshi
 * @date 14点08分2021年4月22日
 */
public interface IStorageService {

    /**
     * 上传文件
     * @param ins           文件内容
     * @param filename      文件完整名称
     * @param storagePath   存储路径 使用第三方云储存则为桶（bucket）路径
     * @return
     * @throws FileStorageException
     */
    NutMap upload(InputStream ins, String filename, String storagePath)throws FileStorageException;

    /**
     * 下载文件
     * @param path      存储路径
     * @param filename  文件完整名称
     * @return 文件字节
     */
    byte[] download(String path,String filename) throws FileStorageException;

    /**
     * 下载文件-直接写入输出流
     * @param path      存储路径
     * @param filename  文件完整名称
     * @param os        输出流
     * @throws FileStorageException
     */
    void download(String path, String filename, OutputStream os) throws FileStorageException;

    /**
     * 下载文件
     * <p>
     * <b style=color:red>注意</b>，它并不会关闭返回流
     * @param path      存储路径 文件完整名称
     * @param filename 文件完整名称
     * @return
     * @throws FileStorageException
     */
    InputStream downloadStream(String path, String filename)throws FileStorageException;

    /**
     * 删除文件
     * @param path      存储路径 使用第三方云储存则为桶（bucket）路径
     * @param filename  文件完整名称
     * @return true成功-false失败
     * @throws FileStorageException
     */
    boolean delete(String path,String filename) throws FileStorageException;

    /**
     * 上传原图及添加水印图
     * @param srcImg        原图
     * @param srcFilename   原图名称
     * @param storagePath   存储路径
     * @param watermark     水印图
     * @param pos           水印位置 1-9 九宫格
     * @param margin        边距
     * @param alpha         透明度 0.0-1.0
     * @return {watermark,original}
     * @throws FileStorageException
     */
    NutMap uploadImageWatermark(InputStream srcImg, String srcFilename,String storagePath,InputStream watermark,int pos,int margin,float alpha) throws FileStorageException;

    /**
     * 上传原图及添加文字水印
     * @param srcImg        原图
     * @param srcFilename   原图名称
     * @param storagePath   存储路径
     * @param pressText     文字水印内容
     * @param font          字体
     * @param x             修正值。 默认在中间，偏移量相对于中间偏移
     * @param y             修正值。 默认在中间，偏移量相对于中间偏移
     * @param alpha         透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @return
     * @throws FileStorageException
     */
    NutMap uploadImagePressText(InputStream srcImg, String srcFilename,String storagePath,String pressText,Color color,Font font,int x,int y,float alpha)throws FileStorageException;

    /**
     * 上传原图及等比缩放图
     * @param srcImg
     * @param srcFilename
     * @param storagePath
     * @param width
     * @param height
     * @return
     */
    NutMap uploadImageScale(InputStream srcImg, String srcFilename,String storagePath,int width,int height);
}
