package com.budwk.app.device.models;

import com.budwk.app.device.enums.DataType;
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
 * @author wizzer.cn
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table
@TableMeta("{'mysql-charset':'utf8mb4'}")
@Comment("产品指令参数")
@ApiModel(description = "产品指令参数")
public class Device_product_cmd_attr extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL(value = "snowflake()")})
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    private String id;

    @Column
    @Comment("产品ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "产品ID")
    private String productId;

    @Column
    @Comment("指令ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "指令ID")
    private String commandId;

    @Column
    @Comment("属性名称")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(description = "属性名称")
    private String name;

    @Column
    @Comment("属性标识")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(description = "属性标识")
    private String code;

    /**
     * @see DataType
     */
    @Column
    @Comment("数据类型")
    @ColDefine(type = ColType.INT)
    @ApiModelProperty(description = "数据类型")
    private DataType dataType;

    @Column
    @Comment("小数位数")
    @ColDefine(type = ColType.INT, width = 10)
    @ApiModelProperty(description = "小数位数")
    private Integer scale;

    @Column
    @Comment("默认值")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(description = "默认值")
    private String defaultValue;

    @Column
    @Comment("说明")
    @ColDefine(type = ColType.VARCHAR, width = 300)
    @ApiModelProperty(description = "说明")
    private String description;

    @Column
    @Comment("是否必填")
    @ColDefine(type = ColType.BOOLEAN)
    @Default("0")
    @ApiModelProperty(description = "是否必填")
    private Boolean required;

    @Column
    @Comment("是否内置")
    @ColDefine(type = ColType.BOOLEAN)
    @Default("0")
    @ApiModelProperty(description = "是否内置")
    private Boolean system;

    @Column
    @Comment("是否为响应字段")
    @ColDefine(type = ColType.BOOLEAN)
    @Default("0")
    @ApiModelProperty(description = "是否为响应字段")
    private Boolean response;

    @Column
    @Comment("排序")
    @ColDefine(type = ColType.INT, width = 10)
    @ApiModelProperty(description = "排序")
    private Integer location;

    @Column
    @Comment("枚举JSON(value:text)")
    @ColDefine(type = ColType.VARCHAR, width = 300)
    @ApiModelProperty(description = "枚举JSON(value:text)")
    private String enumJson;
}
