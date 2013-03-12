package frequency;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
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

	private DefaultHttpClient hc;

	private HttpHost proxy;

	private int frequency;

	public DownloaderThread(String threadName, int frequency) {
		super(threadName);
		this.hc = HttpHelper.getHttpClient(10, 50);
		HttpHelper.setUseragent("Mozilla/5.0 (Windows NT 6.1; rv:19.0) Gecko/20100101 Firefox/19.0", hc);
		this.frequency = frequency;
		setCookies();
	}

	public DownloaderThread(String threadName, int frequency, HttpHost proxy) {
		super(threadName);
		this.hc = HttpHelper.getHttpClient(10, 50);
		HttpHelper.setUseragent("Mozilla/5.0 (Windows NT 6.1; rv:19.0) Gecko/20100101 Firefox/19.0", hc);
		this.frequency = frequency;
		this.proxy = proxy;
		setCookies();
	}

	public void setDownloaderProxy(HttpHost proxy) {
		this.proxy = proxy;
		HttpHelper.setProxy(this.hc, proxy);
	}

	public boolean executeHttp(String url) {
		boolean result = true;
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
			if (statusCode == 503 || statusCode == 403) {
				result = false;
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
		List<String> seeds = new ArrayList<String>();
		File seedsFile = new File("words.txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(seedsFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				seeds.add(line.split(" +")[0]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int index = seeds.size();
		Date start = new Date();
		for (int i = 0; i < index; i++) {
			String url = "http://www.google.com.hk/search?hl=zh-CN&newwindow=1&safe=strict&site=&source=hp&q="
					+ seeds.get(i) + "&btnG=Google+%E6%90%9C%E7%B4%A2";
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

	public void setCookie(String name, String value, String path, String domain, Date date) {
		BasicClientCookie cookie = new BasicClientCookie(name, value);
		cookie.setDomain(domain);
		cookie.setPath(path);
		cookie.setExpiryDate(date);
		this.hc.getCookieStore().addCookie(cookie);
	}

	public void setCookies() {
		setCookie("GDSESS",
				"ID=ba75a2866ff20718:TM=1363087064:C=c:IP=119.98.144.68-:S=APGng0vH-G3P-htg40sITFxZ5J2_29NGYg", "/",
				".google.com.hk", new Date(2014, 1, 1));
		setCookie(
				"NID",
				"67=nUr2P6J3tVFGcebPAczGw1gSZaLmXzN1fUTFshabMO4zOZu8MEiCt-iv9v4SwRxz7xXq4nzruz-UuBfEPwZmE5F5JHfI4VRdHLe5okS-kPqUMyILiV4w7G_VHPTAdBnx",
				"/", ".google.com.hk", new Date(2014, 1, 1));
		setCookie(
				"PREF",
				"ID=52fe7d60e3e7da2b:U=c524392dda21772b:FF=1:LD=zh-CN:NW=1:TM=1363086844:LM=1363086845:S=TApHERxpfkl_rsad",
				"/", ".google.com.hk", new Date(2014, 1, 1));
		setCookie("SNID", "67=FecNEerkDukRrHfO8UfBG8kIpcRp57LB9x6VCBaxcg=QDF_zbQPoVALO6CY", "/verify",
				".google.com.hk", new Date(2014, 1, 1));
	}
}
