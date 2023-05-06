package com.budwk.app.device.handler.common.codec.exception;

/**
 * @author wizzer.cn
 * @author zyang
 */
public class MessageCodecException extends RuntimeException {
    public MessageCodecException(String message) {
        super(message);
    }

    public MessageCodecException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void makeThrow(String message, Object... args) {
        throw new MessageCodecException(String.format(message, args));
    }
}
