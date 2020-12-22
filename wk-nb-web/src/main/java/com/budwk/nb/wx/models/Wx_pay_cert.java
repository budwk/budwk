package com.budwk.nb.wx.models;

import com.budwk.nb.base.model.BaseModel;
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
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    private String id;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String mchid;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String serial_no;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String effective_time;

    @Column
    private Long effective_at;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String expire_time;

    @Column
    private Long expire_at;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String algorithm;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String nonce;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String associated_data;

    @Column
    @ColDefine(type = ColType.TEXT)
    private String ciphertext;

    @Column
    @ColDefine(type = ColType.TEXT)
    private String certificate;
}
