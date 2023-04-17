package com.budwk.app.device.objects.rule;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wizzer.cn
 */
@Data
public class RuleTrigger implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 触发类型
     */
    private TriggerType triggerType = TriggerType.DEVICE;
    /**
     * 触发方式
     */
    private TriggerMethod triggerMethod;

    /**
     * 条件
     */
    private List<RuleCondition> conditionList;

    /**
     * 生成的 SQL 语句
     */
    private String ql;

    /**
     * SQL 语句的参数列表
     */
    private String qlArgs;

    /**
     * 触发类型
     */
    public enum TriggerType {
        /**
         * 设备触发
         */
        DEVICE("设备触发"),
        /**
         * 定时执行
         */
        TIMER("定时执行");

        private String text;

        TriggerType(String text) {
            this.text = text;
        }

        public static Map<String, String> toMap() {
            Map<String, String> map = new HashMap<>();
            Arrays.stream(TriggerType.values()).forEach(v -> map.put(v.name(), v.text()));
            return map;
        }

        public String text() {
            return text;
        }
    }

    /**
     * 触发方式
     */
    public enum TriggerMethod {
        /**
         * 设备属性
         */
        DATA("数据上报");

        private String text;

        TriggerMethod(String text) {
            this.text = text;
        }

        public static Map<String, String> toMap() {
            Map<String, String> map = new HashMap<>();
            Arrays.stream(TriggerMethod.values()).forEach(v -> map.put(v.name(), v.text()));
            return map;
        }

        public String text() {
            return text;
        }
    }
}
