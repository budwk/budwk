package com.budwk.starter.web.validation;

import com.budwk.starter.common.enums.Validation;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wizzer@qq.com
 */
public class ValidationUtil {

    /**
     * 验证字符串是否符合规则
     *
     * @param value      字符串值
     * @param validation 正则表达式枚举
     * @return true=验证通过
     */
    public static boolean regex(String value, Validation validation) {
        final Matcher m = Pattern.compile(validation.getRegex(), Pattern.MULTILINE + Pattern.DOTALL).matcher(value);
        return m.matches();
    }

    /**
     * 验证字符串是否符合规则
     *
     * @param value 字符串值
     * @param regex 正则表达式
     * @return true=验证通过
     */
    public static boolean regex(String value, String regex) {
        final Matcher m = Pattern.compile(regex, Pattern.MULTILINE + Pattern.DOTALL).matcher(value);
        return m.matches();
    }

    /**
     * 验证数据类型
     *
     * @param value 字符串值
     * @param type  数据类型
     * @return true=验证通过
     */
    public static boolean type(String value, String type) {
        switch (type.toLowerCase()) {
            case "integer":
            case "int":
                try {
                    Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return false;
                }
                break;
            case "long":
                try {
                    Long.parseLong(value);
                } catch (NumberFormatException e) {
                    return false;
                }
                break;
            case "float":
            case "double":
                if (!Strings.isNumber(value)) {
                    return false;
                }
                break;
            case "date":
                try {
                    Times.ams(value);
                } catch (Exception e) {
                    return false;
                }
                break;
            default:
                return true;
        }
        return true;
    }


}
