package com.budwk.starter.wechat;

public class WxException extends RuntimeException {

    private static final long serialVersionUID = 2372915232825280617L;

    public WxException() {
	}

	public WxException(String message) {
		super(message);
	}

	public WxException(Throwable cause) {
		super(cause);
	}

	public WxException(String message, Throwable cause) {
		super(message, cause);
	}

}
