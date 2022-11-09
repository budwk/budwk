package com.budwk.app.sys.models;

import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
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
@Table("sys_user_security")
@TableMeta("{'mysql-charset':'utf8mb4'}")
@ApiModel(description = "账户安全配置表")
public class Sys_user_security extends BaseModel implements Serializable {
    private static final long serialVersionUID = -3044639976455791237L;
    @Column
    @Name
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String id;

    @Column
    @Comment("是否启用")
    @ApiModelProperty(description = "是否启用")
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean hasEnabled;

    @Column
    @Comment("密码最小长度")
    @ApiModelProperty(description = "密码最小长度")
    @ColDefine(type = ColType.INT)
    private Integer pwdLengthMin;

    @Column
    @Comment("密码最大长度")
    @ApiModelProperty(description = "密码最大长度")
    @ColDefine(type = ColType.INT)
    private Integer pwdLengthMax;

    @Column
    @Comment("密码字符要求")
    @ApiModelProperty(description = "密码字符要求(0-无要求,1-字母和数字,2-大小写字母和数字,3-大小写字母、特殊字符和数字)")
    @ColDefine(type = ColType.INT, width = 1)
    private Integer pwdCharMust;

    @Column
    @Comment("不能包含的用户信息")
    @ApiModelProperty(description = "密码不能包含的用户信息(loginname/username/email/mobile)")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String pwdCharNot;

    @Column
    @Comment("密码重复性检查")
    @ApiModelProperty(description = "密码重复性检查")
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean pwdRepeatCheck;

    @Column
    @Comment("重复性检查记录数")
    @ApiModelProperty(description = "重复性检查记录数")
    @ColDefine(type = ColType.INT)
    private Integer pwdRepeatNum;

    @Column
    @Comment("超重试次数锁定账号")
    @ApiModelProperty(description = "超重试次数锁定账号")
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean pwdRetryLock;

    @Column
    @Comment("密码错误最大重试次数")
    @ApiModelProperty(description = "密码错误最大重试次数")
    @ColDefine(type = ColType.INT)
    private Integer pwdRetryNum;

    @Column
    @Comment("超重试次数处理方式")
    @ApiModelProperty(description = "超重试次数处理方式(0-不处理,1=锁定账号,2=指定时间内禁止登录)")
    @ColDefine(type = ColType.INT)
    private Integer pwdRetryAction;

    @Column
    @Comment("密码错误禁止登录时长")
    @ApiModelProperty(description = "密码错误禁止登录时长(单位:秒)")
    @ColDefine(type = ColType.INT)
    private Integer pwdRetryTime;

    @Column
    @Comment("指定密码过期时间")
    @ApiModelProperty(description = "指定密码过期时间(单位:天)")
    @ColDefine(type = ColType.INT)
    private Integer pwdTimeoutDay;

    @Column
    @Comment("重置密码后需修改密码")
    @ApiModelProperty(description = "后台重置密码后下次登录是否强制修改密码")
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean pwdResetChange;

    @Column
    @Comment("用户名/手机号输错锁定")
    @ApiModelProperty(description = "用户名/手机号输错锁定")
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean nameRetryLock;

    @Column
    @Comment("用户名/手机号输错次数")
    @ApiModelProperty(description = "用户名/手机号输错次数")
    @ColDefine(type = ColType.INT)
    private Integer nameRetryNum;

    @Column
    @Comment("输错IP锁定时间(s)")
    @ApiModelProperty(description = "用户名/手机号输错IP锁定时间(s)")
    @ColDefine(type = ColType.INT)
    private Integer nameTimeout;

    @Column
    @Comment("用户单一登录")
    @ApiModelProperty(description = "是否启用用户单一登录")
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean userSessionOnlyOne;

    @Column
    @Comment("是否启用登录验证码")
    @ApiModelProperty(description = "是否启用登录验证码")
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean captchaHasEnabled;

    @Column
    @Comment("验证码类型")
    @ApiModelProperty(description = "验证码类型(0-数学题,1-四位数字,2-四位字母)")
    @ColDefine(type = ColType.INT, width = 1)
    private Integer captchaType;
}
