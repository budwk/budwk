package com.budwk.nb.app.wx.models;

import com.budwk.nb.common.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * Created by wizzer on 2016/8/5.
 */
@Table("wx_tpl_log")
public class Wx_tpl_log extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("单位id")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String sys_unit_id;

    @Column
    @Comment("openid")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String openid;

    @Column
    @Comment("微信昵称")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String nickname;

    @Column
    @Comment("发送内容")
    @ColDefine(type = ColType.TEXT)
    private String content;

    @Column
    @Comment("发送状态")
    @ColDefine(type = ColType.INT)
    private int status;//0 默认 1 成功 2 失败

    @Column
    @Comment("发送结果")
    @ColDefine(type = ColType.TEXT)
    private String result;

    @Column
    @Comment("微信ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String wxid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getWxid() {
        return wxid;
    }

    public void setWxid(String wxid) {
        this.wxid = wxid;
    }

    public String getSys_unit_id() {
        return sys_unit_id;
    }

    public void setSys_unit_id(String sys_unit_id) {
        this.sys_unit_id = sys_unit_id;
    }
}
