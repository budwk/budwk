package com.budwk.starter.dubbo.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.budwk.starter.common.constant.GlobalConstant;
import com.budwk.starter.dubbo.utils.TraceIdUtils;
import org.nutz.lang.Strings;
import org.slf4j.MDC;

/**
 * 从RpcContext拿出traceId,放入当前线程
 *
 * @author wizzer@qq.com
 */
@Activate(group = {Constants.PROVIDER})
public class ProviderFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String traceId = RpcContext.getContext().getAttachment(GlobalConstant.WK_TRACE_ID);
        if (Strings.isNotBlank(traceId)) {
            MDC.put(GlobalConstant.WK_LOG_TRACE_ID, traceId);
            TraceIdUtils.setTraceId(traceId);
            RpcContext.getContext().setAttachment(GlobalConstant.WK_TRACE_ID, traceId);
        }
        return invoker.invoke(invocation);
    }
}
