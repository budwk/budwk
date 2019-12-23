package com.budwk.nb.web.commons.processor;

import com.budwk.nb.commons.base.Result;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.ioc.IocException;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.impl.processor.ViewProcessor;

/**
 * @author wizzer(wizzer@qq.com) on 2019/12/12.
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
        if (ac.getError() instanceof IocException) {
            NutShiro.rendAjaxResp(ac.getRequest(), ac.getResponse(), Result.error(500102, Mvcs.getMessage(ac.getRequest(), "system.ioc.error")));
        }
        super.process(ac);
    }
}
