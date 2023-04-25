package com.budwk.app.device.models;

import com.budwk.app.device.enums.DataType;
import com.budwk.app.device.enums.ParamType;
import com.budwk.starter.common.enums.Validation;
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
@Comment("产品设备参数")
@ApiModel(description = "产品设备参数")
public class Device_product_attr extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL(value = "snowflake()")})
    @Comment("id")
    @ApiModelProperty(description = "id")
    private String id;

    @Column
    @Comment("产品ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "产品ID")
    private String productId;

    @Column
    @Comment("名称")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(description = "名称")
    private String name;

    @Column
    @Comment("标识")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(name = "code", description = "标识符", required = true, check = true, validation = Validation.LOWER)
    private String code;

    /**
     * @see DataType
     */
    @Column
    @Comment("数据类型")
    @ColDefine(type = ColType.INT)
    @ApiModelProperty(description = "数据类型")
    private DataType dataType;

    /**
     * @see ParamType
     */
    @Column
    @Comment("参数类型")
    @ColDefine(type = ColType.INT)
    @ApiModelProperty(description = "参数类型")
    private ParamType paramType;

    @Column
    @Comment("字节长度")
    @ColDefine(type = ColType.INT)
    @ApiModelProperty(description = "字节长度")
    private Integer dataLen;

    @Column
    @Comment("小数位数")
    @ColDefine(type = ColType.INT)
    @ApiModelProperty(description = "小数位数")
    private Integer scale;

    @Column
    @Comment("单位")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    @ApiModelProperty(description = "单位")
    private String unit;

    @Column
    @Comment("最小值")
    @ColDefine(type = ColType.VARCHAR, width = 30)
    @ApiModelProperty(description = "最小值")
    private String minimum;

    @Column
    @Comment("最大值")
    @ColDefine(type = ColType.VARCHAR, width = 30)
    @ApiModelProperty(description = "最大值")
    private String maximum;

    @Column
    @Comment("枚举JSON(value:text)")
    @ColDefine(type = ColType.VARCHAR, width = 300)
    @ApiModelProperty(description = "枚举JSON(value:text)")
    private String enumJson;

    @Column
    @Comment("说明")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @ApiModelProperty(description = "说明")
    private String description;

    @Column
    @Comment("排序")
    @ColDefine(type = ColType.INT)
    private Integer location;
}
