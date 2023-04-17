package com.budwk.app.device.models;

import com.budwk.app.device.enums.CommandStatus;
import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;
import java.util.Map;

/**
 * @author wizzer.cn
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table
@TableMeta("{'mysql-charset':'utf8mb4'}")
@TableIndexes({
        @Index(name = "IDX_DEVICE_HANDLER_CODE", fields = "code")
})
@Comment("设备指令记录")
@ApiModel(description = "设备指令记录")
public class Device_command_record extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("snowflake()")}, nullEffective = true)
    @ApiModelProperty(description = "id")
    private String id;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("产品ID")
    @ApiModelProperty(description = "产品ID")
    private String productId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("设备ID")
    @ApiModelProperty(description = "设备ID")
    private String deviceId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("设备通讯号")
    @ApiModelProperty(description = "设备通讯号")
    private String deviceNo;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("指令ID")
    @ApiModelProperty(description = "指令ID")
    private String commandId;

    /**
     * 对应 Device_product_cmd.code
     */
    @Column
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @Comment("指令标识")
    @ApiModelProperty(description = "指令标识")
    private String commandCode;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 500)
    @Comment("指令参数")
    @ApiModelProperty(description = "指令参数")
    private Map<String, Object> params;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 500)
    @Comment("备注")
    @ApiModelProperty(description = "备注")
    private String note;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @Comment("指令名称")
    @ApiModelProperty(description = "指令名称")
    private String commandName;

    /**
     * 有些表协议需要根据这个来查询指令
     */
    @Column
    @Comment("指令序号")
    @ApiModelProperty(description = "指令序号")
    private Integer serialNo;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 10)
    @Comment("指令状态")
    @ApiModelProperty(description = "指令状态")
    private CommandStatus status;

    @Column
    @Comment("下发时间")
    @ApiModelProperty(description = "下发时间")
    private Long sendAt;

    @Column
    @Comment("结束时间")
    @ApiModelProperty(description = "结束时间")
    private Long finishAt;

    @Column
    @Comment("操作人")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    @ApiModelProperty(description = "操作人")
    private String operator;

    @Column
    @Comment("触发原因")
    @ColDefine(type = ColType.VARCHAR, width = 500)
    @ApiModelProperty(description = "触发原因")
    private String reason;

    //响应结果
    private String resultDetail;

    //指令返回内容
    private String cmdResp;

    //响应属性
    private String respAttribute;
}
