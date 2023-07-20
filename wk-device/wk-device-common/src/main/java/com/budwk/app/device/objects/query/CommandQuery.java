package com.budwk.app.device.objects.query;

import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "指令查询参数")
public class CommandQuery extends PageQuery implements Serializable {

    private static final long serialVersionUID = 2919682470026284473L;

    @ApiModelProperty(description = "产品id")
    private String productId;

    @ApiModelProperty(description = "设备id")
    private String deviceId;

    @ApiModelProperty(description = "开始日期（时间戳）")
    private Long startDate;

    @ApiModelProperty(description = "结束日期（时间戳）")
    private Long endDate;

    @ApiModelProperty(description = "指令id")
    private String commandId;

    @ApiModelProperty(description = "查询关键字")
    private String keyword;
}
