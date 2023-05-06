package com.budwk.app.device.handler.common.codec.exception;

/**
 * @author wizzer.cn
 * @author zyang
 */
public class UnSupportCommandException extends RuntimeException {
    public UnSupportCommandException(String message) {
        super(message);
    }

    public UnSupportCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void makeThrow(String message, Object... args) {
        throw new UnSupportCommandException(String.format(message, args));
    }
}
