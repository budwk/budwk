package com.budwk.app.sys.models;

import com.budwk.app.sys.enums.SysLeaderType;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * 系统用户单位关联表
 *
 * @author wizzer@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_unit_user")
@TableMeta("{'mysql-charset':'utf8mb4'}")
public class Sys_unit_user extends BaseModel implements Serializable {

    private static final long serialVersionUID = 9049663935278478679L;
    @Column
    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String id;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String unitId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private SysLeaderType leaderType;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String userId;

    @One(field = "userId")
    public Sys_user user;
}
