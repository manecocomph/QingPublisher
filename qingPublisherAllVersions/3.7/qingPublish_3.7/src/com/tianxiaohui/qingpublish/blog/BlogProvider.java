package com.tianxiaohui.qingpublish.blog;

import java.util.Map;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;

import com.tianxiaohui.qingpublish.Account;

public interface BlogProvider {

	public void asyncGetCategories(final Display dispaly, Account account, final Combo categoriesCombo, final Map<Integer, Integer> categoryMapping) throws Exception;
	
	public Map<String, String> getCategories(Account account) throws Exception;
	public String getSiteName(Account account) throws Exception;
	public String publishAsDraft(Account account, String categoryValue, String title, String content) throws Exception;
	public String publish(Account account, String categoryValue, String title, String content) throws Exception;
}
