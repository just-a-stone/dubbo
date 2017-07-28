package com.alibaba.dubbo.rpc.filter.trace;

import zipkin.reporter.Reporter;

/**
 * Created by shield on 2017/7/27.
 */
public class ReporterRegistry {

    protected static Reporter reporter;
    protected static String appName;

    protected static String percent;

    protected static String getPercent() {
        return percent;
    }

    protected static void setPercent(String percent) {
        ReporterRegistry.percent = percent;
    }

    protected static Reporter getReporter() {
        return reporter;
    }

    protected static String getAppName() {
        return appName;
    }

    protected static void setReporter(Reporter reporter) {
        ReporterRegistry.reporter = reporter;
    }

    protected static void setAppName(String appName) {
        ReporterRegistry.appName = appName;
    }
}
