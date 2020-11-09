package com.budwk.nb.app.commons.shiro.filter;

import com.budwk.nb.app.commons.shiro.service.JwtService;
import com.budwk.nb.app.commons.shiro.token.JwtToken;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/4/16
 */
@IocBean(name = "jwtFilter")
public class JwtFilter extends BasicHttpAuthenticationFilter {
    private static final Log log = Logs.get();
    @Inject
    private JwtService jwtService;

    /**
     * 判断用户是否想要登入。
     * 检测header里面是否包含Authorization字段即可
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("Authorization");
        return authorization != null;
    }

    /**
     * 登录验证
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = httpServletRequest.getHeader("Authorization");
        JwtToken token = new JwtToken(authorization);
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(token);
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginAttempt(request, response)) {
            try {
                return executeLogin(request, response);
            } catch (Exception e) {
                //判断如果抛出token失效，则执行刷新token逻辑
                if (e.getMessage().contains("expired")) {
                    String newToken = jwtService.refreshToken(this.getAuthzHeader(request));
                    if (Strings.isNotBlank(newToken)) {
                        JwtToken jwtToken = new JwtToken(newToken);
                        getSubject(request, response).login(jwtToken);
                        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                        httpServletResponse.setHeader("token", newToken);
                        return true;
                    }

                }
                response401(request, response);
                return false;
            }
        }
        response401(request, response);
        return false;
    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().toUpperCase().equals("OPTIONS")) {
            httpServletResponse.setStatus(200);
            return true;
        }
        return super.preHandle(request, response);
    }

    /**
     * 将非法请求返回httpcode:401
     */
    private void response401(ServletRequest req, ServletResponse resp) {
        try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
            httpServletResponse.setStatus(401);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 重写该方法直接返回false，因为走到这个方法的请求都是因为401过来的，所以直接返回false
     * 如果不重写该方法，父类的方法回返回WWW-Authenticate 头信息导致浏览器自身弹出验证框，影响用户使用体验。本项目的业务要求前端自行判断401的话往登录页面跳转，不需要浏览器自己弹框。
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }
}
