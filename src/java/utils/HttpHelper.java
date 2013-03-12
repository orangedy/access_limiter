package utils;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;

public class HttpHelper {
	public static DefaultHttpClient getHttpClient(){
		DefaultHttpClient hc = new DefaultHttpClient();
		return hc;
	}
	
	public static DefaultHttpClient getHttpClient(int maxPerRoute, int maxConn){
		PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
		cm.setDefaultMaxPerRoute(maxPerRoute);
		cm.setMaxTotal(maxConn);
		DefaultHttpClient hc = new DefaultHttpClient(cm);
		return hc;
	}
	
	public static void setConnectionTimeout(HttpClient hc, int connectionTimeoutMs){
		hc.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeoutMs);
	}
	
	public static void setSoTimeout(HttpClient hc, int soTimeoutMs){
		hc.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeoutMs);
	}
	
	public static void setUseragent(String useragent, HttpClient hc){
		hc.getParams().setParameter(CoreProtocolPNames.USER_AGENT, useragent);
	}
	
	public static void setProxy(HttpClient hc, HttpHost proxy){
		hc.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
	}
	
	public static void setCookiePolicy(HttpClient hc, String policy){
		hc.getParams().setParameter(ClientPNames.COOKIE_POLICY, policy);
	}
	
	public static void setRedirect(HttpClient hc, boolean redirect){
		hc.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, redirect);
	}
	
	public static void configRequestHeader(HttpRequest request, String header, String value) {
		request.addHeader(header, value);
	}
	
	public static void configRequestHeader(HttpRequest request) {
		HttpHelper.configRequestHeader(request, "Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		HttpHelper.configRequestHeader(request, "Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
		HttpHelper.configRequestHeader(request, "Accept-Encoding", "gzip, deflate");
	}
}
