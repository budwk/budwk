package com.budwk.starter.wechat.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.json.Json;
import org.nutz.lang.Encoding;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.MapKeyConvertor;
import org.nutz.lang.Mirror;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.Xmls;
import org.nutz.lang.random.R;
import org.nutz.lang.tmpl.Tmpl;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.View;
import org.nutz.mvc.view.HttpStatusView;
import org.nutz.mvc.view.RawView;
import org.nutz.mvc.view.ViewWrapper;
import com.budwk.starter.wechat.bean.WxArticle;
import com.budwk.starter.wechat.bean.WxEventType;
import com.budwk.starter.wechat.bean.WxImage;
import com.budwk.starter.wechat.bean.WxInMsg;
import com.budwk.starter.wechat.bean.WxMsgType;
import com.budwk.starter.wechat.bean.WxMusic;
import com.budwk.starter.wechat.bean.WxOutMsg;
import com.budwk.starter.wechat.bean.WxVideo;
import com.budwk.starter.wechat.bean.WxVoice;
import com.budwk.starter.wechat.mvc.WxView;
import com.budwk.starter.wechat.repo.com.qq.weixin.mp.aes.AesException;
import com.budwk.starter.wechat.repo.com.qq.weixin.mp.aes.WXBizMsgCrypt;
import com.budwk.starter.wechat.spi.WxHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Wxs {

    private static final Log log = Logs.get();

    public static boolean DEV_MODE = false;

    public static void enableDevMode() {
        DEV_MODE = true;
        log.warn("nutzwx DevMode=true now");
    }

    /**
     * 根据提交参数，生成签名
     *
     * @param map
     *            要签名的集合
     * @param key
     *            商户秘钥
     * @return 签名
     *
     * @see <a href=
     *      "https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_3">
     *      微信商户平台签名算法</a>
     *
     */
    public static String genPaySign(Map<String, Object> map, String key, String signType) {
        String[] nms = map.keySet().toArray(new String[map.size()]);
        Arrays.sort(nms);
        StringBuilder sb = new StringBuilder();
        signType = signType == null ? "MD5" : signType.toUpperCase();
        boolean isMD5 = "MD5".equals(signType);
        for (String nm : nms) {
            Object v = map.get(nm);
            if (null == v)
                continue;
            // JSSDK 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。
            // 但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符
            if (isMD5 && "timestamp".equals(nm)) {
                nm = "timeStamp";
            }
            String s = v.toString();
            if (Strings.isBlank(s))
                continue;
            sb.append(nm).append('=').append(s).append('&');
        }
        sb.append("key=").append(key);
        return Lang.digest(signType, sb).toUpperCase();
    }

    /**
     * 默认采用 MD5 方式的签名
     *
     * @see #genPaySign(Map, String, String)
     */
    public static String genPaySignMD5(Map<String, Object> map, String key) {
        return genPaySign(map, key, "MD5");
    }

    /**
     * 为参数集合填充随机数，以及生成签名
     *
     * @param map
     *            参数集合
     * @param key
     *            商户秘钥
     *
     * @see #genPaySignMD5(Map, String)
     */
    public static void fillPayMap(Map<String, Object> map, String key) {
        // 首先确保有随机数
        map.put("nonce_str", "" + R.random(10000000, 100000000));

        // 填充签名
        String sign = genPaySignMD5(map, key);
        map.put("sign", sign);
    }

    /**
     * 检查一下支付平台返回的 xml，是否签名合法，如果合法，转换成一个 map
     *
     * @param xml
     *            支付平台返回的 xml
     * @param key
     *            商户秘钥
     * @return 合法的 Map
     *
     * @throws "e.wx.sign.invalid"
     *
     * @see #checkPayReturnMap(NutMap, String)
     * @see <a href=
     *      "https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1">
     *      支付平台文档</a>
     */
    public static NutMap checkPayReturn(String xml, String key) {
        try {
            NutMap map = getkPayReturn(xml);
            return checkPayReturnMap(map, key);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw Lang.makeThrow("e.wx.pay.re.error : %s", xml);
        }
    }

    public static NutMap getkPayReturn(String xml) {
        try {
            return Xmls.asMap(xmls().parse(new InputSource(new StringReader(xml)))
                                          .getDocumentElement());
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw Lang.makeThrow("e.wx.pay.re.error : %s", xml);
        }
    }

    /**
     * 检查一下支付平台返回的 xml，是否签名合法，如果合法，转换成一个 map
     *
     * @param map
     *            描述支付平台返回的 xml 信息的 Map 对象
     * @param key
     *            商户秘钥
     * @return 合法的 Map
     *
     * @throws "e.wx.sign.invalid"
     *
     * @see <a href=
     *      "https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1">
     *      支付平台文档</a>
     */
    public static NutMap checkPayReturnMap(NutMap map, String key) {
        if (!map.containsKey("sign")) {
            throw Lang.makeThrow("e.wx.pay.re.error : %s", map);
        }
        String sign = map.remove("sign").toString();
        String sign2 = Wxs.genPaySignMD5(map, key);
        if (!sign.equals(sign2))
            throw Lang.makeThrow("e.wx.pay.re.sign.invalid : expect '%s' but '%s'", sign2, sign);
        return map;
    }

    public static WxInMsg convert(InputStream in) {
        return convert(in, WxInMsg.class);
    }

    public static WxInMsg convert(String data) {
        return convert(new ByteArrayInputStream(data.getBytes()));
    }

    public static <T> T convert(String data, Class<T> klass) {
        return convert(new ByteArrayInputStream(data.getBytes()), klass);
    }

    /**
     * 将一个输入流转为WxInMsg
     */
    public static <T> T convert(InputStream in, Class<T> klass) {
        Map<String, Object> map;
        String raw;
        try {
            // fix:
            // DocumentBuilder不支持直接传入Reader,如果直接传InputStream的话又按系统默认编码,所以,用InputSource中转一下
            Reader r = Streams.utf8r(in);
            raw = Lang.readAll(r);
            map = Xmls.asMap(xmls().parse(new InputSource(new StringReader(raw)))
                                   .getDocumentElement());
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
        Lang.convertMapKey(map, new MapKeyConvertor() {
            @Override
            public String convertKey(String key) {
                return Strings.lowerFirst(key);
            }
        }, true);

        if (DEV_MODE) {
            log.debug("Income >> \n" + Json.toJson(map));
        }
        T t = Lang.map2Object(map, klass);
        if (t instanceof WxInMsg)
            ((WxInMsg) t).raw(raw);
        else if (t instanceof WxOutMsg)
            ((WxOutMsg) t).raw(raw);
        return t;
    }

    /**
     * 检查signature是否合法
     */
    public static boolean check(String token, String signature, String timestamp, String nonce) {
        // 防范长密文攻击
        if (signature == null
            || signature.length() > 128
            || timestamp == null
            || timestamp.length() > 128
            || nonce == null
            || nonce.length() > 128) {
            log.warnf("bad check : signature=%s,timestamp=%s,nonce=%s",
                      signature,
                      timestamp,
                      nonce);
            return false;
        }
        ArrayList<String> tmp = new ArrayList<String>();
        tmp.add(token);
        tmp.add(timestamp);
        tmp.add(nonce);
        Collections.sort(tmp);
        String key = Lang.concat("", tmp).toString();
        return Lang.sha1(key).equalsIgnoreCase(signature);
    }

    /**
     * 根据不同的消息类型,调用WxHandler不同的方法
     */
    public static WxOutMsg handle(WxInMsg msg, WxHandler handler) {
        WxOutMsg out = null;
        switch (WxMsgType.valueOf(msg.getMsgType())) {
        case text:
            out = handler.text(msg);
            break;
        case image:
            out = handler.image(msg);
            break;
        case voice:
            out = handler.voice(msg);
            break;
        case video:
            out = handler.video(msg);
            break;
        case location:
            out = handler.location(msg);
            break;
        case link:
            out = handler.link(msg);
            break;
        case event:
            out = handleEvent(msg, handler);
            break;
        case shortvideo:
            out = handler.shortvideo(msg);
            break;
        default:
            log.infof("New MsyType=%s ? fallback to defaultMsg", msg.getMsgType());
            out = handler.defaultMsg(msg);
            break;
        }
        return out;
    }

    /**
     * 根据msg中Event的类型,调用不同的WxHandler方法
     */
    public static WxOutMsg handleEvent(WxInMsg msg, WxHandler handler) {
        WxOutMsg out = null;
        switch (WxEventType.valueOf(msg.getEvent())) {
        case subscribe:
            out = handler.eventSubscribe(msg);
            break;
        case unsubscribe:
            out = handler.eventUnsubscribe(msg);
            break;
        case subscribe_msg_popup_event:
            out = handler.eventSubscribeMsgPopup(msg);
            break;
        case subscribe_msg_change_event:
            out = handler.eventSubscribeMsgChange(msg);
            break;
        case subscribe_msg_sent_event:
            out = handler.eventSubscribeMsgSent(msg);
            break;
        case LOCATION:
            out = handler.eventLocation(msg);
            break;
        case SCAN:
            out = handler.eventScan(msg);
            break;
        case CLICK:
            out = handler.eventClick(msg);
            break;
        case VIEW:
            out = handler.eventView(msg);
            break;
        case TEMPLATESENDJOBFINISH:
            out = handler.eventTemplateJobFinish(msg);
            break;
        default:
            log.infof("New EventType=%s ? fallback to defaultMsg", msg.getMsgType());
            out = handler.defaultMsg(msg);
            break;
        }
        return out;
    }

    /**
     * 根据输入信息,修正发送信息的发送者和接受者
     */
    public static WxOutMsg fix(WxInMsg in, WxOutMsg out) {
        out.setFromUserName(in.getToUserName());
        out.setToUserName(in.getFromUserName());
        out.setCreateTime(System.currentTimeMillis() / 1000);
        return out;
    }

    /**
     * 创建一条文本响应
     */
    public static WxOutMsg respText(String to, String content) {
        WxOutMsg out = new WxOutMsg("text");
        out.setContent(content);
        if (to != null)
            out.setToUserName(to);
        return out;
    }

    /**
     * 创建一条图片响应
     */
    public static WxOutMsg respImage(String to, String mediaId) {
        WxOutMsg out = new WxOutMsg("image");
        out.setImage(new WxImage(mediaId));
        if (to != null)
            out.setToUserName(to);
        return out;
    }

    /**
     * 创建一个语音响应
     */
    public static WxOutMsg respVoice(String to, String mediaId) {
        WxOutMsg out = new WxOutMsg("voice");
        out.setVoice(new WxVoice(mediaId));
        if (to != null)
            out.setToUserName(to);
        return out;
    }

    /**
     * 创建一个视频响应
     */
    public static WxOutMsg respVideo(String to, String mediaId, String title, String description) {
        WxOutMsg out = new WxOutMsg("video");
        out.setVideo(new WxVideo(mediaId, title, description));
        if (to != null)
            out.setToUserName(to);
        return out;
    }

    /**
     * 创建一个音乐响应
     */
    public static WxOutMsg respMusic(String to,
                                     String title,
                                     String description,
                                     String musicURL,
                                     String hQMusicUrl,
                                     String thumbMediaId) {
        WxOutMsg out = new WxOutMsg("music");
        out.setMusic(new WxMusic(title, description, musicURL, hQMusicUrl, thumbMediaId));
        if (to != null)
            out.setToUserName(to);
        return out;
    }

    /**
     * 创建一个图文响应
     */
    public static WxOutMsg respNews(String to, WxArticle... articles) {
        return respNews(to, Arrays.asList(articles));
    }

    /**
     * 创建一个图文响应
     */
    public static WxOutMsg respNews(String to, List<WxArticle> articles) {
        WxOutMsg out = new WxOutMsg("news");
        out.setArticles(articles);
        if (to != null)
            out.setToUserName(to);
        return out;
    }

    // public static StringBuilder toWxXml(WxOutMsg out) {
    // Map<String, Object> map = Lang.obj2map(out);
    // StringBuilder sb = new StringBuilder();
    // sb.append("<xml>\n");
    // toWxXml(sb, map);
    // sb.append("</xml>");
    // return sb;
    // }
    //
    // @SuppressWarnings("unchecked")
    // public static void toWxXml(StringBuilder sb, Map<String, Object> map) {
    // for (Entry<String, Object> en : map.entrySet()) {
    // Object obj = en.getValue();
    // if (obj == null)
    // continue;
    // if (obj instanceof Number && ((Number)obj).intValue() == 0) {
    // continue;
    // }
    // sb.append("<" + Strings.upperFirst(en.getKey()) + ">");
    // if (obj instanceof String) {
    // sb.append("<![CDATA[").append(obj).append("]]>");
    // } else if (obj instanceof Number) {
    // sb.append(obj);
    // } else if (obj instanceof Map) {
    // sb.append("\n");
    // toWxXml(sb, ((Map<String, Object>)obj));
    // } else if (obj instanceof List) {
    // sb.append("\n");
    // for (Object _obj : ((Collection<Object>)obj)) {
    // toWxXml(sb, ((Map<String, Object>)_obj));
    // }
    // } else {
    // throw Lang.noImplement();
    // }
    // sb.append("</" + Strings.upperFirst(en.getKey()) + ">\n");
    // }
    // }

    public static String cdata(String str) {
        if (Strings.isBlank(str))
            return "";
        return "<![CDATA[" + str.replaceAll("]]", "__") + "]]>";
    }

    public static String tag(String key, String val) {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(key).append(">");
        sb.append(val).append("");
        sb.append("</").append(key).append(">\n");
        return sb.toString();
    }

    /**
     * @see #asXml(Writer, WxOutMsg)
     */
    public static String asXml(WxOutMsg msg) {
        if (msg.raw() != null)
            return msg.raw();
        StringWriter sw = new StringWriter();
        asXml(sw, msg);
        return sw.toString();
    }

    /**
     * 将一个WxOutMsg转为被动响应所需要的XML文本
     *
     * @param msg
     *            微信消息输出对象
     *
     * @return 输出的 XML 文本
     */
    public static void asXml(Writer writer, WxOutMsg msg) {
        try {
            Writer _out = writer;
            if (DEV_MODE) {
                writer = new StringWriter();
            }
            writer.write("<xml>\n");
            writer.write(tag("ToUserName", cdata(msg.getToUserName())));
            writer.write(tag("FromUserName", cdata(msg.getFromUserName())));
            writer.write(tag("CreateTime", "" + msg.getCreateTime()));
            writer.write(tag("MsgType", cdata(msg.getMsgType())));
            switch (WxMsgType.valueOf(msg.getMsgType())) {
            case text:
                writer.write(tag("Content", cdata(msg.getContent())));
                break;
            case image:
                writer.write(tag("Image", tag("MediaId", msg.getImage().getMediaId())));
                break;
            case voice:
                writer.write(tag("Voice", tag("MediaId", msg.getVoice().getMediaId())));
                break;
            case video:
                writer.write("<Video>\n");
                writer.write(tag("MediaId", cdata(msg.getVideo().getMediaId())));
                if (msg.getVideo().getTitle() != null)
                    writer.write(tag("Title", cdata(msg.getVideo().getTitle())));
                if (msg.getVideo().getDescription() != null)
                    writer.write(tag("Description", cdata(msg.getVideo().getDescription())));
                writer.write("</Video>\n");
                break;
            case music:
                writer.write("<Music>\n");
                WxMusic music = msg.getMusic();
                if (music.getTitle() != null)
                    writer.write(tag("Title", cdata(music.getTitle())));
                if (music.getDescription() != null)
                    writer.write(tag("Description", cdata(music.getDescription())));
                if (music.getMusicUrl() != null)
                    writer.write(tag("MusicUrl", cdata(music.getMusicUrl())));
                if (music.getHQMusicUrl() != null)
                    writer.write(tag("HQMusicUrl", cdata(music.getHQMusicUrl())));
                writer.write(tag("ThumbMediaId", cdata(music.getThumbMediaId())));
                writer.write("</Music>\n");
                break;
            case news:
                writer.write(tag("ArticleCount", "" + msg.getArticles().size()));
                writer.write("<Articles>\n");
                for (WxArticle article : msg.getArticles()) {
                    writer.write("<item>\n");
                    if (article.getTitle() != null)
                        writer.write(tag("Title", cdata(article.getTitle())));
                    if (article.getDescription() != null)
                        writer.write(tag("Description", cdata(article.getDescription())));
                    if (article.getPicUrl() != null)
                        writer.write(tag("PicUrl", cdata(article.getPicUrl())));
                    if (article.getUrl() != null)
                        writer.write(tag("Url", cdata(article.getUrl())));
                    writer.write("</item>\n");
                }
                writer.write("</Articles>\n");
                break;
            case transfer_customer_service:
                if (msg.getKfAccount() != null) {
                    writer.write("<TransInfo>\n");
                    writer.write(tag("KfAccount", cdata(msg.getKfAccount().getAccount())));
                    writer.write("</TransInfo>\n");
                }
                break;
            default:
                break;
            }
            writer.write("</xml>");
            if (DEV_MODE) {
                String str = writer.toString();
                log.debug("Outcome >>\n" + str);
                _out.write(str);
            }
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * @see #asJson(Writer, WxOutMsg)
     */
    public static String asJson(WxOutMsg msg) {
        StringWriter sw = new StringWriter();
        asJson(sw, msg);
        return sw.toString();
    }

    /**
     * 将一个WxOutMsg转为主动信息所需要的Json文本
     *
     * @param msg
     *            微信消息输出对象
     *
     * @return 输出的 JSON 文本
     */
    public static void asJson(Writer writer, WxOutMsg msg) {
        NutMap map = new NutMap();
        map.put("touser", msg.getToUserName());
        map.put("msgtype", msg.getMsgType());
        switch (WxMsgType.valueOf(msg.getMsgType())) {
        case text:
            map.put("text", new NutMap().setv("content", msg.getContent()));
            break;
        case image:
            map.put("image", new NutMap().setv("media_id", msg.getImage().getMediaId()));
            break;
        case voice:
            map.put("voice", new NutMap().setv("media_id", msg.getVoice().getMediaId()));
            break;
        case video:
            NutMap _video = new NutMap();
            _video.setv("media_id", msg.getVideo().getMediaId());
            if (msg.getVideo().getTitle() != null)
                _video.put("title", (msg.getVideo().getTitle()));
            if (msg.getVideo().getDescription() != null)
                _video.put("description", (msg.getVideo().getDescription()));
            map.put("video", _video);
            break;
        case music:
            NutMap _music = new NutMap();
            WxMusic music = msg.getMusic();
            if (music.getTitle() != null)
                _music.put("title", (music.getTitle()));
            if (music.getDescription() != null)
                _music.put("description", (music.getDescription()));
            if (music.getMusicUrl() != null)
                _music.put("musicurl", (music.getMusicUrl()));
            if (music.getHQMusicUrl() != null)
                _music.put("hqmusicurl", (music.getHQMusicUrl()));
            _music.put("thumb_media_id", (music.getThumbMediaId()));
            break;
        case news:
            NutMap _news = new NutMap();
            List<NutMap> list = new ArrayList<NutMap>();
            for (WxArticle article : msg.getArticles()) {
                NutMap item = new NutMap();
                if (article.getTitle() != null)
                    item.put("title", (article.getTitle()));
                if (article.getDescription() != null)
                    item.put("description", (article.getDescription()));
                if (article.getPicUrl() != null)
                    item.put("picurl", (article.getPicUrl()));
                if (article.getUrl() != null)
                    item.put("url", (article.getUrl()));
                list.add(item);
            }
            _news.put("articles", list);
            map.put("news", _news);
            break;
        case mpnews:
            map.put("mpnews", new NutMap().setv("media_id", msg.getMedia_id()));
            break;
        case wxcard:
            map.put("wxcard",
                    new NutMap().setv("card_id", msg.getCard().getId())
                                .setv("card_ext", msg.getCard().getExt()));
            break;
        default:
            break;
        }
        Json.toJson(writer, map);
    }

    /**
     * 用一个wxHandler处理对应的用户请求
     */
    public static View handle(WxHandler wxHandler, HttpServletRequest req, String key)
            throws IOException {
        if (wxHandler == null) {
            log.info("WxHandler is NULL");
            return HttpStatusView.HTTP_502;
        }
        String signature = req.getParameter("signature");
        String timestamp = req.getParameter("timestamp");
        String nonce = req.getParameter("nonce");
        String msg_signature = req.getParameter("msg_signature");
        String encrypt_type = req.getParameter("encrypt_type");
        if (!wxHandler.check(signature, timestamp, nonce, key)) {
            log.info("token is invalid");
            return HttpStatusView.HTTP_502;
        }
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            String echostr = req.getParameter("echostr");
            log.info("GET? return echostr=" + echostr);
            return new ViewWrapper(new RawView(null), echostr);
        }
        String postData = Streams.readAndClose(new InputStreamReader(req.getInputStream(),
                                                                     Encoding.CHARSET_UTF8));

        if ("aes".equals(encrypt_type)) {
            WXBizMsgCrypt msgCrypt = wxHandler.getMsgCrypt();
            try {
                // 若抛出Illegal key size,请更新JDK的加密库为不限制长度
                postData = msgCrypt.decryptMsg(msg_signature, timestamp, nonce, postData);
            }
            catch (AesException e) {
                return new HttpStatusView(403);
            }
        }
        WxInMsg in = Wxs.convert(postData);
        in.setExtkey(key);
        WxOutMsg out = wxHandler.handle(in);
        if (out != null) {
            Wxs.fix(in, out);
        }
        return new ViewWrapper(WxView.me, out);
    }

    /**
     * 下载媒体文件(放到临时目录中), 返回对应文件
     *
     * @param accessToken
     * @param mediaId
     */
    public static File downloadMedia(String accessToken, String mediaId) {
        String url = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token="
                     + accessToken
                     + "&media_id="
                     + mediaId;
        File mf = null;
        for (int i = 0; i < 3; i++) {
            InputStream in = null;
            OutputStream out = null;
            try {
                Response resp = Http.get(url, 60 * 1000);
                if (resp.isOK()) {
                    in = resp.getStream();
                    mf = File.createTempFile(mediaId, ".wxmedia");
                    out = new FileOutputStream(mf);
                    Streams.writeAndClose(out, in);
                    // 检查一下是不是报错
                    if (mf.length() < 128) {
                        byte[] data = Files.readBytes(mf);
                        if (data[0] == '{') { // 看上去是个json,悲催了...
                            // 多媒体文件怎么可能是{开头,抛错吧
                            throw new IllegalArgumentException("mediaId="
                                                               + mediaId
                                                               + ","
                                                               + new String(data));
                        }
                    }
                    log.debugf("media download success mediaId=" + mediaId);
                    break;
                } else {}
            }
            catch (Throwable e) {
                log.infof("download %s fail", mediaId, e);
            }
            finally {
                Streams.safeClose(in);
                Streams.safeClose(out);
            }
        }
        return mf;
    }

    public static WxOutMsg respText(String content) {
        return respText(null, content);
    }

    public static String pojoClass2MapClass(Class<?> klass) {
        StringBuilder sb = new StringBuilder();
        sb.append("package " + klass.getPackage().getName() + ";\r\n\r\n");
        sb.append("import org.nutz.lang.util.NutMap;\r\n\r\n");
        sb.append("@SuppressWarnings(\"serial\")\r\n");
        sb.append("public class " + klass.getSimpleName() + " extends NutMap {\r\n");
        for (Field field : klass.getDeclaredFields()) {
            mapField(sb, klass, field);
        }
        sb.append("}");
        return sb.toString();
    }

    @SuppressWarnings("rawtypes")
    public static void mapField(StringBuilder sb, Class<?> klass, Field field) {
        sb.append("\r\n");
        String fieldName = field.getName();
        String className = klass.getSimpleName();
        Mirror mirror = Mirror.me(field.getType());
        String getterTmpl = "return (${fieldType})get(\"${fieldName}\")";
        if (mirror.isPrimitiveNumber()) {
            if (mirror.isBoolean()) {
                getterTmpl = "return getBoolean(\"${fieldName}\", false)";
            } else {
                getterTmpl = "return get"
                             + Strings.upperFirst(mirror.getType().getSimpleName())
                             + "(\"${fieldName}\", 0)";
            }
        }

        Tmpl tmpl = Tmpl.parse("    public ${className} set${upperFieldName}(${fieldType} ${fieldName}){\r\n"
                               + "        put(\"${fieldName}\", ${fieldName});\r\n"
                               + "        return this;\r\n"
                               + "    }\r\n"
                               + "\r\n"
                               + "    public ${fieldType} get${upperFieldName}(){\r\n"
                               + "        "
                               + getterTmpl
                               + ";\r\n"
                               + "    }\r\n");
        NutMap ctx = new NutMap().setv("className", className).setv("fieldName", fieldName);
        ctx.setv("upperFieldName", Strings.upperFirst(fieldName));
        ctx.setv("fieldType", field.getType().getSimpleName());
        sb.append(tmpl.render(ctx));
    }

    public static DocumentBuilder xmls()
            throws ParserConfigurationException, SAXException, IOException {
        // 修复XXE form
        // https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=23_5
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        String FEATURE = null;
        FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
        factory.setFeature(FEATURE, true);
        FEATURE = "http://xml.org/sax/features/external-general-entities";
        factory.setFeature(FEATURE, false);
        FEATURE = "http://xml.org/sax/features/external-parameter-entities";
        factory.setFeature(FEATURE, false);
        FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
        factory.setFeature(FEATURE, false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        return factory.newDocumentBuilder();
    }
}
