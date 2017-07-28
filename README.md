
## xml配置

```xml
<bean name="traceInit" class="com.alibaba.dubbo.rpc.filter.trace.TraceInit" init-method="init" destroy-method="destroy">
        <property name="appName" value="paycenter"/>
        <property name="target" value="http://127.0.0.1:9411/api/v1/spans"/>
</bean>
```
