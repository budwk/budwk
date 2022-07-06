package com.budwk.app.sys.models;

import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * @author wizzer@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_task")
@TableMeta("{'mysql-charset':'utf8mb4'}")
@ApiModel(description = "系统定时任务表")
public class Sys_task extends BaseModel implements Serializable {

    private static final long serialVersionUID = 4544764117720217507L;

    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "ID")
    @PrevInsert(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("名称")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(description = "名称", required = true, check = true)
    private String name;

    @Column
    @Comment("IOC对象名称")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "IOC对象名称", required = true, check = true)
    private String iocName;

    @Column
    @Comment("任务名称")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "任务名称", required = true, check = true)
    private String jobName;

    @Column
    @Comment("任务说明")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "任务说明")
    private String note;

    @Column
    @Comment("定时规则")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(description = "定时规则", required = true, check = true)
    private String cron;

    @Column
    @Comment("执行参数")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "执行参数")
    private String params;

    @Column
    @Comment("是否禁用")
    @ColDefine(type = ColType.BOOLEAN)
    @ApiModelProperty(description = "是否禁用")
    private boolean disabled;
}
