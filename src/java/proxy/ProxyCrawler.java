package proxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import utils.HttpHelper;
import utils.XQueryHelper;

public class ProxyCrawler {

	private static final Logger log = Logger.getLogger(ProxyCrawler.class);

	private List<ProxyPage> proxyPages;

	private String resultFilePath = "proxyTemp.txt";

	private String xqueryPath = "xquery";

	public ProxyCrawler() {
		// TODO Auto-generated constructor stub
	}

	public void readProxyFile() {
		File dir = new File(getClass().getClassLoader().getResource(xqueryPath).getFile());
		File[] filePaths = dir.listFiles();
		this.proxyPages = new ArrayList<ProxyPage>();
		for (File xqueryFile : filePaths) {
			try {
				String url = null;
				StringBuffer xquery = new StringBuffer();
				BufferedReader br = new BufferedReader(new FileReader(xqueryFile));
				String firstLine = br.readLine();
				if (firstLine != null) {
					url = firstLine;
				}
				String line = null;
				while ((line = br.readLine()) != null) {
					xquery.append(line);
				}
				ProxyPage proxyPage = new ProxyPage(url, xquery.toString());
				this.proxyPages.add(proxyPage);
				br.close();
			} catch (FileNotFoundException e) {
				log.error(e.getMessage() + ":" + xqueryFile.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void work() {
		File resultFile = new File(resultFilePath);
		boolean fileExist = true;
		if (!resultFile.exists()) {
			try {
				fileExist = resultFile.createNewFile();
			} catch (IOException e) {
				log.error("create resultFilePath fail:" + resultFile.getAbsolutePath());
			}
		}
		if (fileExist == true) {
			HttpClient hc = HttpHelper.getHttpClient();
			HttpHelper.setConnectionTimeout(hc, 3000);
			HttpHelper.setSoTimeout(hc, 10000);
			HttpHelper.setUseragent("Mozilla/5.0 (Windows NT 6.1; rv:19.0) Gecko/20100101 Firefox/19.0", hc);
			for (ProxyPage proxyPage : this.proxyPages) {
				HttpGet request = new HttpGet(proxyPage.getUrl());
				configHttpHeader(request);
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile));
					HttpResponse response = hc.execute(request);
					if(response.getStatusLine().getStatusCode() == 200){
						String content = EntityUtils.toString(response.getEntity());
						String resultStr = XQueryHelper.parseHTMLToString(content, proxyPage.getXquery(), "utf-8");
						writer.append(resultStr);
					}
					writer.flush();
					writer.close();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally{
					request.abort();
				}
			}
		}
	}

	public void configHttpHeader(HttpRequest request) {
		HttpHelper.configRequestHeader(request, "Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		HttpHelper.configRequestHeader(request, "Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
	}
	
	public static void main(String[] args) {
		ProxyCrawler proxyCrawler = new ProxyCrawler();
		proxyCrawler.readProxyFile();
		proxyCrawler.work();
	}
}
