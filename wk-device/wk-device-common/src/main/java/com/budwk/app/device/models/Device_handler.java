package com.budwk.app.device.models;

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
 * @author wizzer.cn
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table
@TableMeta("{'mysql-charset':'utf8mb4'}")
@TableIndexes({
        @Index(name = "IDX_DEVICE_HANDLER_CODE", fields = "code")
})
@Comment("协议解析包")
@ApiModel(description = "协议解析包")
public class Device_handler extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("snowflake()")})
    @ApiModelProperty(description = "id")
    private String id;

    @Column
    @Comment("名称")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(name = "name", description = "名称")
    private String name;

    @Column
    @Comment("标识符")
    @ColDefine(type = ColType.VARCHAR, width = 30)
    @ApiModelProperty(name = "code", description = "标识符", required = true, check = true, validation = Validation.LOWER_UNDERLINE_NUMBER)
    private String code;

    @Column
    @Comment("文件名称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @ApiModelProperty(name = "fileName", description = "文件名称")
    private String fileName;

    @Column
    @Comment("文件地址")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(name = "filePath", description = "文件地址")
    private String filePath;

    @Column
    @Comment("解析类路径")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @ApiModelProperty(name = "classPath", description = "解析类路径")
    private String classPath;

    @Column
    @Comment("描述")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @ApiModelProperty(name = "description", description = "描述")
    private String description;

    @Column
    @Comment("是否启用")
    @ColDefine(type = ColType.BOOLEAN)
    @ApiModelProperty(name = "enabled", description = "是否启用")
    private Boolean enabled;
}
