package com.budwk.nb.sys.models;

import com.budwk.nb.commons.base.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;
import java.util.List;

/**
 * @author wizzer(wizzer.cn) on 2016/6/21.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_role")
@TableIndexes({@Index(name = "INDEX_SYS_ROLE_CODE", fields = {"code"}, unique = true)})
public class Sys_role extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("角色名称")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String name;

    @Column
    @Comment("角色代码")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String code;

    @Column
    @Comment("是否禁用")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean disabled;

    @Column
    @Comment("单位ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String unitid;

    @Column
    @Comment("角色备注")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String note;

    @One(field = "unitid")
    public Sys_unit unit;

    @One(field = "createdBy")
    public Sys_user createdByUser;

    @ManyMany(from = "roleId", relation = "sys_role_menu", to = "menuId")
    protected List<Sys_menu> menus;

    @ManyMany(from = "roleId", relation = "sys_user_role", to = "userId")
    private List<Sys_user> users;

}
