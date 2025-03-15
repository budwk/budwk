package com.budwk.starter.wechat.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.http.Http;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.http.sender.FilePostSender;
import org.nutz.json.Json;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;
import com.budwk.starter.wechat.bean.WxGroup;
import com.budwk.starter.wechat.bean.WxMaster;
import com.budwk.starter.wechat.bean.WxMenu;
import com.budwk.starter.wechat.bean.WxOutMsg;
import com.budwk.starter.wechat.bean.WxTemplateData;
import com.budwk.starter.wechat.bean.WxUser;
import com.budwk.starter.wechat.spi.WxAPI;
import com.budwk.starter.wechat.util.Wxs;

@SuppressWarnings("unchecked")
@Deprecated
public class WxApiImpl implements WxAPI {

    private static final Log log = Logs.get();

    protected String base = "https://api.weixin.qq.com/cgi-bin";

    protected WxMaster master;

    public WxApiImpl(WxMaster master) {
        super();
        this.master = master;
    }

    public void send(WxOutMsg out) {
        if (out.getFromUserName() == null)
            out.setFromUserName(master.getOpenid());
        String str = Wxs.asJson(out);
        if (Wxs.DEV_MODE)
            log.debug("api out msg>\n" + str);
        call("/message/custom/send", METHOD.POST, str);
    }

    public WxGroup createGroup(WxGroup group) {
        Map<String, Object> map = call("/groups/create",
                                       METHOD.POST,
                                       Json.toJson(new NutMap().setv("group", group)));
        return Lang.map2Object((Map<String, Object>) map.get("group"), WxGroup.class);
    }

    public List<WxGroup> listGroup() {
        Map<String, Object> map = call("/groups/get", METHOD.GET, null);
        List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("groups");
        List<WxGroup> groups = new ArrayList<WxGroup>();
        for (Map<String, Object> e : list) {
            groups.add(Lang.map2Object(e, WxGroup.class));
        }
        return groups;
    }

    public int userGroup(String openid) {
        Map<String, Object> map = call("/groups/getid",
                                       METHOD.POST,
                                       Json.toJson(new NutMap().setv("openid", openid)));
        return ((Number) map.get("groupid")).intValue();
    }

    public void renameGroup(WxGroup group) {
        call("/groups/update", METHOD.POST, Json.toJson(group));
    }

    public void moveUser2Group(String openid, String groupid) {
        call("/groups/members/update",
             METHOD.POST,
             Json.toJson(new NutMap().setv("openid", openid).setv("groupid", groupid)));
    }

    public WxUser fetchUser(String openid, String lang) {
        if (lang == null)
            lang = "zh_CN";
        Map<String, Object> map = call("/user/info?openid=" + openid + "&lang=" + openid,
                                       METHOD.GET,
                                       null);
        return Lang.map2Object(map, WxUser.class);
    }

