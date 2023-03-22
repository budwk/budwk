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
@Table("sys_task_history_${month}")
@TableMeta("{'mysql-charset':'utf8mb4'}")
@ApiModel(description = "定时任务历史记录表")
@TableIndexes({@Index(name = "INDEX_SYS_TASK_HISTORY_${month}", fields = {"instanceId", "jobId"}, unique = true)})
public class Sys_task_history extends BaseModel implements Serializable {

    private static final long serialVersionUID = -3769322238060559811L;

    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "ID")
    @PrevInsert(els = {@EL("snowflake()")})
    private String id;

    @Column
    @Comment("任务ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "任务ID")
    private String taskId;

    @Column
    @Comment("实例ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "实例ID")
    private String instanceId;

    @Column
    @Comment("作业ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "作业ID")
    private String jobId;

    @Column
    @Comment("是否成功")
    @ColDefine(type = ColType.BOOLEAN)
    @ApiModelProperty(description = "是否成功")
    private boolean success;

    @Column
    @Comment("错误信息")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "任务说明")
    private String message;

    @Column
    @Comment("执行耗时")
    @ApiModelProperty(description = "执行耗时")
    private Long tookTime;

    @Column
    @Comment("执行时间")
    @ApiModelProperty(description = "执行时间")
    private Long endTime;
}
