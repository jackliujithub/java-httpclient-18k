package com.jackliu.httpclient18k.basic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jackliu.httpclient18k.basic.async.CallBack;
import com.jackliu.httpclient18k.basic.async.IContext;
import com.jackliu.httpclient18k.basic.async.IContextInner;
import com.jackliu.httpclient18k.basic.async.impl.DefaultContext;
import com.jackliu.httpclient18k.basic.async.impl.DefaultContextInner;


/**
 * http client 同步方法入口
 * @author Administrator
 *
 */

public class BasicHttpClient {
	private static final Logger logger = LoggerFactory.getLogger(BasicHttpClient.class);
	/**回车*/
	private static final byte CR = 13;
	
	/**换行*/
	private static final byte LF = 10;
	
	/**服务端应答*/
	private InputStream inputStream;
	
	private Socket socket;
	
	private OutputStream outputStream;
	
	/**上一次解析的两个字符是CRLF，true表示是，false表示不是*/
	private boolean lastCRLF = false;
	
	/**body 解析完了吗？*/
	private boolean bodyParseOver = false;
		
	/**body中chuck 大小*/
	private int lastChunkLength = -1;
	
	/**response ContentLength 大小*/
	private int contentLength = -1;
	
	/**http 应答结果*/
	private HttpResponseResult responseResult;
	
	/**
	 * 开始http 应答包解析
	 */
	public void parseHttpResponsePackage(){
		beginParse();
	}
	
	public void beginParse(){
		byte[] buffer = new byte[1024];
		try {
			int length = inputStream.read(buffer);
			if(length < 1){
				throw new RuntimeException("没有读到服务端应答");
			}
			parseHeader(buffer,0,length);
		} catch (IOException e) {
			logger.error("",e);
		}
		
	} 
	
	/**
	 * http 头部解析
	 * @param buffer
	 * @param i
	 * @param length
	 */
	public void parseHeader(byte[] buffer,int start,int length){
		try {
			byte resByte = 0;
			int i = start;
			for(;i<length;i++){
				resByte = buffer[i];
				if(resByte == CR){
					//CR是最后一个字节，从inputStream再读字节
					if(i+1 == length){
						byte[] newBuffer = readBytes(buffer);
						parseHeader(newBuffer,0,newBuffer.length);
						return ;
					}
					//CRLF结尾，表示解析到了一个头部，继续回归解析
					if(i+1 <= length && buffer[i+1] == LF){
						if(lastCRLF == true){//header解析完了
							this.responseResult.printHttpHeader();
							byte[] remainByte = null;
							if(i + 1 < (length-1)){//剩余未解析的字节数
								remainByte = new byte[length-(i+2)];
								System.arraycopy(buffer,i+2,remainByte,0, (length -1) -(i+1));
								//继续剩余字节的解析
								beginBodyParse(remainByte,0,remainByte.length);	
								return;
							}
							byte[] newBuffer = readBytes(remainByte);
							beginBodyParse(newBuffer,0,newBuffer.length);
							return;
						}
						
						lastCRLF = true;
						byte[] currentByte = new byte[i];//前面的字节需要保存
						System.arraycopy(buffer, 0,currentByte , 0, i);
						this.responseResult.addHeader(new String(currentByte,"utf-8"));
						byte[] remainByte = null;
						if(i + 1 < (length-1)){//剩余未解析的字节数
							remainByte = new byte[length-(i+2)];
							System.arraycopy(buffer,i+2,remainByte,0, (length -1) -(i+1));
							//继续剩余字节的解析
							parseHeader(remainByte,0,remainByte.length);	
							return;
						}
						byte[] newBuffer = readBytes(remainByte);
						parseHeader(newBuffer,0,newBuffer.length);
						return;
					}
					
				}else {
					lastCRLF = false;
				}
			}
			
			byte[] newBuffer = readBytes(buffer);
			parseHeader(newBuffer,0,newBuffer.length);
			
		} catch (Exception e) {
			logger.error("",e);
		}
		
	}
	
