package com.budwk.app.device.gateway.tcp.server;

import com.budwk.app.device.gateway.tcp.client.TcpClient;

import java.util.function.Consumer;

/**
 * @author wizzer.cn
 */
public interface TcpServer {
    TcpServer start();

    TcpServer handleConnection(Consumer<TcpClient> handler);
}
