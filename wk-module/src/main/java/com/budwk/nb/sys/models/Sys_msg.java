package com.budwk.nb.sys.models;

import com.budwk.nb.commons.base.model.BaseModel;
import com.budwk.nb.sys.enums.SysMsgTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;
import java.util.List;

/**
 * @author wizzer(wizzer@qq.com) on 2018/6/29.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_msg")
public class Sys_msg extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("消息类型")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private SysMsgTypeEnum type;

    @Column
    @Comment("消息标题")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String title;

    @Column
    @Comment("消息内容")
    @ColDefine(type = ColType.TEXT)
    private String note;

    @Column
    @Comment("消息URL")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String url;

    @Column
    @Comment("发送时间")
    private Long sendAt;

    @Many(field = "msgId")
    private List<Sys_msg_user> userList;

}
