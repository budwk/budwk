package com.budwk.nb.cms.models;

import com.budwk.nb.base.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * @author wizzer(wizzer.cn) on 2016/7/18.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("cms_channel")
@TableIndexes({@Index(name = "INDEX_CHANNEL", fields = {"code"}, unique = true)})
public class Cms_channel extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("站点ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String siteid;

    @Column
    @Comment("预留商城ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String shopid;

    @Column
    @Comment("父级ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String parentId;

    @Column
    @Comment("树路径")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String path;

    @Column
    @Comment("栏目名称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String name;

    @Column
    @Comment("栏目标识")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String code;

    @Column
    @Comment("栏目类型")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String type;

    @Column
    @Comment("链接地址")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String url;

    @Column
    @Comment("打开方式")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String target;

    @Column
    @Comment("是否显示")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean showit;

    @Column
    @Comment("是否禁用")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean disabled;

    @Column
    @Comment("排序字段")
    @Prev({
            @SQL(db = DB.MYSQL, value = "SELECT IFNULL(MAX(location),0)+1 FROM cms_channel"),
            @SQL(db = DB.ORACLE, value = "SELECT COALESCE(MAX(location),0)+1 FROM cms_channel")
    })
    private Integer location;

    @Column
    @Comment("有子节点")
    private boolean hasChildren;

}
