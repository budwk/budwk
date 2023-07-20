package com.budwk.app.device.objects.query;

import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wizzer.cn
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class PageQuery {

    public static final PageQuery DEFAULT = new PageQuery();

    @ApiModelProperty(description = "页码")
    private Integer pageNo = 1;
    @ApiModelProperty(description = "每页数量")
    private Integer pageSize = 10;

}