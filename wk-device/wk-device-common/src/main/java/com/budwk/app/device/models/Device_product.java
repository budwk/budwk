package com.budwk.app.device.models;

import com.budwk.app.device.enums.IotPlatform;
import com.budwk.app.device.enums.ProtocolType;
import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * 产品信息
 * @author wizzer.cn
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table
@TableMeta("{'mysql-charset':'utf8mb4'}")
@Comment("产品信息")
@ApiModel(description = "产品信息")
public class Device_product extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("snowflake()")}, nullEffective = true)
    @ApiModelProperty(description = "id")
    private String id;

    @Column
    @Comment("产品名称")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(name = "name", description = "产品名称", required = true)
    private String name;

    /**
     * @see Device_type id
     */
    @Column
    @Comment("类型ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(name = "typeId", description = "类型ID", required = true)
    private String typeId;

    /**
     * @see Device_type id
     */
    @Column
    @Comment("子类型ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(name = "subTypeId", description = "子类型ID", required = true)
    private String subTypeId;

    /**
     * @see Device_supplier id
     */
    @Column
    @Comment("设备厂商ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(name = "supplierId", description = "设备厂商ID", required = true)
    private String supplierId;

    /**
     * @see Device_supplier_code code
     */
    @Column
    @Comment("设备厂商代码")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(name = "supplierCode", description = "设备厂商代码", required = true)
    private String supplierCode;

    @Column
    @ColDefine(type = ColType.BOOLEAN)
    @Comment("阀门控制")
    @ApiModelProperty(name = "valveControl", description = "阀门控制")
    private Boolean valveControl;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 10)
    @Comment("协议类型")
    @ApiModelProperty(description = "协议类型")
    private ProtocolType protocolType;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 10)
    @Comment("接入平台")
    @ApiModelProperty(name = "iotPlatform", description = "接入平台")
    private IotPlatform iotPlatform;

    @Column
    @Comment("解析处理器")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    @ApiModelProperty(name = "handler", description = "解析处理器")
    private String handler;

    @Column
    @Comment("认证信息")
    @ColDefine(type = ColType.VARCHAR, width = 500)
    @ApiModelProperty(name = "authJson", description = "认证信息")
    private String authJson;

    @Column
    @Comment("备注")
    @ColDefine(type = ColType.VARCHAR, width = 300)
    @ApiModelProperty(name = "description", description = "备注")
    private String description;
}
