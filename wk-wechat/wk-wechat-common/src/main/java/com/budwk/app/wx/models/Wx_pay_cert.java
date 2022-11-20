package com.budwk.app.wx.models;

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
@Table("wx_pay_cert")
@TableIndexes({@Index(name = "INDEX_WX_PAY_CERT", fields = {"mchid", "serial_no"}, unique = true)})
public class Wx_pay_cert extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("商户号mchid")
    @ApiModelProperty(description = "商户号mchid")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String mchid;

    @Column
    @Comment("证书序号")
    @ApiModelProperty(description = "证书序号")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String serial_no;

    @Column
    @Comment("证书有效时间")
    @ApiModelProperty(description = "证书有效时间")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String effective_time;

    @Column
    @Comment("证书有效时间")
    @ApiModelProperty(description = "证书有效时间")
    private Long effective_at;

    @Column
    @Comment("证书失效时间")
    @ApiModelProperty(description = "证书失效时间")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String expire_time;

    @Column
    @Comment("证书失效时间")
    @ApiModelProperty(description = "证书失效时间")
    private Long expire_at;

    @Column
    @Comment("algorithm")
    @ApiModelProperty(description = "algorithm")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String algorithm;

    @Column
    @Comment("nonce")
    @ApiModelProperty(description = "nonce")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String nonce;

    @Column
    @Comment("associated_data")
    @ApiModelProperty(description = "associated_data")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String associated_data;

    @Column
    @Comment("ciphertext")
    @ApiModelProperty(description = "ciphertext")
    @ColDefine(type = ColType.TEXT)
    private String ciphertext;

    @Column
    @Comment("certificate")
    @ApiModelProperty(description = "certificate")
    @ColDefine(type = ColType.TEXT)
    private String certificate;
}
