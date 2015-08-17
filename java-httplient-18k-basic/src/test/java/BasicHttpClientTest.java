import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.jackliu.httpclient18k.basic.BasicHttpClient;
import com.jackliu.httpclient18k.basic.HttpRequestParameter;
import com.jackliu.httpclient18k.basic.HttpResponseResult;
import com.jackliu.httpclient18k.basic.util.NameValuePair;


public class BasicHttpClientTest {

	/**
	 * post 方法测试
	 */
	@Test
	public void testPostExecte(){
		BasicHttpClient httpClient = new BasicHttpClient();
		HttpRequestParameter requestParameter = new HttpRequestParameter();
		//设置请求方法
		requestParameter.setMethod("POST");
		//设置url
		requestParameter.setUrl("http://192.168.192.117:8081/token2.action");
		//添加http 头部
		requestParameter.addHeader("Content-Type", "application/x-www-form-urlencoded");
		//post 方法添加http body
		//String body = "accessKey=a188caaf009839ba200bb55bb8fa38407a595c2a&secretKey=e685c8d1daa7e4dec8821a3df41c0b34a56db779&bodyStr=%7B%7D&plat=Android&platVersion=4.2&appVersion=1.2.0&hardPlatform=Iphone6%2C1&utcDate=2015-08-14T10%3A49%3A23.684Z&method=post";
		//requestParameter.setHttpBodyString(body);
		List<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();
		requestParameter.setParams(nameValuePairs);
		NameValuePair nameValuePair = new NameValuePair("accessKey","a188caaf009839ba200bb55bb8fa38407a595c2a");
		nameValuePairs.add(nameValuePair);
		nameValuePair = new NameValuePair("secretKey","e685c8d1daa7e4dec8821a3df41c0b34a56db779");
		nameValuePairs.add(nameValuePair);
		nameValuePair = new NameValuePair("bodyStr","%7B%7D");
		nameValuePairs.add(nameValuePair);
		nameValuePair = new NameValuePair("plat","Android");
		nameValuePairs.add(nameValuePair);
		//platVersion=4.2&appVersion=1.2.0&hardPlatform=Iphone6%2C1&utcDate=2015-08-14T10%3A49%3A23.684Z&method=post";
		nameValuePair = new NameValuePair("platVersion","4.2");
		nameValuePairs.add(nameValuePair);
		nameValuePair = new NameValuePair("appVersion","1.2.0");
		nameValuePairs.add(nameValuePair);
		nameValuePair = new NameValuePair("hardPlatform","Iphone6%2C1");
		nameValuePairs.add(nameValuePair);
		nameValuePair = new NameValuePair("utcDate","2015-08-14T19%3A11%3A23.684Z");
		nameValuePairs.add(nameValuePair);
		nameValuePair = new NameValuePair("method","post");
		nameValuePairs.add(nameValuePair);
		try {
			HttpResponseResult  response = httpClient.execute(requestParameter);
			//获得结果
			System.out.println("========result:===========" + response.getHttpResponseBody());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			httpClient.close();//如果是长连接，可以重用BasicHttpClient
		}
	}
	
	/***
	 * get方法测试  + 长连接请求测试
	 */
	@Test
	public void testGetExecte(){
		long start = System.currentTimeMillis();
		BasicHttpClient httpClient = new BasicHttpClient();
		HttpRequestParameter requestParameter = new HttpRequestParameter();
		requestParameter.setMethod(HttpRequestParameter.METHOD_GET);
		requestParameter.setUrl("http://g.alicdn.com/tbc/webww/1.1.7/tstart-min.css");
		try {
			//requestParameter.addHeader("Accept-Encoding", "gzip, deflate");
			HttpResponseResult  response = httpClient.execute(requestParameter);
			//获得结果
			System.out.println("========result:===========" + response.getHttpResponseBody());
			
			/**http 长连接测试
			response = httpClient.execute(requestParameter);
			//获得结果
			System.out.println("========result:===========" + response.getHttpResponseBody());
			**/
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			httpClient.close();
		}
		System.out.println("cast:" + (System.currentTimeMillis() - start));
	}
}
