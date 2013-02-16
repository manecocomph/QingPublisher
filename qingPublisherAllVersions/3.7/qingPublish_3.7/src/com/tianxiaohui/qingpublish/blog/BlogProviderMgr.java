package com.tianxiaohui.qingpublish.blog;

public class BlogProviderMgr {

	public static BlogProvider getBlogProvider(int accountType) {
		return new BlogWordpress();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
