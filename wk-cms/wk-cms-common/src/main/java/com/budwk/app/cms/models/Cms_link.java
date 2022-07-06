package com.budwk.app.cms.models;

import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * @author wizzer(wizzer@qq.com) on 2016/7/18.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("cms_link")
@TableMeta("{'mysql-charset':'utf8mb4'}")
@ApiModel(description = "CMS链接")
public class Cms_link extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    @ApiModelProperty(description = "ID")
    private String id;

    @Column
    @Comment("链接名称")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    @ApiModelProperty(description = "链接名称",required = true,check = true)
    private String name;

    @Column
    @Comment("链接类型")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    @ApiModelProperty(description = "链接类型")
    private String type;

    @Column
    @Comment("图片地址")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "图片地址")
    private String picUrl;

    @Column
    @Comment("链接地址")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "链接地址")
    private String url;

    @Column
    @Comment("打开方式")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    @ApiModelProperty(description = "打开方式")
    private String target;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "分类ID")
    private String classId;

    @One(field = "classId")
    @ApiModelProperty(description = "所属分类")
    private Cms_link_class linkClass;

}
