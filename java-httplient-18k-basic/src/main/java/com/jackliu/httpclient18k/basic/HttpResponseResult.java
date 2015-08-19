package com.jackliu.httpclient18k.basic;


import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackliu.httpclient18k.basic.util.GzipDecompress;

public class HttpResponseResult {
	
	private static final Logger logger = LoggerFactory.getLogger(HttpResponseResult.class);

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
		return getHttpResponseBody(getResponseCharset());
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
					logger.error("",e);
				}
			}
			
			bodyStr = new String(bodyInBytes.array(),charset);
		}
		return this.bodyStr; 
	}
	
	public void printBody(){
		try {
			String bodyStr = new String(bodyInBytes.array(),"utf-8");
			if(logger.isDebugEnabled()){
				logger.debug("==========body string=============" + bodyStr);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("",e);
		}
	}
	
	public void printHttpHeader(){
		if(logger.isDebugEnabled()){
			logger.debug("============print header begin================");
		}
		for(String head:headers){
			if(logger.isDebugEnabled()){
				logger.debug(head);
			}
			int length = head.indexOf(":");
			//TODO http状态行解析
			if(length > 1){
				headerMap.put(head.substring(0,length), head.substring(length+1,head.length()).trim());
			}
			
		}
		if(logger.isDebugEnabled()){
			logger.debug("============print header end================");
		}
	}

	public void addHeader(String headerStr) {
		this.headers.add(headerStr);
	}
	
	/**
	 * 添加http response body数据到缓存byte
	 * @param chunkData
	 */
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
	
	/**
	 * 获得服务端应答中的charset，如果没有返回默认的编码utf-8
	 * TODO 没考虑vary
	 * @return
	 */
	private String getResponseCharset(){
		String defaultCharset = "utf-8";
		String contentType = headerMap.get("Content-Type");
		if(null == contentType || contentType.trim().length() < 1){
			return defaultCharset;
		}
		String charsetStr = "charset=";
		int startcharsetPosition = contentType.indexOf(charsetStr);
		if(startcharsetPosition < 1){
			return defaultCharset;
		}
		int endcharsetPosition = contentType.indexOf(";", startcharsetPosition);
		if(endcharsetPosition < 1){
			endcharsetPosition = contentType.length();
		}
		String charset = contentType.substring(startcharsetPosition+charsetStr.length(), endcharsetPosition);
		return charset;
	}
}
