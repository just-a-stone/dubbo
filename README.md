## 使用分支rpc-zipkin

## 设计方案
参考dapper数据结构设计，通过切入dubbo的filter，采集信息发送zipkin分析展示

## xml配置

```xml
<bean name="traceInit" class="com.alibaba.dubbo.rpc.filter.trace.TraceInit" init-method="init" destroy-method="destroy">
        <property name="appName" value="paycenter"/>
        <property name="target" value="http://127.0.0.1:9411/api/v1/spans"/>
</bean>
```
