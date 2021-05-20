package com.budwk.starter.database.model;

import lombok.Data;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;
import org.nutz.dao.interceptor.annotation.PrevUpdate;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

import java.io.Serializable;

/**
 * @author wizzer@qq.com
 */
@Data
public abstract class BaseModel implements Serializable {
    private static final long serialVersionUID = -762325615371339568L;

    @Column
    @Comment("创建人")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String createdBy;

    @Column
    @Comment("创建时间")
    @PrevInsert(now = true)
    private Long createdAt;

    @Column
    @Comment("修改人")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String updatedBy;

    @Column
    @Comment("修改时间")
    @PrevInsert(now = true)
    @PrevUpdate(now = true)
    private Long updatedAt;

    @Column
    @Comment("删除标记")
    @PrevInsert(els = @EL("$me.flag()"))
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean delFlag;

    public String toJsonString() {
        return Json.toJson(this, JsonFormat.compact());
    }

    public Boolean flag() {
        return false;
    }

}
