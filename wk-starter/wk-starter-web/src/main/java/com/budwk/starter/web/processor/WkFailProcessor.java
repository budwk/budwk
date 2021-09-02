package com.budwk.starter.web.processor;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.budwk.starter.common.exception.BaseException;
import com.budwk.starter.common.result.Result;
import com.budwk.starter.common.result.ResultCode;
import com.budwk.starter.common.utils.WebUtil;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.dao.DaoException;
import org.nutz.ioc.IocException;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.impl.processor.ViewProcessor;

/**
 * @author wizzer@qq.com
 */
public class WkFailProcessor extends ViewProcessor {

    private static final Log log = Logs.get();

    @Override
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
        view = evalView(config, ai, ai.getFailView());
    }

    @Override
    public void process(ActionContext ac) throws Throwable {
        if (log.isWarnEnabled()) {
            String uri = Mvcs.getRequestPath(ac.getRequest());
            log.warn(String.format("Error@%s :", uri), ac.getError());
        }
        Throwable e = ac.getError();
        // 捕获Ioc异常
        if (e instanceof IocException || e instanceof ObjectLoadException) {
            WebUtil.rendAjaxResp(ac.getRequest(), ac.getResponse(), Result.error(ResultCode.IOC_ERROR, !log.isDebugEnabled() ? ResultCode.IOC_ERROR.getMsg() :e.getMessage()));
            return;
        } else if (e instanceof DaoException) {
            WebUtil.rendAjaxResp(ac.getRequest(), ac.getResponse(), Result.error(ResultCode.DAO_ERROR, !log.isDebugEnabled() ? ResultCode.DAO_ERROR.getMsg() :  e.getMessage()));
            return;
        }  else if (e instanceof FailToCastObjectException) { // 捕获类型转换异常
            WebUtil.rendAjaxResp(ac.getRequest(), ac.getResponse(), Result.error(ResultCode.PARAM_ERROR, !log.isDebugEnabled() ? ResultCode.PARAM_ERROR.getMsg() : e.getMessage()));
            return;
        } else if (e instanceof NotLoginException) {    // 如果是未登录异常
            WebUtil.rendAjaxResp(ac.getRequest(), ac.getResponse(), Result.error(ResultCode.USER_NOT_LOGIN));
            return;
        } else if (e instanceof NotRoleException) {        // 如果是角色异常
            NotRoleException ee = (NotRoleException) e;
            WebUtil.rendAjaxResp(ac.getRequest(), ac.getResponse(), Result.error(ResultCode.USER_NOT_ROLE, ResultCode.USER_NOT_ROLE.getMsg() + ": " + ee.getRole()));
            return;
        } else if (e instanceof NotPermissionException) {    // 如果是权限异常
            NotPermissionException ee = (NotPermissionException) e;
            WebUtil.rendAjaxResp(ac.getRequest(), ac.getResponse(), Result.error(ResultCode.USER_NOT_PERMISSION, ResultCode.USER_NOT_PERMISSION.getMsg() + ": " + ee.getCode()));
            return;
        } else if (e instanceof BaseException) {    // 如果是业务异常
            WebUtil.rendAjaxResp(ac.getRequest(), ac.getResponse(), Result.error(ResultCode.FAILURE, e.getMessage()));
            return;
        } else if (e instanceof RuntimeException){
            WebUtil.rendAjaxResp(ac.getRequest(), ac.getResponse(), Result.error(ResultCode.SERVER_ERROR, !log.isDebugEnabled() ? ResultCode.SERVER_ERROR.getMsg() : e.getMessage()));
            return;
        }
        super.process(ac);
    }
}
