package com.budwk.nb.sys.models;

import com.budwk.nb.base.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * @author wizzer@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_user_unit")
public class Sys_user_unit extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Id
    private long id;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String userId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String unitId;
}
