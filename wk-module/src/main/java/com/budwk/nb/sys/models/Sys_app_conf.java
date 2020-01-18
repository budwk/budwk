package com.budwk.nb.sys.models;

import com.budwk.nb.commons.base.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * 应用管理--配置文件表
 * @author wizzer(wizzer@qq.com) on 2019/2/27.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_app_conf")
public class Sys_app_conf extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("实例名")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String confName;

    @Column
    @Comment("版本号")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String confVersion;

    @Column
    @Comment("配置内容")
    @ColDefine(type = ColType.TEXT)
    private String confData;

    @Column
    @Comment("是否禁用")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean disabled;

    @One(field = "createdBy")
    private Sys_user user;

}
