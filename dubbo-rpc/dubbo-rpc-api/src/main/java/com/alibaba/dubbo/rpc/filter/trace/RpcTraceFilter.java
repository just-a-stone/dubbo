package com.alibaba.dubbo.rpc.filter.trace;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.remoting.TimeoutException;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.dubbo.rpc.filter.trace.dapper.AttachmentUtil;
import com.alibaba.dubbo.rpc.filter.trace.dapper.Constants;
import com.alibaba.dubbo.rpc.filter.trace.dapper.EndPoint;
import com.alibaba.dubbo.rpc.filter.trace.dapper.Span;

/**
 * Created by shield on 2017/7/24.
 */
public class RpcTraceFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RpcTraceFilter.class);

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContext rpcContext = RpcContext.getContext();
        boolean isConsumerSide = rpcContext.isConsumerSide();
        boolean isProviderSide = rpcContext.isProviderSide();
        String methodName = rpcContext.getMethodName();
        RpcInvocation invocation1 = (RpcInvocation) invocation;

        Tracer tracer = Tracer.getInstance();

        //@TODO serviceName设计
        EndPoint endPoint = tracer.buildEndPoint(rpcContext.getLocalAddressString(), rpcContext.getLocalPort(), "");

        Span span = null;
        try {
            if (isConsumerSide) {
                logger.debug("消费者");
                Span parentSpan = tracer.getParentSpan();
                if (null == parentSpan) {
                    //根节点
                    //@TODO serviceId设计
                    span = tracer.newSpan(methodName, "");
                } else {
                    //叶子节点
                    span = tracer.buildSpan(parentSpan.getTraceId(), parentSpan.getId(), tracer.generateSpanId(), methodName,
                            parentSpan.isSample(), "");
                }
            } else if (isProviderSide) {
                logger.debug("生产者");
                Long traceId = AttachmentUtil.getAttachmentLong(invocation1, Constants.TRACE_ID);
                Long parentId = AttachmentUtil.getAttachmentLong(invocation1, Constants.PARENT_ID);
                Long spanId = AttachmentUtil.getAttachmentLong(invocation1, Constants.SPAN_ID);
                boolean isSample = traceId != null;
                //TODO serviceId设计
                span = tracer.buildSpan(traceId, parentId, spanId, methodName, isSample, "");
            }

            logger.debug("Span：" + span.toString());

            //before
            this.invokeBefore(span, isConsumerSide, isProviderSide);
            this.setAttachment(invocation1, span);

            Result result = invoker.invoke(invocation);
            if (result.hasException()) {
                this.processException(endPoint,result.getException().getMessage(),span);
            }

            return result;
        } catch (RpcException e) {
            if (null != e.getCause() && e.getCause() instanceof TimeoutException) {
                this.processTimeoutException();
            } else {
                this.processException();
            }
            //抛出异常
            throw e;
        } finally {
            if (null != span) {
                this.invokeAfter(span, isConsumerSide, isProviderSide);
            }
        }

    }

    private void processTimeoutException() {
    }

    private void processException() {
    }

    private void invokeAfter(Span span, boolean isConsumerSide, boolean isProviderSide) {
        Tracer tracer = Tracer.getInstance();
        if (isConsumerSide && span.isSample()) {
            //client send
        } else if (isProviderSide) {
            if (span.isSample()) {
                //server receive
            }
            tracer.setParentSpan(span);
        }
    }

    private void invokeBefore(Span span, boolean isConsumerSide, boolean isProviderSide) {
        Tracer tracer = Tracer.getInstance();
        if (isConsumerSide && span.isSample()) {
            //client receive
        } else if (isProviderSide) {
            if (span.isSample()) {
                //server send
            }
            tracer.removeParentSpan();
        }
    }

    /**
     * span相关信息设置到attachment,传递给下游节点
     *
     * @param invocation
     * @param span
     */
    private void setAttachment(RpcInvocation invocation, Span span) {
        if (null != span && span.isSample()) {
            invocation.setAttachment(Constants.TRACE_ID, span.getTraceId() == null ? null : String.valueOf(span.getTraceId()));
            invocation.setAttachment(Constants.SPAN_ID, span.getId() == null ? null : String.valueOf(span.getId()));
            invocation.setAttachment(Constants.PARENT_ID, span.getParentId() == null ? null : String.valueOf(span.getParentId()));
            invocation.setAttachment(Constants.SAMPLE, span.isSample() == null ? null : String.valueOf(span.isSample()));
        }
    }
}
