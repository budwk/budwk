package com.budwk.app.sys.services.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.budwk.app.sys.enums.SysMsgScope;
import com.budwk.app.sys.enums.SysMsgType;
import com.budwk.app.sys.models.Sys_msg;
import com.budwk.app.sys.models.Sys_msg_user;
import com.budwk.app.sys.models.Sys_user;
import com.budwk.app.sys.services.SysMsgService;
import com.budwk.app.sys.services.SysMsgUserService;
import com.budwk.app.sys.services.SysUserService;
import com.budwk.starter.common.exception.BaseException;
import com.budwk.starter.common.page.Pagination;
import com.budwk.starter.database.service.BaseServiceImpl;
import com.budwk.starter.websocket.publish.MessageSendServer;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean(args = {"refer:dao"})
public class SysMsgServiceImpl extends BaseServiceImpl<Sys_msg> implements SysMsgService {
    public SysMsgServiceImpl(Dao dao) {
        super(dao);
    }

    @Inject
    private SysMsgUserService sysMsgUserService;

    @Inject
    private SysUserService sysUserService;

    @Override
    public int getUnreadNum(String userId) {
        int size = sysMsgUserService.count(Cnd.where("delFlag", "=", false).and("userId", "=", userId)
                .and("status", "=", 0));
        return size;
    }

    @Override
    public List<Sys_msg_user> getUnreadList(String userId, int pageNumber, int pageSize) {
        return sysMsgUserService.query(Cnd.where("delFlag", "=", false).and("userId", "=", userId)
                .and("status", "=", 0)
                .desc("createdAt"), "msg", Cnd.orderBy().desc("sendAt"), new Pager().setPageNumber(pageNumber).setPageSize(pageSize));

    }

    @Override
    public void deleteMsg(String id) {
        this.vDelete(id);
        sysMsgUserService.vDelete(Cnd.where("msgId", "=", id));
    }

    @Override
    public void saveMsg(Sys_msg msg, String[] ids) {
        Sys_msg dbMsg = this.insert(msg);
        if (dbMsg != null) {
            if (SysMsgScope.SCOPE.equals(dbMsg.getScope()) && ids != null) {
                for (String userId : ids) {
                    Sys_user user = sysUserService.fetch(userId);
                    Sys_msg_user sys_msg_user = new Sys_msg_user();
                    sys_msg_user.setMsgId(dbMsg.getId());
                    sys_msg_user.setStatus(0);
                    sys_msg_user.setCreatedBy(dbMsg.getCreatedBy());
                    sys_msg_user.setUserId(user.getId());
                    sys_msg_user.setLoginname(user.getLoginname());
                    sys_msg_user.setUsername(user.getUsername());
                    sysMsgUserService.insert(sys_msg_user);
                    this.getMsg(userId, true);
                }
            }
            if (SysMsgScope.ALL.equals(dbMsg.getScope())) {
                Cnd cnd = Cnd.where("disabled", "=", false).and("delFlag", "=", false);
                int total = sysUserService.count(cnd);
                int size = 500;
                Pagination pagination = new Pagination();
                pagination.setTotalCount(total);
                pagination.setPageSize(size);
                for (int i = 1; i <= pagination.getTotalPage(); i++) {
                    Pagination pagination2 = sysUserService.listPage(i, size, cnd);
                    for (Sys_user user : pagination2.getList(Sys_user.class)) {
                        Sys_msg_user sys_msg_user = new Sys_msg_user();
                        sys_msg_user.setMsgId(dbMsg.getId());
                        sys_msg_user.setStatus(0);
                        sys_msg_user.setCreatedBy(dbMsg.getCreatedBy());
                        sys_msg_user.setUserId(user.getId());
                        sys_msg_user.setLoginname(user.getLoginname());
                        sys_msg_user.setUsername(user.getUsername());
                        sysMsgUserService.insert(sys_msg_user);
                        this.getMsg(user.getId(), true);
                    }
                }
            }
        }
    }

    @Inject
    private SysMsgService sysMsgService;
    @Inject
    private MessageSendServer messageSendServer;

    @Override
    public void wsSendMsg(String userId, String token, String msg) {
        messageSendServer.wsSendMsg(userId, token, msg);
    }

    @Override
    public void wsSendMsg(String userId, String msg) {
        messageSendServer.wsSendMsg(userId, msg);
    }

    @Override
    public void wsSendMsg(List<String> userId, String msg) {
        messageSendServer.wsSendMsg(userId, msg);
    }

    @Override
    public void wsCheckLogin(String userId, String token) {
        if (!StpUtil.getTokenValueListByLoginId(userId).contains(token)) {
            throw new BaseException("WS 用户登录信息不正确");
        }
    }

    @Override
    public void sendMsg(String userId, SysMsgType type, String title, String url, String note, String sendUserId) {
        this.sendMsg(new String[]{userId}, type, title, url, note, sendUserId);
    }

    @Override
    public void sendMsg(String[] userId, SysMsgType type, String title, String url, String note, String sendUserId) {
        Sys_msg msg = new Sys_msg();
        msg.setType(type);
        msg.setScope(SysMsgScope.SCOPE);
        msg.setNote(note);
        msg.setTitle(title);
        msg.setUrl(url);
        msg.setSendAt(System.currentTimeMillis());
        msg.setCreatedBy(Strings.sNull(sendUserId));
        this.saveMsg(msg, userId);
    }

    @Override
    @Async
    public void getMsg(String userId, boolean notify) {
        int size = this.getUnreadNum(userId);
        List<Sys_msg_user> list = this.getUnreadList(userId, 1, 5);
        List<NutMap> mapList = new ArrayList<>();
        for (Sys_msg_user msgUser : list) {
            if (Lang.isNotEmpty(msgUser.getMsg())) {
                mapList.add(NutMap.NEW().addv("msgId", msgUser.getMsgId()).addv("type", msgUser.getMsg().getType().getValue())
                        .addv("typeText", msgUser.getMsg().getType().getText())
                        .addv("title", msgUser.getMsg().getTitle())
                        .addv("url", msgUser.getMsg().getUrl())
                        .addv("time", Times.format("yyyy-MM-dd HH:mm", Times.D(msgUser.getMsg().getSendAt()))));
            }
        }
        NutMap map = new NutMap();
        map.put("action", "notice");
        // 未读消息数
        map.put("size", size);
        map.put("notify", notify);
        // 最新3条消息列表  type--系统消息/用户消息  title--标题  time--时间戳
        map.put("list", mapList);
        String msg = Json.toJson(map, JsonFormat.compact());
        this.wsSendMsg(userId, msg);
    }
}
