package com.budwk.app.device.handler.common.codec.result;

import com.budwk.app.device.handler.common.message.codec.EncodedMessage;

import java.util.List;

/**
 * @author wizzer.cn
 * @author zyang
 */
public interface EncodeResult {
    /**
     * 是否已经发送过。针对http的，如果已经调用接口发送过了，就设置为true
     */
    boolean isSend();

    /**
     * 编码后的数据
     */
    List<EncodedMessage> getMessage();

    static EncodeResult createDefault(boolean isSend, List<EncodedMessage> messages) {
        return new DefaultEncodeResult(isSend, messages);
    }
}
