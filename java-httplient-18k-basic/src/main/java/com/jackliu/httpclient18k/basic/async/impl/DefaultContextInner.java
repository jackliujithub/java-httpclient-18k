package com.jackliu.httpclient18k.basic.async.impl;

import com.jackliu.httpclient18k.basic.HttpRequestParameter;
import com.jackliu.httpclient18k.basic.HttpResponseResult;
import com.jackliu.httpclient18k.basic.async.IContextInner;

public class DefaultContextInner implements IContextInner{

	/**请求参数*/
	private HttpRequestParameter httpRequestParameter;

	/**应答结果*/
	private HttpResponseResult httpResponseResult;
	
	
	@Override
	public void setHttpRequestParameter(HttpRequestParameter requestParameter) {
		this.httpRequestParameter = requestParameter;
	}

	@Override
	public void setHttpResponseResult(HttpResponseResult responseResult) {
		this.httpResponseResult = responseResult;
	}
	
	public HttpRequestParameter getHttpRequestParameter() {
		return httpRequestParameter;
	}

	public HttpResponseResult getHttpResponseResult() {
		return httpResponseResult;
	}
}
