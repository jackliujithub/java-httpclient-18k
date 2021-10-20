import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.Test;

import com.jackliu.httpclient18k.basic.AsyncHttpClient;
import com.jackliu.httpclient18k.basic.BasicHttpClient;
import com.jackliu.httpclient18k.basic.HttpRequestParameter;
import com.jackliu.httpclient18k.basic.HttpResponseResult;
import com.jackliu.httpclient18k.basic.async.CallBack;
import com.jackliu.httpclient18k.basic.async.IContext;
import com.jackliu.httpclient18k.basic.util.NameValuePair;

public class TestBasicHttpClient {

	/**
	 * post 同步方法测试
	 */
	@Test
	public void testSyncPost() {
		BasicHttpClient httpClient = new BasicHttpClient();
		HttpRequestParameter requestParameter = buildPostRquestParameter();
		try {
			HttpResponseResult response = httpClient.execute(requestParameter);
			// 获得结果
			System.out.println("========result:==========="+ response.getHttpResponseBody());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			httpClient.releaseConnection();// 如果是长连接，可以重用BasicHttpClient
		}

	}

	/***
	 * get方法测试 + 长连接请求测试
	 */
	@Test
	public void testSyncGet() {
		long start = System.currentTimeMillis();
		BasicHttpClient httpClient = new BasicHttpClient();
		HttpRequestParameter requestParameter = new HttpRequestParameter();
		requestParameter.setMethod(HttpRequestParameter.METHOD_GET);
		// requestParameter.setUrl("http://g.alicdn.com/tbc/webww/1.1.7/tstart-min.css");
		requestParameter.setUrl("http://www.iciba.com/index.php");
		try {
			//告诉服务端，客户端支持gzip解压
			// requestParameter.addHeader("Accept-Encoding", "gzip, deflate");
			//添加请求参数
			List<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();
			requestParameter.setParams(nameValuePairs);
			NameValuePair nameValuePair = new NameValuePair("a", "suggestnew");
			nameValuePairs.add(nameValuePair);
			nameValuePair = new NameValuePair("s", "%E5%BC%82%E6%AD%A5");
			nameValuePairs.add(nameValuePair);
			HttpResponseResult response = httpClient.execute(requestParameter);
			httpClient.releaseConnection();
			// 获得结果
			System.out.println("========result:==========="+ response.getHttpResponseBody());

			/** http 长连接测试，重用httpClient**/
			response = httpClient.execute(requestParameter);
			httpClient.releaseConnection();
			// 获得结果
			System.out.println("========result:==========="+ response.getHttpResponseBody());
			 
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
		}
		System.out.println("cast:" + (System.currentTimeMillis() - start));
	}

