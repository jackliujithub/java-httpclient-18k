package com.jackliu.httpclient18k.basic;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackliu.httpclient18k.basic.async.CallBack;
import com.jackliu.httpclient18k.basic.async.IContext;
import com.jackliu.httpclient18k.basic.async.IContextInner;
import com.jackliu.httpclient18k.basic.async.impl.DefaultContext;
import com.jackliu.httpclient18k.basic.async.impl.DefaultContextInner;
/**
 * http 异步执行入口
 * @author Administrator
 *
 */
public class AsyncHttpClient {
	private static final Logger logger = LoggerFactory.getLogger(BasicHttpClient.class);
	
	private static int corePoolSize = 10;
	
	private static int maximumPoolSize = 20;
	
	private static int keepAliveTime = 5000;
	
	private static TimeUnit unit = TimeUnit.MILLISECONDS;
	
	private static BlockingQueue workQueue = new ArrayBlockingQueue(100);
	
	private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,new NamedThreadFactory("http-18k-", true));
	
	/**
	 * 异步带回调的http请求方法
	 * @param requestParameter
	 * @param callBack
	 */
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
					logger.error("",e);
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
	
	/**
	 * 调用此方法，会立即返回Future
	 * @param requestParameter
	 * @param callBack
	 * @param timeOut
	 */
	public Future<IContext> AsyncExecute(final HttpRequestParameter requestParameter){
		
		 Future<IContext> future = threadPoolExecutor.submit(new Callable<IContext>() {
			@Override
			public IContext call() throws Exception {
				IContextInner contextInner = new DefaultContextInner();
				IContext context = new DefaultContext(contextInner);
				BasicHttpClient httpClient = null;
				try {
					contextInner.setHttpRequestParameter(requestParameter);
					httpClient = new BasicHttpClient();
					HttpResponseResult  responseResult =  httpClient.execute(requestParameter);
					contextInner.setHttpResponseResult(responseResult);
					return context;
				} finally{
					if(null != httpClient){
						httpClient.close();
					}
				}
			}
		});
		 return future;
	}
}


class NamedThreadFactory implements ThreadFactory
{
	private static final AtomicInteger POOL_SEQ = new AtomicInteger(1);

	private final AtomicInteger mThreadNum = new AtomicInteger(1);

	private final String mPrefix;

	private final boolean mDaemo;

	private final ThreadGroup mGroup;

	public NamedThreadFactory()
	{
		this("pool-" + POOL_SEQ.getAndIncrement(),false);
	}

	public NamedThreadFactory(String prefix)
	{
		this(prefix,false);
	}

	public NamedThreadFactory(String prefix,boolean daemo)
	{
		mPrefix = prefix + "-thread-";
		mDaemo = daemo;
        SecurityManager s = System.getSecurityManager();
        mGroup = ( s == null ) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
	}

	public Thread newThread(Runnable runnable)
	{
		String name = mPrefix + mThreadNum.getAndIncrement();
        Thread ret = new Thread(mGroup,runnable,name,0);
        ret.setDaemon(mDaemo);
        return ret;
	}

	public ThreadGroup getThreadGroup()
	{
		return mGroup;
	}
}