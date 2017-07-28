package com.alibaba.dubbo.rpc.filter.trace;

import zipkin.reporter.AsyncReporter;
import zipkin.reporter.Reporter;
import zipkin.reporter.Sender;
import zipkin.reporter.okhttp3.OkHttpSender;

/**
 * Created by shield on 2017/7/27.
 */
public class TraceInit {

    private String target;
    private String appName;
    private Reporter reporter;
    private String samplerPercent;

    public void init() throws Exception {
        Sender sender = OkHttpSender.create(target);
        reporter = AsyncReporter.create(sender);

        ReporterRegistry.setAppName(appName);
        ReporterRegistry.setReporter(reporter);
        ReporterRegistry.setPercent(samplerPercent);
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void destroy() {
        TraceHelper.getInstance().close();
    }

    public void setSamplerPercent(String samplerPercent) {
        this.samplerPercent = samplerPercent;
    }
}
