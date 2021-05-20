# wk-starter-storage

文件存储组件

## 配置说明
####本地存储
```yaml
storage:
  enable: true
  #本地方式
  provider: local
  local:
    #本地存储位置
    path: /data
```
####FTP存储
```yaml
storage:
  enable: true
  #FTP方式
  provider: ftp
  ftp:
    #存储位置
    path: /data
```
####天翼云OSS存储
```yaml
storage:
  enable: true
  provider: ctyun
  ctyun:
    oss:
      access-id: 123
      #access secret key
      secret: 567
      endpoint: https://oos-cn.ctyunapi.cn
      #默认存储桶位置
      bucket: myfiles
    
```
## 使用说明

```java

@Inject
private StorageServer storageServer;

//文件上传
NutMap upload(InputStream ins, String filename, String path)

//文件下载
InputStream downloadStream(String path, String filename)
void download(String path, String filename, OutputStream os)

//删除文件
boolean delete(String path, String filename)

//上传原图+水印图
NutMap uploadImageWatermark(InputStream srcImg, String srcFilename, String storagePath, InputStream pressImg, int pos, int margin, float alpha)

//上传原图+文字水印
NutMap uploadImagePressText(InputStream srcImg, String srcFilename,String storagePath,String pressText,Color color,Font font,int x,int y,float alpha)

//上传原图+缩略图
NutMap uploadImageScale(InputStream srcImg, String srcFilename, String storagePath, int width, int height)
