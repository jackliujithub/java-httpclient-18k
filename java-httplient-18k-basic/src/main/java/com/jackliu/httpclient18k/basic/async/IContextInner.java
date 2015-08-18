package com.jackliu.httpclient18k.basic.async;

import com.jackliu.httpclient18k.basic.HttpRequestParameter;
import com.jackliu.httpclient18k.basic.HttpResponseResult;

public interface IContextInner extends IContext {

	void setHttpRequestParameter(HttpRequestParameter requestParameter);

	void setHttpResponseResult(HttpResponseResult responseResult);

}
