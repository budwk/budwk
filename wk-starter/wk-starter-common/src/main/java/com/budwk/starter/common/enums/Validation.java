package com.budwk.starter.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wizzer@qq.com
 */
@AllArgsConstructor
@Getter
public enum Validation implements IValidation{
    NONE("", ""),
    MOBILE("1[3-9]\\d{9}", "手机号码不正确"),
    MONTH("[1-2][0-9][0-9][0-9][0-3][0-9]", "年月不正确"),
    EMAIL("^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$", "EMail不正确"),
    CHINESE("[\u4e00-\u9fa5]+", "要求为汉字"),
    UPPER("[A-Z]+", "要求全部为大写字母"),
    LOWER("[a-z]+", "要求全部为小写字母"),
    LOWER_UNDERLINE_NUMBER("[a-z][a-z0-9_]+", "为小写字母、下划线和数字的组合，并以字母开头"),
    PASSWORD("^(?!([a-zA-Z\\d]*|[\\d!@#\\$%_\\.]*|[a-zA-Z!@#\\$%_\\.]*)$)[a-zA-Z\\d!@#\\$%_\\.]{8,}$","密码需8位以上并且包含数字、字母、特殊字符");

    final String regex;
    final String msg;
}