	/**
	 *开始body的解析 
	 * @param remainByte
	 * @param start
	 * @param length
	 */
	public void beginBodyParse(byte[] remainByte, int start, int length) {
		String isTransFerEncode = this.responseResult.getHeader("Transfer-Encoding");
		String contentLengthString = this.responseResult.getHeader("Content-Length");
		if(isTransFerEncode != null && isTransFerEncode.trim().length() > 0){//transferEncoding解析方式
			parseHttpBodyTransferEncoding(remainByte, start, length);
		}else if(contentLengthString != null && contentLengthString.trim().length() > 0){//content-length解析方式
			this.contentLength  = Integer.parseInt(contentLengthString);
			parseHttpBodyContenLength(remainByte, start, length);
		}
	}

	private byte[] readBytes(byte[] buffer){
		byte[] newBuffer = new byte[1024];
		byte[] result = null;
		try {
			if(buffer == null){
				int length = this.inputStream.read(newBuffer);
				result = new byte[length];
				System.arraycopy(newBuffer, 0, result, 0, length);
				return result;
			}
			int length = this.inputStream.read(newBuffer);
			result = new byte[buffer.length + length];
			System.arraycopy(buffer, 0, result, 0, buffer.length);
			System.arraycopy(newBuffer, 0, result, buffer.length, length);
		} catch (IOException e) {
			logger.error("",e);
		}
		return result;
	}
	
	/**
	 * transfer应答解析
	 * @param inputStream
	 */
	public void parseHttpBodyTransferEncoding(byte[] buffer,int start,int length){
		try {
			byte resByte = 0;
			//获得chunk中的byte字节，拿到后面lastChunkLength长度的字节大小
			if(lastChunkLength > 0){
				//取buffer中lastChunkLength大小的数据
				if(length > (lastChunkLength+2)){//最后的CRCL一起计算
					byte[] chunkData = new byte[lastChunkLength];
					//处理chunkData
					System.arraycopy(buffer,0,chunkData,0, lastChunkLength);
					cacheChunkData(chunkData);
					byte[] remainByte = null;
					if(length > (lastChunkLength+2)){
						remainByte = new byte[length - lastChunkLength-2];
						System.arraycopy(buffer,lastChunkLength,remainByte,0, length - lastChunkLength-2);
						//继续剩余字节的解析
						lastChunkLength = -1;
						parseHttpBodyTransferEncoding(remainByte,0,remainByte.length);	
						return;
					}else{
						//从inputStream中读取字节，放入缓存继续执行
						byte[] newBuffer = readBytes(remainByte);
						lastChunkLength = -1;
						parseHttpBodyTransferEncoding(newBuffer,0,newBuffer.length);	
						return;
					}
					
				}else{
					//当前缓存中数据不够，从inputStream中读取数据到缓存中
					byte[] newBuffer = readBytes(buffer);
					parseHttpBodyTransferEncoding(newBuffer,0,newBuffer.length);
				}
				
				return ;
			}
			//body结束处理
			if(lastChunkLength ==0){
				if(length >=2){ //2个CRCL
					bodyParseOver = true;
					return;
				}else{
					byte[] newBuffer = readBytes(buffer);
					parseHttpBodyTransferEncoding(newBuffer,0,newBuffer.length);
					return ;
				}
			}
			
			//获得body 中chunk的大小
			for(;start<length;start++){
				resByte = buffer[start];
				if(resByte == CR){
					// CR是最后一个字节，回归解析
					if(start+1 == length){
						byte[] newBuffer = readBytes(buffer);
						parseHttpBodyTransferEncoding(newBuffer,0,newBuffer.length);
						return ;
					}
					//CRLF结尾，表示解析到尾部，继续回归解析
					if(start+1 <= length && buffer[start+1] == LF){
						if(lastCRLF == true){
							bodyParseOver = true;
							//printBodyInfo();
						}
						
						lastCRLF = true;
						byte[] currentByte = new byte[start];//前面的字节需要保存
						System.arraycopy(buffer, 0,currentByte , 0, start);
						handTrunkLength(currentByte);
						byte[] remainByte = null;
						if(start + 1 < (length-1)){//剩余未解析的字节数
							remainByte = new byte[length-(start+2)];
							System.arraycopy(buffer,start+2,remainByte,0, (length -1) -(start+1));//跳过CRCL
							//继续剩余字节的解析
							parseHttpBodyTransferEncoding(remainByte,0,remainByte.length);	
							return;
						}
						//如果已经是最有一个chunk了，直接返回吧
						if(lastChunkLength == 0 && length - 1 ==2){
							return;
						}
						byte[] newBuffer = readBytes(remainByte);
						parseHttpBodyTransferEncoding(newBuffer,0,newBuffer.length);
						return;
					}
					
				}else {
					lastCRLF = false;
				}
			}
			
			byte[] newBuffer = readBytes(buffer);
			parseHttpBodyTransferEncoding(newBuffer,0,newBuffer.length);
			return ;
		} catch (Exception e) {
			logger.error("",e);
		}
	}
	
