package com.budwk.nb.wx.models;

import com.budwk.nb.commons.base.model.BaseModel;
import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * @author wizzer(wizzer@qq.com) on 2016/7/2.
 */
@Table("wx_mass_news")
public class Wx_mass_news extends BaseModel implements Serializable {
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
    @Comment("缩略图ID")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String thumb_media_id;

    @Column
    @Comment("缩略图URL")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String picurl;

    @Column
    @Comment("作者")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String author;

    @Column
    @Comment("标题")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String title;

    @Column
    @Comment("原地址")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String content_source_url;

    @Column
    @Comment("图文内容")
    @ColDefine(type = ColType.TEXT)
    private String content;

    @Column
    @Comment("摘要")
    @ColDefine(type = ColType.TEXT)
    private String digest;

    /**
     * 1为显示，0为不显示
     */
    @Column
    @Comment("显示封面")
    @ColDefine(type = ColType.INT)
    private Integer show_cover_pic;

    /**
     * 0不打开，1打开
     */
    @Column
    @Comment("是否打开评论")
    @ColDefine(type = ColType.INT)
    private Integer need_open_comment;

    /**
     * 0所有人可评论，1粉丝才可评论
     */
    @Column
    @Comment("是否粉丝才可评论")
    @ColDefine(type = ColType.INT)
    private Integer only_fans_can_comment;

    @Column
    @Comment("排序字段")
    @Prev({
            @SQL(db = DB.MYSQL, value = "SELECT IFNULL(MAX(location),0)+1 FROM wx_mass_news"),
            @SQL(db = DB.ORACLE, value = "SELECT COALESCE(MAX(location),0)+1 FROM wx_mass_news")
    })
    private Integer location;

    @Column
    @Comment("微信ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String wxid;

    @One(field = "wxid")
    private Wx_config wxConfig;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumb_media_id() {
        return thumb_media_id;
    }

    public void setThumb_media_id(String thumb_media_id) {
        this.thumb_media_id = thumb_media_id;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent_source_url() {
        return content_source_url;
    }

    public void setContent_source_url(String content_source_url) {
        this.content_source_url = content_source_url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public Integer getShow_cover_pic() {
        return show_cover_pic;
    }

    public void setShow_cover_pic(Integer show_cover_pic) {
        this.show_cover_pic = show_cover_pic;
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    public String getWxid() {
        return wxid;
    }

    public void setWxid(String wxid) {
        this.wxid = wxid;
    }

    public Wx_config getWxConfig() {
        return wxConfig;
    }

    public void setWxConfig(Wx_config wxConfig) {
        this.wxConfig = wxConfig;
    }

    public String getSys_unit_id() {
        return sys_unit_id;
    }

    public void setSys_unit_id(String sys_unit_id) {
        this.sys_unit_id = sys_unit_id;
    }

    public Integer getNeed_open_comment() {
        return need_open_comment;
    }

    public void setNeed_open_comment(Integer need_open_comment) {
        this.need_open_comment = need_open_comment;
    }

    public Integer getOnly_fans_can_comment() {
        return only_fans_can_comment;
    }

    public void setOnly_fans_can_comment(Integer only_fans_can_comment) {
        this.only_fans_can_comment = only_fans_can_comment;
    }
}
