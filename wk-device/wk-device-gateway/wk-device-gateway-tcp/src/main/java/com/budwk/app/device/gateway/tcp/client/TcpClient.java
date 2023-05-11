package com.budwk.app.device.gateway.tcp.client;

import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

/**
 * @author wizzer.cn
 */
public interface TcpClient {
    String getId();

    TcpClient onMessage(Consumer<byte[]> messageHandler);

    TcpClient onClose(Consumer<Void> closeHandler);

    TcpClient onError(Consumer<Throwable> errorHandler);

    CompletionStage<Void> send(byte[] bytes);
}
