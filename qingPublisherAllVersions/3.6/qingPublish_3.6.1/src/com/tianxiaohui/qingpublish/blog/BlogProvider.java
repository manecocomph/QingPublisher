package com.tianxiaohui.qingpublish.blog;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;

import com.tianxiaohui.qingpublish.Account;
import com.tianxiaohui.qingpublish.blog.impl.BlogMetaWeblog;
import com.tianxiaohui.qingpublish.blog.impl.BlogWordpress;

public abstract class BlogProvider {

	/*public void asyncGetCategories(final Display dispaly, Account account, final Combo categoriesCombo, final Map<Integer, Integer> categoryMapping) throws Exception;*/
	public abstract void asyncGetCategories(final Display display, Account account, final Combo categoriesCombo) throws Exception;
	
	/*public Map<String, String> getCategories(Account account) throws Exception;*/
	public abstract String getSiteName(Account account) throws Exception;
	public abstract String publishAsDraft(Account account, String categoryValue, String title, String content) throws Exception;
	public abstract String publish(Account account, String categoryValue, String title, String content) throws Exception;
	
private static Map<String, BlogProvider> providerMapping = null; // save<name, className>
	
	public static BlogProvider getBlogProvider(String blogType) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (null == providerMapping) {
			BlogProvider.initProvideMapping();
		}
		
		return providerMapping.get(blogType);
		//return (BlogProvider) Class.forName(providerMapping.get(blogType)).newInstance();
	}
	
	public static Set<String> getBlogTypeList() {
		if (null == providerMapping) {
			BlogProvider.initProvideMapping();
		}
		
		return providerMapping.keySet();
	}
	
	private static void initProvideMapping() {
		if (null == providerMapping) {
			providerMapping = new HashMap<String, BlogProvider>();
			
			//Add default 2
			providerMapping.put("Wordpress", new BlogWordpress());
			providerMapping.put("metaWeblog", new BlogMetaWeblog());
			
			IExtensionPoint[] ieps = Platform.getExtensionRegistry().getExtensionPoints();
			for (IExtensionPoint iep : ieps) {
				if ("BlogProvider".equalsIgnoreCase(iep.getLabel())) {
					IExtension[] exts = iep.getExtensions();
					for (IExtension ie : exts) {
						/*System.out.println("Iextension label: " + ie.getLabel());
						System.out.println("Iextension label: " + ie.getExtensionPointUniqueIdentifier());
						System.out.println("Iextension label: " + ie.getSimpleIdentifier());
						System.out.println("Iextension label: " + ie.getUniqueIdentifier());*/
						IConfigurationElement[] ices = ie.getConfigurationElements();
						
						for (IConfigurationElement ice : ices) {
							String name = ice.getAttribute("name");
							System.out.println("inter  what I get name: " + name);
							 name = ice.getAttribute("class");
							System.out.println("inter  what I get name: " + name);
							name = ice.getAttribute("urlPattern");
							System.out.println("inter  what I get name: " + name);
							try {
								BlogProvider provider = (BlogProvider) ice.createExecutableExtension("class");
								providerMapping.put(ice.getAttribute("name"), provider);
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					}
				}
			}
		}
	}
	
}
