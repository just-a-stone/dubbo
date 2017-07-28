package com.alibaba.dubbo.rpc.filter.trace;

import brave.Span;

/**
 * Created by shield on 2017/7/28.
 */
public class BusiLogHelper {

    //在日志跟踪中记录业务信息
    public static void log(String key, String value) {
        Span span = TraceHelper.getInstance().tracer().currentSpan();
        if (null != span) {
            span.tag(key, value);
        }
    }
}
