package com.alibaba.dubbo.rpc.filter.trace.dapper;


/**
 * Created by shield on 2017/7/24.
 */
public enum AnnotationType {

    CS(Constants.CS_KEY, Constants.CS_VALUE),

    SR(Constants.SR_KEY, Constants.SR_VALUE),

    SS(Constants.SS_KEY, Constants.SS_VALUE),

    CR(Constants.CR_KEY, Constants.CR_VALUE);

    private String symbol;

    private String label;

    private AnnotationType(String symbol, String label) {
        this.symbol = symbol;
        this.label = label;
    }

    public String label() {
        return label;
    }

    public String symbol() {
        return symbol;
    }

}
