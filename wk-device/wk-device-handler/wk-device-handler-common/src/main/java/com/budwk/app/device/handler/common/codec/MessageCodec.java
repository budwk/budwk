package com.budwk.app.device.handler.common.codec;

import com.budwk.app.device.handler.common.codec.context.DecodeContext;
import com.budwk.app.device.handler.common.codec.context.EncodeContext;
import com.budwk.app.device.handler.common.codec.result.DecodeResult;
import com.budwk.app.device.handler.common.codec.result.EncodeResult;

/**
 * 编解码
 * @author wizzer.cn
 */
public interface MessageCodec {
    /**
     * 数据解码
     *
     * @param context 解码上下文
     * @return 解码结果
     * @see DecodeResult
     */
    DecodeResult decode(DecodeContext context);

    /**
     * 数据编码
     *
     * @param context 编码上下文
     * @return 编码结果
     * @see EncodeResult
     */
    EncodeResult encode(EncodeContext context);
}
