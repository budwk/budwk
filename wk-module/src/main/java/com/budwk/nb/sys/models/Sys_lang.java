package com.budwk.nb.sys.models;

import com.budwk.nb.commons.base.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * 多语言字符串
 * @author wizzer(wizzer@qq.com) on 2019/10/29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_lang")
@TableIndexes({@Index(name = "INDEX_SYS_LANG_LOCALE", fields = {"locale"}, unique = false)})
public class Sys_lang extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("语言标识")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String locale;

    @Column
    @Comment("KEY")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String lang_key;

    @Column
    @Comment("VALUE")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String lang_value;

}
