package com.budwk.nb.sys.models;

import com.budwk.nb.commons.base.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * 应用管理--推送任务表
 * @author wizzer(wizzer.cn) on 2019/2/27.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_app_task")
public class Sys_app_task extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("实例名")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String name;

    @Column
    @Comment("执行动作")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String action;

    @Column
    @Comment("APP版本")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String appVersion;

    @Column
    @Comment("配置版本")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String confVersion;

    @Column
    @Comment("进程ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String processId;

    /**
     *  主机名 如果是推送给全部主机则为多条
     */
    @Column
    @Comment("推送主机")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String hostName;

    /**
     * 只为展示用
     */
    @Column
    @Comment("主机IP")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String hostAddress;

    /**
     * 0-待执行,1-执行中,2-执行成功,3-执行失败,4-撤销任务
     */
    @Column
    @Comment("推送状态")
    @ColDefine(type = ColType.INT, width = 1)
    private Integer status;

    @Column
    @Comment("反馈时间")
    private Long pushAt;

    @Column
    @Comment("反馈结果")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String pushResult;

    @One(field = "createdBy")
    private Sys_user user;

}
