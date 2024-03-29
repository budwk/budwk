package com.budwk.app.device.handler.common.codec;

/**
 * 解析器加载
 *
 * @author wizzer.cn
 */
public interface HandlerLoader {
    Handler loadHandler(String handlerCode);

    void reload(String handlerCode);
}
