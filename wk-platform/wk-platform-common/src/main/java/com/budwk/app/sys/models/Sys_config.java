package com.budwk.app.sys.models;

import com.budwk.app.sys.enums.SysConfigType;
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
 * 系统参数配置
 *
 * @author wizzer@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_config")
@TableMeta("{'mysql-charset':'utf8mb4'}")
@TableIndexes({@Index(name = "INDEX_SYS_CONFIG", fields = {"appId", "configKey"}, unique = true)})
@ApiModel(description = "系统参数配置表")
public class Sys_config extends BaseModel implements Serializable {

    private static final long serialVersionUID = -3134615340396215317L;

    @Name
    @Column
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(uu32 = true)
    private String id;

    @Column
    @Comment("应用ID,COMMON=公共")
    @ApiModelProperty(description = "COMMON=公共", example = "COMMON", required = true, check = true, validation = Validation.UPPER)
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String appId;

    @Column
    @Comment("参数类型")
    @ApiModelProperty(description = "参数类型(TEXT-字符串/IMAGE-图片路径/BOOL-布尔值)", example = "TEXT")
    @ColDefine(type = ColType.VARCHAR, width = 10)
    private SysConfigType type;

    @Column
    @Comment("配置key")
    @ApiModelProperty(description = "配置key")
    @ColDefine(type = ColType.VARCHAR, width = 125)
    private String configKey;

    @Column
    @Comment("配置value")
    @ApiModelProperty(description = "配置value")
    @ColDefine(type = ColType.VARCHAR, width = 125)
    private String configValue;

    @Column
    @Comment("配置说明")
    @ApiModelProperty(description = "配置说明")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String note;

    @Column
    @Comment("是否开放")
    @ApiModelProperty(description = "是否开放")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean opened;
}
