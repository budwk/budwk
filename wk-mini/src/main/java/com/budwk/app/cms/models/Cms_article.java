package com.budwk.app.cms.models;

import com.budwk.starter.common.enums.Validation;
import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * @author wizzer(wizzer@qq.com) on 2016/7/18.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("cms_article")
@TableMeta("{'mysql-charset':'utf8mb4'}")
@ApiModel(description = "CMS文章")
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
    @ApiModelProperty(description = "ID")
    private String id;

    @Column
    @Comment("站点ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "站点ID")
    private String siteId;

    @Column
    @Comment("预留商城ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "预留商城ID")
    private String shopId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "栏目ID")
    private String channelId;

    @Column
    @Comment("文章标题")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    @ApiModelProperty(description = "文章标题", required = true,check = true)
    private String title;

    @Column
    @Comment("链接地址")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "链接地址")
    private String url;

    @Column
    @Comment("文章简介")
    @ColDefine(type = ColType.VARCHAR, width = 500)
    @ApiModelProperty(description = "文章简介")
    private String info;

    @Column
    @Comment("文章作者")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(description = "文章作者", required = true,check = true)
    private String author;

    @Column
    @Comment("标题图")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String picUrl;

    @Column
    @Comment("文章内容")
    @ColDefine(type = ColType.TEXT)
    @ApiModelProperty(description = "文章内容", required = true)
    private String content;

    @Column
    @Comment("是否禁用")
    @ColDefine(type = ColType.BOOLEAN)
    @ApiModelProperty(description = "是否禁用")
    private boolean disabled;

    @Column
    @Comment("发布时间")
    @ApiModelProperty(description = "发布时间")
    private Long publishAt;

    @Column
    @Comment("截至时间")
    @ApiModelProperty(description = "截至时间")
    private Long endAt;

    @Column
    @Comment("同步状态")
    @ColDefine(type = ColType.INT)
    @Default(value = "0")
    @ApiModelProperty(description = "同步状态")
    private int status;

    @Column
    @Comment("浏览量")
    @ColDefine(type = ColType.INT)
    @Default("0")
    @ApiModelProperty(description = "浏览量")
    private Integer viewNum;

    @Column
    @Comment("排序字段")
    @Prev({
            @SQL(db = DB.MYSQL, value = "SELECT IFNULL(MAX(location),0)+1 FROM cms_article"),
            @SQL(db = DB.ORACLE, value = "SELECT COALESCE(MAX(location),0)+1 FROM cms_article")
    })
    @ApiModelProperty(description = "排序字段")
    private Integer location;

    @One(field = "channelId")
    @ApiModelProperty(description = "所属栏目")
    private Cms_channel channel;

}
