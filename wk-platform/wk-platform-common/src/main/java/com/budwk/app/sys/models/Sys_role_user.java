package com.budwk.app.sys.models;

import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * 系统角色用户关联表
 *
 * @author wizzer@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_role_user")
@TableMeta("{'mysql-charset':'utf8mb4'}")
public class Sys_role_user extends BaseModel implements Serializable {

    private static final long serialVersionUID = -507747309971899026L;
    @Column
    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String id;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String roleId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String userId;
}
