package com.budwk.app.device.models;

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
@Comment("产品菜单")
@ApiModel(description = "产品菜单")
public class Device_product_menu extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL(value = "snowflake()")})
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    private String id;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("产品ID")
    @ApiModelProperty(description = "产品ID")
    private String productId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 30)
    @Comment("名称")
    @ApiModelProperty(description = "菜单名称")
    private String name;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 30)
    @Comment("标识")
    @ApiModelProperty(description = "标识")
    private String code;

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

    @Column
    @ColDefine(type = ColType.BOOLEAN)
    @Comment("是否显示设置字段")
    @ApiModelProperty(description = "是否显示设置字段")
    private Boolean setting;

    @Column
    @Comment("是否内置")
    @ColDefine(type = ColType.BOOLEAN)
    @Default("0")
    @ApiModelProperty(description = "是否内置")
    private Boolean system;
}
