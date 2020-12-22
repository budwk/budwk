package com.budwk.nb.cms.models;

import com.budwk.nb.base.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;
import java.util.List;

/**
 * @author wizzer(wizzer.cn) on 2016/7/18.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("cms_link_class")
@TableIndexes({@Index(name = "INDEX_CMS_LINK_CLASS", fields = {"code"}, unique = true)})
public class Cms_link_class extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("分类名称")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String name;

    @Column
    @Comment("分类编码")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String code;

    @ManyMany(from = "classId", relation = "cms_class_link", to = "linkId")
    private List<Cms_link> links;

}
