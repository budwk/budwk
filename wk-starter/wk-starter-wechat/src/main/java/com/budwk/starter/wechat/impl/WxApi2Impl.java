package com.budwk.starter.wechat.impl;

import org.nutz.castor.Castors;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.http.sender.FilePostSender;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.*;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;
import com.budwk.starter.wechat.bean.*;
import com.budwk.starter.wechat.spi.WxResp;
import com.budwk.starter.wechat.util.WxPaySSL;
import com.budwk.starter.wechat.util.WxPaySign;
import com.budwk.starter.wechat.util.Wxs;

import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WxApi2Impl extends AbstractWxApi2 {

    private static final Log log = Logs.get().setTag("weixin");

    public WxApi2Impl() {
    }

    public WxApi2Impl(String token, String appid, String appsecret, String openid, String encodingAesKey) {
        super(token, appid, appsecret, openid, encodingAesKey);
    }

    // ===============================
    // 基本API

    @Override
    public WxResp send(WxOutMsg out) {
        if (out.getFromUserName() == null)
            out.setFromUserName(openid);
        String str = Wxs.asJson(out);
        if (Wxs.DEV_MODE)
            log.debug("api out msg>\n" + str);
        return call("/message/custom/send", METHOD.POST, str);
    }

    @Override
    public List<String> getcallbackip() {
        return get("/getcallbackip").getList("ip_list", String.class);
    }

    // -------------------------------
    // 用户API

    @Override
    public WxResp user_info(String openid, String lang) {
        return get("/user/info", "openid", openid, "lang", Strings.sBlank(lang, "zh_CN"));
    }

    @Override
    public WxResp user_info_updatemark(String openid, String remark) {
        return postJson("/user/info/updateremark", "openid", openid, "remark", remark);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void user_get(Each<String> each) {
        String next_openid = null;
        WxResp map = null;
        int count = 0;
        int total = 0;
        int index = 0;
        while (true) {
            if (next_openid == null)
                map = call("/user/get", METHOD.GET, null);
            else
                map = call("/user/get?next_openid=" + next_openid, METHOD.GET, null);
            count = ((Number) map.get("count")).intValue();
            if (count < 1)
                return;
            total = ((Number) map.get("total")).intValue();
            next_openid = Strings.sNull(map.get("next_openid"));
            if (next_openid.length() == 0)
                next_openid = null;
            List<String> openids = (List<String>) ((Map<String, Object>) map.get("data")).get("openid");
            for (String openid : openids) {
                try {
                    each.invoke(index, openid, total);
                } catch (ExitLoop e) {
                    return;
                } catch (ContinueLoop e) {
                    continue;
                } catch (LoopException e) {
                    throw e;
                }
                index++;
            }
        }
    }

    @Override
    public WxResp groups_create(WxGroup group) {
        return postJson("/groups/create", "group", group);
    }

    @Override
    public WxResp groups_get() {
        return call("/groups/get", METHOD.GET, null);
    }

    @Override
    public WxResp groups_getid(String openid) {
        return postJson("/groups/getid", "openid", openid);
    }

    @Override
    public WxResp groups_update(WxGroup group) {
        return postJson("/groups/update", "group", group);
    }

    @Override
    public WxResp groups_member_update(String openid, String to_groupid) {
        return postJson("/groups/members/update", "openid", openid, "to_groupid", to_groupid);
    }

    @Override
    public WxResp tags_create(WxTag tag) {
        return postJson("/tags/create", "tag", tag);
    }

    @Override
    public WxResp tags_get() {
        return call("/tags/get", METHOD.GET, null);
    }

    @Override
    public WxResp tags_update(WxTag tag) {
        return postJson("/tags/update", "tag", tag);
    }

    @Override
    public WxResp tags_delete(WxTag tag) {
        return postJson("/tags/delete", "tag", tag);
    }

    @Override
    public WxResp tag_getusers(String tagid, String nextOpenid) {
        return postJson("/user/tag/get", "tagid", tagid, "next_openid", nextOpenid);
    }

    @Override
    public WxResp tags_members_batchtagging(List<String> openids, String tagid) {
        return postJson("/tags/members/batchtagging", "openid_list", openids, "tagid", tagid);
    }

    @Override
    public WxResp tags_members_chuntagging(List<String> openids, String tagid) {
        return postJson("/tags/members/batchuntagging", "openid_list", openids, "tagid", tagid);
    }

    @Override
    public WxResp tags_getidlist(String openid) {
        return postJson("/tags/getidlist", "openid", openid);
    }

    // -------------------------------------------------------
    // 二维码API

    @Override
    public WxResp qrcode_create(Object scene_id, int expire_seconds) {
        NutMap params = new NutMap();
        NutMap scene;
        // 临时二维码
        if (expire_seconds > 0) {
            params.put("expire_seconds", expire_seconds);

            // 临时整型二维码
            if (scene_id instanceof Number) {
                params.put("action_name", "QR_SCENE");
                scene = Lang.map("scene_id", Castors.me().castTo(scene_id, Integer.class));
                // 临时字符串二维码
            } else {
                params.put("action_name", "QR_STR_SCENE");
                scene = Lang.map("scene_str", scene_id.toString());
            }
        }
        // 永久二维码
        else if (scene_id instanceof Number) {
            params.put("action_name", "QR_LIMIT_SCENE");
            scene = Lang.map("scene_id", Castors.me().castTo(scene_id, Integer.class));
        }
        // 永久字符串二维码
        else {
            params.put("action_name", "QR_LIMIT_STR_SCENE");
            scene = Lang.map("scene_str", scene_id.toString());
        }
        params.put("action_info", Lang.map("scene", scene));
        return postJson("/qrcode/create", params);
    }

    @Override
    public String qrcode_show(String ticket) {
        return mpBase + "/cgi-bin/showqrcode?ticket=" + ticket;
    }

    @Override
    public String shorturl(String long_url) {
        return postJson("/shorturl", new NutMap().setv("long_url", long_url).setv("action", "long2short"))
                .getString("short_url");
    }

    // --------------------------------------------------------
    // 模板消息

    @Override
    public WxResp template_api_set_industry(String industry_id1, String industry_id2) {
        return postJson("/template/api_set_industry", "industry_id1", industry_id1, "industry_id2", industry_id2);
    }

    @Override
    public WxResp template_api_add_template(String template_id_short) {
        return postJson("/template/api_add_template", "template_id_short", template_id_short);
    }

    @Override
    public WxResp template_api_del_template(String template_id) {
        return postJson("/template/del_private_template", "template_id", template_id);
    }

    @Override
    public WxResp template_send(String touser, String template_id, String url, Map<String, WxTemplateData> data) {
        return postJson("/message/template/send",
                "touser",
                touser,
                "template_id",
                template_id,
                "url",
                url,
                "data",
                data);
    }

    @Override
    public WxResp template_send(String touser,
                                String template_id,
                                String url,
                                Map<String, Object> miniprogram,
                                Map<String, WxTemplateData> data) {
        return postJson("/message/template/send",
                "touser",
                touser,
                "template_id",
                template_id,
                "url",
                url,
                "miniprogram",
                miniprogram,
                "data",
                data);
    }

    // ------------------------------------------------------------
    // 微信卡券

    /**
     * 微信卡券：创建卡券
     *
     * @param card
     * @return
     * @author JinYi
     */
    @Override
    public WxResp card_create(NutMap card) {
        // 由于创建卡券API中没有“/cgi-bin”，所以uri不能只写“/card/create”
        return postJson("https://api.weixin.qq.com/card/create", "card", card);
    }

    /**
     * 微信卡券：投放卡券，创建二维码
     *
     * @param body
     * @return
     * @author JinYi
     */
    @Override
    public WxResp card_qrcode_create(NutMap body) {
        // 由于投放卡券创建二维码API中没有“/cgi-bin”，所以uri不能只写“/card/qrcode/create”
        return postJson("https://api.weixin.qq.com/card/qrcode/create", body);
    }

    /**
     * 微信卡券：查询Code
     *
     * @param code         卡券Code码，一张卡券的唯一标识，必填
     * @param cardId       卡券ID代表一类卡券，null表示不填此参数。自定义code卡券必填
     * @param checkConsume 是否校验code核销状态，填入true和false时的code异常状态返回数据不同，null表示不填此参数
     * @return
     * @author JinYi
     */
    @Override
    public WxResp card_code_get(String code, String cardId, Boolean checkConsume) {
        NutMap body = NutMap.NEW().addv("code", code);
        if (cardId != null) {
            body.addv("card_id", cardId);
        }
        if (checkConsume != null) {
            body.addv("check_consume", checkConsume);
        }

        // 由于查询Code API中没有“/cgi-bin”，所以uri不能只写“/card/code/get”
        return postJson("https://api.weixin.qq.com/card/code/get", body);
    }

    /**
     * 微信卡券：查询Code
     *
     * @param code   卡券Code码，一张卡券的唯一标识，必填
     * @param cardId 卡券ID代表一类卡券，null表示不填此参数。自定义code卡券必填
     * @return
     * @author JinYi
     */
    @Override
    public WxResp card_code_get(String code, String cardId) {
        return card_code_get(code, cardId, null);
    }

    /**
     * 微信卡券：查询Code
     *
     * @param code 卡券Code码，一张卡券的唯一标识，必填
     * @return
     * @author JinYi
     */
    @Override
    public WxResp card_code_get(String code) {
        return card_code_get(code, null, null);
    }

    /**
     * 微信卡券：核销卡券<br/>
     * 建议在调用核销卡券接口之前调用查询Code接口<br/>
     * 以便在核销之前对非法状态的Code（如转赠中、已删除、已核销等）做出处理
     *
     * @param code   需核销的Code码，必填
     * @param cardId 卡券ID代表一类卡券，null表示不填此参数。创建卡券时use_custom_code填写true时必填。非自定义Code不必填写
     * @return
     * @author JinYi
     */
    @Override
    public WxResp card_code_consume(String code, String cardId) {
        NutMap body = NutMap.NEW().addv("code", code);
        if (cardId != null) {
            body.addv("card_id", cardId);
        }

        // 由于核销卡券API中没有“/cgi-bin”，所以uri不能只写“/card/code/consume”
        return postJson("https://api.weixin.qq.com/card/code/consume", body);
    }

    /**
     * 微信卡券：核销卡券<br/>
     * 建议在调用核销卡券接口之前调用查询Code接口<br/>
     * 以便在核销之前对非法状态的Code（如转赠中、已删除、已核销等）做出处理
     *
     * @param code 需核销的Code码，必填
     * @return
     * @author JinYi
     */
    @Override
    public WxResp card_code_consume(String code) {
        return card_code_consume(code, null);
    }

    // 自定义菜单

    @Override
    public WxResp menu_create(NutMap map) {
        return postJson("/menu/create", map);
    }

    @Override
    public WxResp menu_create(List<WxMenu> button) {
        return postJson("/menu/create", "button", button);
    }

    @Override
    public WxResp menu_get() {
        return call("/menu/get", METHOD.GET, null);
    }

    @Override
    public WxResp menu_delete() {
        return call("/menu/delete", METHOD.GET, null);
    }

    // 多媒体上传下载

    @Override
    public WxResp media_upload(String type, File f) {
        if (type == null)
            throw new NullPointerException("media type is NULL");
        if (f == null)
            throw new NullPointerException("meida file is NULL");
        String url = String.format("%s/cgi-bin/media/upload?access_token=%s&type=%s", wxBase, getAccessToken(), type);
        Request req = Request.create(url, METHOD.POST);
        req.getParams().put("media", f);
        Response resp = new FilePostSender(req).send();
        if (!resp.isOK())
            throw new IllegalStateException("media upload file, resp code=" + resp.getStatus());
        return Json.fromJson(WxResp.class, resp.getReader("UTF-8"));
    }

    //获取临时素材
    @Override
    public NutResource media_get(String mediaId) {
        String url = String.format("%s/cgi-bin/media/get?access_token=%s&media_id=%s",
                wxBase,
                getAccessToken(),
                mediaId);
        final Response resp = Sender.create(Request.create(url, METHOD.GET)).send();
        if (!resp.isOK())
            throw new IllegalStateException("download media file, resp code=" + resp.getStatus());
        String disposition = resp.getHeader().get("Content-disposition");
        return new WxResource(disposition, resp.getStream());
    }

    //获取从JSSDK的uploadVoice接口上传的临时语音素材，格式为speex
    @Override
    public NutResource media_get_jssdk(String mediaId) {
        String url = String.format("%s/cgi-bin/media/get/jssdk?access_token=%s&media_id=%s",
                wxBase,
                getAccessToken(),
                mediaId);
        final Response resp = Sender.create(Request.create(url, METHOD.GET)).send();
        if (!resp.isOK())
            throw new IllegalStateException("download media jssdk file, resp code=" + resp.getStatus());
        String disposition = resp.getHeader().get("Content-disposition");
        return new WxResource(disposition, resp.getStream());
    }

    // 高级群发
    @Override
    public WxResp mass_uploadnews(List<WxArticle> articles) {
        return postJson("/message/mass/uploadnews", "articles", articles);
    }

    public WxResp _mass_send(NutMap filter, List<String> to_user, String touser, WxOutMsg msg) {
        NutMap params = new NutMap();
        if (filter != null)
            params.setv("filter", filter);
        else if (to_user != null) {
            params.setv("touser", to_user);
        } else {
            params.put("touser", touser);
        }
        String tp = msg.getMsgType();
        if ("text".equals(tp)) {
            params.put("text", new NutMap().setv("content", msg.getContent()));
        } else if ("image".equals(tp) || "voice".equals(tp) || "mpnews".equals(tp)) {
            params.put(tp, new NutMap().setv("media_id", msg.getMedia_id()));
        } else if ("video".equals(tp)) {
            NutMap tm = new NutMap();
            tm.put("media_id", msg.getMedia_id());
            tm.put("thumb_media_id", msg.getVideo().getThumb_media_id());
            tm.put("title", msg.getVideo().getTitle());
            tm.put("description", msg.getVideo().getDescription());
            params.put(tp, tm);
        } else if ("music".equals(tp)) {
            NutMap tm = new NutMap();
            tm.put("musicurl", msg.getMusic().getMusicUrl());
            tm.put("hqmusicurl", msg.getMusic().getHQMusicUrl());
            tm.put("thumb_media_id", msg.getMusic().getThumbMediaId());
            tm.put("title", msg.getMusic().getTitle());
            tm.put("description", msg.getMusic().getDescription());
            params.put(tp, tm);
        } else if ("news".equals(tp)) {
            params.put("news", msg.getArticles());
        } else if ("wxcard".equals(tp)) {
            params.put("wxcard",
                    new NutMap().setv("card_id", msg.getCard().getId()).setv("card_ext", msg.getCard().getExt()));
        } else {
            params.put(msg.getMsgType(), new NutMap().setv("media_id", msg.getMedia_id()));
        }
        params.setv("msgtype", msg.getMsgType());

        if (msg.getKfAccount() != null) {
            params.setv("customservice", new NutMap().setv("kf_account", msg.getKfAccount().getAccount()));
        }

        if (filter != null)
            return postJson("/message/mass/sendall", params);
        else if (to_user != null)
            return postJson("/message/mass/send", params);
        return postJson("/message/mass/preview", params);
    }

    @Override
    public WxResp mass_sendall(boolean is_to_all, String group_id, WxOutMsg msg) {
        NutMap filter = new NutMap();
        filter.put("is_to_all", is_to_all);
        if (!is_to_all) {
            filter.put("group_id", group_id);
        }
        return this._mass_send(filter, null, null, msg);
    }

    @Override
    public WxResp mass_send(List<String> to_user, WxOutMsg msg) {
        return this._mass_send(null, to_user, null, msg);
    }

    @Override
    public WxResp mass_del(String msg_id) {
        return this.postJson("/message/mass/del", "msg_id", msg_id);
    }

    @Override
    public WxResp mass_get(String msg_id) {
        return postJson("/message/mass/get", "msg_id", msg_id);
    }

    @Override
    public WxResp mass_preview(String touser, WxOutMsg msg) {
        return _mass_send(null, null, touser, msg);
    }

    @Override
    public WxResp applyId(int quantity, String apply_reason, String comment, int poi_id) {
        return postJson(wxBase + "/shakearound/device/applyid",
                "quantity",
                quantity,
                "apply_reason",
                apply_reason,
                "comment",
                comment);
    }

    @Override
    public WxResp applyStatus(String apply_id) {
        return postJson(wxBase + "/shakearound/device/applystatus", "apply_id", apply_id);
    }

    @Override
    public WxResp update(int device_id, String comment) {
        NutMap params = new NutMap();
        params.put("device_identifier", new NutMap().setv("device_id", device_id));
        params.put("comment", comment);
        return postJson(wxBase + "/shakearound/device/update", params);
    }

    @Override
    public WxResp update(String uuid, int major, int minor, String comment) {
        NutMap params = new NutMap();
        params.put("device_identifier", new NutMap().setv("uuid", uuid).setv("major", major).setv("minor", minor));
        params.put("comment", comment);
        return postJson(wxBase + "/shakearound/device/update", params);
    }

    @Override
    public WxResp bindLocation(int device_id, int poi_id) {
        NutMap params = new NutMap();
        params.put("device_identifier", new NutMap().setv("device_id", device_id));
        params.put("poi_id", poi_id);
        return postJson(wxBase + "/shakearound/device/bindlocation", params);
    }

    @Override
    public WxResp bindLocation(String uuid, int major, int minor, int poi_id) {
        NutMap params = new NutMap();
        params.put("device_identifier", new NutMap().setv("uuid", uuid).setv("major", major).setv("minor", minor));
        params.put("poi_id", poi_id);
        return postJson(wxBase + "/shakearound/device/bindlocation", params);
    }

    @Override
    public WxResp search(int device_id) {
        NutMap params = new NutMap();
        params.put("device_identifier", new NutMap().setv("device_id", device_id));
        return postJson(wxBase + "/shakearound/device/search", params);
    }

    @Override
    public WxResp search(String uuid, int major, int minor) {
        NutMap params = new NutMap();
        params.put("device_identifier", new NutMap().setv("uuid", uuid).setv("major", major).setv("minor", minor));
        return postJson(wxBase + "/shakearound/device/search", params);
    }

    @Override
    public WxResp search(int begin, int count) {
        return postJson(wxBase + "/shakearound/device/search", "begin", begin, "count", count);
    }

    @Override
    public WxResp search(int apply_id, int begin, int count) {
        return postJson(wxBase + "/shakearound/device/search", "apply_id", apply_id, "begin", begin, "count", count);
    }

    @Override
    public WxResp getShakeInfo(String ticket, int need_poi) {
        return postJson(wxBase + "/shakearound/user/getshakeinfo", "ticket", ticket, "need_poi", need_poi);
    }

    @Override
    public WxResp createQRTicket(long expire, Type type, int id) {
        if (type != Type.EVER || type != Type.TEMP) {// 非整形场景自动适配一下
            return createQRTicket(expire, type, id + "");
        }
        NutMap json = NutMap.NEW();
        json.put("expire_seconds", expire);
        json.put("action_name", type.getValue());
        NutMap action = NutMap.NEW();
        NutMap scene = NutMap.NEW();
        scene.put("scene_id", id);
        action.put("scene", scene);
        json.put("action_info", action);
        return postJson("/qrcode/create", json);
    }

    @Override
    public WxResp createQRTicket(long expire, Type type, String str) {
        NutMap json = NutMap.NEW();
        json.put("expire_seconds", expire);
        json.put("action_name", type.getValue());
        NutMap action = NutMap.NEW();
        NutMap scene = NutMap.NEW();
        scene.put("scene_str", str);
        action.put("scene", scene);
        json.put("action_info", action);
        return postJson("/qrcode/create", json);
    }

    @Override
    public String qrURL(String ticket) {
        return String.format("%s/cgi-bin/showqrcode?ticket=%s", mpBase, ticket);
    }

    @Override
    public WxResp get_all_private_template() {
        return postJson("/template/get_all_private_template", NutMap.NEW());
    }

    @Override
    public WxResp get_industry() {
        return postJson("/template/get_industry", NutMap.NEW());
    }

    @Override
    public WxResp add_news(WxMassArticle... news) {
        return postJson("/material/add_news", "articles", Arrays.asList(news));
    }

    @Override
    public WxResp uploadimg(File f) {
        if (f == null)
            throw new NullPointerException("meida file is NULL");
        String url = String.format("%s/cgi-bin/media/uploadimg?access_token=%s", wxBase, getAccessToken());
        Request req = Request.create(url, METHOD.POST);
        req.getParams().put("media", f);
        Response resp = new FilePostSender(req).send();
        if (!resp.isOK())
            throw new IllegalStateException("uploadimg, resp code=" + resp.getStatus());
        return Json.fromJson(WxResp.class, resp.getReader("UTF-8"));
    }

    @Override
    public WxResp uploadnews(List<WxMassArticle> articles) {
        // 用postJson方法总是抛空指针异常,只好用下面写法了,不知道原因
        return call("/media/uploadnews", METHOD.POST, Json.toJson(new NutMap().setv("articles", articles)));
    }

    @Override
    public WxResp add_material(String type, File f) {
        if (f == null)
            throw new NullPointerException("meida file is NULL");
        String url = String.format("%s/cgi-bin/material/add_material?access_token=%s&type=%s",
                wxBase,
                getAccessToken(),
                type);
        Request req = Request.create(url, METHOD.POST);
        req.getParams().put("media", f);
        Response resp = new FilePostSender(req).send();
        if (!resp.isOK())
            throw new IllegalStateException("add_material, resp code=" + resp.getStatus());
        return Json.fromJson(WxResp.class, resp.getReader("UTF-8"));
    }

    @Override
    public WxResp add_video(File f, String title, String introduction) {
        if (f == null)
            throw new NullPointerException("meida file is NULL");
        String url = String.format("%s/cgi-bin/material/add_material?type=video&access_token=%s",
                wxBase,
                getAccessToken());
        Request req = Request.create(url, METHOD.POST);
        req.getParams().put("media", f);
        req.getParams()
                .put("description",
                        Json.toJson(new NutMap().setv("title", title).setv("introduction", introduction),
                                JsonFormat.compact().setQuoteName(true)));
        Response resp = new FilePostSender(req).send();
        if (!resp.isOK())
            throw new IllegalStateException("add_material, resp code=" + resp.getStatus());
        return Json.fromJson(WxResp.class, resp.getReader("UTF-8"));
    }

    @Override
    public NutResource get_material(String media_id) {
        String url = String.format("%s/cgi-bin/material/get_material?access_token=%s", wxBase, getAccessToken());
        Request req = Request.create(url, METHOD.POST);
        NutMap body = new NutMap();
        body.put("media_id", media_id);
        req.setData(Json.toJson(body));
        final Response resp = Sender.create(req).send();
        if (!resp.isOK())
            throw new IllegalStateException("download media file, resp code=" + resp.getStatus());
        String disposition = resp.getHeader().get("Content-disposition");
        return new WxResource(disposition, resp.getStream());
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List<WxArticle> get_material_news(String media_id) {
        try {
            NutMap re = Json.fromJson(NutMap.class, get_material(media_id).getReader());
            List<WxArticle> list = new ArrayList<WxArticle>();
            for (Object obj : re.getAs("news_item", List.class)) {
                list.add(Lang.map2Object((Map) obj, WxArticle.class));
            }
            return list;
        } catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

    @Override
    public WxResp get_material_video(String media_id) {
        return postJson("/material/get_material", new NutMap().setv("media_id", media_id));
    }

    @Override
    public WxResp del_material(String media_id) {
        return postJson("/material/del_material", new NutMap().setv("media_id", media_id));
    }

    @Override
    public WxResp update_material(String media_id, int index, WxArticle article) {
        return postJson("/material/update_news",
                new NutMap().setv("media_id", media_id).setv("index", index).setv("articles", article));
    }

    @Override
    public WxResp get_materialcount() {
        return get("/material/get_materialcount");
    }

    @Override
    public WxResp batchget_material(String type, int offset, int count) {
        return postJson("/material/batchget_material",
                new NutMap().setv("type", type).setv("offset", offset).setv("count", count));
    }

    static class WxResource extends NutResource {
        String disposition;
        InputStream ins;

        public WxResource(String disposition, InputStream ins) {
            super();
            this.disposition = disposition;
            this.ins = ins;
        }

        @Override
        public String getName() {
            if (disposition == null)
                return "file.data";
            for (String str : disposition.split(";")) {
                str = str.trim();
                if (str.startsWith("filename=")) {
                    str = str.substring("filename=".length());
                    if (str.startsWith("\""))
                        str = str.substring(1);
                    if (str.endsWith("\""))
                        str = str.substring(0, str.length() - 1);
                    return str.trim().intern();
                }
            }
            return "file.data";
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return ins;
        }
    }

    @Override
    public List<WxKfAccount> getkflist() {
        return get("/customservice/getkflist").check().getTo("kf_list", WxKfAccount.class);
    }

    @Override
    public List<WxKfAccount> getonlinekflist() {
        return get("/customservice/getonlinekflist").check().getTo("kf_online_list", WxKfAccount.class);
    }

    @Override
    public WxResp kfaccount_add(String kf_account, String nickname, String password) {
        return postJson("/customservice/kfaccount/add",
                "kf_account",
                kf_account,
                "nickname",
                nickname,
                "password",
                password);
    }

    @Override
    public WxResp kfaccount_update(String kf_account, String nickname, String password) {
        return postJson("/customservice/kfaccount/update",
                "kf_account",
                kf_account,
                "nickname",
                nickname,
                "password",
                password);
    }

    @Override
    public WxResp kfaccount_uploadheadimg(String kf_account, File f) {
        if (f == null)
            throw new NullPointerException("meida file is NULL");
        String url = String.format("%s/customservice/kfaccount/uploadheadimg?access_token=%s",
                wxBase,
                getAccessToken());
        Request req = Request.create(url, METHOD.POST);
        req.getParams().put("media", f);
        Response resp = new FilePostSender(req).send();
        if (!resp.isOK())
            throw new IllegalStateException("uploadimg, resp code=" + resp.getStatus());
        return Json.fromJson(WxResp.class, resp.getReader("UTF-8"));
    }

    @Override
    public WxResp kfaccount_del(String kf_account) {
        return postJson("/customservice/kfaccount/del", "kf_account", kf_account);
    }

    /**
     * 微信支付公共POST方法（不带证书）
     *
     * @param url    请求路径
     * @param key    商户KEY
     * @param params 参数
     * @return
     */
    @Override
    public NutMap postPay(String url, String key, Map<String, Object> params) {
        params.remove("sign");
        String sign = WxPaySign.createSign(key, params);
        params.put("sign", sign);
        Request req = Request.create(url, METHOD.POST);
        req.setData(Xmls.mapToXml(params));
        Response resp = Sender.create(req).send();
        if (!resp.isOK())
            throw new IllegalStateException("postPay, resp code=" + resp.getStatus());
        return Xmls.xmlToMap(resp.getContent("UTF-8"));
    }

    /**
     * 微信支付公共POST方法（带证书）
     *
     * @param url      请求路径
     * @param key      商户KEY
     * @param params   参数
     * @param keydata  证书文件
     * @param password 证书密码
     * @return
     */
    @Override
    public NutMap postPay(String url, String key, Map<String, Object> params, Object keydata, String password) {
        params.remove("sign");
        String sign = WxPaySign.createSign(key, params);
        params.put("sign", sign);
        Request req = Request.create(url, METHOD.POST);
        req.setData(Xmls.mapToXml(params));
        Sender sender = Sender.create(req);
        SSLSocketFactory sslSocketFactory;
        try {
            sslSocketFactory = WxPaySSL.buildSSL(keydata, password);
        } catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
        sender.setSSLSocketFactory(sslSocketFactory);
        Response resp = sender.send();
        if (!resp.isOK())
            throw new IllegalStateException("postPay with SSL, resp code=" + resp.getStatus());
        return Xmls.xmlToMap(resp.getContent("UTF-8"));
    }

    /**
     * 统一下单
     *
     * @param key               商户KEY
     * @param wxPayUnifiedOrder 交易订单内容
     * @return
     */
    @Override
    public NutMap pay_unifiedorder(String key, WxPayUnifiedOrder wxPayUnifiedOrder) {
        String url = payBase + "/pay/unifiedorder";
        Map<String, Object> params = Lang.obj2map(wxPayUnifiedOrder);
        return this.postPay(url, key, params);
    }

    /**
     * 微信公众号JS支付
     *
     * @param key               商户KEY
     * @param wxPayUnifiedOrder 交易订单内容
     * @return
     */
    @Override
    public NutMap pay_jsapi(String key, WxPayUnifiedOrder wxPayUnifiedOrder) {
        NutMap map = this.pay_unifiedorder(key, wxPayUnifiedOrder);
        NutMap params = NutMap.NEW();
        params.put("appId", wxPayUnifiedOrder.getAppid());
        params.put("timeStamp", String.valueOf((int) (System.currentTimeMillis() / 1000)));
        params.put("nonceStr", R.UU32());
        params.put("package", "prepay_id=" + map.getString("prepay_id"));
        params.put("signType", "MD5");
        String sign = WxPaySign.createSign(key, params);
        params.put("paySign", sign);
        return params;
    }

    /**
     * 企业向个人付款
     *
     * @param key            商户KEY
     * @param wxPayTransfers 付款内容
     * @param keydata        证书文件
     * @param password       证书密码
     * @return
     */
    @Override
    public NutMap pay_transfers(String key, WxPayTransfers wxPayTransfers, Object keydata, String password) {
        String url = payBase + "/mmpaymkttransfers/promotion/transfers";
        Map<String, Object> params = Lang.obj2map(wxPayTransfers);
        return this.postPay(url, key, params, keydata, password);
    }

    /**
     * 发送普通红包
     *
     * @param key       商户KEY
     * @param wxRedPack 红包内容
     * @param keydata   证书文件
     * @param password  证书密码
     * @return
     */
    @Override
    public NutMap send_redpack(String key, WxPayRedPack wxRedPack, Object keydata, String password) {
        String url = payBase + "/mmpaymkttransfers/sendredpack";
        Map<String, Object> params = Lang.obj2map(wxRedPack);
        return this.postPay(url, key, params, keydata, password);
    }

    /**
     * 发送裂变红包
     *
     * @param key            商户KEY
     * @param wxRedPackGroup 红包内容
     * @param keydata        证书文件
     * @param password       证书密码
     * @return
     */
    @Override
    public NutMap send_redpackgroup(String key, WxPayRedPackGroup wxRedPackGroup, Object keydata, String password) {
        String url = payBase + "/mmpaymkttransfers/sendgroupredpack";
        Map<String, Object> params = Lang.obj2map(wxRedPackGroup);
        return this.postPay(url, key, params, keydata, password);
    }

    /**
     * 发送代金卷
     *
     * @param key         商户KEY
     * @param wxPayCoupon 代金卷内容
     * @param keydata     证书文件
     * @param password    证书密码
     * @return
     */
    @Override
    public NutMap send_coupon(String key, WxPayCoupon wxPayCoupon, Object keydata, String password) {
        String url = payBase + "/mmpaymkttransfers/send_coupon";
        Map<String, Object> params = Lang.obj2map(wxPayCoupon);
        return this.postPay(url, key, params, keydata, password);
    }

    /**
     * @param key         商户KEY
     * @param wxPayRefund 退款申请参数
     * @param keydata     证书文件
     * @param password    证书密码
     * @return
     */
    @Override
    public NutMap pay_refund(String key, WxPayRefund wxPayRefund, Object keydata, String password) {
        String url = payBase + "/secapi/pay/refund";
        Map<String, Object> params = Lang.obj2map(wxPayRefund);
        return this.postPay(url, key, params, keydata, password);
    }

    /**
     * @param key              商户KEY
     * @param wxPayRefundQuery 退款查询参数
     * @return
     */
    @Override
    public NutMap pay_refundquery(String key, WxPayRefundQuery wxPayRefundQuery) {
        String url = payBase + "/pay/refundquery";
        Map<String, Object> params = Lang.obj2map(wxPayRefundQuery);
        return this.postPay(url, key, params);
    }

    @Override
    public void setPayBase(String url) {
        this.payBase = url;
    }

    @Override
    public void setWxBase(String url) {
        this.wxBase = url;
    }

    @Override
    public void setMpBase(String url) {
        this.mpBase = url;
    }

    @Override
    public WxResp menu_addconditional(List<WxMenu> button, WxMatchRule matchrule) {
        return postJson("/menu/addconditional", NutMap.NEW().addv("button", button).addv("matchrule", matchrule));
    }

    @Override
    public WxResp menu_delconditional(String menuid) {
        return postJson("/menu/delconditional", NutMap.NEW().addv("menuid", menuid));
    }

    @Override
    public WxResp menu_trymatch(String user_id) {
        return postJson("/menu/trymatch", NutMap.NEW().addv("user_id", user_id));
    }

    @Override
    public NutMap pay_transfers(String key, WxPayTransfers wxPayTransfers, File keydata, String password) {
        return pay_transfers(key, wxPayTransfers, (Object) keydata, password);
    }

    @Override
    public NutMap send_redpack(String key, WxPayRedPack wxRedPack, File keydata, String password) {
        return send_redpack(key, wxRedPack, (Object) keydata, password);
    }

    @Override
    public NutMap send_redpackgroup(String key, WxPayRedPackGroup wxRedPackGroup, File keydata, String password) {
        return send_redpackgroup(key, wxRedPackGroup, (Object) keydata, password);
    }

    @Override
    public NutMap send_coupon(String key, WxPayCoupon wxPayCoupon, File keydata, String password) {
        return send_coupon(key, wxPayCoupon, (Object) keydata, password);
    }

    @Override
    public NutMap pay_refund(String key, WxPayRefund wxPayRefund, File keydata, String password) {
        return pay_refund(key, wxPayRefund, (Object) keydata, password);
    }

    @Override
    public WxResp newtmpl_addtemplate(String tid, int[] kidList, String sceneDesc) {
        return postJson(wxBase + "/wxaapi/newtmpl/addtemplate", "tid", tid, "kidList", kidList, "sceneDesc", sceneDesc);
    }

    @Override
    public WxResp newtmpl_deleteTemplate(String priTmplId) {
        return postJson(wxBase + "/wxaapi/newtmpl/deltemplate", "priTmplId", priTmplId);
    }

    @Override
    public WxResp newtmpl_getCategory() {
        return get(wxBase + "/wxaapi/newtmpl/getcategory");
    }

    @Override
    public WxResp newtmpl_getPubTemplateKeyWordsById(String tid) {
        return get(wxBase + "/wxaapi/newtmpl/getpubtemplatekeywords", "tid", tid);
    }

    @Override
    public WxResp newtmpl_getPubTemplateTitleList(String ids, int start, int limit) {
        return get(wxBase + "/wxaapi/newtmpl/getpubtemplatetitles", "ids", ids, "start", "" + start, "limit", "" + limit);
    }

    @Override
    public WxResp newtmpl_getTemplateList() {
        return get(wxBase + "/wxaapi/newtmpl/gettemplate");
    }

    @Override
    public WxResp newtmpl_send(String touser, String template_id, String page, NutMap miniprogram, NutMap data) {
        NutMap body = NutMap.NEW();
        body.addv("touser", touser);
        body.addv("template_id", template_id);
        if (Strings.isNotBlank(page)) {
            body.addv("page", page);
        }
        if (Lang.isNotEmpty(miniprogram)) {
            body.addv("miniprogram", miniprogram);
        }
        body.addv("data", data);
        return postJson("/message/subscribe/bizsend", body);
    }
}
