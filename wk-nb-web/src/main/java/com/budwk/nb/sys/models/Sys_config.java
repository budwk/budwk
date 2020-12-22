package com.budwk.nb.sys.models;

import com.budwk.nb.base.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * @author wizzer(wizzer.cn) on 2016/6/21.
 */
@Data
@EqualsAndHashCode(callSuper = true)
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

}
