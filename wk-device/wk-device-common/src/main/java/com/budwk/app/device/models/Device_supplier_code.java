package com.budwk.app.device.models;

import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * 供应商设备编码
 *
 * @author wizzer.cn
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table
@TableMeta("{'mysql-charset':'utf8mb4'}")
@TableIndexes({
        @Index(name = "IDX_DEVICE_SUPPLIER_CODE", fields = "code")
})
@Comment("供应商设备信息")
@ApiModel(description = "供应商设备信息")
public class Device_supplier_code extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("snowflake()")}, nullEffective = true)
    @ApiModelProperty(description = "id")
    private String id;

    @Column
    @Comment("厂商ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(name = "supplierId", description = "厂商ID", required = true)
    private String supplierId;

    @Column
    @Comment("型号名称")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(name = "name", description = "型号名称", required = true)
    private String name;

    @Column
    @Comment("型号编码")
    @ColDefine(type = ColType.VARCHAR, width = 10)
    @ApiModelProperty(name = "code", description = "型号编码", required = true)
    private String code;
}
