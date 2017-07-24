package com.alibaba.dubbo.rpc.filter.trace.dapper;

/**
 * Created by shield on 2017/7/24.
 */
public class Constants {

    // annotation type相关
    public static final String CS_KEY = "cs";
    public static final String CS_VALUE = "client send";
    public static final String SR_KEY = "sr";
    public static final String SR_VALUE = "server receive";
    public static final String SS_KEY = "ss";
    public static final String SS_VALUE = "server send";
    public static final String CR_KEY = "cr";
    public static final String CR_VALUE = "client receive";

    // RpcInvocation attachment和捕捉annotation中的exception相关
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    public static final String PARENT_ID = "parentId";
    public static final String SAMPLE = "isSample";

    public static final String DUBBO_EXCEPTION = "dubbo.exception";
    public static final String EXCEPTION = "exception";
    public static final String DUBBO_TIMEPUTEXCEPTION = "dubbo.timeoutexception";
}
