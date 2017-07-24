package com.alibaba.dubbo.rpc.filter.trace.dapper;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 按百分比采样
 * 0-100/s 100%
 * 101-500/s 50%
 * 501- /s 10%
 * <p>
 * Created by shield on 2017/7/24.
 */
public class PercentageSampler implements Sampler {

	private AtomicLong count = new AtomicLong();
	private int levelOne = 100;
	private int levelTwo = 500;
	private Long lastTime = -1L;

	public boolean isCollect() {
		boolean isSample = true;
		long n = count.incrementAndGet();
		if (System.currentTimeMillis() - lastTime < 1000) {
			if (n > levelOne && n < levelTwo) {
				if (n % 2 != 0)
					isSample = false;
			}
			if (n > levelTwo) {
				if (n % 10 != 0) {
					isSample = false;
				}
			}
		} else {
			count.getAndAdd(0);
			lastTime = System.currentTimeMillis();
		}

		return isSample;
	}
}
