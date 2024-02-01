package com.budwk.app.device.models;

import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("device_event_record_${month}")
@TableIndexes(value = {
        @Index(name = "I_EVT_DID", fields = "deviceId", unique = false),
        @Index(name = "I_EVT_REC_PRODID", fields = "productId", unique = false),
        @Index(name = "I_EVT_REC_DEVNO", fields = "deviceNo", unique = false)
})
@Comment("设备事件记录")
@ApiModel(description = "设备事件记录")
public class Device_event_record extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL(value = "snowflake()")})
    @Comment("id")
    @ApiModelProperty(description = "id")
    private String id;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("设备id")
    @ApiModelProperty(description = "设备id")
    private String deviceId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("设备编号")
    @ApiModelProperty(description = "设备编号")
    private String deviceNo;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("产品id")
    @ApiModelProperty(description = "产品id")
    private String productId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("用户号(预留)")
    @ApiModelProperty(description = "用户号")
    private String accountNo;

    @Column
    @Comment("发生时间")
    @ApiModelProperty(description = "发生时间")
    private Long eventTime;

    @Column
    @Comment("事件类型")
    @ColDefine(type = ColType.VARCHAR,width = 20)
    @ApiModelProperty(description = "事件类型")
    private String eventType;

    @Column
    @Comment("事件内容")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    @ApiModelProperty(description = "事件内容")
    private String content;

    @Column
    @Comment("事件内容")
    @ColDefine(type = ColType.MYSQL_JSON, width = 500)
    @ApiModelProperty(description = "事件数据")
    private List<Map<String,Object>> eventData;

}