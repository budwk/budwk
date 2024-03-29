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
@Table("wx_user")
@ApiModel(description = "微信用户表")
@TableIndexes({@Index(name = "INDEX_WX_USER_OPENID", fields = {"openid"}, unique = true)})
public class Wx_user extends BaseModel implements Serializable {
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
    @Comment("unionid")
    @ApiModelProperty(description = "unionid")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String unionid;

    @Column
    @Comment("微信昵称")
    @ApiModelProperty(description = "微信昵称")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String nickname;

    @Column
    @Comment("是否关注")
    @ApiModelProperty(description = "是否关注")
    private boolean subscribe;

    @Column
    @Comment("关注时间")
    @ApiModelProperty(description = "关注时间")
    private Long subscribeAt;

    @Column
    @Comment("性别")
    @ApiModelProperty(description = "性别")
    @ColDefine(type = ColType.INT)
    private Integer sex;

    @Column
    @Comment("国家")
    @ApiModelProperty(description = "国家")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String country;

    @Column
    @Comment("省份")
    @ApiModelProperty(description = "省份")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String province;

    @Column
    @Comment("城市")
    @ApiModelProperty(description = "城市")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String city;

    @Column
    @Comment("头像")
    @ApiModelProperty(description = "头像")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String headimgurl;

    @Column
    @Comment("微信ID")
    @ApiModelProperty(description = "微信ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String wxid;

    @One(field = "wxid")
    private Wx_config wxConfig;

}
