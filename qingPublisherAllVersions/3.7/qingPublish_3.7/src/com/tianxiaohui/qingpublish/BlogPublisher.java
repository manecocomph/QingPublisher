package com.tianxiaohui.qingpublish;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.Style;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcClientRequestImpl;
import org.eclipse.core.runtime.ILog;

public class BlogPublisher {

	ILog log = Activator.getDefault().getLog();
	
	public static Object publish(String title, String content) {
		XmlRpcClient client = null;
		try {
			client = new XmlRpcClient();
			XmlRpcClientConfigImpl pConfig = new XmlRpcClientConfigImpl();
			/*pConfig.setBasicUserName("manecocomph");
			pConfig.setBasicPassword("Cqy851216");
			pConfig.setServerURL(new URL("http://tianxiaohui.com/xmlrpc.php"));
			*/
			Account defaultAccount = AccountMgr.getDefaultAccount();
			pConfig.setBasicUserName(defaultAccount.getUserName());
			pConfig.setBasicPassword(defaultAccount.getUserPwd());
			pConfig.setServerURL(new URL(defaultAccount.getServerUrl()));
			client.setConfig(pConfig);
			
			Map<String, String> post = new HashMap<String, String>();
			post.put("post_title", title);
			if (null != content && content.length() > 3) {
				//post.put("post_content", "<pre><code>" + content.replaceAll("\r\n\\s+", "\r\n&nbsp;").replaceAll("\\s+", "") + "</code></pre>");
				//StringUtils.replaceEach(content, new String[]{"&", "\"", "<", ">"}, new String[]{"&amp;", "&quot;", "&lt;", "&gt;"});
				post.put("post_content", "<pre><code>" + content + "</code></pre>");
				//post.put("post_content", "<code>" + content.replaceAll(">\\s+</span>", ">&nbsp;&nbsp;</span>").replaceAll("\\s+", " ") + "</code>");
			} else {
				post.put("post_content", "<h3>this is content with h3 tag</h3>");
			}
			
			Object[] paras = new Object[]{"1", "manecocomph", "Cqy851216", post, true};
			
			XmlRpcRequest pRequest = new XmlRpcClientRequestImpl(pConfig, "wp.newPost", paras); 
			Object obj = client.execute(pRequest);
			System.out.println("result: " + obj);
			return obj;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//BlogPublisher.publish(null);
		HTMLEditorKit kit = new HTMLEditorKit();
		System.out.println(":" + kit.DEFAULT_CSS);
        HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
        StyleSheet styles = doc.getStyleSheet();
        
        Enumeration rules = styles.getStyleNames();
        while (rules.hasMoreElements()) {
            String name = (String) rules.nextElement();
            Style rule = styles.getStyle(name);
            System.out.println(rule.toString());
        }
        
        StyleSheet ss = new StyleSheet();
        ss.addRule("");
        System.exit(0);
	}

}
