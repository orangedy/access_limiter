package site;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import utils.HttpHelper;

public class GoogleTest1 {

	public int test1() {
		int successCount = 0;

		DefaultHttpClient hc = HttpHelper.getHttpClient();
		hc.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
		hc.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
		HttpHelper.setUseragent("User-Agent: Mozilla/5.0 (Windows NT 6.1; rv:19.0) Gecko/20100101 Firefox/19.0", hc);

		setCookies(hc);
		
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

		int page = 20;
		for (String seed : seeds) {
			for (int i = 0; i < page; i++) {
				HttpGet url = new HttpGet(
						"http://www.google.com.hk/search?hl=zh-CN&newwindow=1&safe=strict&site=&source=hp&q=" + seed
								+ "&btnG=Google+%E6%90%9C%E7%B4%A2&start=" + 10 * i);
				HttpHelper.configRequestHeader(url);
				HttpHelper.configRequestHeader(url, "Referer", "http://www.google.com.hk/");
				try {
					HttpResponse response = hc.execute(url);
					System.out.println(response.getStatusLine());
					if (response.getStatusLine().getStatusCode() == 200) {
						successCount++;
					} else if (response.getStatusLine().getStatusCode() == 503) {
						return successCount;
					}
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						EntityUtils.consume(entity);
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
				}
				try {
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return successCount;
	}

	public int test2() {
		int successCount = 0;

		DefaultHttpClient hc1 = HttpHelper.getHttpClient();
		DefaultHttpClient hc2 = HttpHelper.getHttpClient();

		hc1.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
		hc1.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
		HttpHelper.setUseragent("User-Agent: Mozilla/5.0 (Windows NT 6.1; rv:19.0) Gecko/20100101 Firefox/19.0", hc1);
		setCookies(hc1);
		
		hc2.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
		hc2.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
		HttpHelper.setUseragent("User-Agent: Mozilla/5.0 (Windows NT 6.1; rv:19.0) Gecko/20100101 Firefox/19.0", hc2);
		setCookies(hc2);

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

		int page = 20;
		for (String seed : seeds) {
			for (int i = 0; i < page; i++) {
				HttpGet url = new HttpGet(
						"http://www.google.com.hk/search?hl=zh-CN&newwindow=1&safe=strict&site=&source=hp&q=" + seed
								+ "&btnG=Google+%E6%90%9C%E7%B4%A2&start=" + 10 * i);
				HttpHelper.configRequestHeader(url);
				HttpHelper.configRequestHeader(url, "Referer", "http://www.google.com.hk/");
				try {
					HttpResponse response;
					if (i % 2 == 0) {
						response = hc1.execute(url);
					} else {
						response = hc2.execute(url);
					}
					System.out.println(response.getStatusLine());
					if (response.getStatusLine().getStatusCode() == 200) {
						successCount++;
					} else if (response.getStatusLine().getStatusCode() == 503) {
						return successCount;
					}
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						EntityUtils.consume(entity);
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
				}
				try {
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return successCount;
	}

	public void setCookie(DefaultHttpClient hc, String name, String value, String path, String domain, Date date) {
		BasicClientCookie cookie = new BasicClientCookie(name, value);
		cookie.setDomain(domain);
		cookie.setPath(path);
		cookie.setExpiryDate(date);
		hc.getCookieStore().addCookie(cookie);
	}

	public void setCookies(DefaultHttpClient hc) {
		setCookie(hc, "GDSESS",
				"ID=ba75a2866ff20718:TM=1363087064:C=c:IP=119.98.144.68-:S=APGng0vH-G3P-htg40sITFxZ5J2_29NGYg", "/",
				".google.com.hk", new Date(2014, 1, 1));
		setCookie(
				hc,
				"NID",
				"67=nUr2P6J3tVFGcebPAczGw1gSZaLmXzN1fUTFshabMO4zOZu8MEiCt-iv9v4SwRxz7xXq4nzruz-UuBfEPwZmE5F5JHfI4VRdHLe5okS-kPqUMyILiV4w7G_VHPTAdBnx",
				"/", ".google.com.hk", new Date(2014, 1, 1));
		setCookie(
				hc,
				"PREF",
				"ID=52fe7d60e3e7da2b:U=c524392dda21772b:FF=1:LD=zh-CN:NW=1:TM=1363086844:LM=1363086845:S=TApHERxpfkl_rsad",
				"/", ".google.com.hk", new Date(2014, 1, 1));
		setCookie(hc, "SNID", "67=FecNEerkDukRrHfO8UfBG8kIpcRp57LB9x6VCBaxcg=QDF_zbQPoVALO6CY", "/verify",
				".google.com.hk", new Date(2014, 1, 1));
	}

	public static void main(String[] args) {
		GoogleTest1 test = new GoogleTest1();
		System.out.println(test.test1());
	}
}
