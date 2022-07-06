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
 * 角色分组
 *
 * @author wizzer@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_group")
@TableMeta("{'mysql-charset':'utf8mb4'}")
@ApiModel(description = "系统角色分组表")
public class Sys_group extends BaseModel implements Serializable {
    private static final long serialVersionUID = 8106561665016556432L;

    @Column
    @Name
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(uu32 = true, nullEffective = true)
    private String id;

    @Column
    @Comment("租户ID")
    @ApiModelProperty(description = "租户ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String tenantId;

    @Column
    @Comment("单位ID")
    @ApiModelProperty(description = "单位ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String unitId;

    @Column
    @Comment("单位PATH")
    @ApiModelProperty(description = "单位PATH")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String unitPath;

    @Column
    @Comment("分组名称")
    @ApiModelProperty(description = "分组名称")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String name;

    @Many(field = "groupId")
    @ApiModelProperty(description = "角色列表")
    protected List<Sys_role> roles;
}
