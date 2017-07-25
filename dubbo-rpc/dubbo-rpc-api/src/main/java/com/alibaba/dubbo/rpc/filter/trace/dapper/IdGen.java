package com.alibaba.dubbo.rpc.filter.trace.dapper;


public interface IdGen {

    /**
     * 生成下一个ID
     * @return
     */
    String nextId();
}
