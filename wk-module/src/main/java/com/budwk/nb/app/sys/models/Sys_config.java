package com.budwk.nb.app.sys.models;

import com.budwk.nb.common.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * Created by wizzer on 2016/6/21.
 */
@Table("sys_config")
public class Sys_config extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Name
    @Column
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String configKey;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String configValue;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String note;

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
