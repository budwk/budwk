package com.budwk.app.wx.models;

import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.DB;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * @author wizzer(wizzer.cn) on 2016/7/2.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("wx_menu")
@ApiModel(description = "微信菜单表")
public class Wx_menu extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ApiModelProperty(description = "ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @PrevInsert(els = {@EL("uuid()")})
    private String id;

    @Column
    @Comment("单位id")
    @ApiModelProperty(description = "单位id")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String sys_unit_id;

    public String getSys_unit_id() {
        return sys_unit_id;
    }

    public void setSys_unit_id(String sys_unit_id) {
        this.sys_unit_id = sys_unit_id;
    }

    @Column
    @Comment("父ID")
    @ApiModelProperty(description = "父ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String parentId;

    @Column
    @Comment("树路径")
    @ApiModelProperty(description = "树路径")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String path;

    @Column
    @Comment("菜单名称")
    @ApiModelProperty(description = "菜单名称")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String menuName;

    @Column
    @Comment("菜单类型")
    @ApiModelProperty(description = "菜单类型")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String menuType;

    @Column
    @Comment("关键词")
    @ApiModelProperty(description = "关键词")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    private String menuKey;

    @Column
    @Comment("网址")
    @ApiModelProperty(description = "网址")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String url;

    @Column
    @Comment("小程序appid")
    @ApiModelProperty(description = "小程序appid")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String appid;

    @Column
    @Comment("小程序入口页")
    @ApiModelProperty(description = "小程序入口页")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String pagepath;

    @Column
    @Comment("排序字段")
    @ApiModelProperty(description = "排序字段")
    @Prev({
            @SQL(db= DB.MYSQL,value = "SELECT IFNULL(MAX(location),0)+1 FROM wx_menu"),
            @SQL(db= DB.ORACLE,value = "SELECT COALESCE(MAX(location),0)+1 FROM wx_menu")
    })
    private Integer location;

    @Column
    @Comment("有子节点")
    @ApiModelProperty(description = "有子节点")
    private boolean hasChildren;

    @Column
    @Comment("微信ID")
    @ApiModelProperty(description = "微信ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String wxid;

    @One(field = "wxid")
    private Wx_config wxConfig;

}
