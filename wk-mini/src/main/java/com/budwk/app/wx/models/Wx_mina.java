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
 * @author wizzer(wizzer.cn)
 * @date 2020/4/15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("wx_mina")
@ApiModel(description = "微信小程序表")
public class Wx_mina extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("小程序名称")
    @ApiModelProperty(description = "小程序名称")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String appname;

    @Column
    @Comment("小程序APPID")
    @ApiModelProperty(description = "小程序APPID")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String appid;

    @Column
    @Comment("小程序密钥")
    @ApiModelProperty(description = "小程序密钥")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String appsecret;

    @Column
    @Comment("微信ID")
    @ApiModelProperty(description = "微信ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String wxid;

    @One(field = "wxid")
    private Wx_config wxConfig;

    @Column
    @Comment("微信支付商户号")
    @ApiModelProperty(description = "微信支付商户号")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String mchid;
}
