package com.budwk.nb.cms.models;

import com.budwk.nb.base.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * @author wizzer(wizzer.cn) on 2016/7/18.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("cms_article")
public class Cms_article extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * @PrevInsert(els = {@EL("ig(view.tableName,'')")})
     * 主键生成器示例
     */
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("站点ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String siteid;

    @Column
    @Comment("预留商城ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String shopid;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String channelId;

    @Column
    @Comment("文章标题")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String title;

    @Column
    @Comment("链接地址")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String url;

    @Column
    @Comment("文章简介")
    @ColDefine(type = ColType.VARCHAR, width = 500)
    private String info;

    @Column
    @Comment("文章作者")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String author;

    @Column
    @Comment("标题图")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String picurl;

    @Column
    @Comment("文章内容")
    @ColDefine(type = ColType.TEXT)
    private String content;

    @Column
    @Comment("是否禁用")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean disabled;

    @Column
    @Comment("发布时间")
    private Long publishAt;

    @Column
    @Comment("截至时间")
    private Long endAt;

    @Column
    @Comment("同步状态")
    @ColDefine(type = ColType.INT)
    @Default(value = "0")
    private int status;

    @Column
    @Comment("浏览量")
    @ColDefine(type = ColType.INT)
    @Default("0")
    private Integer view_num;

    @Column
    @Comment("排序字段")
    @Prev({
            @SQL(db = DB.MYSQL, value = "SELECT IFNULL(MAX(location),0)+1 FROM cms_article"),
            @SQL(db = DB.ORACLE, value = "SELECT COALESCE(MAX(location),0)+1 FROM cms_article")
    })
    private Integer location;

    @One(field = "channelId")
    private Cms_channel channel;

}
