package com.budwk.app.cms.models;

import com.budwk.starter.common.enums.Validation;
import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import com.budwk.starter.database.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * @author wizzer(wizzer@qq.com) on 2016/7/18.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("cms_site")
@TableMeta("{'mysql-charset':'utf8mb4'}")
@ApiModel(description = "CMS站点信息")
public class Cms_site extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column
    @Name
    @Comment("ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "ID",required = true,check = true,validation = Validation.UPPER)
    private String id;

    @Column
    @Comment("预留商城ID")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    @ApiModelProperty(description = "预留商城ID")
    private String shopId;

    @Column
    @Comment("名称")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    @ApiModelProperty(description = "名称")
    private String site_name;

    @Column
    @Comment("域名")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    @ApiModelProperty(description = "域名")
    private String site_domain;

    @Column
    @Comment("ICP")
    @ColDefine(type = ColType.VARCHAR, width = 120)
    @ApiModelProperty(description = "ICP")
    private String site_icp;

    @Column
    @Comment("LOGO")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "LOGO")
    private String site_logo;

    @Column
    @Comment("WAPLOGO")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "WAPLOGO")
    private String site_wap_logo;

    @Column
    @Comment("客服QQ")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    @ApiModelProperty(description = "客服QQ")
    private String site_qq;

    @Column
    @Comment("邮箱")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "邮箱")
    private String site_email;

    @Column
    @Comment("电话")
    @ColDefine(type = ColType.VARCHAR, width = 20)
    @ApiModelProperty(description = "电话")
    private String site_tel;

    @Column
    @Comment("微博")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(description = "微博")
    private String weibo_name;

    @Column
    @Comment("微博地址")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "微博地址")
    private String weibo_url;

    @Column
    @Comment("微博二维码")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "微博二维码")
    private String weibo_qrcode;

    @Column
    @Comment("微信名称")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(description = "微信名称")
    private String wechat_name;

    @Column
    @Comment("微信ID")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(description = "微信ID")
    private String wechat_id;

    @Column
    @Comment("微信二维码")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "微信二维码")
    private String wechat_qrcode;

    @Column
    @Comment("关键词")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "关键词")
    private String seo_keywords;


    @Column
    @Comment("描述")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @ApiModelProperty(description = "描述")
    private String seo_description;

    @Column
    @Comment("底部版权")
    @ColDefine(type = ColType.TEXT)
    @ApiModelProperty(description = "底部版权")
    private String footer_content;

}
