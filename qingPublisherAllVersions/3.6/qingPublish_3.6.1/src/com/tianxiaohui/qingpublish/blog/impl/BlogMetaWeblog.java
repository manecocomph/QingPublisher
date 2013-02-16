package com.tianxiaohui.qingpublish.blog.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.AsyncCallback;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcClientRequestImpl;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;

import com.tianxiaohui.qingpublish.Account;
import com.tianxiaohui.qingpublish.Activator;
import com.tianxiaohui.qingpublish.blog.BlogProvider;

	// CSDN：http://blog.csdn.net/spytian/services/metablogapi.aspx
	//sina：http://upload.move.blog.sina.com.cn/blog_rebuild/blog/xmlrpc.php
public class BlogMetaWeblog extends BlogProvider {

	//ILog log = Activator.getDefault().getLog();
	
	public void asyncGetCategories(final Display display, final Account account, final Combo categoriesCombo) throws Exception {
		String blogId = this.getBlogId(account);
		
		XmlRpcClient client = null;
		try {
			client = new XmlRpcClient();
			XmlRpcClientConfigImpl pConfig = new XmlRpcClientConfigImpl();
			pConfig.setBasicUserName(account.getUserName());
			pConfig.setBasicPassword(account.getUserPwd());
			pConfig.setServerURL(new URL(account.getServerUrl()));
			client.setConfig(pConfig);
			Object[] paras = new Object[]{blogId, account.getUserName(), account.getUserPwd()};
			
			XmlRpcRequest pRequest = new XmlRpcClientRequestImpl(pConfig, "metaWeblog.getCategories", paras); 
			//Object obj = client.execute(pRequest);
			AsyncCallback pCallback = new AsyncCallback(){
				
				public void handleError(XmlRpcRequest arg0, Throwable arg1) {
					System.out.println("in method handle error");
					arg1.printStackTrace();
				}

				
				public void handleResult(XmlRpcRequest arg0, Object obj) {
					System.out.println("in method handle result");
					if (null != obj) {
						Object[] objs = (Object[]) obj;
						if (null != objs) {
							final Map<String, String> categories = new HashMap<String, String>();
							for (Object obj0 : objs) {
								if (null != obj0) {
									if (obj0 instanceof Map) {
										@SuppressWarnings("unchecked")
										Map<String, String> infos = (Map<String, String>) obj0;
										//infos.get("");
										categories.put(infos.get("categoryid"), infos.get("title"));
										System.out.println("categoryId: " + infos.get("categoryid"));
										System.out.println("categoryName: " + infos.get("title"));
									}
								}
							}
							
							if (!display.isDisposed()) {
								Runnable runnable = new Runnable() {
									
									public void run() {
										if (null != categories) {
											/*categoryMapping.clear();*/
											categoriesCombo.removeAll();
											int i = 0;
											for (Map.Entry<String, String> entry : categories.entrySet()) {
												//categoryMapping.put(Integer.valueOf(i), Integer.valueOf(entry.getKey()));
												categoriesCombo.add(entry.getValue(), i++);
												System.out.println("entry.getValue()" + entry.getValue());
												System.out.println("entry.getKey()" + entry.getKey());
											}
											categoriesCombo.getParent().redraw();
											categoriesCombo.pack();
											categoriesCombo.redraw();
										}
									}
								};
								
								display.asyncExec(runnable);
							}
							
						}
					}
				}};
				client.executeAsync(pRequest, pCallback );
		} catch (MalformedURLException e) {
			e.printStackTrace();
			//log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		}
		
	}
/*
	
	public Map<String, String> getCategories(Account account) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}*/

	public String getSiteName(Account account) throws Exception {
		XmlRpcClient client = null;
		try {
			client = new XmlRpcClient();
			XmlRpcClientConfigImpl pConfig = new XmlRpcClientConfigImpl();
			pConfig.setBasicUserName(account.getUserName());
			pConfig.setBasicPassword(account.getUserPwd());
			pConfig.setServerURL(new URL(account.getServerUrl()));
			client.setConfig(pConfig);
			Object[] paras = new Object[]{"appkey", account.getUserName(), account.getUserPwd()};
			
			XmlRpcRequest pRequest = new XmlRpcClientRequestImpl(pConfig, "blogger.getUsersBlogs", paras); 
			Object obj = client.execute(pRequest);
			if (null != obj) {
				Object[] objs = (Object[]) obj;
				if (null != objs[0]) {
					@SuppressWarnings("unchecked")
					Map<String, String> infos = (Map<String, String>) objs[0];
					return infos.get("blogName");
				}
			}
			return account.getServerUrl();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			//log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			//log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		}
	}

	public String publishAsDraft(Account account, String categoryValue, String title, String content) throws Exception {
		return this.publish(account, categoryValue, title, content, true);
	}

	public String publish(Account account, String categoryValue, String title, String content) throws Exception {
		return this.publish(account, categoryValue, title, content, false);
	}
	
	private String publish(Account account, String categoryValue, String title, String content, boolean isDraft) throws Exception {
		XmlRpcClient client = null;
		try {
			client = new XmlRpcClient();
			XmlRpcClientConfigImpl pConfig = new XmlRpcClientConfigImpl();
			pConfig.setBasicUserName(account.getUserName());
			pConfig.setBasicPassword(account.getUserPwd());
			pConfig.setServerURL(new URL(account.getServerUrl()));
			client.setConfig(pConfig);
			
			Map<String, Object> post = new HashMap<String, Object>();
			if (isDraft) {
				post.put("post_status", "draft");
			} else {
				post.put("post_status", "publish");
			}
			post.put("title", title);
			//post.put("post_category", new Integer[]{categoryId, 2});
			System.out.println("categoryValue: " + categoryValue);
			if (null != categoryValue) {
				post.put("categories", new String[]{categoryValue});
			}
			
			System.out.println("categoryId     " + 481589);
			if (null != content && content.length() > 3) {
				System.out.println("content : \r\n" + content);
				post.put("description", content);
			} else {
				post.put("description", "<h3>this is content with h3 tag</h3>");
			}
			
			Object[] paras = new Object[]{String.valueOf(this.getBlogId(account)),  account.getUserName(), account.getUserPwd(), post, !isDraft};
			
			XmlRpcRequest pRequest = new XmlRpcClientRequestImpl(pConfig, "metaWeblog.newPost", paras);
			//XmlRpcRequest pRequest = new XmlRpcClientRequestImpl(pConfig, "blogger.newPost", paras); 
			Object obj = client.execute(pRequest);
			return this.getBlogLink(account, obj.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			//log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			//log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		}
	}

	private String getBlogId(Account account) throws Exception {
		XmlRpcClient client = null;
		try {
			client = new XmlRpcClient();
			XmlRpcClientConfigImpl pConfig = new XmlRpcClientConfigImpl();
			pConfig.setBasicUserName(account.getUserName());
			pConfig.setBasicPassword(account.getUserPwd());
			pConfig.setServerURL(new URL(account.getServerUrl()));
			client.setConfig(pConfig);
			Object[] paras = new Object[]{"appkey", account.getUserName(), account.getUserPwd()};
			
			XmlRpcRequest pRequest = new XmlRpcClientRequestImpl(pConfig, "blogger.getUsersBlogs", paras); 
			Object obj = client.execute(pRequest);
			if (null != obj) {
				Object[] objs = (Object[]) obj;
				if (null != objs[0]) {
					@SuppressWarnings("unchecked")
					Map<String, String> infos = (Map<String, String>) objs[0];
					return String.valueOf(infos.get("blogid")); //blogName
				}
			}
			return "Can't get blog ID";
		} catch (MalformedURLException e) {
			e.printStackTrace();
			//log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			//log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		}
	}
	
	private String getBlogLink(Account account, String postId) throws Exception {
		XmlRpcClient client = null;
		try {
			client = new XmlRpcClient();
			XmlRpcClientConfigImpl pConfig = new XmlRpcClientConfigImpl();
			pConfig.setBasicUserName(account.getUserName());
			pConfig.setBasicPassword(account.getUserPwd());
			pConfig.setServerURL(new URL(account.getServerUrl()));
			client.setConfig(pConfig);
			Object[] paras = new Object[]{postId, account.getUserName(), account.getUserPwd()};
			
			XmlRpcRequest pRequest = new XmlRpcClientRequestImpl(pConfig, "metaWeblog.getPost", paras); 
			Object obj = client.execute(pRequest);
			if (null != obj) {
				@SuppressWarnings("unchecked")
				Map<String, String> infos = (Map<String, String>) obj;
				String link = infos.get("link");
				return (null == link ? "No link return from your blog." : link); 
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			//log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			//log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		}
		
		return null;
	}
	
	private String getBlogLink1(Account account, String postId) throws Exception {
		XmlRpcClient client = null;
		try {
			client = new XmlRpcClient();
			XmlRpcClientConfigImpl pConfig = new XmlRpcClientConfigImpl();
			pConfig.setBasicUserName(account.getUserName());
			pConfig.setBasicPassword(account.getUserPwd());
			pConfig.setServerURL(new URL(account.getServerUrl()));
			client.setConfig(pConfig);
			Object[] paras = new Object[]{postId, account.getUserName(), account.getUserPwd()};
			
			XmlRpcRequest pRequest = new XmlRpcClientRequestImpl(pConfig, "blogger.getPost", paras); 
			Object obj = client.execute(pRequest);
			if (null != obj) {
				@SuppressWarnings("unchecked")
				Map<String, String> infos = (Map<String, String>) obj;
				System.out.println("blog link llllllllllllllllllllllllllllllll " + infos.get("link"));
				
				return infos.get("link"); 
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			//log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			//log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		}
		return null;
	}
	
	public static void main(String[] strs) {
		Account account = new Account();
		account.setUserName("QingPublisher");
		account.setServerUrl("spytian");
		account.setServerUrl("http\\://www.cnblogs.com/QingPublisher/services/metablogapi.aspx");
		
		BlogMetaWeblog blog = new BlogMetaWeblog();
		try {
			blog.getBlogId(account);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
