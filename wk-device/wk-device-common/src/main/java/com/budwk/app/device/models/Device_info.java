package com.budwk.app.device.models;

import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import com.budwk.starter.excel.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * 设备信息
 *
 * @author wizzer.cn
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table
@TableMeta("{'mysql-charset':'utf8mb4'}")
@TableIndexes({
        @Index(name = "IDX_DEVICE_CODE", fields = "deviceCode"),
        @Index(name = "IDX_DEVICE_CONNET_NO", fields = "deviceNo", unique = false),
        @Index(name = "IDX_DEVICE_NO", fields = "deviceNo", unique = false),
        @Index(name = "IDX_DEVICE_IMEI", fields = "imei", unique = false),
        @Index(name = "IDX_DEVICE_PRODUCT_ID", fields = "productId", unique = false),
})
@Comment("设备信息")
@ApiModel(description = "设备信息")
public class Device_info extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("snowflake()")})
    @ApiModelProperty(description = "id")
    private String id;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("产品ID")
    @ApiModelProperty(description = "产品ID")
    private String productId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 20)
    @Comment("IMEI")
    @Excel(name = "IMEI")
    private String imei;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 30)
    @Comment("ICCID")
    @Excel(name = "ICCID")
    private String iccid;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("设备通讯号")
    @ApiModelProperty(description = "设备通讯号")
    @Excel(name = "设备通讯号")
    private String deviceNo;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 35)
    @Comment("设备编码（厂家编码+设备通讯号）")
    @ApiModelProperty(description = "设备编码（厂家编码+设备通讯号）")
    @Excel(name = "设备编码")
    private String deviceCode;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("设备编号（铭牌号）")
    @ApiModelProperty(description = "设备编号（铭牌号）")
    @Excel(name = "设备编号（铭牌号）")
    private String nameplateNo;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @Comment("第三方平台设备ID")
    @ApiModelProperty(description = "第三方平台设备ID")
    @Excel(name = "第三方平台设备ID")
    private String platformDeviceId;

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

    @Column
    @Comment("厂商ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(name = "supplierId", description = "厂商ID", required = true)
    private String supplierId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("经度")
    @ApiModelProperty(description = "经度")
    @Excel(name = "经度")
    private String lng;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("纬度")
    @ApiModelProperty(description = "纬度")
    @Excel(name = "纬度")
    private String lat;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 10)
    @Comment("出厂日期")
    @ApiModelProperty(description = "出厂日期")
    @Excel(name = "出厂日期")
    private String manufactureDate;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 10)
    @Comment("安装日期")
    @ApiModelProperty(description = "安装日期")
    @Excel(name = "安装日期")
    private String installDate;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @Comment("安装区域")
    @ApiModelProperty(description = "安装区域")
    @Excel(name = "安装区域")
    private String installArea;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @Comment("安装地址")
    @ApiModelProperty(description = "安装地址")
    @Excel(name = "安装地址")
    private String installAddress;

}