    public void listWatcher(Each<String> each) {
        String next_openid = null;
        Map<String, Object> map = null;
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
                }
                catch (ExitLoop e) {
                    return;
                }
                catch (ContinueLoop e) {
                    continue;
                }
                catch (LoopException e) {
                    throw e;
                }
                index++;
            }
        }
    }

    public void creatMenu(WxMenu menu) {
        call("/menu/create", METHOD.POST, Json.toJson(menu));
    }

    public WxMenu fetchMenu() {
        Map<String, Object> map = call("/menu/get", METHOD.GET, null);
        return Lang.map2Object((Map<String, Object>) map.get("menu"), WxMenu.class);
    }

    public void clearMenu() {
        call("/menu/clear", METHOD.GET, null);
    }

    public String tmpQr(int expire_seconds, String scene_id) {
        NutMap map = new NutMap().setv("expire_seconds", expire_seconds)
                                 .setv("action_name", "QR_SCENE")
                                 .setv("scene", new NutMap().setv("scene_id", scene_id));
        return call("", METHOD.POST, Json.toJson(map)).get("ticket").toString();
    }

    public String godQr(int scene_id) {
        NutMap map = new NutMap().setv("action_name", "QR_LIMIT_SCENE")
                                 .setv("scene", new NutMap().setv("scene_id", scene_id));
        return call("", METHOD.POST, Json.toJson(map)).get("ticket").toString();
    }

    public String qrUrl(String ticket) {
        return base + "/showqrcode?ticket=" + ticket;
    }

    public void reflushAccessToken() {
        String url = String.format("%s/token?grant_type=client_credential&appid=%s&secret=%s",
                                   base,
                                   master.getAppid(),
                                   master.getAppsecret());
        Response resp = Http.get(url);
        if (!resp.isOK())
            throw new IllegalArgumentException("reflushAccessToken FAIL , openid="
                                               + master.getOpenid());
        String str = resp.getContent();
        Map<String, Object> map = (Map<String, Object>) Json.fromJson(str);
        master.setAccess_token(map.get("token").toString());
        master.setAccess_token_expires(System.currentTimeMillis()
                                       + (((Number) map.get("expires_in")).intValue() - 60)
                                       * 1000);
    }

    protected Map<String, Object> call(String URL, METHOD method, String body) {
        String token = getAccessToken();
        if (URL.contains("?")) {
            URL = base + URL + "&access_token=" + token;
        } else {
            URL = base + URL + "?access_token=" + token;
        }
        Request req = Request.create(URL, method);
        if (body != null)
            req.setData(body);
        Response resp = Sender.create(req).send();
        if (!resp.isOK())
            throw new IllegalArgumentException("resp code=" + resp.getStatus());
        Map<String, Object> map = (Map<String, Object>) Json.fromJson(resp.getReader());
        if (map != null
            && map.containsKey("errcode")
            && ((Number) map.get("errcode")).intValue() != 0) {
            throw new IllegalArgumentException(map.toString());
        }
        return map;
    }

    public String getAccessToken() {
        String token = master.getAccess_token();
        if (token == null || master.getAccess_token_expires() < System.currentTimeMillis()) {
            synchronized (master) {
                if (token == null || master.getAccess_token_expires() < System.currentTimeMillis()) {
                    reflushAccessToken();
                    token = master.getAccess_token();
                }
            }
        }
        return token;
    }

    @Override
    public String mediaUpload(String type, File f) {
        if (type == null)
            throw new NullPointerException("media type is NULL");
        if (f == null)
            throw new NullPointerException("meida file is NULL");
        String url = String.format("http://file.api.weixin.qq.com/cgi-bin/media/upload?token=%s&type=%s",
                                   getAccessToken(),
                                   type);
        Request req = Request.create(url, METHOD.POST);
        req.getParams().put("media", f);
        Response resp = new FilePostSender(req).send();
        if (!resp.isOK())
            throw new IllegalStateException("media upload file, resp code=" + resp.getStatus());
        Map<String, Object> map = (Map<String, Object>) Json.fromJson(resp.getReader());
        if (map != null
            && map.containsKey("errcode")
            && ((Number) map.get("errcode")).intValue() != 0) {
            throw new IllegalArgumentException(map.toString());
        }
        return map.get("media_id").toString();
    }

    @Override
    public NutResource mediaGet(String mediaId) {
        String url = "http://file.api.weixin.qq.com/cgi-bin/media/get";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", getAccessToken());
        params.put("media_id", mediaId);
        final Response resp = Sender.create(Request.create(url, METHOD.GET)).send();
        if (!resp.isOK())
            throw new IllegalStateException("download media file, resp code=" + resp.getStatus());
        final String disposition = resp.getHeader().get("Content-disposition");
        return new NutResource() {

            public String getName() {
                if (disposition == null)
                    return "file.data";
                for (String str : disposition.split(";")) {
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

            public InputStream getInputStream() throws IOException {
                return resp.getStream();
            }
        };
    }

    public String sendTemplateMsg(String touser,
                                  String template_id,
                                  String topcolor,
                                  Map<String, WxTemplateData> data) {
        if (Strings.isBlank(topcolor))
            topcolor = WxTemplateData.DFT_COLOR;
        NutMap map = new NutMap();
        map.put("touser", touser);
        map.put("template_id", template_id);
        map.put("topcolor", topcolor);
        map.put("data", data);
        return call("/message/template/send", METHOD.POST, Json.toJson(map)).get("msgid")
                                                                            .toString();
    }

    public void userRemark(String openid, String remark) {
        NutMap map = new NutMap();
        map.put("openid", openid);
        map.put("remark", remark);
        call("/user/info/updateremark", METHOD.POST, Json.toJson(map));
    }

}
