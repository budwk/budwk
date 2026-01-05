package com.budwk.starter.websocket;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.Mvcs;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * @author wizzer.cn
 */
public class WkWsConfigurator extends ServerEndpointConfig.Configurator {

    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        Ioc ioc = Mvcs.getIoc();
        if (ioc == null)
            ioc = Mvcs.ctx().getDefaultIoc();
        return ioc.get(endpointClass);
    }

    public void modifyHandshake(ServerEndpointConfig sec,
                                HandshakeRequest request,
                                HandshakeResponse response) {
        javax.servlet.http.HttpSession session = null;

        // 方案1: 如果是Nutz MVC环境, 从mvc上下文直接获取Request对象
        HttpServletRequest req = Mvcs.getReq();
        if (req != null) {
            // 使用 getSession(true) 确保创建 HttpSession，这对于 WebSocket 连接保持至关重要
            // 如果 session 为 null，WebSocket 连接可能会因为无法验证用户身份而被断开
            session = req.getSession(true);
        } else {
            // 方案2: 从 HandshakeRequest 获取 HttpSession
            session = (javax.servlet.http.HttpSession) request.getHttpSession();
        }

        // 将 HttpSession 存入 UserProperties，供后续 WebSocket handler 使用
        // 这个 session 对于维持 WebSocket 连接的用户会话状态是必需的
        if (session != null) {
            sec.getUserProperties().put("HttpSession", session);
        }
    }
}
