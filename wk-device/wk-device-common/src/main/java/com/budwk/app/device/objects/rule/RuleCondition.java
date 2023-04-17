package com.budwk.app.device.objects.rule;

import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wizzer.cn
 */
@Data
public class RuleCondition implements Serializable {
    private static final long serialVersionUID = 1L;

    private String key;

    private String value;

    private Operator operator;

    public enum Operator {
        EQ("="),
        NEQ("!="),
        GT(">"),
        GTE(">="),
        LT("<"),
        LTE("<="),
        IN("in"),
        NOT_IN("not in"),
        BETWEEN("between"),
        NOT_BETWEEN("not between"),
        LIKE("like");

        Operator(String operation) {
            this.operation = operation;
        }

        private String operation;

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public static Map<String, String> toMap() {
            Map<String, String> map = new HashMap<>();
            Arrays.stream(values()).forEach(v -> map.put(v.operation, v.operation));
            return map;
        }
    }
}
