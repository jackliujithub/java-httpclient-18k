package com.jackliu.httpclient18k.basic.base;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackliu.httpclient18k.basic.BasicHttpClient;
import com.jackliu.httpclient18k.basic.HttpRequestParameter;

/**
 * 连接池管理
 * @author Administrator
 *
 */
public class ConnectionPoolManager {

	private static final Logger logger = LoggerFactory.getLogger(BasicHttpClient.class);

	/**http 连接池
	 *TODO socket 封装，需要对http response 的keep-alive头中的时间进行处理 
	 * */
	Map<String, BlockingQueue<Socket>> httpConnectionPoll = new ConcurrentHashMap<String, BlockingQueue<Socket>>();
	
	/**https 连接池*/
	Map<String, BlockingQueue<Socket>> httpsConnectionPoll = new ConcurrentHashMap<String, BlockingQueue<Socket>>();
	
	public Socket getSocket(HttpRequestParameter requestParameter){
		Socket socket = getCacheSoket(requestParameter);
		if(socket != null){
			return socket;
		}
		
		try {
			if(requestParameter.isHttps()){
				socket = buildSslSocket(requestParameter);
			}else {
				socket = new Socket();
				socket.connect(new InetSocketAddress(requestParameter.getHost(),requestParameter.getPort()), requestParameter.getConnectionTimeOut());
			}
			socket.setTcpNoDelay(true);
			socket.setSoTimeout(requestParameter.getReadTimeOut());
		} catch (IOException e) {
			logger.error("",e);
		}
		return socket;
	}
	
	private Socket getCacheSoket(HttpRequestParameter requestParameter) {
		if(requestParameter.isHttps()){
			BlockingQueue<Socket> https = httpsConnectionPoll.get(requestParameter.getHost());
			if(null == https){
				https = new LinkedBlockingQueue<Socket>();
				httpsConnectionPoll.put(requestParameter.getHost(), https);
				return null;
			}
			return https.poll();
		}
		
		if(!requestParameter.isHttps()){
			BlockingQueue<Socket> httpPool = httpConnectionPoll.get(requestParameter.getHost());
			if(null == httpPool){
				httpPool = new LinkedBlockingQueue<Socket>();
				httpConnectionPoll.put(requestParameter.getHost(), httpPool);
				return null;
			}
			return httpPool.poll();
		}
		
		return null;
	}

	/**
	 * 构建ssl socket
	 * @param requestParameter
	 * @return
	 */
	public SSLSocket buildSslSocket(HttpRequestParameter requestParameter){
		SSLContext sslContenxt = requestParameter.getSslContext();
		if(null ==sslContenxt){
			throw new RuntimeException("https请求，请HttpRequestParameter设置sslContenxt");
		}
		SSLSocketFactory factory = sslContenxt.getSocketFactory();
		SSLSocket s = null;
		try {
			s = (SSLSocket) factory.createSocket(requestParameter.getHost(), requestParameter.getPort());
		} catch (UnknownHostException e) {
			logger.error("",e);
		} catch (IOException e) {
			logger.error("",e);
		} 
		return s;
	}
	
	public void releaseConnection(Socket socket,HttpRequestParameter requestParameter){
		if(requestParameter.isHttps()){
			BlockingQueue<Socket> https = httpsConnectionPoll.get(requestParameter.getHost());
			if(null == https){
				https = new LinkedBlockingQueue<Socket>();
				httpsConnectionPoll.put(requestParameter.getHost(), https);
			}
			https.add(socket);
		}
		
		if(!requestParameter.isHttps()){
			BlockingQueue<Socket> httpPool = httpConnectionPoll.get(requestParameter.getHost());
			if(null == httpPool){
				httpPool = new LinkedBlockingQueue<Socket>();
				httpConnectionPoll.put(requestParameter.getHost(), httpPool);
			}
			httpPool.add(socket);
		}
		
	}
	
}
