package com.budwk.app.sys.models;

import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * 系统角色菜单关联表
 *
 * @author wizzer@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_role_menu")
@TableMeta("{'mysql-charset':'utf8mb4'}")
@TableIndexes({@Index(name = "INDEX_SYS_ROLE_MENU", fields = {"appId", "roleId", "menuId"}, unique = true)})
public class Sys_role_menu extends BaseModel implements Serializable {

    private static final long serialVersionUID = 2202555838954679931L;
    @Column
    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String id;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String appId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String roleId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String menuId;
}
