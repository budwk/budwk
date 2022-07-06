package com.budwk.app.sys.models;

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
 * 系统角色表
 *
 * @author wizzer@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_role")
@TableMeta("{'mysql-charset':'utf8mb4'}")
@ApiModel(description = "系统角色表")
@TableIndexes({@Index(name = "INDEX_SYS_ROLE_CODE", fields = {"code"}, unique = true)})
public class Sys_role extends BaseModel implements Serializable {
    private static final long serialVersionUID = -3044639976455798647L;
    @Column
    @Name
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(uu32 = true)
    private String id;

    @Column
    @Comment("租户ID")
    @ApiModelProperty(description = "租户ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String tenantId;

    @Column
    @Comment("角色名称")
    @ApiModelProperty(description = "角色名称")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String name;

    @Column
    @Comment("角色代码")
    @ApiModelProperty(description = "角色代码")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String code;

    @Column
    @Comment("是否禁用")
    @ApiModelProperty(description = "是否禁用")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean disabled;

    @Column
    @Comment("单位ID")
    @ApiModelProperty(description = "单位ID", example = "角色必须绑定单位,支持权限继承")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String unitId;

    @Column
    @Comment("分组ID")
    @ApiModelProperty(description = "分组ID", example = "角色必须绑定分组")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String groupId;

    @Column
    @Comment("角色备注")
    @ApiModelProperty(description = "角色备注")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String note;

    @One(field = "unitId")
    @ApiModelProperty(description = "单位信息")
    public Sys_unit unit;

    @One(field = "createdBy")
    @ApiModelProperty(description = "创建者信息")
    public Sys_user createdByUser;

    @One(field = "groupId")
    @ApiModelProperty(description = "所属分组")
    protected Sys_group group;

    @ManyMany(from = "roleId", relation = "sys_role_menu", to = "menuId")
    @ApiModelProperty(description = "菜单权限列表")
    protected List<Sys_menu> menus;

    @ManyMany(from = "roleId", relation = "sys_role_app", to = "appId")
    @ApiModelProperty(description = "菜单权限列表")
    protected List<Sys_app> apps;

    @ManyMany(from = "roleId", relation = "sys_role_user", to = "userId")
    @ApiModelProperty(description = "角色用户列表")
    private List<Sys_user> users;

}
