package com.budwk.app.sys.models;

import com.budwk.starter.common.enums.Validation;
import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单表
 *
 * @author wizzer@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_menu")
@TableMeta("{'mysql-charset':'utf8mb4'}")
@ApiModel(description = "系统菜单表")
@TableIndexes({@Index(name = "INDEX_SYS_MENU_PATH", fields = {"appId", "path"}, unique = true), @Index(name = "INDEX_SYS_MENU_PREM", fields = {"permission"}, unique = true)})
public class Sys_menu extends BaseModel implements Serializable {

    private static final long serialVersionUID = 5599458335246369118L;

    @Column
    @Name
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(uu32 = true)
    private String id;

    @Column
    @Comment("应用ID")
    @ApiModelProperty(description = "应用ID", example = "PLATFORM", required = true, check = true, validation = Validation.UPPER)
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String appId;

    @Column
    @Comment("父级ID")
    @ApiModelProperty(description = "父级ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String parentId;

    @Column
    @Comment("树路径")
    @ApiModelProperty(description = "树路径")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String path;

    @Column
    @Comment("菜单名称")
    @ApiModelProperty(description = "菜单名称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String name;

    @Column
    @Comment("多语言标识符")
    @ApiModelProperty(description = "多语言标识符")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String alias;

    @Column
    @Comment("资源类型")
    @ApiModelProperty(description = "资源类型", example = "MENU-菜单/DATA-权限")
    @ColDefine(type = ColType.VARCHAR, width = 10)
    private String type;

    @Column
    @Comment("菜单链接")
    @ApiModelProperty(description = "菜单链接")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String href;

    @Column
    @Comment("打开方式")
    @ApiModelProperty(description = "打开方式")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String target;

    @Column
    @Comment("菜单图标")
    @ApiModelProperty(description = "菜单图标")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String icon;

    @Column
    @Comment("是否显示")
    @ApiModelProperty(description = "是否显示")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean showit;

    @Column
    @Comment("是否禁用")
    @ApiModelProperty(description = "是否禁用")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean disabled;

    @Column
    @Comment("权限标识")
    @ApiModelProperty(description = "权限标识")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String permission;

    @Column
    @Comment("菜单介绍")
    @ApiModelProperty(description = "菜单介绍")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String note;

    @Column
    @Comment("排序字段")
    @ApiModelProperty(description = "排序字段")
    @Prev({
            @SQL(db = DB.MYSQL, value = "SELECT IFNULL(MAX(location),0)+1 FROM sys_menu"),
            @SQL(db = DB.ORACLE, value = "SELECT COALESCE(MAX(location),0)+1 FROM sys_menu")
    })
    private Integer location;

    @Column
    @Comment("有子节点")
    @ApiModelProperty(description = "有子节点")
    private boolean hasChildren;

    @ApiModelProperty(description = "按钮列表")
    private List<Sys_menu> buttons;

}