	/**
	 * 把chunkData中的数据缓存到内容中
	 * TODO 优化，把chunkData 放入List中，最后一起合并
	 * @param chunkData
	 */
	private void cacheChunkData(byte[] chunkData) {
		this.responseResult.addBodyBytes(chunkData);
	}

	/**
	 *chunk 块大小 16进制的数字转换为十进制
	 * @param currentByte
	 */
	private void handTrunkLength(byte[] currentByte) {
		try {
			if(null == currentByte || currentByte.length < 1){
				return;
			}
			String hexString = new String(currentByte,"utf-8");
			lastChunkLength = Integer.parseInt(hexString, 16);
		} catch (Exception e) {
			logger.error("",e);
		}
		
	}

	/**
	 * content-lengt 应答解析
	 * @param inputStream
	 */
	public void parseHttpBodyContenLength(byte[] buffer,int start,int length){
		if(length >= (this.contentLength)){//最后的CRCL一起计算
			byte[] chunkData = new byte[this.contentLength];
			//处理chunkData
			System.arraycopy(buffer,0,chunkData,0, this.contentLength);
			cacheChunkData(chunkData);
			bodyParseOver = true;
			return;
		}else{
			//当前缓存中数据不够，从inputStream中读取数据到缓存中
			byte[] newBuffer = readBytes(buffer);
			parseHttpBodyContenLength(newBuffer,0,newBuffer.length);
		}
	}
	
	/**
	 * 获得一个socket
	 * @return
	 */
	public Socket getSocket(HttpRequestParameter requestParameter){
		Socket socket = null;
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
	
	public HttpResponseResult execute(HttpRequestParameter requestParameter) throws UnsupportedEncodingException, IOException {
		init();
		byte[] requestPackage = requestParameter.bulidRequestPackage();
		if(this.socket == null){
			this.socket = getSocket(requestParameter);
			socket.setKeepAlive(true);
			socket.setTcpNoDelay(true);
		}
		outputStream = socket.getOutputStream();
		outputStream.write(requestPackage);
		outputStream.flush();
		this.inputStream = socket.getInputStream();
		parseHttpResponsePackage();
		return responseResult;
	}
	
	/**
	 * 带回调的同步方法执行
	 * @param requestParameter
	 * @param callBack
	 * @return
	 * @throws Exception
	 */
	public HttpResponseResult execute(HttpRequestParameter requestParameter,CallBack callBack) throws Exception {
		HttpResponseResult result = null;
		IContextInner contextInner = new DefaultContextInner();
		IContext context = new DefaultContext(contextInner);
		try {
			callBack.onBeforeSend(context);
			result = this.execute(requestParameter);
			contextInner.setHttpResponseResult(result);
			callBack.onSuccess(context);
		} catch (Exception e) {
			callBack.onError(context);
			throw e;
		}finally{
			callBack.onComplete(context);
		}
		
		
		return result;
	}
	
	private void init() {
		this.responseResult = new HttpResponseResult();
		lastCRLF = false;
		/**body 解析完了吗？*/
		bodyParseOver = false;
		/**body中chuck 大小*/
		lastChunkLength = -1;
		/**response ContentLength 大小*/
		contentLength = -1;
	}

	/**
	 * 关闭http 连接
	 */
	public void close(){
		if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {
				logger.error("",e);
			}
		}
		
		if(inputStream != null){
			try {
				inputStream.close();
			} catch (IOException e) {
				logger.error("",e);
			}
		}
		
		if(outputStream != null){
			try {
				outputStream.close();
			} catch (IOException e) {
				logger.error("",e);
			}
		}
	}
}
