package com.budwk.app.device.models;

import com.budwk.app.device.enums.SubscribeType;
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
@TableMeta("{'mysql-charset':'utf8mb4'}")
@Comment("产品订阅")
@ApiModel(description = "产品订阅")
public class Device_product_subscribe extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL(value = "snowflake()")})
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    private String id;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("产品ID")
    @ApiModelProperty(description = "产品ID")
    private String productId;

    /**
     * @see SubscribeType
     */
    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("订阅类型")
    @ApiModelProperty(description = "订阅类型")
    private SubscribeType subscribeType;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 512)
    @Comment("订阅地址")
    @ApiModelProperty(description = "订阅地址")
    private String subscribeUrl;

    @Column
    @ColDefine(type = ColType.BOOLEAN)
    @Comment("是否启用")
    @ApiModelProperty(description = "是否启用")
    private Boolean enabled;

}
