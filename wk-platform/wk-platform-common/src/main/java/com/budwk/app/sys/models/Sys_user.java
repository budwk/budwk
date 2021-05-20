package com.budwk.app.sys.models;

import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;
import java.util.List;

/**
 * 系统用户表
 *
 * @author wizzer@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("sys_user")
@ApiModel(description = "系统用户表")
@TableIndexes({@Index(name = "INDEX_SYS_USER_LOGINNAMAE", fields = {"loginname"}, unique = true)})
public class Sys_user extends BaseModel implements Serializable {
    private static final long serialVersionUID = -4700089133783566804L;
    @Column
    @Name
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(uu32 = true)
    private String id;

    @Column
    @Comment("租户ID")
    @ApiModelProperty(description = "租户ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String tenantId;

    @Column
    @Comment("员工编号")
    @ApiModelProperty(description = "员工编号", required = true, check = true)
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String serialNo;

    @Column
    @Comment("用户名")
    @ApiModelProperty(description = "用户名", required = true, check = true)
    @ColDefine(type = ColType.VARCHAR, width = 120)
    private String loginname;

    /**
     * transient 修饰符可让此字段不在对象里显示
     */
    @Column
    @Comment("密码")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @ApiModelProperty(description = "密码")
    private String password;

    @Column
    @Comment("密码盐")
    @ApiModelProperty(description = "密码盐")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    private String salt;

    @Column
    @Comment("姓名昵称")
    @ApiModelProperty(description = "姓名昵称", required = true, check = true)
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String username;

    @Column
    @Comment("头像")
    @ApiModelProperty(description = "头像")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String avatar;

    @Column
    @Comment("是否禁用")
    @ApiModelProperty(description = "是否禁用")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean disabled;

    @Column
    @Comment("电子邮箱")
    @ApiModelProperty(description = "电子邮箱")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String email;

    @Column
    @Comment("手机号码")
    @ApiModelProperty(description = "手机号码")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String mobile;

    @Column
    @Comment("登陆时间")
    @ApiModelProperty(description = "登陆时间")
    private Long loginAt;

    @Column
    @Comment("登陆IP")
    @ApiModelProperty(description = "登陆IP")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String loginIp;

    @Column
    @Comment("登陆次数")
    @ApiModelProperty(description = "登陆次数")
    private Integer loginCount;

    @Column
    @Comment("需要修改密码")
    @ApiModelProperty(description = "需要修改密码")
    @ColDefine(type = ColType.BOOLEAN)
    private boolean needChangePwd;

    @Column
    @Comment("布局样式")
    @ApiModelProperty(description = "布局样式")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String themeConfig;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "公司ID")
    private String companyId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "单位ID")
    private String unitId;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @ApiModelProperty(description = "单位PATH")
    private String unitPath;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "职务ID")
    private String postId;

    @One(field = "postId")
    @ApiModelProperty(description = "职务对象")
    private Sys_post post;

    @One(field = "unitId")
    @ApiModelProperty(description = "单位对象")
    private Sys_unit unit;

    @One(field = "createdBy")
    @ApiModelProperty(description = "用户对象")
    public Sys_user createdByUser;

    @One(field = "updatedBy")
    @ApiModelProperty(description = "用户对象")
    public Sys_user updatedByUser;

    @ManyMany(from = "userId", relation = "sys_role_user", to = "roleId")
    @ApiModelProperty(description = "角色列表")
    private List<Sys_role> roles;

}
