package frequency;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import proxy.ProxyManager;
import utils.HttpHelper;

public class FrequencySniffer {

	private static Logger log = Logger.getLogger(FrequencySniffer.class);

	private int startFrequencyMs;

	private int stopFrequencyMs;

	private boolean proxyOn;

	private ProxyManager proxyManager;

	private DownloaderThread[] threads;

	public FrequencySniffer() {
		init();
	}

	public void init() {
		Properties prop = new Properties();
		InputStream is = getClass().getClassLoader().getResourceAsStream("config.ini");
		try {
			prop.load(is);
			is.close();
			this.startFrequencyMs = Integer.parseInt(prop.getProperty("startFrequencyMs", "1000"));
			this.stopFrequencyMs = Integer.parseInt(prop.getProperty("stopFrequencyMs", "30000"));
			this.proxyOn = Boolean.parseBoolean(prop.getProperty("proxyOn", "true"));
			this.proxyManager = new ProxyManager("proxy.txt");
		} catch (IOException e) {
			log.error("init: config error");
		}
	}

	public void work() throws InterruptedException {
		if (this.proxyOn) {
			int num = this.proxyManager.getProxyNum();
			this.threads = new DownloaderThread[num];
			for (int i = 0; i < num; i++) {
				this.threads[i] = new DownloaderThread(i + "", this.startFrequencyMs
						+ (this.stopFrequencyMs - this.startFrequencyMs) / num * i);
				this.threads[i].setDownloaderProxy(this.proxyManager.getProxy());
			}
			for (int i = 0; i < num; i++) {
				this.threads[i].start();
			}
			for (int i = 0; i < num; i++) {
				this.threads[i].join();
			}
		} else {

		}
	}

	public static void main(String[] args) throws InterruptedException {
		FrequencySniffer fs = new FrequencySniffer();
		fs.work();
	}
}

class DownloaderThread extends Thread {
	private Logger log = Logger.getLogger(DownloaderThread.class);

	private HttpClient hc;

	private HttpHost proxy;

	private int frequency;

	public DownloaderThread(String threadName, int frequency) {
		super(threadName);
		this.hc = HttpHelper.getHttpClient(10, 50);
		HttpHelper.setUseragent("Mozilla/5.0 (Windows NT 6.1; rv:19.0) Gecko/20100101 Firefox/19.0", hc);
		this.frequency = frequency;
	}

	public DownloaderThread(String threadName, int frequency, HttpHost proxy) {
		super(threadName);
		this.hc = HttpHelper.getHttpClient(10, 50);
		HttpHelper.setUseragent("Mozilla/5.0 (Windows NT 6.1; rv:19.0) Gecko/20100101 Firefox/19.0", hc);
		this.frequency = frequency;
		this.proxy = proxy;
	}

	public void setDownloaderProxy(HttpHost proxy) {
		this.proxy = proxy;
		HttpHelper.setProxy(this.hc, proxy);
	}

	public boolean executeHttp(String url) {
		boolean result = false;
		HttpGet request = new HttpGet(url);
		configRequestHeader(request);
		// 每次执行都创建新的context和cookie，没有连续性
		// HttpContext localContext = new BasicHttpContext();
		// CookieStore cookieStore = new BasicCookieStore();
		// localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		try {
			HttpResponse response = this.hc.execute(request);
			// HttpResponse response = this.hc.execute(request, localContext);
			HttpEntity entity = response.getEntity();
			this.log.debug(Thread.currentThread().getName() + " " + response.getStatusLine());
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200 || statusCode == 302 || statusCode == 303) {
				result = true;
			}
			EntityUtils.consume(entity);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void configRequestHeader(HttpRequest request) {
		request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.addHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
		request.addHeader("Referer", "http://www.google.com.hk");
	}

	public void run() {
		int index = 10000;
		Date start = new Date();
		for (int i = 0; i < index; i++) {
			String url = "http://www.google.com.hk/#q=fd" + i;
			boolean result = executeHttp(url);
			if (result == true) {
				try {
					Thread.sleep(frequency);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				log.info("when frequency = " + frequency + " total url: " + i + " starttime:" + start.toString()
						+ "finishtime:" + new Date());
				break;
			}
		}
	}

}
