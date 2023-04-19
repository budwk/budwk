package com.budwk.app.device.models;

import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * @author wizzer.cn
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table
@TableMeta("{'mysql-charset':'utf8mb4'}")
@Comment("设备最新数据")
@ApiModel(description = "设备最新数据")
public class Device_info_latest_data extends BaseModel implements Serializable {
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
    @ColDefine(type = ColType.TEXT)
    @Comment("属性数据(JSON)")
    @ApiModelProperty(description = "属性数据(JSON)")
    private String attrJson;
}
