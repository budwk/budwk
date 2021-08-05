package com.budwk.starter.common.result;

import cn.hutool.core.text.StrFormatter;
import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import lombok.Data;
import org.nutz.lang.Strings;

import java.io.Serializable;

/**
 * @author wizzer@qq.com
 */
@Data
@ApiModel(description = "执行结果")
public class Result<T> implements Serializable {
    private static final long serialVersionUID = -361877494960801611L;

    @ApiModelProperty(description = "状态码", required = true)
    private int code;

    @ApiModelProperty(description = "消息内容", required = true)
    private String msg;

    @ApiModelProperty(description = "时间戳", required = true)
    private long time;

    @ApiModelProperty(description = "业务数据")
    private T data;

    public static <T> Result<T> NEW() {
        return new Result<>();
    }

    private Result() {
        this.time = System.currentTimeMillis();
    }

    private Result(IResultCode resultCode) {
        this(resultCode, null, resultCode.getMsg());
    }

    private Result(IResultCode resultCode, String msg) {
        this(resultCode, null, msg);
    }

    private Result(IResultCode resultCode, T data) {
        this(resultCode, data, resultCode.getMsg());
    }

    private Result(IResultCode resultCode, T data, String msg) {
        this(resultCode.getCode(), data, msg);
    }

    private Result(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.time = System.currentTimeMillis();
    }

    public Result<T> addCode(int code) {
        this.code = code;
        return this;
    }

    public Result<T> addMsg(String msg) {
        this.msg = Strings.sNull(msg);
        return this;
    }

    public Result<T> addData(T data) {
        this.data = data;
        return this;
    }

    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS.code;
    }

    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS, ResultCode.SUCCESS.getMsg());
    }

    public static <T> Result<T> success(IResultCode resultCode) {
        return new Result<>(resultCode);
    }

    public static <T> Result<T> success(String msg) {
        return new Result<>(ResultCode.SUCCESS, msg);
    }

    public static <T> Result<T> success(IResultCode resultCode, String msg) {
        return new Result<>(resultCode, msg);
    }

    public static <T> Result<T> success(int code, String msg) {
        return new Result<>(code, null, msg);
    }

    public static <T> Result<T> success(T data) {
        return success(data, ResultCode.SUCCESS.getMsg());
    }

    public static <T> Result<T> success(T data, String msg) {
        return success(ResultCode.SUCCESS.code, data, msg);
    }

    public static <T> Result<T> success(int code, T data, String msg) {
        return new Result<>(code, data, data == null ? ResultCode.FAILURE.getMsg() : msg);
    }

    public static <T> Result<T> data(T data) {
        return data(data, ResultCode.SUCCESS.getMsg());
    }

    public static <T> Result<T> data(T data, String msg) {
        return data(ResultCode.SUCCESS.code, data, msg);
    }

    public static <T> Result<T> data(int code, T data, String msg) {
        return new Result<>(code, data, data == null ? ResultCode.NULL_DATA_ERROR.getMsg() : msg);
    }

    public static <T> Result<T> error() {
        return new Result<>(ResultCode.FAILURE, ResultCode.FAILURE.getMsg());
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(ResultCode.FAILURE, msg);
    }

    public static <T> Result<T> error(String msg, String... args) {
        return new Result<>(ResultCode.FAILURE, StrFormatter.format(msg, args));
    }

    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, null, msg);
    }

    public static <T> Result<T> error(IResultCode resultCode) {
        return new Result<>(resultCode);
    }

    public static <T> Result<T> error(IResultCode resultCode, String msg) {
        return new Result<>(resultCode, msg);
    }

    public static <T> Result<T> condition(boolean flag) {
        return flag ? success(ResultCode.SUCCESS.getMsg()) : error(ResultCode.FAILURE.getMsg());
    }

}
