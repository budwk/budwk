package com.budwk.nb.web.controllers.platform.sys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.budwk.nb.commons.base.Result;
import com.budwk.nb.commons.constants.RedisConstant;
import com.budwk.nb.commons.utils.StringUtil;
import com.budwk.nb.sys.models.Sys_log;
import com.budwk.nb.sys.models.Sys_user;
import com.budwk.nb.sys.services.SysUserService;
import com.budwk.nb.web.commons.base.Globals;
import com.budwk.nb.web.commons.ext.websocket.WkNotifyService;
import com.budwk.nb.web.commons.shiro.exception.CaptchaEmptyException;
import com.budwk.nb.web.commons.shiro.exception.CaptchaIncorrectException;
import com.budwk.nb.web.commons.shiro.filter.PlatformAuthenticationFilter;
import com.budwk.nb.web.commons.slog.SLogServer;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.util.Set;

/**
 * Created by wizzer.cn on 2019/10/29
 */
@IocBean
@At("/api/{version}/platform/login")
@Ok("json")
@ApiVersion("1.0.0")
public class SysLoginController {
    private static final Log log = Logs.get();
    @Inject
    @Reference
    private SysUserService sysUserService;
    @Inject
    private SLogServer sLogServer;
    @Inject
    private RedisService redisService;
    @Inject
    private StringUtil stringUtil;

    @Inject("java:$conf.getInt('shiro.session.cache.redis.ttl')")
    private int RedisKeySessionTTL;

    @Inject
    private WkNotifyService wkNotifyService;
    @Inject("refer:shiroWebSessionManager")
    private DefaultWebSessionManager webSessionManager;

    /**
     * @api {post} /api/1.0.0/platform/login/login 用户登陆
     * @apiName login
     * @apiGroup LOGIN
     * @apiVersion 1.0.0
     * @apiParam {String} loginname 用户账号
     * @apiParam {String} password  用户密码
     * @apiParam {String} captcha   验证码
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   登陆成功
     * @apiSuccess {Object} data  用户数据
     * @apiSuccess {String} data.token  用户Token
     * @apiSuccess {Object} data.user  用户数据
     * @apiSuccess {Object} data.menus  用户菜单
     * @apiSuccess {Object} data.roles  用户角色
     */
    @At
    @Ok("json:{locked:'password|salt',ignoreNull:false}")
    @POST
    @Filters(@By(type = PlatformAuthenticationFilter.class))
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
            if ("true".equals(Globals.MyConfig.getOrDefault("AppPlatformUserSessionOnlyOne", "false"))) {
                try {
                    //把其他在线用户踢下线
                    Set<String> set = redisService.keys(RedisConstant.REDIS_KEY_LOGIN_ADMIN_SESSION + user.getId() + ":*");
                    for (String key : set) {
                        String sessionId = Strings.sNull(redisService.get(key));
                        if (!sessionId.equals(session.getId())) {
                            try {
                                Session oldSession = webSessionManager.getSessionDAO().readSession(sessionId);
                                if (oldSession != null) {
                                    //通知其他用户被踢下线
                                    wkNotifyService.offline(user.getLoginname(), sessionId);
                                    oldSession.stop();
                                    webSessionManager.getSessionDAO().delete(oldSession);
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            }
            sysUserService.update(Chain.make("loginIp", Lang.getIP(req)).add("loginAt", Times.now().getTime())
                            .add("loginCount", count + 1)
                    , Cnd.where("id", "=", user.getId()));
            Sys_log sysLog = new Sys_log();
            sysLog.setType("info");
            sysLog.setTag("用户登陆");
            sysLog.setSrc(this.getClass().getName() + "#doLogin");
            sysLog.setMsg("成功登录系统！");
            sysLog.setIp(Lang.getIP(req));
            sysLog.setUserAgent(req.getHeader("User-Agent"));
            sysLog.setCreatedBy(user.getId());
            sysLog.setUpdatedBy(user.getId());
            sysLog.setUsername(user.getUsername());
            sysLog.setLoginname(user.getLoginname());
            sLogServer.async(sysLog);
            long now = Times.getTS();
            String userToken = stringUtil.generateUserToken(user.getId(), now, "budwk.com");
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

    /**
     * @api {get} /api/1.0.0/platform/login/logout 退出登陆
     * @apiName logout
     * @apiGroup LOGIN
     * @apiVersion 1.0.0
     * @apiSuccess {Number} code  0
     * @apiSuccess {String} msg   操作成功
     * @apiSuccess {Object} data  用户数据
     */
    @At
    @Ok("json")
    @GET
    public Object logout(HttpSession session, HttpServletRequest req) {
        try {
            String userToken = Strings.sNull(session.getAttribute("userToken"));
            Subject currentUser = SecurityUtils.getSubject();
            Sys_user user = (Sys_user) currentUser.getPrincipal();
            currentUser.logout();
            if (user != null) {
                Sys_log sysLog = new Sys_log();
                sysLog.setType("info");
                sysLog.setTag("用户登出");
                sysLog.setSrc(this.getClass().getName() + "#logout");
                sysLog.setMsg("成功退出系统！");
                sysLog.setIp(Lang.getIP(req));
                sysLog.setCreatedBy(user.getId());
                sysLog.setUpdatedBy(user.getId());
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

    /**
     * @api {get} /api/1.0.0/platform/login/captcha 获取验证码
     * @apiName captcha
     * @apiGroup LOGIN
     * @apiVersion 1.0.0
     * @apiSuccess {Object} data  图片字节流
     */
    @At("/captcha")
    @Ok("raw:png")
    public BufferedImage captcha(HttpSession session, @Param("w") int w, @Param("h") int h) {
        if (w * h < 1) { //长或宽为0?重置为默认长宽.
            w = 200;
            h = 60;
        }
        String text = R.captchaNumber(4);
        redisService.setex(RedisConstant.REDIS_KEY_LOGIN_ADMIN_CAPTCHA + session.getId(), 300, text);
        return Images.createCaptcha(text, w, h, null, null, null);
    }
}
