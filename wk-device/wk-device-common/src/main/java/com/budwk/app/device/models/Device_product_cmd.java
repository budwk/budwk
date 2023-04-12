package com.budwk.app.device.models;

import com.budwk.app.device.enums.CommandType;
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
@Comment("产品设备指令")
@ApiModel(description = "产品设备指令")
public class Device_product_cmd extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL(value = "snowflake()")}, nullEffective = true)
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    private String id;

    @Column
    @Comment("产品ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "产品ID")
    private String productId;

    @Column
    @Comment("指令名称")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(description = "指令名称")
    private String name;

    @Column
    @Comment("指令标识")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(description = "指令标识")
    private String identifier;

    /**
     * @see CommandType
     */
    @Column
    @Comment("指令类型")
    @ColDefine(type = ColType.INT)
    @ApiModelProperty(description = "指令类型")
    private CommandType commandType;

    @Column
    @Comment("说明")
    @ColDefine(type = ColType.VARCHAR, width = 300)
    @ApiModelProperty(description = "说明")
    private String description;

    @Column
    @Comment("是否内置")
    @ColDefine(type = ColType.BOOLEAN)
    @Default("0")
    @ApiModelProperty(description = "是否内置")
    private Boolean system;

    @Column
    @Comment("是否启用")
    @ColDefine(type = ColType.BOOLEAN)
    @Default("1")
    @ApiModelProperty(description = "是否启用")
    private Boolean enabled;

    private List<Device_product_cmd_attr> attributes;
}
