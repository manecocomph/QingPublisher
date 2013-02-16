package com.tianxiaohui.qingpublish;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;

import com.tianxiaohui.qingpublish.blog.BlogProvider;
import com.tianxiaohui.qingpublish.blog.BlogProviderMgr;

/*
 * the config file should be like 
 * <accounts>
 * 		<account>
 * 			<type>1</type>
 * 			<userName>eric</userName>
 * 			<pwd>password</pwd>
 * 			<other>
 * 				<config key="" value=""/>
 * 				<config key="" value=""/>
 * 			</other>
 * 		</account>
 * </accounts>
 */
public class AccountMgr {
	private static ILog log = Activator.getDefault().getLog();
	
	private static final String ACCOUNT_PREFIX = "ACCOUNT_";
	//private static final String ACCOUNT_ID = "_ID";
	private static final String ACCOUNT_TYPE = "_TYPE";
	private static final String ACCOUNT_USER_NAME = "_USER_NAME";
	private static final String ACCOUNT_USER_PWD = "_USER_PWD";
	private static final String ACCOUNT_SERVER_URL = "_SERVER_URL";
	private static final String ACCOUNT_SITE_NAME = "_SITE_NAME";
	private static final String ACCOUNT_IS_DEFAULT = "_DEFAULT";
	private static final String ACCOUNT_OTHER = "_OTHER";
	
	private static File configFile = new File(System.getProperty("user.home") + File.separator + ".qingPublishConfig");
	public static List<Account> getAccountList() {
		if (!configFile.exists()) {
			return null;
		}
		
		Properties config = new Properties();
		try {
			config.load(new FileReader(configFile));
			String accountKey = null;
			Account account = null;
			List<Account> accountList = new ArrayList<Account>();
			for (int i = 0; ; i++) {
				accountKey = config.getProperty(ACCOUNT_PREFIX + i);
				if (null == accountKey) {
					break;
				} else {
					account = new Account();
					account.setId(Integer.valueOf(config.getProperty(ACCOUNT_PREFIX + i)));
					account.setAccountType(Integer.valueOf(config.getProperty(ACCOUNT_PREFIX + i + ACCOUNT_TYPE)));
					account.setUserName(config.getProperty(ACCOUNT_PREFIX + i + ACCOUNT_USER_NAME));
					account.setUserPwd(config.getProperty(ACCOUNT_PREFIX + i + ACCOUNT_USER_PWD));
					account.setServerUrl(config.getProperty(ACCOUNT_PREFIX + i + ACCOUNT_SERVER_URL));
					account.setSiteName(config.getProperty(ACCOUNT_PREFIX + i + ACCOUNT_SITE_NAME));
					account.setDefaulted(Boolean.valueOf(config.getProperty(ACCOUNT_PREFIX + i + ACCOUNT_IS_DEFAULT)));
					account.setOtherConfig(AccountMgr.convertToMap(config.getProperty(ACCOUNT_PREFIX + i + ACCOUNT_OTHER)));
					accountList.add(account);
				}
			}
			
			return accountList;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Account getDefaultAccount() {
		List<Account> accountList = AccountMgr.getAccountList();
		if (null != accountList) {
			for (Account account : accountList) {
				if (account.isDefaulted()) {
					return account;
				}
			}
		}
		
		return null;
	}
	
	public static boolean hasAccount() {
		List<Account> accountList = AccountMgr.getAccountList();
		if (null == accountList || accountList.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
	
	public static void addAccount(Account account) {
		List<Account> accountList = AccountMgr.getAccountList();
		if (null == accountList) {
			accountList = new ArrayList<Account>();
		}
		if (account.isDefaulted()) {
			for (Account oneAccount : accountList) {
				oneAccount.setDefaulted(false);
			}
		}
		
		accountList.add(account);
		AccountMgr.persistToFile(accountList);
	}
	
	private static void persistToFile(List<Account> accountList) {
		System.out.println("account count: " + accountList.size());
		Properties configs = new Properties();
		int i = 0;
		for (Account oneAccount : accountList) {
			configs.setProperty(ACCOUNT_PREFIX + i, String.valueOf(oneAccount.getId()));
			configs.setProperty(ACCOUNT_PREFIX + i + ACCOUNT_TYPE, String.valueOf(oneAccount.getAccountType()));
			configs.setProperty(ACCOUNT_PREFIX + i + ACCOUNT_USER_NAME,  oneAccount.getUserName());
			configs.setProperty(ACCOUNT_PREFIX + i + ACCOUNT_USER_PWD,  oneAccount.getUserPwd());
			configs.setProperty(ACCOUNT_PREFIX + i + ACCOUNT_SERVER_URL, oneAccount.getServerUrl());
			if (null != oneAccount.getSiteName()) {
				configs.setProperty(ACCOUNT_PREFIX + i + ACCOUNT_SITE_NAME, oneAccount.getSiteName());
			} else {
				BlogProvider provider = BlogProviderMgr.getBlogProvider(oneAccount.getAccountType());
				String siteName = null;
				try {
					siteName = provider.getSiteName(oneAccount);
				} catch (Exception e) {
					e.printStackTrace();
					log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
				}
				if (null != siteName) {
					configs.setProperty(ACCOUNT_PREFIX + i + ACCOUNT_SITE_NAME, siteName);
				} else {
					configs.setProperty(ACCOUNT_PREFIX + i + ACCOUNT_SITE_NAME, oneAccount.getServerUrl());
				}
			}
			
			configs.setProperty(ACCOUNT_PREFIX + i + ACCOUNT_OTHER, AccountMgr.convertToString(oneAccount.getOtherConfig()));
			configs.setProperty(ACCOUNT_PREFIX + i + ACCOUNT_IS_DEFAULT, String.valueOf(oneAccount.isDefaulted()));
			i++;
		}
		
		try {
			//configs.save(new FileOutputStream(AccountMgr.configFile), "");
			configs.store(new FileWriter(AccountMgr.configFile), "save to disk now");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String, String> convertToMap(String str) {
		if (null == str) {
			return null;
		} else {
			String[] entries = str.split("`");
			if (null != entries && entries.length > 0) {
				Map<String, String> others = new HashMap<String, String>();
				for (String entry : entries) {
					String[] keyValue = entry.split("~");
					if (null != keyValue & 2 == keyValue.length) {
						others.put(keyValue[0], keyValue[1]);
					}
				}
				
				others.remove(null);
				others.remove("");
				return others;
			} else {
				return null;
			}
		}
	}
	
	public static String convertToString (Map<String, String> map) {
		if (null != map) {
			StringBuffer str = new StringBuffer();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				str.append(entry.getKey() + "~" + entry.getValue() + "`");
			}
			
			return str.toString();
		} else {
			return "";
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*Properties p = System.getProperties();
		for (Map.Entry<Object, Object> entry : p.entrySet()) {
			System.out.println(entry.getKey() + "  " + entry.getValue());
		}*/
		
		Account account = new Account();
		account.setAccountType(0);
		account.setUserName("eric");
		account.setUserPwd("pwd");
		account.setServerUrl("http://tianxiaoui.com");
		account.setDefaulted(true);
		
		System.out.println("hasAccount" + AccountMgr.hasAccount());
		AccountMgr.addAccount(account);
		System.out.println("ddddddddd" + AccountMgr.hasAccount());
		for (Account acc : AccountMgr.getAccountList()) {
			System.out.println("acc " + acc.getServerUrl());
		}
	}

}
