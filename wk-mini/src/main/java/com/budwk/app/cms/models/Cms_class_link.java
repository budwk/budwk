package com.budwk.app.cms.models;

import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * @author wizzer@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("cms_class_link")
@TableMeta("{'mysql-charset':'utf8mb4'}")
public class Cms_class_link extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column
    @Id
    private long id;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String classId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String linkId;
}
