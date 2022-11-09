package com.budwk.starter.web.validation;

import org.nutz.lang.util.NutMap;

/**
 * @author wizzer@qq.com
 */
public class Errors {
    /**
     * 错误内容
     */
    private NutMap errorMap = NutMap.NEW();

    /**
     * 是否有错误
     *
     * @return
     */
    public boolean hasError() {
        return errorMap.size() > 0;
    }

    /**
     * 错误总数
     *
     * @return
     */
    public int errorCount() {
        return errorMap.size();
    }

    /**
     * 条件错误信息
     *
     * @param fieldName    字段名
     * @param fieldDesc    字段说明
     * @param errorMessage 错误提示
     */
    public void add(String fieldName, String fieldDesc, String errorMessage) {
        errorMap.put(fieldName, NutMap.NEW().addv("name", fieldDesc).addv("msg", errorMessage));
    }

    /**
     * 获取所有错误
     *
     * @return
     */
    public NutMap getErrorMap() {
        return errorMap;
    }
}
