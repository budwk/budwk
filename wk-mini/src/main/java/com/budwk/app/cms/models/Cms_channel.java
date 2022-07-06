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
 * @author wizzer(wizzer @ qq.com) on 2016/7/18.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("cms_channel")
@TableMeta("{'mysql-charset':'utf8mb4'}")
@TableIndexes({@Index(name = "INDEX_CHANNEL", fields = {"code"}, unique = true)})
@ApiModel(description = "CMS栏目")
public class Cms_channel extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
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
    @Comment("父级ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "父级ID")
    private String parentId;

    @Column
    @Comment("树路径")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @ApiModelProperty(description = "树路径")
    private String path;

    @Column
    @Comment("栏目名称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @ApiModelProperty(description = "栏目名称", required = true, check = true)
    private String name;

    @Column
    @Comment("栏目标识")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @ApiModelProperty(description = "栏目标识", required = true, check = true, validation = Validation.UPPER)
    private String code;

    @Column
    @Comment("栏目类型")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    @ApiModelProperty(description = "栏目类型")
    private String type;

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
    @Comment("是否显示")
    @ColDefine(type = ColType.BOOLEAN)
    @ApiModelProperty(description = "是否显示")
    private boolean showit;

    @Column
    @Comment("是否禁用")
    @ColDefine(type = ColType.BOOLEAN)
    @ApiModelProperty(description = "是否禁用")
    private boolean disabled;

    @Column
    @Comment("排序字段")
    @Prev({
            @SQL(db = DB.MYSQL, value = "SELECT IFNULL(MAX(location),0)+1 FROM cms_channel"),
            @SQL(db = DB.ORACLE, value = "SELECT COALESCE(MAX(location),0)+1 FROM cms_channel")
    })
    @ApiModelProperty(description = "排序字段")
    private Integer location;

    @Column
    @Comment("有子节点")
    @ApiModelProperty(description = "有子节点")
    private boolean hasChildren;

}
