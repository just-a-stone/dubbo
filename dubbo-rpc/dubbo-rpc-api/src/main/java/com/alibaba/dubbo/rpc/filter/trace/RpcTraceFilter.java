package com.alibaba.dubbo.rpc.filter.trace;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.remoting.TimeoutException;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.dubbo.rpc.filter.trace.dapper.*;

/**
 * Created by shield on 2017/7/24.
 */
@Activate(group = {com.alibaba.dubbo.common.Constants.CONSUMER, com.alibaba.dubbo.common.Constants.PROVIDER})
public class RpcTraceFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RpcTraceFilter.class);

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        long start = System.currentTimeMillis();

        RpcContext rpcContext = RpcContext.getContext();
        boolean isConsumerSide = rpcContext.isConsumerSide();
        boolean isProviderSide = rpcContext.isProviderSide();
        String methodName = rpcContext.getMethodName();
        RpcInvocation invocation1 = (RpcInvocation) invocation;

        Tracer tracer = Tracer.getInstance();

        String serviceId = rpcContext.getUrl().getServiceInterface() + "_" + methodName;
        EndPoint endPoint = tracer.buildEndPoint(rpcContext.getLocalAddressString(), rpcContext.getLocalPort(), serviceId);

        Span span = null;
        try {
            if (isConsumerSide) {
                logger.debug("消费者");
                Span parentSpan = tracer.getParentSpan();
                if (null == parentSpan) {
                    //根节点

                    span = tracer.newSpan(methodName, serviceId);
                } else {
                    //叶子节点
                    span = tracer.buildSpan(parentSpan.getTraceId(), parentSpan.getId(), tracer.generateSpanId(), methodName,
                            parentSpan.isSample(), serviceId);
                }
            } else if (isProviderSide) {
                logger.debug("生产者");
                Long traceId = AttachmentUtil.getAttachmentLong(invocation1, Constants.TRACE_ID);
                Long parentId = AttachmentUtil.getAttachmentLong(invocation1, Constants.SPAN_ID);
                Long spanId = Tracer.getInstance().generateSpanId();
                boolean isSample = traceId != null;

                span = tracer.buildSpan(traceId, parentId, spanId, methodName, isSample, serviceId);
            }

            //before
            this.invokeBefore(span, endPoint, start, isConsumerSide, isProviderSide);
            this.setAttachment(invocation1, span);

            Result result = invoker.invoke(invocation);
            if (result.hasException()) {
                this.processException(endPoint, result.getException().getMessage(), ExceptionType.EXCEPTION);
            }

            return result;
        } catch (RpcException e) {
            if (null != e.getCause() && e.getCause() instanceof TimeoutException) {
                this.processException(endPoint, e.getMessage(), ExceptionType.TIMEOUTEXCEPTION);
            } else {
                this.processException(endPoint, e.getMessage(), ExceptionType.EXCEPTION);
            }
            //抛出异常
            throw e;
        } finally {
            if (null != span) {
                long end = System.currentTimeMillis();
                this.invokeAfter(span, endPoint, end, isConsumerSide, isProviderSide);
            }
        }

    }


    private void processException(EndPoint endPoint, String message, ExceptionType exception) {
        BinaryAnnotation annotation = new BinaryAnnotation();
        annotation.setKey(exception.label());
        annotation.setValue(message);
        annotation.setType(exception.symbol());
        annotation.setEndPoint(endPoint);

        Tracer tracer = Tracer.getInstance();
        tracer.addBinaryAnnotation(annotation);

    }

    private void invokeAfter(Span span, EndPoint endPoint, long end, boolean isConsumerSide, boolean isProviderSide) {
        Tracer tracer = Tracer.getInstance();
        if (isConsumerSide && span.isSample()) {
            //client receive
            tracer.clientReceive(span, endPoint, end);
        } else if (isProviderSide) {
            if (span.isSample()) {
                //server send
                tracer.serverSend(span, endPoint, end);
            }
        }
        tracer.removeParentSpan();
    }

    private void invokeBefore(Span span, EndPoint endPoint, long start, boolean isConsumerSide, boolean isProviderSide) {
        Tracer tracer = Tracer.getInstance();
        if (isConsumerSide && span.isSample()) {
            //client send
            tracer.clientSend(span, endPoint, start);
        } else if (isProviderSide) {
            if (span.isSample()) {
                //server receive
                tracer.serverReceive(span, endPoint, start);
            }
        }
        tracer.setParentSpan(span);
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
