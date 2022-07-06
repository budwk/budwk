package com.budwk.app.cms.models;

import com.budwk.starter.common.enums.Validation;
import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;
import java.util.List;

/**
 * @author wizzer(wizzer @ qq.com) on 2016/7/18.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "CMS链接分类")
@Table("cms_link_class")
@TableMeta("{'mysql-charset':'utf8mb4'}")
@TableIndexes({@Index(name = "INDEX_CMS_LINK_CLASS", fields = {"code"}, unique = true)})
public class Cms_link_class extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    @ApiModelProperty(description = "ID")
    private String id;

    @Column
    @Comment("分类名称")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    @ApiModelProperty(description = "分类名称")
    private String name;

    @Column
    @Comment("分类编码")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    @ApiModelProperty(description = "分类编码", required = true, check = true, validation = Validation.UPPER)
    private String code;

    @ManyMany(from = "classId", relation = "cms_class_link", to = "linkId")
    @ApiModelProperty(description = "链接列表")
    private List<Cms_link> links;

}
