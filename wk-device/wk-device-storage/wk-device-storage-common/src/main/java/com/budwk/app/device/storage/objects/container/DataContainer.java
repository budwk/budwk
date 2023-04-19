package com.budwk.app.device.storage.objects.container;

import com.budwk.app.device.storage.enums.DataType;
import java.io.Serializable;
/**
 * @author wizzer.cn
 */

public class DataContainer<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    //字段名
    private String field;

    //字段值
    private T value;

    //数据类型
    private DataType dataType;
}
