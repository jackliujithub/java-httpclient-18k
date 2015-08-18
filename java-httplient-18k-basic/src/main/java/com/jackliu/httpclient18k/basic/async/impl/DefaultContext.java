package com.jackliu.httpclient18k.basic.async.impl;

import com.jackliu.httpclient18k.basic.HttpRequestParameter;
import com.jackliu.httpclient18k.basic.HttpResponseResult;
import com.jackliu.httpclient18k.basic.async.IContext;

public class DefaultContext implements IContext{

	private IContext contextFacade;
	
	public DefaultContext(IContext context){
		this.contextFacade = context;
	}

	@Override
	public HttpRequestParameter getHttpRequestParameter() {
		return contextFacade.getHttpRequestParameter();
	}

	@Override
	public HttpResponseResult getHttpResponseResult() {
		return contextFacade.getHttpResponseResult();
	}
	
	
}
