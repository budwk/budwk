package com.budwk.app.sys.models;

import com.budwk.app.sys.enums.SysMsgScope;
import com.budwk.app.sys.enums.SysMsgType;
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
 * 站内消息表
 *
 * @author wizzer@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_msg")
@TableMeta("{'mysql-charset':'utf8mb4'}")
@ApiModel( description = "站内消息表")
public class Sys_msg extends BaseModel implements Serializable {
    private static final long serialVersionUID = 6416003551694659705L;
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
    @Comment("消息类型")
    @ApiModelProperty(description = "消息类型",example = "SYSTEM-系统消息/USER-用户消息")
    @ColDefine(type = ColType.VARCHAR, width = 10)
    private SysMsgType type;

    @Column
    @Comment("发送范围")
    @ApiModelProperty(description = "发送范围",example = "ALL-全部用户/SCOPE-指定用户")
    @ColDefine(type = ColType.VARCHAR, width = 10)
    private SysMsgScope scope;

    @Column
    @Comment("消息标题")
    @ApiModelProperty(description = "消息标题")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String title;

    @Column
    @Comment("消息内容(500字节以内)")
    @ApiModelProperty(description = "消息内容(500字节以内)")
    @ColDefine(type = ColType.VARCHAR,width = 500)
    private String note;

    @Column
    @Comment("跳转链接")
    @ApiModelProperty(description = "跳转链接")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String url;

    @Column
    @Comment("发送时间")
    @ApiModelProperty(description = "发送时间")
    private Long sendAt;

    @Many(field = "msgId")
    @ApiModelProperty(description = "用户列表")
    private List<Sys_msg_user> userList;

    @One(field = "createdBy")
    @ApiModelProperty(description = "创建人")
    public Sys_user createdByUser;
}
