package com.budwk.starter.tdengine.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wizzer.cn
 */
@Data
public class TableInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    //字段名称
    private String field;
    //字段类型
    private String type;
    //字段长度
    private Integer length;
    //字段说明
    private String note;
}
