package com.budwk.app.sys.models;

import com.budwk.app.sys.enums.SysUnitType;
import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * 系统单位表
 *
 * @author wizzer@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_unit")
@TableMeta("{'mysql-charset':'utf8mb4'}")
@ApiModel(description = "系统单位表")
@TableIndexes({@Index(name = "INDEX_SYS_UNIT_PATH", fields = {"path"}, unique = true)})
public class Sys_unit extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1545072582160620868L;
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
    @Comment("单位类型")
    @ApiModelProperty(description = "单位类型")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private SysUnitType type;

    @Column
    @Comment("单位名称")
    @ApiModelProperty(description = "单位名称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String name;

    @Column
    @Comment("单位别名")
    @ApiModelProperty(description = "单位别名")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String aliasName;

    @Column
    @Comment("机构编码")
    @ApiModelProperty(description = "机构编码")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String unitcode;

    @Column
    @Comment("单位logo")
    @ApiModelProperty(description = "单位logo")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String logo;

    @Column
    @Comment("单位介绍")
    @ApiModelProperty(description = "单位介绍")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String note;

    @Column
    @Comment("所属区域编号")
    @ApiModelProperty(description = "所属区域编号")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String areaCode;

    @Column
    @Comment("所属区域名称")
    @ApiModelProperty(description = "所属区域名称")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String areaText;

    @Column
    @Comment("单位地址")
    @ApiModelProperty(description = "单位地址")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String address;

    @Column
    @Comment("联系电话")
    @ApiModelProperty(description = "联系电话")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String telephone;

    @Column
    @Comment("单位邮箱")
    @ApiModelProperty(description = "单位邮箱")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String email;

    @Column
    @Comment("单位网站")
    @ApiModelProperty(description = "单位网站")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String website;

    @Column
    @Comment("负责人姓名")
    @ApiModelProperty(description = "负责人姓名")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String leaderName;

    @Column
    @Comment("负责人电话")
    @ApiModelProperty(description = "负责人电话")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String leaderMobile;

    @Column
    @Comment("是否禁用")
    @ApiModelProperty(description = "是否禁用")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean disabled;

    @Column
    @Comment("排序字段")
    @ApiModelProperty(description = "排序字段")
    @Prev({
            @SQL(db = DB.MYSQL, value = "SELECT IFNULL(MAX(location),0)+1 FROM sys_unit"),
            @SQL(db = DB.ORACLE, value = "SELECT COALESCE(MAX(location),0)+1 FROM sys_unit")
    })
    private Integer location;

    @Column
    @Comment("有子节点")
    @ApiModelProperty(description = "有子节点")
    private boolean hasChildren;

}
