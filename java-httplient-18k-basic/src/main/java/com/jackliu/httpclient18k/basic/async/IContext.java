package com.jackliu.httpclient18k.basic.async;

import com.jackliu.httpclient18k.basic.HttpRequestParameter;
import com.jackliu.httpclient18k.basic.HttpResponseResult;

public interface IContext {

	public HttpRequestParameter getHttpRequestParameter();

	public HttpResponseResult getHttpResponseResult();
}
