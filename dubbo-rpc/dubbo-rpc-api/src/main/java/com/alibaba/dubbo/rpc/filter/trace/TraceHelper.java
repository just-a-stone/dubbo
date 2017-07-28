package com.alibaba.dubbo.rpc.filter.trace;

import brave.Tracing;
import brave.propagation.CurrentTraceContext;
import brave.sampler.Sampler;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.filter.trace.dapper.PercentageSampler;
import zipkin.Endpoint;

/**
 * Created by shield on 2017/7/27.
 */
public class TraceHelper {

    protected static final CurrentTraceContext currentTraceContext = new CurrentTraceContext.Default();

    private static volatile Tracing tracing;

    private TraceHelper() {

        Endpoint endpoint = Endpoint.builder()
                .serviceName(ReporterRegistry.getAppName())
                .ipv4(IPV42Integer(RpcContext.getContext().getLocalAddressString()))
                .port(RpcContext.getContext().getLocalPort())
                .build();

        //当前跟踪所有请求
        tracing = Tracing.newBuilder()
                .localServiceName("serviceNoUse")
                .currentTraceContext(currentTraceContext)
                .localEndpoint(endpoint)
                .sampler(createSampler())
                .reporter(ReporterRegistry.getReporter())
                .build();

    }

    /**
     * 默认动态调整百分比采样
     *
     * 设置了，则固定百分比采样
     * @return
     */
    private Sampler createSampler() {
        if (StringUtils.isBlank(ReporterRegistry.getPercent())) {
            return new PercentageSampler();
        } else {
            return Sampler.create(Float.parseFloat(ReporterRegistry.getPercent()));
        }
    }

    public static Tracing getInstance() {
        if (tracing == null) {
            synchronized (TraceHelper.class) {
                if (tracing == null) {
                    new TraceHelper();
                }
            }
        }
        return tracing;
    }

    public static int IPV42Integer(String strIPV4) {

        strIPV4 = strIPV4.split(":")[0];

        int iIPV4 = 0x00;
        String[] segArray = strIPV4.split("\\.");
        for (int i = 0; i < segArray.length; i++) {
            // seg <=255, so all the bits except rightest 8 ones are 0
            int seg = Integer.parseInt(segArray[i]);
            // left shift the current iIPV4 1-byte-distance, then "append" the
            // strIPV4Seg to rightest side
            iIPV4 = (iIPV4 << 8) | seg;
        }
        return iIPV4;
    }
}
