package site;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import proxy.ProxyManager;

import utils.HttpHelper;

public class GoogleTest {

	private List<String> seeds;

	private ProxyManager proxyManager;
	
	private HttpClient hc;

	private int successCount = 0;
	
	private int protocolErr = 0;
	
	private int ioErr = 0;
	
	public GoogleTest() {
		hc = HttpHelper.getHttpClient(5, 20);
		hc.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
		hc.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
		HttpHelper.setUseragent("User-Agent: Mozilla/5.0 (Windows NT 6.1; rv:19.0) Gecko/20100101 Firefox/19.0", hc);
		this.seeds = new ArrayList<String>();
		this.proxyManager = new ProxyManager("proxy.txt");
	}
	
	public void addSeeds(List<String> seeds){
		this.seeds.addAll(seeds);
	}
	
	public void process() {
		int page = 100;
		for (String seed : seeds) {
			for (int i = 0; i < page; i++) {
				HttpGet url = new HttpGet(
						"http://www.google.com.hk/search?hl=zh-CN&newwindow=1&safe=strict&site=&source=hp&q=" + seed
								+ "&btnG=Google+%E6%90%9C%E7%B4%A2&start=" + 10 * i);
				HttpHelper.configRequestHeader(url);
				HttpHelper.configRequestHeader(url, "Referer", "http://www.google.com.hk/");
				executeHttp(url);
			}
		}
	}
	
	public void executeHttp(HttpUriRequest url){
		try {
			HttpResponse response = hc.execute(url);
			System.out.println(response.getStatusLine());
			if(response.getStatusLine().getStatusCode() == 200){
				this.successCount++;
			}else{
				changeProxy(hc);
			}
			HttpEntity entity = response.getEntity();
			if(entity != null){
				EntityUtils.consume(entity);
			}
		} catch (ClientProtocolException e) {
			this.protocolErr++;
			e.printStackTrace();
		} catch (IOException e) {
			this.ioErr++;
			changeProxy(hc);
			e.printStackTrace();
		} finally{
		}
	}

	public void changeProxy(HttpClient hc1){
		this.hc = HttpHelper.getHttpClient(5, 20);
		this.hc.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
		this.hc.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
		HttpHelper.setUseragent("User-Agent: Mozilla/5.0 (Windows NT 6.1; rv:19.0) Gecko/20100101 Firefox/19.0", this.hc);
		HttpHost proxy = proxyManager.getProxy();
		HttpHelper.setProxy(this.hc, proxy);
	}
	
	public String decompressionGZIP(InputStream in){
		StringBuilder builder = new StringBuilder();
		try {
			GZIPInputStream input = new GZIPInputStream(in);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			input.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
	
	public static void main(String[] args) {
		GoogleTest test = new GoogleTest();
		List<String> seeds = new ArrayList<String>();
		File seedsFile = new File("words.txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(seedsFile));
			String line = null;
			while((line = reader.readLine()) != null){
				seeds.add(line.split(" +")[0]);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		test.addSeeds(seeds);
		test.process();
	}
}
