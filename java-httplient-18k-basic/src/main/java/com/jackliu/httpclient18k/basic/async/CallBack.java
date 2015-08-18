package com.jackliu.httpclient18k.basic.async;

import com.jackliu.httpclient18k.basic.HttpRequestParameter;
import com.jackliu.httpclient18k.basic.HttpResponseResult;

/**
 * 异步调用回调函数
 * @author Administrator
 *
 */
public interface CallBack {

	/**
	 * 发送请求前调用
	 */
	public void onBeforeSend(IContext context);
	
	
	public void onSuccess(IContext context);
	
	/**
	 * 请求失败时调用此函数
	 */
	public void onError(IContext context);
		
	/**
	 * 请求完成后回调函数 (请求成功或失败之后均调用)
	 */
	public void onComplete(IContext context);
}
