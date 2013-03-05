package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;


public class NekoHelper {

	/**
	 * 给定xpath路径，在给定的content下获得指定的路径下的节点集合
	 * @param content
	 * @param xpath
	 * @return
	 */
	public static NodeList getContentByXpath(String content, String xpath){
		DOMParser parser = getStrParser(content);
		
		Document doc = parser.getDocument();
		if(doc == null){
			return null;
		}
		NodeList list = null;
		try{
			list = XPathAPI.selectNodeList(doc, xpath);
		}catch(TransformerException e){
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 返回唯一节点
	 * @param content
	 * @param xpath
	 * @return
	 */
	public static Node getNodeByXpath(String content, String xpath){
		NodeList list = NekoHelper.getContentByXpath(content, xpath);
		if(list.getLength() == 0){
			return null;
		}
		Node node = list.item(0);
		if(node == null){
			return null;
		}
		return node;
	}
	
	/**
	 * 获得指定的本地文件的解析器 DOMParser
	 * @param str
	 * @return
	 */
	private static DOMParser getStrParser(String str){
		DOMParser parser = null;
		try{
			parser = new DOMParser();
			parser.setProperty("http://cyberneko.org/html/properties/default-encoding","gb2312");
			parser.setFeature("http://xml.org/sax/features/namespaces", false);			
			parser.parse(new InputSource(new StringReader(str)));
		}catch(Exception e){

		}
		return parser;
	}
	
	/**
	 * 获得指定的本地文件或者网络上指定的url下的解析器 DOMParser
	 * @param url
	 * @param isurl true 本地文件  false 网络文件
	 * @return
	 */
	public static DOMParser getParser(String url, boolean isurl){
		DOMParser parser = null;
		try{
			parser = new DOMParser();
			parser.setProperty("http://cyberneko.org/html/properties/default-encoding","gb2312");
			parser.setFeature("http://xml.org/sax/features/namespaces", false);
			BufferedReader in = null;
			if(isurl){
				File file = new File(url);			
				in = new BufferedReader(new InputStreamReader(new FileInputStream(file),"gb2312"));
				parser.parse(new InputSource(in));
				in.close();
			}		
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return parser;
	} 
	
	/**
	 * 获得指定的本地文件或者网络上指定的url下的解析器 DOMParser
	 * @param url
	 * @param isurl true 本地文件  false 网络文件
	 * @return
	 */
	public static DOMParser getParser(){
		DOMParser parser = null;
		try{
			parser = new DOMParser();
			parser.setProperty("http://cyberneko.org/html/properties/default-encoding","gb2312");
			parser.setFeature("http://xml.org/sax/features/namespaces", false);
		}catch(Exception e){

		}
		return parser;
	} 
	

	
	public static Document getDocument(String content){
		Document doc = null;
		try {

			DOMParser parser = getParser();
			parser.parse(content);
			doc = parser.getDocument();
			doc.normalize();
			NodeList list = doc.getElementsByTagName("HTML");
			Element e = (Element) list.item(0);
			e.removeAttribute("xmlns");
			return doc;
		}catch(Exception e){

		} 
		return null;
	}
	
	
	
	public static DOMParser getParser(String encoding){
		if(encoding=="" || encoding==null){
			encoding = "gb2312";
		}
		DOMParser parser = new DOMParser();
		try {
			parser.setProperty(
					"http://cyberneko.org/html/properties/default-encoding",
					encoding);
			parser.setFeature("http://xml.org/sax/features/namespaces", false);
		} catch (SAXNotRecognizedException e) {
			e.printStackTrace();
		} catch (SAXNotSupportedException e) {
			e.printStackTrace();
		}
		return parser;
	}
	
	public static void main(String[] args) {
		NekoHelper nh = new NekoHelper();
		String url = "http://quan.webcars.com.cn/audi/topic/";
	}
	
	
}