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
	public static HttpClient getHttpClient(){
		HttpClient hc = new DefaultHttpClient();
		return hc;
	}
	
	public static HttpClient getHttpClient(int maxPerRoute, int maxConn){
		PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
		cm.setDefaultMaxPerRoute(maxPerRoute);
		cm.setMaxTotal(maxConn);
		HttpClient hc = new DefaultHttpClient(cm);
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
}
