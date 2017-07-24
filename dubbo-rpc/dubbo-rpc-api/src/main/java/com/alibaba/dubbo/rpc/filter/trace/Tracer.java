package com.alibaba.dubbo.rpc.filter.trace;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.rpc.filter.trace.dapper.EndPoint;
import com.alibaba.dubbo.rpc.filter.trace.dapper.Span;

import java.util.Random;

/**
 * Created by aaa on 2017/7/24.
 */
public class Tracer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tracer.class);

    // 保存parentSpan
    private ThreadLocal<Span> parentSpan = new ThreadLocal<Span>();

    // 单例
    private static volatile Tracer tracer;

    private static final String SPAN_GENERATER_URL = "http://localhost:8889/id/span";
    private static final String TRACE_GENERATER_URL = "http://localhost:8889/id/trace";

    // 保证单例
    private Tracer() {

    }

    /**
     * double check 保证示例化的Tracer对象为单例
     *
     * @return
     */
    public static Tracer getInstance() {
        if (tracer == null) {
            synchronized (Tracer.class) {
                if (tracer == null) {
                    tracer = new Tracer();
                }
            }
        }
        return tracer;
    }

    /**
     * 是否初始化
     *
     * @return
     */
    public static boolean isInstanced() {
        return tracer != null;
    }

    public void removeParentSpan() {
        this.parentSpan.remove();
    }

    public Span getParentSpan() {
        return this.parentSpan.get();
    }

    public void setParentSpan(Span span) {
        this.parentSpan.set(span);
    }

    /**
     * 构造span，参数通过上游接口传递过来
     *
     * @param traceId
     * @param parentId
     * @param id
     * @param name
     * @param isSample
     * @param serviceId
     * @return
     */
    public Span buildSpan(Long traceId, Long parentId, Long id, String name, boolean isSample, String serviceId) {
        Span span = new Span();
        span.setId(id);
        span.setParentId(parentId);
        span.setName(name);
        span.setSample(isSample);
        span.setTraceId(traceId);
        span.setServiceId(serviceId);
        return span;
    }

    /**
     * 构造span, 生成id
     *
     * @param name
     * @param serviceId
     * @return
     */
    public Span newSpan(String name, String serviceId) {
        boolean s = true;
        Span span = new Span();
        span.setTraceId(s ? this.generateTraceId() : null);
        span.setId(s ? this.generateSpanId() : null);
        span.setName(name);
        span.setServiceId(serviceId);
        span.setSample(s);
//        if (s) {//应用名写入
//            BinaryAnnotation appname = new BinaryAnnotation();
//            appname.setKey("dubbo.applicationName");
//            appname.setValue(transfer.appName().getBytes());
//            appname.setType("string");
//            appname.setHost(endpoint);
//            span.addBinaryAnnotation(appname);
//        }
        return span;
    }

    /**
     * 构造EndPoint
     *
     * @return
     */
    public EndPoint buildEndPoint(String ip, Integer port, String serviceName) {
        return new EndPoint(ip, port, serviceName);
    }

    /**
     * 生成spanId
     * @TODO 生成全局唯一的id
     * @return
     */
    public Long generateSpanId() {
        return new Random(1).nextLong();
    }

    /**
     * 生成traceId
     * @TODO 生成全局唯一的id
     * @return
     */
    public Long generateTraceId() {
        return new Random(1).nextLong();
    }
}
