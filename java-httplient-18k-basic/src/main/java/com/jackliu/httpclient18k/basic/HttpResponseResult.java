package com.jackliu.httpclient18k.basic;


import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jackliu.httpclient18k.basic.util.GzipDecompress;

public class HttpResponseResult {
	
	/**http response header 字段*/
	private Map<String, String> headerMap = new HashMap<String, String>();
	
	/**头部信息的list*/
	private List<String> headers = new ArrayList<String>();
	
	/**已经解析好的body内容大小*/
	private ByteBuffer bodyInBytes = null;
	
	private String bodyStr = null;
	
	/**内容压缩*/
	private static final String GZIP_ENCODING = "gzip";
	
	public String getHeader(String key){
		return getHeaders().get(key);
	}

	public Map<String, String> getHeaders() {
		return headerMap;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headerMap = headers;
	}

	public ByteBuffer getBodyInBytes() {
		return bodyInBytes;
	}

	public void setBodyInBytes(ByteBuffer bodyInBytes) {
		this.bodyInBytes = bodyInBytes;
	}
	
	public String getHttpResponseBody() throws UnsupportedEncodingException{
		return getHttpResponseBody("utf-8");
	}
	
	
	public String getHttpResponseBody(String charset) throws UnsupportedEncodingException{
		if(bodyStr == null){
			String ContentEncoding = getHeader("Content-Encoding");
			if(ContentEncoding != null && GZIP_ENCODING.equals(ContentEncoding)){
				try {
					byte[] decompressBody = GzipDecompress.decompress(bodyInBytes.array());
					bodyStr = new String(decompressBody,charset);
					return bodyStr;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			bodyStr = new String(bodyInBytes.array(),charset);
		}
		return this.bodyStr; 
	}
	
	public void printBody(){
		try {
			String bodyStr = new String(bodyInBytes.array(),"utf-8");
			System.out.println("==========body string=============" + bodyStr);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void printHttpHeader(){
		System.out.println("============print header begin:");
		for(String head:headers){
			System.out.println(head);
			int length = head.indexOf(":");
			//TODO http状态行解析
			if(length > 1){
				headerMap.put(head.substring(0,length), head.substring(length+1,head.length()).trim());
			}
			
		}
		System.out.println("============print header end:");
	}

	public void addHeader(String headerStr) {
		this.headers.add(headerStr);
	}

	public void addBodyBytes(byte[] chunkData) {
		if(bodyInBytes == null){
			bodyInBytes = ByteBuffer.wrap(chunkData);
			return;
		}
		ByteBuffer newBodyInBytes = ByteBuffer.allocate(bodyInBytes.capacity() + chunkData.length);
		newBodyInBytes.put(bodyInBytes);
		newBodyInBytes.put(chunkData);
		bodyInBytes = newBodyInBytes;
		
	}
}
