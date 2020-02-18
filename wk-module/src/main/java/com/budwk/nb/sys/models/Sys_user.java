package com.budwk.nb.sys.models;

import com.budwk.nb.commons.base.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;
import org.nutz.integration.json4excel.annotation.J4EIgnore;
import org.nutz.integration.json4excel.annotation.J4EName;
import org.nutz.plugins.validation.annotation.Validations;

import java.io.Serializable;
import java.util.List;

/**
 * @author wizzer(wizzer@qq.com) on 2016/6/21.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_user")
@J4EName("用户数据")
@TableIndexes({@Index(name = "INDEX_SYS_USER_LOGINNAMAE", fields = {"loginname"}, unique = true)})
public class Sys_user extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @J4EIgnore
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(uu32 = true)
    private String id;

    @Column
    @Comment("用户名")
    @J4EName("用户名")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    @Validations(required=true,errorMsg = "sys.manage.user.form.loginname.required")
    private String loginname;

    /**
     *  transient 修饰符可让此字段不在对象里显示
     */
    @Column
    @Comment("密码")
    @J4EIgnore
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String password;

    @Column
    @Comment("密码盐")
    @J4EIgnore
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String salt;

    @Column
    @Comment("姓名")
    @J4EName("姓名")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String username;

    @Column
    @Comment("头像")
    @J4EIgnore
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String avatar;

    /**
     * 通过session计算获得
     */
    @Comment("是否在线")
    @J4EIgnore
    private boolean userOnline;

    @Column
    @Comment("是否禁用")
    @J4EIgnore
    @ColDefine(type = ColType.BOOLEAN)
    private boolean disabled;

    @Column
    @Comment("电子邮箱")
    @J4EName("电子邮箱")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String email;

    @Column
    @Comment("手机号码")
    @J4EName("手机号码")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String mobile;

    @Column
    @Comment("登陆时间")
    @J4EIgnore
    private Long loginAt;

    @Column
    @Comment("登陆IP")
    @J4EIgnore
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String loginIp;

    @Column
    @Comment("登陆次数")
    @J4EIgnore
    @ColDefine(type = ColType.INT)
    private Integer loginCount;

    @Column
    @Comment("常用菜单")
    @J4EIgnore
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String customMenu;

    @Column
    @Comment("布局样式")
    @J4EIgnore
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String themeConfig;

    @Column
    @J4EIgnore
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String unitid;

    @One(field = "unitid")
    @J4EIgnore
    private Sys_unit unit;

    @One(field = "createdBy")
    @J4EIgnore
    public Sys_user createdByUser;

    @One(field = "updatedBy")
    @J4EIgnore
    public Sys_user updatedByUser;

    @ManyMany(from = "userId", relation = "sys_user_role", to = "roleId")
    @J4EIgnore
    private List<Sys_role> roles;

    @ManyMany(from = "userId", relation = "sys_user_unit", to = "unitId")
    @J4EIgnore
    protected List<Sys_unit> units;

}
