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
 * @author wizzer@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("wx_pay")
@ApiModel(description = "微信支付配置表")
public class Wx_pay extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("商户名称")
    @ApiModelProperty(description = "商户名称")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String name;

    @Column
    @Comment("商户mchid")
    @ApiModelProperty(description = "商户mchid")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String mchid;

    @Column
    @Comment("V2密钥")
    @ApiModelProperty(description = "V2密钥")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String v2key;

    @Column
    @Comment("V2证书路径")
    @ApiModelProperty(description = "V2证书路径")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String v2certPath;

    @Column
    @Comment("V3密钥")
    @ApiModelProperty(description = "V3密钥")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String v3key;

    @Column
    @Comment("V3密钥路径")
    @ApiModelProperty(description = "V3密钥路径")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String v3keyPath;

    @Column
    @Comment("V3证书路径")
    @ApiModelProperty(description = "V3证书路径")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String v3certPath;

    @Column
    @Comment("V3证书P12路径")
    @ApiModelProperty(description = "V3证书P12路径")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String v3certP12Path;

    /**
     * 平台证书失效时间
     */
    @Column
    @Comment("平台证书失效时间")
    @ApiModelProperty(description = "平台证书失效时间")
    private Long expire_at;
}
