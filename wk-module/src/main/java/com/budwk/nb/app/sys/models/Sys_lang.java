package com.budwk.nb.app.sys.models;

import com.budwk.nb.common.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * 多语言字符串
 * Created by wizzer on 2019/10/29
 */
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLang_key() {
        return lang_key;
    }

    public void setLang_key(String lang_key) {
        this.lang_key = lang_key;
    }

    public String getLang_value() {
        return lang_value;
    }

    public void setLang_value(String lang_value) {
        this.lang_value = lang_value;
    }
}
