package com.budwk.starter.log.model;

import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * @author wizzer@qq.com
 */
@Data
@Accessors(chain = true)
@Table("sys_log_${month}")
@ApiModel(description = "日志对象")
public class Sys_log implements Serializable {
    private static final long serialVersionUID = -1400608463498555197L;

    @Column
    @Name
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "主键ID")
    private String id;

    @Column
    @Comment("租户ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "租户ID")
    private String tenantId;

    @Column
    @Comment("应用ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "应用ID")
    private String appId;

    @Column
    @Comment("用户ID")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "用户ID")
    private String userId;

    @Column
    @Comment("用户名")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "用户名")
    private String loginname;

    @Column
    @Comment("用户姓名")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "用户姓名")
    private String username;

    @Column
    @Comment("日志类型")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "日志类型")
    private String type;

    @Column
    @Comment("日志标签")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "日志内容")
    private String tag;

    @Column
    @Comment("日志内容")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "日志内容")
    private String msg;

    @Column
    @Comment("执行方法")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "执行方法")
    private String method;

    @Column
    @Comment("请求路径")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "请求路径")
    private String url;

    @Column
    @Comment("IP地址")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "IP地址")
    private String ip;

    @Column
    @Comment("请求参数")
    @ColDefine(type = ColType.TEXT)
    @ApiModelProperty(description = "请求参数")
    private String params;

    @Column
    @Comment("执行结果")
    @ColDefine(type = ColType.TEXT)
    @ApiModelProperty(description = "执行结果")
    private String result;

    @Column
    @Comment("浏览器")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "浏览器")
    private String browser;

    @Column
    @Comment("操作系统")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "操作系统")
    private String os;

    @Column
    @Comment("异常信息")
    @ColDefine(type = ColType.TEXT)
    @ApiModelProperty(description = "异常信息")
    private String exception;

    @Column
    @Comment("执行耗时")
    @ApiModelProperty(description = "执行耗时")
    private Long executeTime;

    @Column
    @Comment("创建人ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "创建人ID")
    private String createdBy;

    @Column
    @Comment("创建时间")
    @ApiModelProperty(description = "创建时间")
    private Long createdAt;

    @Column
    @Comment("修改人ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "修改人ID")
    private String updatedBy;

    @Column
    @Comment("修改时间")
    @ApiModelProperty(description = "修改时间")
    private Long updatedAt;

    @Column
    @Comment("删除标记")
    @ColDefine(type = ColType.BOOLEAN)
    @ApiModelProperty(description = "删除标记")
    private Boolean delFlag;

}
