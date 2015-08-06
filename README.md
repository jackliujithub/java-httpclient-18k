# java-httplient-18k
a simple httpclient,only 18k size
#使用示例
## http get 方法使用示例
BasicHttpClient httpClient = new BasicHttpClient();<br>
HttpRequestParameter requestParameter = new HttpRequestParameter();<br>
		//设置请求方法<br>
requestParameter.setMethod("POST");
//设置url
requestParameter.setUrl("http://192.168.192.113:8085/getToken");
//添加http 头部
requestParameter.addHeader("Content-Type", "application/x-www-form-urlencoded");
//post 方法添加http body
String body = "accessKey=65989asdf8989e9&hardPlatform=Iphone6%2C1&utcDate=2015-08-03T14%3A55%3A56.422Z&method=post";
requestParameter.setHttpBodyString(body);
try {
	HttpResponseResult  response = httpClient.execute(requestParameter);
	//获得结果
	System.out.println("========result:===========" + response.getHttpResponseBody());
} catch (UnsupportedEncodingException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}finally{
	httpClient.close();//如果是长连接，可以重用BasicHttpClient
}
