package com.budwk.app.device.storage.objects.dto;

import lombok.Data;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Id;

import java.io.Serializable;

/**
 * 设备上报数据基本信息
 * customType = "TAG" 时序数据库的TAG字段,可以为所属公司等固定数据
 * @author wizzer.cn
 */
@Data
public class DeviceDataDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @ColDefine(type = ColType.TIMESTAMP)
    @Comment("时间主键")
    private Long ts;

    @ColDefine(type = ColType.TIMESTAMP)
    @Comment("接收数据时间")
    private Long receive_time;

    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String product_id;

    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String device_no;

    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String device_id;

    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String device_code;

    @ColDefine(type = ColType.VARCHAR, width = 32, customType = "TAG")
    private String ext1;

    @ColDefine(type = ColType.VARCHAR, width = 32, customType = "TAG")
    private String ext2;

    @ColDefine(type = ColType.VARCHAR, width = 32, customType = "TAG")
    private String ext3;
}
