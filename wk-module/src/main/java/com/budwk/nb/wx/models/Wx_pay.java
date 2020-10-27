package com.budwk.nb.wx.models;

import com.budwk.nb.commons.base.model.BaseModel;
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
public class Wx_pay extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    private String id;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String name;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String mchid;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String v3key;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String v3keyPath;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String v3certPath;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String v3certP12Path;

    /**
     * 平台证书publicKey
     */
    @Column
    @ColDefine(type = ColType.TEXT)
    private String platformCertificate;

    /**
     * 平台证书失效时间
     */
    @Column
    private Long expire_at;
}
