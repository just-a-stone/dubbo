package com.alibaba.dubbo.rpc.filter.trace.dapper;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.rpc.RpcInvocation;

/**
 * Created by shield on 2017/7/24.
 */
public class AttachmentUtil {

	/**
	 * 附带数据转long
	 *
	 * @param invocation
	 * @param key
	 * @return
	 */
	public static String getAttachment(RpcInvocation invocation, String key) {
		String value = invocation.getAttachment(key);
		if (StringUtils.isBlank(value)) {
			return null;
		}
		return value;
	}

	/**
	 * 附带数据转boolean
	 *
	 * @param invocation
	 * @param key
	 * @return
	 */
	public static Boolean getAttachmentBoolean(RpcInvocation invocation, String key) {
		String value = invocation.getAttachment(key);
		if (StringUtils.isBlank(value)) {
			return false;
		}
		return Boolean.valueOf(value);
	}
}
