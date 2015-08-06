package com.jackliu.httpclient18k.basic;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpRequestParameter {

	/**get 方法*/
	public static String METHOD_GET = "GET";
	/**post 方法*/
	public static String METHOD_POST = "POST";
	
	/**请求的url 比如http://g.alicdn.com/tbc/webww/1.1.7/tstart-min.css */
	private String url;
	
	private static String HTTP_VERSION = "HTTP/1.1";
	
	/**换行分隔符*/
	private static String sperator = "\r\n";
	
	/**GET,POST,DELETE,PUT等*/
	private String method;
	
	/**http header字段*/
	private Map<String, String> headerMap = new HashMap<String, String>();
	
	/**http body String*/
	private String HttpBodyString;
	
	/**bodyString charset*/
	private String bodyStringCharSet = "utf-8";
	
	private byte[] bodyBytes;
	
	/**host*/
	private String host;
	
	/**host port*/
	private int port = 80;
	
	/**相对uri*/
	private String uri;
	
	/**
	 * 构建http 请求报文
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public byte[] bulidRequestPackage() throws UnsupportedEncodingException{
		StringBuilder sb = new StringBuilder();
		handerlURL();
		sb.append(getMethod() + " " + getUri() + " " + HTTP_VERSION + sperator);
		handerHeaders(sb);
		byte[] httpPackage = null;
		byte[] headers = sb.toString().getBytes("utf-8");
		if(bodyBytes != null && bodyBytes.length > 0){
			httpPackage = new byte[headers.length + bodyBytes.length];
			System.arraycopy(headers, 0, httpPackage, 0, headers.length);
			System.arraycopy(bodyBytes, 0, httpPackage, headers.length, bodyBytes.length);
		}else {
			httpPackage = headers;
		}
		return httpPackage;
	}

	
	private void handerHeaders(StringBuilder sb) throws UnsupportedEncodingException {
		Set<Entry<String, String>> headers = ((Map<String,String>) headerMap).entrySet();
		for(Entry<String, String> header:headers){
			sb.append(header.getKey() + ": " + header.getValue() + sperator);
		}
		//虚拟主机
		sb.append("Host: " + getHost() + sperator);
		//Content-length
		if(null != getHttpBodyString() && getHttpBodyString().trim().length() > 1){
			bodyBytes = getHttpBodyString().getBytes(bodyStringCharSet);
			sb.append("Content-Length: " +  bodyBytes.length + sperator);
		}
		sb.append("User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:35.0) Gecko/20100101 Firefox/35.0" + sperator);
		//header结束
		sb.append(sperator);
	}


	public void handerlURL() throws UnsupportedEncodingException {
		if(getUrl() == null || getUrl().trim().length() < 1){
			throw new RuntimeException("url为空");
		}
		if(!getUrl().startsWith("http://")){
			throw new RuntimeException("url只支持http://开头");
		}
		String urlString = url;
		urlString = urlString.substring(7,urlString.length());
		int start = urlString.indexOf("/");
		String hostStr = urlString.substring(0, start);
		int portPosition = hostStr.indexOf(":");
		if(portPosition > 0){
			host = hostStr.substring(0, portPosition);
			setPort(Integer.parseInt(hostStr.substring(portPosition+1, hostStr.length())));
		}else {
			host = hostStr;
		}
		uri = urlString.substring(start, urlString.length());
	}
	
	public String getHost(){
		return this.host;
	}
	
	public String getUri(){
		return this.uri;
	}


	public void addHeader(String headerName,String headerValue){
		this.headerMap.put(headerName, headerValue);
	}
	
	public String getMethod() {
		return method;
	}


	public void setMethod(String method) {
		this.method = method;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}


	public String getHttpBodyString() {
		return HttpBodyString;
	}


	public void setHttpBodyString(String httpBodyString) {
		HttpBodyString = httpBodyString;
	}
	
}