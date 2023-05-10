package com.budwk.app.device.models;

import com.budwk.app.device.enums.ValveState;
import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * 表具设备最新信息
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
public class Device_info_meter extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Name
    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("设备ID")
    @ApiModelProperty(description = "设备ID")
    private String deviceId;

    @Column
    @Comment("接收时间")
    @ApiModelProperty(description = "接收时间")
    private Long receiveTime;

    @Column
    @Comment("上报时间")
    @ApiModelProperty(description = "上报时间")
    private Long reportTime;

    @Column
    @Comment("抄表时间")
    @ApiModelProperty(description = "抄表时间")
    private Long readTime;

    @Column
    @Comment("表具读数")
    @ApiModelProperty(description = "表具读数")
    private Double readNumber;

    @Column
    @Comment("阀门状态")
    @ApiModelProperty(description = "阀门状态")
    @ColDefine(type = ColType.INT, width = 1)
    private ValveState valveState;
}
