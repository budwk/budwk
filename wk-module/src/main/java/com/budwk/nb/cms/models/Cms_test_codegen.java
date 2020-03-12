package com.budwk.nb.cms.models;

import com.budwk.nb.commons.base.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * 代码生成的测试类
 *
 * @author wizzer(wizzer.cn)
 * @date 2020/3/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("cms_test_codegen")
public class Cms_test_codegen extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("标题")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String title;

    @Column
    @Comment("内容")
    @ColDefine(type = ColType.TEXT)
    private String content;
}
