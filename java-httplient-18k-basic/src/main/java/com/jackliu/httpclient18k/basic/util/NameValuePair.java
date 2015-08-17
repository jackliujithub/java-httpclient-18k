package com.jackliu.httpclient18k.basic.util;

public class NameValuePair {

	/**参数名*/
	private String key;
	
	/**参数值*/
	private String value;

	
	public NameValuePair(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
	
}
