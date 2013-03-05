package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.Configuration;
import net.sf.saxon.dom.DocumentWrapper;
import net.sf.saxon.query.DynamicQueryContext;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XQueryHelper {
    
    private static final Logger logger = Logger.getLogger(XQueryHelper.class.getName());
    
    public static Document parseHTML(String html, String xquery, String encoding){
        Document doc = html2Xml(html, encoding);
        Document result = parseDocument(doc, xquery);
        return result;
    }
    
    public static String parseHTMLToString(String html, String xquery, String encoding){
        Document doc = html2Xml(html, encoding);
        String result = parseDocumentToString(doc, xquery);
        return result;
    }

    public static Document html2Xml(String htmlStr, String encoding) {

        Document doc = null;
        DOMParser parser = NekoHelper.getParser(encoding);
        StringReader sr = new StringReader(htmlStr);
        try {
            parser.parse(new InputSource(sr));
            doc = parser.getDocument();
            doc.normalize();
            NodeList list = doc.getElementsByTagName("HTML");
            Element e = (Element) list.item(0);
            e.removeAttribute("xmlns");
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    
    /**
     * 在内存中完成操作
     * @param doc
     * @return
     */
    public static Document parseDocument(Document doc, String templateContent) {
        Document document = null;
        StringWriter sw = null;
        try {
            Configuration c = new Configuration();
            StaticQueryContext qp = c.newStaticQueryContext();
            XQueryExpression xe = qp.compileQuery(templateContent);
            DynamicQueryContext dqc = new DynamicQueryContext(c);
            dqc.setContextItem(new DocumentWrapper(doc, null, c));

            sw = new StringWriter();  
            final Properties props = new Properties();
            props.setProperty(OutputKeys.METHOD, "xml");
            props.setProperty(OutputKeys.INDENT, "yes");
            xe.run(dqc, new StreamResult(sw), props);
            String xml = sw.toString();
            xml = xml.replaceAll("&#.*;", "");
            document = string2Xml(xml);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(sw != null){
                try {
                    sw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            doc = null;
        }
        return document;
    }
    
    public static String parseDocumentToString(Document doc, String templateContent) {
        String result = null;
        StringWriter sw = null;
        try {
            Configuration c = new Configuration();
            StaticQueryContext qp = c.newStaticQueryContext();
            XQueryExpression xe = qp.compileQuery(templateContent);
            DynamicQueryContext dqc = new DynamicQueryContext(c);
            dqc.setContextItem(new DocumentWrapper(doc, null, c));

            sw = new StringWriter();  
            final Properties props = new Properties();
            props.setProperty(OutputKeys.METHOD, "text");
            props.setProperty(OutputKeys.INDENT, "yes");
            xe.run(dqc, new StreamResult(sw), props);
            result = sw.toString();
            result = result.replaceAll("&#.*;", "");
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(sw != null){
                try {
                    sw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            doc = null;
        }
        return result;
    }
    
    /**
     * 在内存中完成操作
     * @param doc
     * @return
     */
    public static String getStringResult(Document doc, String templateContent) {
        String result = null;
        StringWriter sw = null;
        try {
            Configuration c = new Configuration();
            StaticQueryContext qp = c.newStaticQueryContext();
            XQueryExpression xe = qp.compileQuery(templateContent);
            DynamicQueryContext dqc = new DynamicQueryContext(c);
            dqc.setContextItem(new DocumentWrapper(doc, null, c));

            sw = new StringWriter();  
            final Properties props = new Properties();
            props.setProperty(OutputKeys.METHOD, "xml");
            props.setProperty(OutputKeys.INDENT, "yes");
            xe.run(dqc, new StreamResult(sw), props);
            result = sw.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(sw != null){
                try {
                    sw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            doc = null;
        }
        return result;
    }

    /**
     * 
     * 将字符串解析为 DOM 文档
     * @param strXml
     * @return
     * 
     * @since  crawler_agent　Ver1.0
     */
    private static Document string2Xml(String strXml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder;
        Document doc = null;
        StringReader is = null;
        try {
            is = new StringReader(strXml);
            InputSource inputSource = new InputSource(is);
            builder = factory.newDocumentBuilder();
            doc = builder.parse(inputSource);
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            is.close();
        }
        doc.normalize();
        return doc;
    }


}

