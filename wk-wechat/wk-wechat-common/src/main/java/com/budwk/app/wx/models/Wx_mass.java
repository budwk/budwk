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
 * @author wizzer(wizzer.cn) on 2016/7/1.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("wx_mass")
@ApiModel(description = "微信配置表")
public class Wx_mass extends BaseModel implements Serializable {
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
    @Comment("群发名称")
    @ApiModelProperty(description = "群发名称")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String name;

    /**
     * text image news
     */
    @Column
    @Comment("群发类型")
    @ApiModelProperty(description = "群发类型")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String type;

    @Column
    @Comment("媒体文件ID")
    @ApiModelProperty(description = "媒体文件ID")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String media_id;

    /**
     * type=image 时有效
     */
    @Column
    @Comment("图片地址")
    @ApiModelProperty(description = "图片地址")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String picurl;

    @Column
    @Comment("Scope")
    @ApiModelProperty(description = "范围")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String scope;

    @Column
    @Comment("Content")
    @ApiModelProperty(description = "内容")
    @ColDefine(type = ColType.TEXT)
    private String content;

    @Column
    @Comment("发送状态")
    @ApiModelProperty(description = "发送状态")
    @ColDefine(type = ColType.INT)
    protected Integer status;

    @Column
    @Comment("微信ID")
    @ApiModelProperty(description = "微信ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String wxid;

    @One(field = "wxid")
    private Wx_config wxConfig;

    @One(field = "id",key = "massId")
    private Wx_mass_send massSend;

}
