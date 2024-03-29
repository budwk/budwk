package com.budwk.app.device.models;

import com.budwk.starter.common.openapi.annotation.ApiModel;
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
@Comment("规则设备关联表")
@ApiModel(description = "规则设备关联表")
public class Device_rule_device extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL(value = "snowflake()")})
    private String id;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String ruleId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String deviceId;
}
