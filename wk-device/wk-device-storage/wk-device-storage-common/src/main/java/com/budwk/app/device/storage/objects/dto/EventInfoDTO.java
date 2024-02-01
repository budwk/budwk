package com.budwk.app.device.storage.objects.dto;

import lombok.Data;

import java.util.List;


/**
 * 事件记录
 *
 */
@Data
public class EventInfoDTO {


    /**
     * 事件发生时间
     */
    private Long eventTime;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 事件内容
     */
    private String content;
    /**
     * 异常事件的异常类型
     */
    private String alarmType;
    private String alarmTypeName;

    /**
     * 事件的数据
     */
    private List<ValueItemDTO<?>> attributes;
}
