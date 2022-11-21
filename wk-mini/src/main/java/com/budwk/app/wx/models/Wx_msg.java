package com.budwk.app.wx.models;

import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * @author wizzer(wizzer.cn) on 2016/7/2.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("wx_msg")
@ApiModel(description = "微信消息表")
public class Wx_msg extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("单位id")
    @ApiModelProperty(description = "单位id")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String sys_unit_id;

    @Column
    @Comment("openid")
    @ApiModelProperty(description = "openid")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String openid;

    @Column
    @Comment("微信昵称")
    @ApiModelProperty(description = "微信昵称")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String nickname;

    @Column
    @Comment("信息类型")
    @ApiModelProperty(description = "信息类型")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String type;

    @Column
    @Comment("信息内容")
    @ApiModelProperty(description = "信息内容")
    @ColDefine(type = ColType.TEXT)
    private String content;

    @Column
    @Comment("回复ID")
    @ApiModelProperty(description = "回复ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String replyId;

    @Column
    @Comment("微信ID")
    @ApiModelProperty(description = "微信ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String wxid;

    @One(field = "replyId")
    private Wx_msg_reply reply;

    @One(field = "wxid")
    private Wx_config wxConfig;

}
