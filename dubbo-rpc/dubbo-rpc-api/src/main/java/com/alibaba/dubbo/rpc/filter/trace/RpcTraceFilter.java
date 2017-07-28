package com.alibaba.dubbo.rpc.filter.trace;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.remoting.TimeoutException;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.dubbo.rpc.filter.trace.dapper.AttachmentUtil;
import com.alibaba.dubbo.rpc.filter.trace.dapper.Constants;
import com.alibaba.dubbo.rpc.filter.trace.dapper.ExceptionType;

/**
 * Created by shield on 2017/7/24.
 */
@Activate(group = {com.alibaba.dubbo.common.Constants.CONSUMER, com.alibaba.dubbo.common.Constants.PROVIDER})
public class RpcTraceFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RpcTraceFilter.class);

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        RpcContext rpcContext = RpcContext.getContext();
        boolean isConsumerSide = rpcContext.isConsumerSide();
        boolean isProviderSide = rpcContext.isProviderSide();
        String methodName = rpcContext.getMethodName();
        RpcInvocation invocation1 = (RpcInvocation) invocation;

//		Trace tracer = Trace.getInstance();
        String serviceId = rpcContext.getUrl().getServiceInterface() + "_" + methodName;

//        EndPoint endPoint = tracer.buildEndPoint(rpcContext.getLocalAddressString(), rpcContext.getLocalPort(), serviceId);

        Span span = TraceHelper.getInstance().tracer().currentSpan();
        Tracer.SpanInScope spanInScope = null;
        try {
            if (isConsumerSide) {

                if (null == span) {
                    span = TraceHelper.getInstance().tracer().newTrace().name(serviceId).start();
                    TraceHelper.getInstance().tracer().withSpanInScope(span);
                } else {
                    //服务端接收请求后，再次调用其他服务
                    span = TraceHelper.getInstance().tracer().nextSpan().name(serviceId).start();
                    spanInScope = TraceHelper.getInstance().tracer().withSpanInScope(span);
                }

            } else if (isProviderSide) {
                Long traceId = AttachmentUtil.getAttachmentLong(invocation1, Constants.TRACE_ID);
                Long parentId = AttachmentUtil.getAttachmentLong(invocation1, Constants.PARENT_ID);
                Long spanId = AttachmentUtil.getAttachmentLong(invocation1, Constants.SPAN_ID);
                Boolean sampled = AttachmentUtil.getAttachmentBoolean(invocation1, Constants.SAMPLE);

                TraceContext traceContext = TraceContext.newBuilder()
                        .parentId(parentId)
                        .traceId(traceId)
                        .spanId(spanId)
                        .sampled(sampled)
                        .build();
//                CurrentTraceContext.Scope oldContext = currentTraceContext.newScope(traceContext);

                span = TraceHelper.getInstance().tracer().joinSpan(traceContext).name(serviceId).start();
                TraceHelper.getInstance().tracer().withSpanInScope(span);
            }

            //before
            this.invokeBefore(span, isConsumerSide, isProviderSide);
            this.setAttachment(invocation1, TraceHelper.currentTraceContext.get());

            Result result = invoker.invoke(invocation);
            if (result.hasException()) {
                this.processException(span, result.getException().getMessage(), ExceptionType.EXCEPTION);
            }

            return result;
        } catch (RpcException e) {
            if (null != e.getCause() && e.getCause() instanceof TimeoutException) {
                this.processException(span, e.getMessage(), ExceptionType.TIMEOUTEXCEPTION);
            } else {
                this.processException(span, e.getMessage(), ExceptionType.EXCEPTION);
            }
            //抛出异常
            throw e;
        } finally {
            if (null != span) {
                this.invokeAfter(span, isConsumerSide, isProviderSide);
                span.finish();
            }

            if (spanInScope != null){
                spanInScope.close();
            }else {
                TraceHelper.getInstance().tracer().withSpanInScope(null);
            }
        }

    }


    private void processException(Span span, String message, ExceptionType exception) {
        span.tag(exception.label(), message);
    }

    private void invokeAfter(Span span, boolean isConsumerSide, boolean isProviderSide) {
        if (isConsumerSide && TraceHelper.currentTraceContext.get().sampled()) {
            //client receive
            span.annotate(zipkin.Constants.CLIENT_RECV);
        } else if (isProviderSide) {
            if (TraceHelper.currentTraceContext.get().sampled()) {
                //server send
                span.annotate(zipkin.Constants.SERVER_SEND);
            }
        }
    }

    private void invokeBefore(Span span, boolean isConsumerSide, boolean isProviderSide) {

        if (isConsumerSide && TraceHelper.currentTraceContext.get().sampled()) {
            //client send
            span.annotate(zipkin.Constants.CLIENT_SEND);
        } else if (isProviderSide) {
            if (TraceHelper.currentTraceContext.get().sampled()) {
                //server receive
                span.annotate(zipkin.Constants.SERVER_RECV);
            }
        }
    }

    /**
     * span相关信息设置到attachment,传递给下游节点
     *
     * @param invocation
     */
    private void setAttachment(RpcInvocation invocation, TraceContext context) {
        if (null != context) {
            invocation.setAttachment(Constants.TRACE_ID, String.valueOf(context.traceId()));
            invocation.setAttachment(Constants.SPAN_ID, String.valueOf(context.spanId()));
            invocation.setAttachment(Constants.PARENT_ID, String.valueOf(context.parentId()));
            invocation.setAttachment(Constants.SAMPLE, String.valueOf(context.sampled()));
        }
    }
}
