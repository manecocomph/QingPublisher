package com.tianxiaohui.qingpublish.blog;

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
import com.tianxiaohui.qingpublish.AccountMgr;
import com.tianxiaohui.qingpublish.Activator;

public class BlogWordpress implements BlogProvider {
	ILog log = Activator.getDefault().getLog();
	
	public void asyncGetCategories(final Display display, Account account, final Combo categoriesCombo, final Map<Integer, Integer> categoryMapping) throws Exception {
		int blogId = this.getBlogId(account);
		
		XmlRpcClient client = null;
		try {
			client = new XmlRpcClient();
			XmlRpcClientConfigImpl pConfig = new XmlRpcClientConfigImpl();
			pConfig.setBasicUserName(account.getUserName());
			pConfig.setBasicPassword(account.getUserPwd());
			pConfig.setServerURL(new URL(account.getServerUrl()));
			client.setConfig(pConfig);
			Object[] paras = new Object[]{blogId, account.getUserName(), account.getUserPwd()};
			
			XmlRpcRequest pRequest = new XmlRpcClientRequestImpl(pConfig, "wp.getCategories", paras); 
			//Object obj = client.execute(pRequest);
			AsyncCallback pCallback = new AsyncCallback(){
				@Override
				public void handleError(XmlRpcRequest arg0, Throwable arg1) {
					
				}

				@Override
				public void handleResult(XmlRpcRequest arg0, Object obj) {
					if (null != obj) {
						Object[] objs = (Object[]) obj;
						if (null != objs) {
							final Map<String, String> categories = new HashMap<String, String>();
							for (Object obj0 : objs) {
								if (null != obj0) {
									Map<String, String> infos = (Map<String, String>) obj0;
									//infos.get("");
									categories.put(infos.get("categoryId"), infos.get("categoryName"));
								}
							}
							
							if (!display.isDisposed()) {
								Runnable runnable = new Runnable() {
									@Override
									public void run() {
										if (null != categories) {
											categoryMapping.clear();
											categoriesCombo.removeAll();
											int i = 0;
											for (Map.Entry<String, String> entry : categories.entrySet()) {
												categoryMapping.put(Integer.valueOf(i), Integer.valueOf(entry.getKey()));
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
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		} /*catch (XmlRpcException e) {
			e.printStackTrace();
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		}*/
	}
	
	@Override
	public Map<String, String> getCategories(Account account) throws Exception {
		int blogId = this.getBlogId(account);
		
		XmlRpcClient client = null;
		try {
			client = new XmlRpcClient();
			XmlRpcClientConfigImpl pConfig = new XmlRpcClientConfigImpl();
			pConfig.setBasicUserName(account.getUserName());
			pConfig.setBasicPassword(account.getUserPwd());
			pConfig.setServerURL(new URL(account.getServerUrl()));
			client.setConfig(pConfig);
			Object[] paras = new Object[]{blogId, account.getUserName(), account.getUserPwd()};
			
			XmlRpcRequest pRequest = new XmlRpcClientRequestImpl(pConfig, "wp.getCategories", paras); 
			Object obj = client.execute(pRequest);
			AsyncCallback pCallback = new AsyncCallback(){
				@Override
				public void handleError(XmlRpcRequest arg0, Throwable arg1) {
					
				}

				@Override
				public void handleResult(XmlRpcRequest arg0, Object arg1) {
					
				}};
			client.executeAsync(pRequest, pCallback );
			if (null != obj) {
				Object[] objs = (Object[]) obj;
				if (null != objs) {
					Map<String, String> categories = new HashMap<String, String>();
					for (Object obj0 : objs) {
						if (null != obj0) {
							Map<String, String> infos = (Map<String, String>) obj0;
							//infos.get("");
							categories.put(infos.get("categoryId"), infos.get("categoryName"));
						}
					}
					return categories;
				}
			}
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		}
	}

	@Override
	public String getSiteName(Account account) throws Exception {
		XmlRpcClient client = null;
		try {
			client = new XmlRpcClient();
			XmlRpcClientConfigImpl pConfig = new XmlRpcClientConfigImpl();
			pConfig.setBasicUserName(account.getUserName());
			pConfig.setBasicPassword(account.getUserPwd());
			pConfig.setServerURL(new URL(account.getServerUrl()));
			client.setConfig(pConfig);
			Object[] paras = new Object[]{account.getUserName(), account.getUserPwd()};
			
			XmlRpcRequest pRequest = new XmlRpcClientRequestImpl(pConfig, "metaWeblog.getUsersBlogs", paras); 
			Object obj = client.execute(pRequest);
			if (null != obj) {
				Object[] objs = (Object[]) obj;
				if (null != objs[0]) {
					Map<String, String> infos = (Map<String, String>) objs[0];
					return infos.get("blogName");
				}
			}
			return account.getServerUrl();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		}
	}
	
	public String getSiteName1(Account account) throws Exception {
		XmlRpcClient client = null;
		try {
			client = new XmlRpcClient();
			XmlRpcClientConfigImpl pConfig = new XmlRpcClientConfigImpl();
			pConfig.setBasicUserName(account.getUserName());
			pConfig.setBasicPassword(account.getUserPwd());
			pConfig.setServerURL(new URL(account.getServerUrl()));
			client.setConfig(pConfig);
			Object[] paras = new Object[]{account.getUserName(), account.getUserPwd()};
			
			XmlRpcRequest pRequest = new XmlRpcClientRequestImpl(pConfig, "wp.getUsersBlogs", paras); 
			Object obj = client.execute(pRequest);
			if (null != obj) {
				Object[] objs = (Object[]) obj;
				if (null != objs[0]) {
					Map<String, String> infos = (Map<String, String>) objs[0];
					return infos.get("blogName");
				}
			}
			return account.getServerUrl();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		}
	}

	@Override
	public String publishAsDraft(Account account, String categoryValue, String title, String content) throws Exception {
		return this.metaWeblogPublish(account, categoryValue, title, content, true);
	}

	@Override
	public String publish(Account account, String categoryValue, String title, String content) throws Exception {
		return this.metaWeblogPublish(account, categoryValue, title, content, false);
	}
	
	public String publish(Account account, String categoryValue, String title, String content, boolean isDraft) throws Exception {
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
			post.put("post_title", title);
			//post.put("post_category", new Integer[]{categoryId, 2});
			Map<Integer, String> mp = new HashMap<Integer, String>();
			mp.put(8, "google map");
			System.out.println("categoryValue: " + categoryValue);
			if (null != categoryValue) {
				post.put("categories", new String[]{categoryValue});
			}
			
			System.out.println("categoryId     " + 481589);
			if (null != content && content.length() > 3) {
				System.out.println("content : \r\n" + content);
				post.put("post_content", content);
			} else {
				post.put("post_content", "<h3>this is content with h3 tag</h3>");
			}
			
			Object[] paras = new Object[]{account.getId(),  account.getUserName(), account.getUserPwd(), post, true};
			
			XmlRpcRequest pRequest = new XmlRpcClientRequestImpl(pConfig, "wp.newPost", paras);
			//XmlRpcRequest pRequest = new XmlRpcClientRequestImpl(pConfig, "blogger.newPost", paras); 
			Object obj = client.execute(pRequest);
			return this.getBlogLink(account, obj.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		}
	}
	
	public String metaWeblogPublish(Account account, String categoryValue, String title, String content, boolean isDraft) throws Exception {
		XmlRpcClient client = null;
		try {
			client = new XmlRpcClient();
			XmlRpcClientConfigImpl pConfig = new XmlRpcClientConfigImpl();
			pConfig.setBasicUserName(account.getUserName());
			pConfig.setBasicPassword(account.getUserPwd());
			pConfig.setServerURL(new URL(account.getServerUrl()));
			client.setConfig(pConfig);
			
			Map<String, Object> post = new HashMap<String, Object>();
			post.put("title", title);
			if (null != categoryValue) {
				post.put("categories", new String[]{categoryValue});
			}
			System.out.println("categoryId     " + categoryValue);
			if (null != content && content.length() > 3) {
				System.out.println("content : \r\n" + content);
				post.put("description", content);
			} else {
				post.put("description", "<h3>No Content!</h3>");
			}
			
			Object[] paras = new Object[]{account.getId(),  account.getUserName(), account.getUserPwd(), post, !isDraft};
			
			XmlRpcRequest pRequest = new XmlRpcClientRequestImpl(pConfig, "metaWeblog.newPost", paras);
			//XmlRpcRequest pRequest = new XmlRpcClientRequestImpl(pConfig, "blogger.newPost", paras); 
			Object obj = client.execute(pRequest);
			return this.getBlogLink(account, obj.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
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
				Map<String, String> infos = (Map<String, String>) obj;
				return infos.get("link"); 
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
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
			Object[] paras = new Object[]{account.getId(), account.getUserName(), account.getUserPwd(), postId};
			
			XmlRpcRequest pRequest = new XmlRpcClientRequestImpl(pConfig, "wp.getPost", paras); 
			Object obj = client.execute(pRequest);
			if (null != obj) {
				Map<String, String> infos = (Map<String, String>) obj;
				return infos.get("link"); 
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		}
		
		return null;
	}
	
	
	private int getBlogId(Account account) throws Exception {
		XmlRpcClient client = null;
		try {
			client = new XmlRpcClient();
			XmlRpcClientConfigImpl pConfig = new XmlRpcClientConfigImpl();
			pConfig.setBasicUserName(account.getUserName());
			pConfig.setBasicPassword(account.getUserPwd());
			pConfig.setServerURL(new URL(account.getServerUrl()));
			client.setConfig(pConfig);
			Object[] paras = new Object[]{account.getUserName(), account.getUserPwd()};
			
			XmlRpcRequest pRequest = new XmlRpcClientRequestImpl(pConfig, "wp.getUsersBlogs", paras); 
			Object obj = client.execute(pRequest);
			if (null != obj) {
				Object[] objs = (Object[]) obj;
				if (null != objs[0]) {
					Map<String, String> infos = (Map<String, String>) objs[0];
					return Integer.valueOf(infos.get("blogid")); //blogName
				}
			}
			return 0;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		} catch (XmlRpcException e) {
			e.printStackTrace();
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			throw e;
		}
	}
	
	public static void main(String[] args) throws Exception {
		Account account = AccountMgr.getDefaultAccount();
		//System.out.println(new BlogWordpress().getBlogId(account));
		/*new BlogWordpress().getCategories(account);*/
		//System.out.println(new BlogWordpress().getBlogLink(account, "647"));
		new BlogWordpress().publish(account, "cool", "text", "content");
		
	}

}
