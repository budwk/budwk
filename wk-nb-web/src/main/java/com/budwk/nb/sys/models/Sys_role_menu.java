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
@Table("sys_role_menu")
public class Sys_role_menu extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Id
    private long id;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String roleId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String menuId;
}
