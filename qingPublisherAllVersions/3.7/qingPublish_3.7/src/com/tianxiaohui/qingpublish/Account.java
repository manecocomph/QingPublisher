package com.tianxiaohui.qingpublish;

import java.util.Date;
import java.util.Map;

public class Account {

	private int id;
	private String date;
	private int accountType;
	private String userName;
	private String userPwd;
	private String serverUrl;
	private String siteName;
	private boolean defaulted;
	private Map<String, String> otherConfig;
	
	public Account() {
		this.date = new Date().toString();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAccountType() {
		return accountType;
	}


	public void setAccountType(int accountType) {
		this.accountType = accountType;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getUserPwd() {
		return userPwd;
	}


	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public boolean isDefaulted() {
		return defaulted;
	}


	public void setDefaulted(boolean defaulted) {
		this.defaulted = defaulted;
	}


	public Map<String, String> getOtherConfig() {
		return otherConfig;
	}


	public void setOtherConfig(Map<String, String> otherConfig) {
		this.otherConfig = otherConfig;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
