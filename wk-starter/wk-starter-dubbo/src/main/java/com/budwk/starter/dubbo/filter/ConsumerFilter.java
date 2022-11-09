package com.budwk.starter.dubbo.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.budwk.starter.common.constant.GlobalConstant;
import com.budwk.starter.dubbo.utils.TraceIdUtils;
import org.nutz.lang.Strings;
import org.slf4j.MDC;

/**
 * 从当前线程拿出traceId，放入RpcContext
 *
 * @author wizzer@qq.com
 */
@Activate(group = {Constants.CONSUMER})
public class ConsumerFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String traceId = MDC.get(GlobalConstant.WK_LOG_TRACE_ID);
        if (Strings.isNotBlank(traceId)) {
            TraceIdUtils.setTraceId(traceId);
            RpcContext.getContext().setAttachment(GlobalConstant.WK_TRACE_ID, traceId);
        }
        return invoker.invoke(invocation);
    }
}
