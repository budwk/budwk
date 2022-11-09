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
@Table("sys_user_pwd")
@TableMeta("{'mysql-charset':'utf8mb4'}")
@ApiModel(description = "用户密码记录表")
public class Sys_user_pwd extends BaseModel implements Serializable {
    private static final long serialVersionUID = -3044639976455798712L;
    @Column
    @Name
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(uu32 = true)
    private String id;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String userId;

    @Column
    @Comment("密码")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @ApiModelProperty(description = "密码")
    private String password;

    @Column
    @Comment("密码盐")
    @ApiModelProperty(description = "密码盐")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String salt;
}
