package com.budwk.starter.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wizzer@qq.com
 */
@Getter
@AllArgsConstructor
public enum ResultCode implements IResultCode {
    SUCCESS(0,"操作成功"),
    FAILURE(200400,"业务异常"),
    NOT_FOUND(200404,"服务未找到"),
    TOO_MANY_REQUESTS(200429, "请求次数过多"),
    SERVER_ERROR(200500,"服务异常"),
    IOC_ERROR(500000,"IOC对象加载异常"),
    NULL_DATA_ERROR(500100,"数据不存在"),
    HAVE_DATA_ERROR(500110,"数据已存在"),
    PARAM_ERROR(500200,"参数错误"),
    HEADER_PARAM_TOKEN_ERROR(500201,"header参数wk-user-token错误"),
    HEADER_PARAM_APPID_ERROR(500202,"header参数appId错误"),
    HEADER_PARAM_MEMBER_ERROR(500203,"header参数wk-member-token错误"),
    HEADER_PARAM_LANG_ERROR(500204,"header参数lang错误"),
    XSS_SQL_ERROR(500300,"传参被拦截"),
    BLACKLIST_ERROR(500400,"IP黑名单"),
    DAO_ERROR(500600,"DAO数据库查询错误"),
    DEMO_ERROR(500700,"演示环境，限制操作"),

    APPID_ERROR(500801, "应用ID错误"),
    TIMESTAMP_ERROR(500802, "时间戳不正确"),
    NONCE_ERROR(500803, "随机字符串缺失"),
    NONCE_DUPLICATION(500804, "随机字符串重复"),
    SIGIN_ERROR(500805, "签名错误"),

    USER_NOT_LOGIN(600098, "登录失效"),
    USER_NOT_ROLE(600099, "无此角色"),
    USER_NOT_PERMISSION(600100, "无此权限"),
    USER_NOT_FOUND(600101, "用户不存在"),
    USER_DISABLED(600102, "用户被禁用"),
    USER_LOCKED(600103, "用户已锁定"),
    USER_NAME_ERROR(600104, "用户名错误"),
    USER_PWD_ERROR(600105, "用户密码错误"),
    USER_PWD_EXPIRED(600106, "用户密码过期"),
    USER_VERIFY_ERROR(600107, "验证码错误"),
    USER_LOGIN_FAIL(600108, "用户登录失败");

    final int code;
    final String msg;
}
