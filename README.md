# java-httplient-18k
a simple httpclient,only 18k size
#使用示例
## http get 方法使用示例
BasicHttpClient httpClient = new BasicHttpClient();<br>
HttpRequestParameter requestParameter = new HttpRequestParameter();<br>
		//设置请求方法<br>
requestParameter.setMethod("POST");<br
\//设置url<br>
requestParameter.setUrl("http://192.168.192.113:8085/getToken");<br>
//添加http 头部<br>
requestParameter.addHeader("Content-Type", "application/x-www-form-urlencoded");<br>
//post 方法添加http body<br>
String body = "accessKey=65989asdf8989e9&hardPlatform=Iphone6%2C1&utcDate=2015-08-03T14%3A55%3A56.422Z&method=post";<br>
requestParameter.setHttpBodyString(body);<br>
try {<br>
	HttpResponseResult  response = httpClient.execute(requestParameter);<br>
	//获得结果<br>
	System.out.println("========result:===========" + response.getHttpResponseBody());<br>
} catch (UnsupportedEncodingException e) {<br>
	e.printStackTrace();<br>
} catch (IOException e) {<br>
	e.printStackTrace();<br>
}finally{<br>
	httpClient.close();//如果是长连接，可以重用BasicHttpClient<br>
}<br>
