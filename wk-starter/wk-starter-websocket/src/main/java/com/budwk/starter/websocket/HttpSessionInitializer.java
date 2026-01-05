package com.budwk.starter.websocket;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * WebSocket HttpSession 初始化器
 *
 * 使用 ServletContainerInitializer 在应用启动时注册 ServletRequestListener，
 * 用于解决 WebSocket 握手时 HttpSession 为 null 的问题。
 *
 * 在每个请求初始化时强制创建 HttpSession，确保 WebSocket 握手过程中能够获取到有效的 session。
 *
 * 这对于 WebSocket 连接的稳定性至关重要：
 * - 如果没有 HttpSession，WebSocket handler 无法识别用户身份
 * - 缺少 session 会导致 WebSocket 连接被服务器主动断开
 * - 用户认证、权限检查、会话数据访问都依赖于 HttpSession
 *
 * @author wizzer.cn
 */
public class HttpSessionInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        // 注册 ServletRequestListener 来确保每个请求都有 HttpSession
        ctx.addListener(new ServletRequestListener() {
            @Override
            public void requestInitialized(ServletRequestEvent sre) {
                if (sre.getServletRequest() instanceof HttpServletRequest) {
                    HttpServletRequest request = (HttpServletRequest) sre.getServletRequest();
                    // 调用 getSession() 会创建 session（如果不存在）
                    // 这确保了后续的 WebSocket 握手能够获取到有效的 HttpSession
                    request.getSession();
                }
            }

            @Override
            public void requestDestroyed(ServletRequestEvent sre) {
                // 不需要特殊处理
            }
        });
    }
}
