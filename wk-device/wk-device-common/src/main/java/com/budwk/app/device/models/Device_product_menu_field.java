package com.budwk.app.device.models;

import com.budwk.app.device.enums.FieldType;
import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * @author wizzer.cn
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table
@TableMeta("{'mysql-charset':'utf8mb4'}")
@Comment("产品菜单字段")
@ApiModel(description = "产品菜单字段")
public class Device_product_menu_field extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL(value = "snowflake()")}, nullEffective = true)
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    private String id;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("菜单id")
    @ApiModelProperty(description = "关联菜单id")
    private String menuId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 30)
    @Comment("名称")
    @ApiModelProperty(description = "名称")
    private String name;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("标识")
    @ApiModelProperty(description = "标识")
    private String code;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 10)
    @Comment("属性/参数")
    @ApiModelProperty(description = "属性/参数")
    private FieldType type;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @Comment("描述")
    @ApiModelProperty(description = "描述")
    private String description;

    @Column
    @ColDefine(type = ColType.INT)
    @Comment("排序")
    @ApiModelProperty(description = "排序")
    private Integer location;

    @Column
    @ColDefine(type = ColType.BOOLEAN)
    @Comment("是否显示")
    @ApiModelProperty(description = "是否显示")
    private Boolean display;
}
