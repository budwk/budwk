package com.budwk.nb.web.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.constants.RedisConstant;
import com.budwk.nb.starter.swagger.annotation.ApiFormParam;
import com.budwk.nb.starter.swagger.annotation.ApiFormParams;
import com.budwk.nb.sys.models.Sys_log;
import com.budwk.nb.sys.models.Sys_user;
import com.budwk.nb.sys.services.SysMsgService;
import com.budwk.nb.sys.services.SysUserService;
import com.budwk.nb.web.commons.base.Globals;
import com.budwk.nb.web.commons.shiro.exception.CaptchaEmptyException;
import com.budwk.nb.web.commons.shiro.exception.CaptchaIncorrectException;
import com.budwk.nb.web.commons.shiro.filter.PlatformAuthenticationFilter;
import com.budwk.nb.web.commons.slog.SLogServer;
import com.budwk.nb.web.commons.utils.JwtUtil;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.img.Images;
import org.nutz.integration.jedis.JedisAgent;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import redis.clients.jedis.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static com.budwk.nb.commons.constants.RedisConstant.REDIS_KEY_APP_DEPLOY;

/**
 * @author wizzer(wizzer @ qq.com) on 2019/10/29
 */
@IocBean
@At("/api/{version}/platform/login")
@Ok("json")
@ApiVersion("1.0.0")
@OpenAPIDefinition(tags = {@Tag(name = "系统_系统登陆")}, servers = @Server(url = "/"))
public class SysLoginController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysUserService sysUserService;
    @Inject
    private SLogServer sLogServer;
    @Inject
    private JedisAgent jedisAgent;
    @Inject
    private RedisService redisService;
    
    @Inject("java:$conf.getInt('shiro.session.cache.redis.ttl')")
    private int RedisKeySessionTTL;

    @Inject
    @Reference
    private SysMsgService sysMsgService;
    @Inject("refer:shiroWebSessionManager")
    private DefaultWebSessionManager webSessionManager;


    @At
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @POST
    @Filters(@By(type = PlatformAuthenticationFilter.class))
    @Operation(
            tags = "系统_系统登陆", summary = "登陆系统",
            security = {
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "username", description = "用户名", required = true),
                    @ApiFormParam(name = "password", description = "用户密码", required = true),
                    @ApiFormParam(name = "captcha", description = "验证码", required = false)
            }
    )
    public Object login(@Attr("platformLoginToken") AuthenticationToken formLoginToken, HttpServletRequest req, HttpSession session) {
        int errCount = 0;
        try {
            //session失效咯
            if (formLoginToken == null) {
                return Result.error(500207, "system.login.error.session");
            }
            //输错三次显示验证码窗口
            errCount = NumberUtils.toInt(Strings.sNull(SecurityUtils.getSubject().getSession(true).getAttribute("platformErrCount")));
            Subject subject = SecurityUtils.getSubject();
            ThreadContext.bind(subject);
            subject.login(formLoginToken);
            Sys_user user = (Sys_user) subject.getPrincipal();
            int count = user.getLoginCount() == null ? 0 : user.getLoginCount();
            //如果启用了用户单一登录
            if ("true".equals(Globals.MyConfig.getOrDefault("AppWebUserOnlyOne", "false"))) {
                try {
                    if (jedisAgent.isClusterMode()) {
                        JedisCluster jedisCluster = jedisAgent.getJedisClusterWrapper().getJedisCluster();
                        List<String> keys = new ArrayList<>();
                        for (JedisPool pool : jedisCluster.getClusterNodes().values()) {
                            try (Jedis jedis = pool.getResource()) {
                                //把其他在线用户踢下线
                                ScanParams match = new ScanParams().match(RedisConstant.REDIS_KEY_LOGIN_ADMIN_SESSION + user.getId() + ":*");
                                ScanResult<String> scan = null;
                                do {
                                    scan = jedis.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                                    keys.addAll(scan.getResult());
                                } while (!scan.isCompleteIteration());
                            }
                        }
                        for (String key : keys) {
                            String userToken = key.substring(key.lastIndexOf(":") + 1);
                            String sessionId = Strings.sNull(redisService.get(key));
                            if (!sessionId.equals(session.getId())) {
                                try {
                                    Session oldSession = webSessionManager.getSessionDAO().readSession(sessionId);
                                    if (oldSession != null) {
                                        //通知其他用户被踢下线
                                        sysMsgService.offline(user.getLoginname(), userToken);
                                        oldSession.stop();
                                        webSessionManager.getSessionDAO().delete(oldSession);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                    } else {
                        //把其他在线用户踢下线
                        ScanParams match = new ScanParams().match(RedisConstant.REDIS_KEY_LOGIN_ADMIN_SESSION + user.getId() + ":*");
                        ScanResult<String> scan = null;
                        do {
                            scan = redisService.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                            for (String key : scan.getResult()) {
                                String userToken = key.substring(key.lastIndexOf(":") + 1);
                                String sessionId = Strings.sNull(redisService.get(key));
                                if (!sessionId.equals(session.getId())) {
                                    try {
                                        Session oldSession = webSessionManager.getSessionDAO().readSession(sessionId);
                                        if (oldSession != null) {
                                            //通知其他用户被踢下线
                                            sysMsgService.offline(user.getLoginname(), userToken);
                                            oldSession.stop();
                                            webSessionManager.getSessionDAO().delete(oldSession);
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        } while (!scan.isCompleteIteration());
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            }
            sysUserService.update(Chain.make("loginIp", Lang.getIP(req)).add("loginAt", Times.now().getTime())
                            .add("loginCount", count + 1)
                    , Cnd.where("id", "=", user.getId()));
            Sys_log sysLog = new Sys_log();
            sysLog.setId(R.UU32());
            sysLog.setType("info");
            sysLog.setTag("用户登陆");
            sysLog.setSrc(this.getClass().getName() + "#doLogin");
            sysLog.setMsg("成功登录系统！");
            sysLog.setIp(Lang.getIP(req));
            sysLog.setUserAgent(req.getHeader("User-Agent"));
            sysLog.setCreatedBy(user.getId());
            sysLog.setUpdatedBy(user.getId());
            sysLog.setCreatedAt(Times.now().getTime());
            sysLog.setUpdatedAt(Times.now().getTime());
            sysLog.setDelFlag(false);
            sysLog.setUsername(user.getUsername());
            sysLog.setLoginname(user.getLoginname());
            sLogServer.async(sysLog);
            String userToken = JwtUtil.sign(user, RedisKeySessionTTL * 1000);
            session.setAttribute("userToken", userToken);
            session.setAttribute("userId", user.getId());
            redisService.setex(RedisConstant.REDIS_KEY_LOGIN_ADMIN_SESSION + user.getId() + ":" + userToken, RedisKeySessionTTL, session.getId());
            return Result.success("system.login.success").addData(
                    NutMap.NEW().addv("token", userToken)
                            .addv("user", user)
                            .addv("menus", sysUserService.getMenusAndButtons(user.getId()))
                            .addv("roles", sysUserService.getRoles(user))
            );
        } catch (CaptchaIncorrectException e) {
            //自定义的验证码错误异常
            return Result.error(500201, "system.login.error.captcha");
        } catch (CaptchaEmptyException e) {
            //验证码为空
            return Result.error(500202, "system.login.error.captcha.empty");
        } catch (LockedAccountException e) {
            return Result.error(500203, "system.login.error.locked");
        } catch (UnknownAccountException e) {
            log.error(e.getMessage(), e);
            errCount++;
            SecurityUtils.getSubject().getSession(true).setAttribute("platformErrCount", errCount);
            return Result.error(500204, "system.login.error.nouser");
        } catch (AuthenticationException e) {
            log.error(e.getMessage(), e);
            errCount++;
            SecurityUtils.getSubject().getSession(true).setAttribute("platformErrCount", errCount);
            return Result.error(500205, "system.login.error.password");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errCount++;
            SecurityUtils.getSubject().getSession(true).setAttribute("platformErrCount", errCount);
            return Result.error(500206, "system.login.error");
        }
    }

    @At
    @Ok("json")
    @GET
    @Operation(
            tags = "系统_系统登陆", summary = "退出登陆",
            security = {
                    @SecurityRequirement(name = "登陆认证")
            },
            parameters = {
                    @Parameter(name = "X-Token", description = "X-Token", in = ParameterIn.HEADER, required = true)
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(implementation = Result.class), mediaType = "application/json"))
            }
    )
    public Object logout(HttpSession session, HttpServletRequest req) {
        try {
            Subject currentUser = SecurityUtils.getSubject();
            Sys_user user = (Sys_user) currentUser.getPrincipal();
            currentUser.logout();
            if (user != null) {
                Sys_log sysLog = new Sys_log();
                sysLog.setId(R.UU32());
                sysLog.setType("info");
                sysLog.setTag("用户登出");
                sysLog.setSrc(this.getClass().getName() + "#logout");
                sysLog.setMsg("成功退出系统！");
                sysLog.setUserAgent(req.getHeader("User-Agent"));
                sysLog.setIp(Lang.getIP(req));
                sysLog.setCreatedBy(user.getId());
                sysLog.setUpdatedBy(user.getId());
                sysLog.setCreatedAt(Times.now().getTime());
                sysLog.setUpdatedAt(Times.now().getTime());
                sysLog.setDelFlag(false);
                sysLog.setLoginname(user.getLoginname());
                sysLog.setUsername(user.getUsername());
                sLogServer.async(sysLog);
            }
            return Result.success();
        } catch (SessionException ise) {
            log.debug("Encountered session exception during logout.  This can generally safely be ignored.", ise);
        } catch (Exception e) {
            log.debug("Logout error", e);
        }
        return Result.error();
    }


    @At("/captcha")
    @Ok("raw:png")
    @GET
    @Operation(
            tags = "系统_系统登陆", summary = "登陆系统",
            security = {
                    @SecurityRequirement(name = "获取验证码")
            },
            requestBody = @RequestBody(content = @Content()),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "执行成功",
                            content = @Content(schema = @Schema(example = "", name = "example"), mediaType = "image/png"))
            }
    )
    @ApiFormParams(
            apiFormParams = {
                    @ApiFormParam(name = "w", description = "宽度", example = "300", type = "integer"),
                    @ApiFormParam(name = "h", description = "高度", example = "200", type = "integer")
            }
    )
    public BufferedImage captcha(HttpSession session, @Param("w") int w, @Param("h") int h) {
        if (w * h < 1) {
            w = 200;
            h = 60;
        }
        String text = R.captchaNumber(4);
        redisService.setex(RedisConstant.REDIS_KEY_LOGIN_ADMIN_CAPTCHA + session.getId(), 300, text);
        return Images.createCaptcha(text, w, h, null, null, null);
    }
}
