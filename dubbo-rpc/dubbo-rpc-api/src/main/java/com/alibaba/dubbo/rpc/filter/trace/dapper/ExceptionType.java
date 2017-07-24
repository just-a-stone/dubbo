package com.alibaba.dubbo.rpc.filter.trace.dapper;

/**
 * Created by shield on 2017/7/24.
 */
public enum ExceptionType {

	EXCEPTION(Constants.DUBBO_EXCEPTION, Constants.EXCEPTION),
	TIMEOUTEXCEPTION(Constants.DUBBO_TIMEPUTEXCEPTION, Constants.EXCEPTION);

	private String symbol;

	private String label;

	private ExceptionType(String symbol, String label) {
		this.symbol = symbol;
		this.label = label;
	}

	public String symbol() {
		return this.symbol;
	}

	public String label() {
		return this.label;
	}
}
