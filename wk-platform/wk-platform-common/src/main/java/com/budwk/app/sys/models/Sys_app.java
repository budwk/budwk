package com.budwk.app.sys.models;

import com.budwk.starter.common.enums.Validation;
import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * 系统应用表
 *
 * @author wizzer@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_app")
@TableMeta("{'mysql-charset':'utf8mb4'}")
@ApiModel(description = "系统应用表")
public class Sys_app extends BaseModel implements Serializable {
    private static final long serialVersionUID = 7641962702833856043L;

    @Name
    @Column
    @Comment("ID")
    @ApiModelProperty(description = "应用ID", required = true, check = true, validation = Validation.UPPER)
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String id;

    @Column
    @Comment("应用名称")
    @ApiModelProperty(description = "应用名称", required = true, check = true)
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String name;

    @Column
    @Comment("应用路径")
    @ApiModelProperty(description = "应用路径")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String path;

    @Column
    @Comment("应用图标")
    @ApiModelProperty(description = "应用图标")
    @ColDefine(type = ColType.TEXT)
    private String icon;

    @Column
    @Comment("是否隐藏")
    @ApiModelProperty(description = "是否隐藏")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean hidden;

    @Column
    @Comment("是否禁用")
    @ApiModelProperty(description = "是否禁用")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean disabled;

    @Column
    @Comment("排序字段")
    @ApiModelProperty(description = "排序字段")
    @Prev({
            @SQL(db = DB.MYSQL, value = "SELECT IFNULL(MAX(location),0)+1 FROM sys_app"),
            @SQL(db = DB.ORACLE, value = "SELECT COALESCE(MAX(location),0)+1 FROM sys_app")
    })
    private Integer location;
}
