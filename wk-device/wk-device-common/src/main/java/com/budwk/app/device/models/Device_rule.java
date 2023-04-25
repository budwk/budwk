package com.budwk.app.device.models;

import com.budwk.app.device.enums.RuleAction;
import com.budwk.app.device.objects.rule.RuleTrigger;
import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;
import java.util.List;

/**
 * @author wizzer.cn
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table
@TableMeta("{'mysql-charset':'utf8mb4'}")
@Comment("规则配置")
@ApiModel(description = "规则配置")
public class Device_rule extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL(value = "snowflake()")})
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    private String id;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @Comment("规则名称")
    @ApiModelProperty(description = "规则名称")
    private String name;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @Comment("产品ID")
    @ApiModelProperty(description = "产品ID")
    private String productId;

    @Column
    @ColDefine(type = ColType.TEXT)
    @Comment("触发条件")
    @ApiModelProperty(description = "触发条件")
    private List<RuleTrigger> triggerList;

    @Column
    @ColDefine(type = ColType.TEXT)
    @Comment("生成的QL语句")
    private String triggerQL;

    @Column
    @ColDefine(type = ColType.INT, width = 1)
    @Comment("动作类型")
    @ApiModelProperty(description = "动作类型")
    private RuleAction action;

    /**
     * 通知：[{ "type": "sms","mobile":"手机号"}]
     * URL转发：{ "url":"https://"}
     * 指令下发：{ "cmd":"指令名称","params":{}}
     */
    @Column
    @ColDefine(type = ColType.VARCHAR, width = 2000)
    @Comment("动作定义")
    @ApiModelProperty(description = "动作定义")
    private String actionJson;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 512)
    @Comment("规则说明")
    @ApiModelProperty(description = "规则说明")
    private String note;
}
