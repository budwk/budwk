package com.budwk.app.device.models;

import com.budwk.app.device.enums.DeviceType;
import com.budwk.starter.common.enums.Validation;
import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * 设备类型
 *
 * @author wizzer.cn
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table
@TableMeta("{'mysql-charset':'utf8mb4'}")
@Comment("设备类型")
@ApiModel(description = "设备类型")
public class Device_type extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("snowflake()")})
    @ApiModelProperty(description = "id")
    private String id;

    @Column
    @Comment("分类名称")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(name = "name", description = "分类名称")
    private String name;

    @Column
    @Comment("业务类型")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    @ApiModelProperty(name = "name", description = "业务类型")
    private DeviceType type;

    @Column
    @Comment("类型标识")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(name = "code", description = "分类编码", required = true, check = true, validation = Validation.LOWER_UNDERLINE_NUMBER)
    private String code;

    @Column
    @Comment("父级ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(name = "parentId", description = "父级ID")
    private String parentId;

    @Column
    @Comment("树路径")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @ApiModelProperty(name = "path", description = "树路径")
    private String path;

    @Column
    @Comment("有子节点")
    @ApiModelProperty(name = "hasChildren", description = "有子节点")
    private boolean hasChildren;

    @Column
    @Comment("排序")
    @ColDefine(type = ColType.INT)
    private Integer location;

    @Column
    @Comment("图标")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(name = "icon", description = "图标")
    private String icon;

    @Column
    @Comment("颜色")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    @ApiModelProperty(name = "color", description = "颜色")
    private String color;
}