	/**
	 * http 异步请求
	 */
	@Test
	public void testAsynPost() {
		AsyncHttpClient httpClient = new AsyncHttpClient();
		HttpRequestParameter requestParameter = buildPostRquestParameter();
		try {
			httpClient.AsyncExecute(requestParameter, new CallBack() {
				@Override
				public void onBeforeSend(IContext context) {
					System.out.println("=====onBeforeSend======");
				}

				@Override
				public void onSuccess(IContext context) {
					System.out.println("=====onSuccess======");
					HttpResponseResult response = context
							.getHttpResponseResult();
					try {
						System.out.println("=====result======"
								+ response.getHttpResponseBody());
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(IContext context) {
					System.out.println("======onError==========");
				}

				@Override
				public void onComplete(IContext context) {
					System.out.println("========onComplete=========");
				}

			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private HttpRequestParameter buildPostRquestParameter() {
		HttpRequestParameter requestParameter = new HttpRequestParameter();
		// 设置请求方法
		requestParameter.setMethod("POST");
		// 设置url
		requestParameter.setUrl("http://192.168.192.117:8081/token2.action");
		// 添加http 头部
		requestParameter.addHeader("Content-Type","application/x-www-form-urlencoded");
		List<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();
		requestParameter.setParams(nameValuePairs);
		//添加参数
		NameValuePair nameValuePair = new NameValuePair("accessKey",
				"a188caaf009839ba200bb55bb8fa38407a595c2a");
		nameValuePairs.add(nameValuePair);
		nameValuePair = new NameValuePair("secretKey",
				"e685c8d1daa7e4dec8821a3df41c0b34a56db779");
		nameValuePairs.add(nameValuePair);
		nameValuePair = new NameValuePair("bodyStr", "%7B%7D");
		nameValuePairs.add(nameValuePair);
		nameValuePair = new NameValuePair("plat", "Android");
		nameValuePairs.add(nameValuePair);
		nameValuePair = new NameValuePair("platVersion", "4.2");
		nameValuePairs.add(nameValuePair);
		nameValuePair = new NameValuePair("appVersion", "1.2.0");
		nameValuePairs.add(nameValuePair);
		nameValuePair = new NameValuePair("hardPlatform", "Iphone6%2C1");
		nameValuePairs.add(nameValuePair);
		nameValuePair = new NameValuePair("utcDate",
				"2015-08-14T19%3A11%3A23.684Z");
		nameValuePairs.add(nameValuePair);
		nameValuePair = new NameValuePair("method", "post");
		nameValuePairs.add(nameValuePair);
		return requestParameter;
	}

	private HttpRequestParameter buildGetRquestParameter() {
		HttpRequestParameter requestParameter = new HttpRequestParameter();
		// 设置请求方法
		requestParameter.setMethod(HttpRequestParameter.METHOD_GET);
		// 设置url
		requestParameter.setUrl("https://www.baidu.com/");
		return requestParameter;
	}
	
	public static void main(String[] args) throws Exception {
		// %E5%BC%82%E6%AD%A5
		System.out.println(URLEncoder.encode("异步", "utf-8"));
	}

	/**
	 * http 并行请求
	 */
	@Test
	public void testParaAsynPostExecute() {
		HttpRequestParameter requestParameter = buildPostRquestParameter();
		// { 同步调用begin
		BasicHttpClient synHttpClient1 = new BasicHttpClient();
		BasicHttpClient synHttpClient2 = new BasicHttpClient();
		long start = System.currentTimeMillis();
		try {
			HttpResponseResult result1 = synHttpClient1.execute(requestParameter);
			HttpResponseResult result2 = synHttpClient2.execute(requestParameter);

			System.out.println("context1:" + result1.getHttpResponseBody());
			System.out.println("context2:" + result2.getHttpResponseBody());
			System.out.println("cast:" + (System.currentTimeMillis() - start));

		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally{
			if(null != synHttpClient1){
				synHttpClient1.releaseConnection();
			}
			if(null != synHttpClient2){
				synHttpClient2.releaseConnection();
			}
		}
		// { 同步调用end

		// 并发调用begin
		AsyncHttpClient asynhttpClient1 = new AsyncHttpClient();
		AsyncHttpClient asynhttpClient2 = new AsyncHttpClient();
		start = System.currentTimeMillis();

		Future<IContext> request1 = asynhttpClient1
				.AsyncExecute(requestParameter);
		Future<IContext> request2 = asynhttpClient2
				.AsyncExecute(requestParameter);
		try {
			IContext contenxt1 = request1.get(5, TimeUnit.SECONDS);
			IContext contenxt2 = request2.get(5, TimeUnit.SECONDS);
			System.out.println("context1:"
					+ contenxt1.getHttpResponseResult().getHttpResponseBody());
			System.out.println("context2:"
					+ contenxt2.getHttpResponseResult().getHttpResponseBody());
			System.out.println("cast:" + (System.currentTimeMillis() - start));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 并发调用end
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * https 请求
	 */
	@Test
	public void testHttpsGet() {
		BasicHttpClient synHttpClient1 = null;
		try {
			SSLContext ctx = SSLContext.getInstance("SSL");
			// Implementation of a trust manager for X509 certificates
			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			HttpRequestParameter  requestParameter = buildGetRquestParameter();
			requestParameter.setSslContext(ctx);
			synHttpClient1 = new BasicHttpClient();
			HttpResponseResult result1 = synHttpClient1.execute(requestParameter);
			System.out.println("========result==========" + result1.getHttpResponseBody());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(null != synHttpClient1){
				synHttpClient1.releaseConnection();
			}
		}
	}
	
	
	private void testSyncGetNoRealease() {
		long start = System.currentTimeMillis();
		BasicHttpClient httpClient = new BasicHttpClient();
		HttpRequestParameter requestParameter = new HttpRequestParameter();
		requestParameter.setMethod(HttpRequestParameter.METHOD_GET);
		// requestParameter.setUrl("http://g.alicdn.com/tbc/webww/1.1.7/tstart-min.css");
		requestParameter.setUrl("http://www.iciba.com/index.php");
		try {
			//告诉服务端，客户端支持gzip解压
			// requestParameter.addHeader("Accept-Encoding", "gzip, deflate");
			//添加请求参数
			List<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();
			requestParameter.setParams(nameValuePairs);
			NameValuePair nameValuePair = new NameValuePair("a", "suggestnew");
			nameValuePairs.add(nameValuePair);
			nameValuePair = new NameValuePair("s", "%E5%BC%82%E6%AD%A5");
			nameValuePairs.add(nameValuePair);
			HttpResponseResult response = httpClient.execute(requestParameter);
			// 获得结果
			System.out.println("========result:==========="+ response.getHttpResponseBody());
			//不释放连接
			//httpClient.releaseConnection();
			
			 
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
		System.out.println("cast:" + (System.currentTimeMillis() - start));
	}
	
	
	/***
	 * get方法测试 + 长连接请求测试
	 */
	@Test
	public void testConnectionPoolsSyncGet(){
		testSyncGetNoRealease();
		testSyncGetNoRealease();
	}
}
