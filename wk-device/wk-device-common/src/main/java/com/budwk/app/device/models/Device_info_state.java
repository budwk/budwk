package com.budwk.app.device.models;

import com.budwk.app.device.enums.DeviceOnline;
import com.budwk.app.device.enums.DeviceState;
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
@TableIndexes({
        @Index(name = "IDX_DEVICE_STATE", fields = "state", unique = false),
        @Index(name = "IDX_DEVICE_ONLINE", fields = "online", unique = false),
})
@Comment("设备基础信息")
@ApiModel(description = "设备基础信息")
public class Device_info_state extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Name
    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("设备ID")
    @ApiModelProperty(description = "设备ID")
    private String deviceId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 10)
    @Comment("设备状态")
    @ApiModelProperty(description = "设备状态")
    @Default("NORMAL")
    private DeviceState state;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 10)
    @Comment("在线状态")
    @ApiModelProperty(description = "在线状态")
    @Default("NOTACTIVE")
    private DeviceOnline online;

}
