package proxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import utils.HttpHelper;

public class ProxyManager {

	private static Logger log = Logger.getLogger(ProxyManager.class);

	private List<HttpHost> pool;

	private HttpClient hc;

	private String proxyFilePath = "proxy.txt";
	
	public void addProxy(String ipAddress, int port) {
		HttpHost proxy = new HttpHost(ipAddress, port);
		pool.add(proxy);
	}

	public HttpHost getProxy() {
		if (pool.isEmpty()) {
			return null;
		}
		HttpHost proxy = pool.remove(0);
		return proxy;
	}

	public int getProxyNum() {
		return pool.size();
	}

	public boolean testProxy(String ipAddress, int port) {
		HttpHost proxy = new HttpHost(ipAddress, port);
		return testProxy(proxy);
	}

	public boolean testProxy(HttpHost proxy) {
		boolean result = false;
		hc = HttpHelper.getHttpClient();
		hc.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
		hc.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
		HttpHelper.setUseragent("Mozilla/5.0 (Windows NT 6.1; rv:19.0) Gecko/20100101 Firefox/19.0", hc);
		HttpHelper.setProxy(hc, proxy);
		HttpGet request = new HttpGet("http://www.google.com.hk/#q=fd");
		try {
			HttpResponse response = hc.execute(request);
			log.debug(proxy.toString() + ":" + response.getStatusLine());
			if (response.getStatusLine().getStatusCode() == 200) {
				result = true;
			} else {
				result = false;
			}
			EntityUtils.consume(response.getEntity());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			hc.getConnectionManager().shutdown();
		}
		return result;
	}

	public ProxyManager(String proxyFilePath) {
		pool = new ArrayList<HttpHost>();
		this.proxyFilePath = proxyFilePath;
//		System.out.println(ProxyManager.class.getClassLoader().getResource("proxy.txt"));
//		File proxyFile = new File(ProxyManager.class.getClassLoader().getResource("proxy.txt").getFile());
		File proxyFile = new File(this.proxyFilePath);
		if (proxyFile.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(proxyFile));
				String line = null;
				while ((line = reader.readLine()) != null) {
					String ipAddress = line.split(":")[0];
					int port = Integer.parseInt(line.split(":")[1]);
					HttpHost proxy = new HttpHost(ipAddress, port);
					pool.add(proxy);
				}
				reader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		List<HttpHost> workProxy = new ArrayList<HttpHost>();
		ProxyManager manager = new ProxyManager("proxyTemp.txt");
		HttpHost proxy = null;
		int trytime = 3;
		while ((proxy = manager.getProxy()) != null) {
			int count = 0;
			for (int i = 0; i < trytime; i++) {
				boolean result = manager.testProxy(proxy);
				if (result == true) {
					count++;
				}
			}
			if((double)count / (double)trytime > 0.8){
				workProxy.add(proxy);
			}
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("proxy.txt")));
			for (HttpHost host : workProxy) {
				bw.append(host.toHostString() + "\n");
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
