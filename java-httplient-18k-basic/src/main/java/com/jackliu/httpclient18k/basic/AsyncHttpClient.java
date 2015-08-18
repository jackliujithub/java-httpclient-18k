package com.jackliu.httpclient18k.basic;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.jackliu.httpclient18k.basic.async.CallBack;
import com.jackliu.httpclient18k.basic.async.IContext;
import com.jackliu.httpclient18k.basic.async.IContextInner;
import com.jackliu.httpclient18k.basic.async.impl.DefaultContext;
import com.jackliu.httpclient18k.basic.async.impl.DefaultContextInner;
import com.jackliu.httpclient18k.basic.util.NamedThreadFactory;
/**
 * http 异步执行入口
 * @author Administrator
 *
 */
public class AsyncHttpClient {

	private static int corePoolSize = 10;
	
	private static int maximumPoolSize = 20;
	
	private static int keepAliveTime = 5000;
	
	private static TimeUnit unit = TimeUnit.MILLISECONDS;
	
	private static BlockingQueue workQueue = new ArrayBlockingQueue(100);
	
	private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,new NamedThreadFactory("http-18k-", true));
	
	
	public void AsyncExecute(final HttpRequestParameter requestParameter,final CallBack callBack){
		threadPoolExecutor.execute(new Runnable() {
			@Override
			public void run() {
				IContextInner contextInner = new DefaultContextInner();
				IContext context = new DefaultContext(contextInner);
				BasicHttpClient httpClient = null;
				try {
					contextInner.setHttpRequestParameter(requestParameter);
					callBack.onBeforeSend(context);
					httpClient = new BasicHttpClient();
					HttpResponseResult  responseResult =  httpClient.execute(requestParameter);
					contextInner.setHttpResponseResult(responseResult);
					callBack.onSuccess(context);
				} catch (Exception e){
					e.printStackTrace();
					callBack.onError(context);
				}finally{
					if(null != httpClient){
						httpClient.close();
					}
					callBack.onComplete(context);
				}
			}
		});
	}
	
	
	
}
