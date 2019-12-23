package com.budwk.nb.sys.models;

import com.budwk.nb.commons.base.model.BaseModel;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;
import org.nutz.integration.json4excel.annotation.J4EIgnore;
import org.nutz.integration.json4excel.annotation.J4EName;

import java.io.Serializable;
import java.util.List;

/**
 * @author wizzer(wizzer@qq.com) on 2016/6/21.
 */
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isUserOnline() {
        return userOnline;
    }

    public void setUserOnline(boolean userOnline) {
        this.userOnline = userOnline;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getLoginAt() {
        return loginAt;
    }

    public void setLoginAt(Long loginAt) {
        this.loginAt = loginAt;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public String getCustomMenu() {
        return customMenu;
    }

    public void setCustomMenu(String customMenu) {
        this.customMenu = customMenu;
    }

    public String getThemeConfig() {
        return themeConfig;
    }

    public void setThemeConfig(String themeConfig) {
        this.themeConfig = themeConfig;
    }

    public String getUnitid() {
        return unitid;
    }

    public void setUnitid(String unitid) {
        this.unitid = unitid;
    }

    public Sys_unit getUnit() {
        return unit;
    }

    public void setUnit(Sys_unit unit) {
        this.unit = unit;
    }

    public List<Sys_role> getRoles() {
        return roles;
    }

    public void setRoles(List<Sys_role> roles) {
        this.roles = roles;
    }

    public List<Sys_unit> getUnits() {
        return units;
    }

    public void setUnits(List<Sys_unit> units) {
        this.units = units;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Sys_user getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(Sys_user createdByUser) {
        this.createdByUser = createdByUser;
    }

    public Sys_user getUpdatedByUser() {
        return updatedByUser;
    }

    public void setUpdatedByUser(Sys_user updatedByUser) {
        this.updatedByUser = updatedByUser;
    }
}
