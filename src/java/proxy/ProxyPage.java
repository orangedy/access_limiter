package proxy;

public class ProxyPage {

	private String url;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private String xquery;
	
	public String getXquery() {
		return xquery;
	}

	public void setXquery(String xquery) {
		this.xquery = xquery;
	}

	public ProxyPage(String url, String xquery) {
		this.url = url;
		this.xquery = xquery;
	}
}
