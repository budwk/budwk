package com.budwk.app.wx.models;

import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * @author wizzer(wizzer.cn) on 2016/7/1.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("wx_config")
@ApiModel(description = "微信配置表")
public class Wx_config extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String id;

    @Column
    @Comment("公众号名称")
    @ApiModelProperty(description = "公众号名称")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String appname;

    @Column
    @Comment("原始ID")
    @ApiModelProperty(description = "原始ID")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String ghid;

    @Column
    @Comment("Appid")
    @ApiModelProperty(description = "Appid")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String appid;

    @Column
    @Comment("Appsecret")
    @ApiModelProperty(description = "Appsecret")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String appsecret;

    @Column
    @Comment("单位id")
    @ApiModelProperty(description = "单位id")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String sys_unit_id;

    @Column
    @Comment("EncodingAESKey")
    @ApiModelProperty(description = "EncodingAESKey")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String encodingAESKey;

    @Column
    @Comment("Token")
    @ApiModelProperty(description = "Token")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String token;

    @Column
    @Comment("access_token")
    @ApiModelProperty(description = "access_token")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String access_token;

    @Column
    @Comment("access_token_expires")
    @ApiModelProperty(description = "access_token_expires")
    @ColDefine(type = ColType.INT)
    private Integer access_token_expires;

    @Column
    @Comment("access_token_lastat")
    @ApiModelProperty(description = "access_token_lastat")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String access_token_lastat;

    @Column
    @Comment("微信支付商户号")
    @ApiModelProperty(description = "微信支付商户号")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String mchid;

}
