package com.tianxiaohui.qingpublish.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.tianxiaohui.qingpublish.Account;
import com.tianxiaohui.qingpublish.AccountMgr;
import com.tianxiaohui.qingpublish.Activator;
import com.tianxiaohui.qingpublish.blog.BlogProvider;

public class BlogEditor extends Dialog {
	private ILog log = Activator.getDefault().getLog();
	
	private Shell shell;
	private File blogFile = null;
	private static int WIDTH = 960;
	private static int LINE_HEIGHT = 26;
	/*private final Map<Integer, Integer> categoryMapping = new HashMap<Integer, Integer>();*/
	
	private Text titleText = null;
	private StyledText headerText = null;
	private StyledText tailText = null;
	
	private String fontName = "Arial"; //Courier, 
	private Font pageFont = null;
	private Font titleFont = null;
	private Font textFont = null;
	
	public BlogEditor(Shell parentShell, File blogFile) {
		super(parentShell);
		this.shell = parentShell;
		this.shell.setSize(900, 800);
		this.blogFile = blogFile;
		
		pageFont = new Font(shell.getDisplay(), new FontData(fontName, 13, SWT.NORMAL));
		titleFont = new Font(shell.getDisplay(), new FontData(fontName, 18, SWT.BOLD));
		textFont = new Font(shell.getDisplay(), new FontData(fontName, 15, SWT.NORMAL));
	}

	/**
	 * override this method in order to remove the "ok" & "cancel" button
	 * @param parent
	 * @return
	 */
	
	protected Control createContents(Composite parent) {
		parent.getShell().setText("Blog Publisher");
		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// initialize the dialog units
		initializeDialogUnits(composite);
		// create the dialog area and button bar
		dialogArea = createDialogArea(composite);
		//buttonBar = createButtonBar(composite);
		//JFaceResources.getFont(symbolicName)
		
		return composite;
	}
	
	private void createMetaDataArea(Composite parent) {
		// first line container
		Composite top = new Composite(parent, SWT.NONE);
		top.setSize(BlogEditor.WIDTH, BlogEditor.LINE_HEIGHT);
		
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.marginHeight = 8;
		layout.justify = true;
		top.setLayout(layout);
		
		/*RowData topRowData = new RowData();
		topRowData.height = BlogEditor.LINE_HEIGHT;
		topRowData.width = BlogEditor.WIDTH;
		top.setLayoutData(topRowData);*/
		/*GridData topGridData = new GridData();
		topGridData.heightHint = BlogEditor.LINE_HEIGHT;
		topGridData.widthHint = BlogEditor.WIDTH;
		top.setLayoutData(topGridData);*/
		
		Button addAccount = new Button(top, SWT.NONE);
		addAccount.setText("Accounts");
		addAccount.setFont(pageFont);
		addAccount.addListener(SWT.MouseDown, new Listener(){
			
			public void handleEvent(Event event) {
				new AccountPanel(shell).open();
			}
		});
		
		final Combo currentAccounts = new Combo(top, SWT.READ_ONLY);
		currentAccounts.setFont(pageFont);
		currentAccounts.select(0);
		List<Account> accountList = AccountMgr.getAccountList();
		if (null != accountList && accountList.size() > 0) {
			int i = 0;
			for (Account account : accountList) {
				if (null != account.getSiteName()) {
					currentAccounts.add(account.getSiteName(), i++);
					//currentAccounts.add
				}
			}
		}
		currentAccounts.setSize(200, BlogEditor.LINE_HEIGHT);
		//currentAccounts.set
		/*RowData currentAccountsRowData = new RowData();
		currentAccountsRowData.width = 200;
		currentAccountsRowData.height = BlogEditor.LINE_HEIGHT;
		currentAccounts.setLayoutData(currentAccountsRowData);*/
		
		
		Label categoryLabel = new Label(top, SWT.NONE);
		categoryLabel.setFont(pageFont);
		categoryLabel.setText("Category: ");
		
		//TODO get category from different blog type
		final Combo categoriesCombo = new Combo(top, SWT.READ_ONLY);
		categoriesCombo.setFont(pageFont);
		/*categoriesCombo.add("linux", 0);
		categoriesCombo.add("life", 1);*/
		categoriesCombo.setSize(60, LINE_HEIGHT);
		RowData categoriesRowData = new RowData();
		categoriesRowData.width = 200;
		categoriesRowData.height = BlogEditor.LINE_HEIGHT;
		categoriesCombo.setLayoutData(categoriesRowData);
		
		Button adDraft = new Button(top, SWT.NONE);
		adDraft.setFont(pageFont);
		adDraft.setText("Publish As Draft");
		
		Button asBlog = new Button(top, SWT.NONE);
		asBlog.setFont(pageFont);
		asBlog.setText("Publish");
		
		/*currentAccounts.addListener(SWT.Selection, new Listener(){
			
			public void handleEvent(Event event) {
				String choosed = currentAccounts.getText();
				List<Account> accountList = AccountMgr.getAccountList();
				if (null != accountList && accountList.size() > 0) {
					for (Account account : accountList) {
						if (choosed.equalsIgnoreCase(account.getSiteName())) {
							Map<String, String> categories = null;
							try {
								categories = BlogProviderMgr.getBlogProvider(account.getAccountType()).getCategories(account);
							} catch (Exception e) {
								e.printStackTrace();
								log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
								MessageDialog.openInformation(shell, "Get Category Fail", e.getMessage());
								return;
							}
							
							if (null != categories) {
								categoryMapping.clear();
								int i = 0;
								for (Map.Entry<String, String> entry : categories.entrySet()) {
									categoryMapping.put(Integer.valueOf(i), Integer.valueOf(entry.getKey()));
									categoriesCombo.add(entry.getValue(), i++);
									System.out.println("entry.getValue()" + entry.getValue());
								}
								categoriesCombo.pack();
								categoriesCombo.redraw();
							}
						}
					}
				}
			}
		});*/
		
		currentAccounts.addListener(SWT.Selection, new Listener(){
			
			public void handleEvent(Event event) {
				String choosed = currentAccounts.getText();
				List<Account> accountList = AccountMgr.getAccountList();
				if (null != accountList && accountList.size() > 0) {
					for (Account account : accountList) {
						if (choosed.equalsIgnoreCase(account.getSiteName())) {
							try {
								BlogProvider.getBlogProvider(account.getAccountType()).asyncGetCategories(shell.getDisplay(), account, categoriesCombo);
							} catch (Exception e) {
								e.printStackTrace();
								log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
								MessageDialog.openInformation(shell, "Get Category Fail", e.getMessage());
								return;
							}
						}
					}
				}
			}
		});
		
		adDraft.addListener(SWT.MouseDown, new Listener() {
			
			public void handleEvent(Event event) {
				if (0 > currentAccounts.getSelectionIndex()) {
					MessageDialog.openInformation(shell, "Publish Blog", "No Account Selected");
					return;
				}
				String accountName = currentAccounts.getText();
				List<Account> accountList = AccountMgr.getAccountList();
				for (Account account : accountList) {
					if (accountName.equals(account.getSiteName())) {
						/*int categoryId = 0;
						if (null != categoryMapping) {
							categoryId = categoryMapping.get(categoriesCombo.getSelectionIndex());
						}*/
						String categoryValue = categoriesCombo.getText();
						String title = titleText.getText();
						String content = assembleBlogContent();
						String link = null;
						try {
							link = BlogProvider.getBlogProvider(account.getAccountType()).publishAsDraft(account, categoryValue, title, content);
						} catch (Exception e) {
							e.printStackTrace();
							log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
							MessageDialog.openInformation(shell, "Publish Blog Fail", e.getMessage());
							return;
						}
						MessageDialog.openInformation(shell, "Publish Blog Success", link);
					}
				}
			}
			
		});
		
		asBlog.addListener(SWT.MouseDown, new Listener() {
			
			public void handleEvent(Event event) {
				if (0 > currentAccounts.getSelectionIndex()) {
					MessageDialog.openInformation(shell, "Publish Blog", "No Account Selected");
					return;
				}
				String accountName = currentAccounts.getText();
				List<Account> accountList = AccountMgr.getAccountList();
				for (Account account : accountList) {
					if (accountName.equals(account.getSiteName())) {
						//int categoryId = categoryMapping.get(currentAccounts.getSelectionIndex());
						String categoryValue = categoriesCombo.getText();
						String title = titleText.getText();
						String content = assembleBlogContent();
						String link;
						try {
							link = BlogProvider.getBlogProvider(account.getAccountType()).publish(account, categoryValue, title, content);
						} catch (Exception e) {
							e.printStackTrace();
							log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
							MessageDialog.openInformation(shell, "Publish Blog Fail", e.getMessage());
							return;
						}
						MessageDialog.openInformation(shell, "Publish Blog Success", link);
					}
				}
			}
		});
	}
	
/*	private void createMetaDataArea1(Composite parent) {
		// first line container
		Composite top = new Composite(parent, SWT.BORDER_DASH);
		top.setSize(BlogEditor.WIDTH, BlogEditor.LINE_HEIGHT);
		
		FormLayout layout = new FormLayout();
		layout.marginHeight = 8;
		layout.marginWidth = 5;
		FormData topFormData = new FormData();
		topFormData.width = WIDTH;
		topFormData.right = new FormAttachment(100, -10);
		top.setSize(WIDTH, LINE_HEIGHT);
		top.setLayout(layout);
		
		RowData topRowData = new RowData();
		topRowData.height = BlogEditor.LINE_HEIGHT;
		topRowData.width = BlogEditor.WIDTH;
		top.setLayoutData(topRowData);
		GridData topGridData = new GridData();
		topGridData.heightHint = BlogEditor.LINE_HEIGHT;
		topGridData.widthHint = BlogEditor.WIDTH;
		top.setLayoutData(topGridData);
		
		Button addAccount = new Button(top, SWT.NONE);
		addAccount.setText("Accounts");
		addAccount.addListener(SWT.MouseDown, new Listener(){
			
			public void handleEvent(Event event) {
				new AccountPanel(shell).open();
			}
		});
		
		final Combo currentAccounts = new Combo(top, SWT.READ_ONLY);
		currentAccounts.select(0);
		List<Account> accountList = AccountMgr.getAccountList();
		if (null != accountList && accountList.size() > 0) {
			int i = 0;
			for (Account account : accountList) {
				if (null != account.getSiteName()) {
					currentAccounts.add(account.getSiteName(), i++);
					//currentAccounts.add
				}
			}
		}
		currentAccounts.setSize(200, BlogEditor.LINE_HEIGHT);
		//currentAccounts.set
		RowData currentAccountsRowData = new RowData();
		currentAccountsRowData.width = 200;
		currentAccountsRowData.height = BlogEditor.LINE_HEIGHT;
		currentAccounts.setLayoutData(currentAccountsRowData);
		
		
		Label categoryLabel = new Label(top, SWT.NONE);
		categoryLabel.setText("Category: ");
		
		//TODO get category from different blog type
		final Combo categoriesCombo = new Combo(top, SWT.READ_ONLY);
		categoriesCombo.add("linux", 0);
		categoriesCombo.add("life", 1);
		categoriesCombo.setSize(60, LINE_HEIGHT);
		RowData categoriesRowData = new RowData();
		categoriesRowData.width = 200;
		categoriesRowData.height = BlogEditor.LINE_HEIGHT;
		categories.setLayoutData(categoriesRowData);
		
		Button adDraft = new Button(top, SWT.NONE);
		adDraft.setText("Publish As Draft");
		FormData data = new FormData();
		data.width = 500;
		data.left = new FormAttachment(100, 390);
		data.right = new FormAttachment(parent, -190);
		
		Button asBlog = new Button(top, SWT.NONE);
		asBlog.setText("Publish");
		
		currentAccounts.addListener(SWT.Selection, new Listener(){
			
			public void handleEvent(Event event) {
				String choosed = currentAccounts.getText();
				List<Account> accountList = AccountMgr.getAccountList();
				if (null != accountList && accountList.size() > 0) {
					for (Account account : accountList) {
						if (choosed.equalsIgnoreCase(account.getSiteName())) {
							Map<String, String> categories = null;
							try {
								categories = BlogProviderMgr.getBlogProvider(account.getAccountType()).getCategories(account);
							} catch (Exception e) {
								e.printStackTrace();
								log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
								MessageDialog.openInformation(shell, "Get Category Fail", e.getMessage());
								return;
							}
							
							if (null != categories) {
								categoryMapping.clear();
								int i = 0;
								for (Map.Entry<String, String> entry : categories.entrySet()) {
									categoryMapping.put(Integer.valueOf(i), Integer.valueOf(entry.getKey()));
									categoriesCombo.add(entry.getValue(), i++);
									System.out.println("entry.getValue()" + entry.getValue());
								}
								categoriesCombo.pack();
								categoriesCombo.redraw();
							}
						}
					}
				}
			}
		});
		
		currentAccounts.addListener(SWT.Selection, new Listener(){
			
			public void handleEvent(Event event) {
				String choosed = currentAccounts.getText();
				List<Account> accountList = AccountMgr.getAccountList();
				if (null != accountList && accountList.size() > 0) {
					for (Account account : accountList) {
						if (choosed.equalsIgnoreCase(account.getSiteName())) {
							try {
								BlogProviderMgr.getBlogProvider(account.getAccountType()).asyncGetCategories(shell.getDisplay(), account, categoriesCombo);
							} catch (Exception e) {
								e.printStackTrace();
								log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
								MessageDialog.openInformation(shell, "Get Category Fail", e.getMessage());
								return;
							}
						}
					}
				}
			}
		});
		
		adDraft.addListener(SWT.MouseDown, new Listener() {
			
			public void handleEvent(Event event) {
				if (0 > currentAccounts.getSelectionIndex()) {
					MessageDialog.openInformation(shell, "Publish Blog", "No Account Selected");
					return;
				}
				String accountName = currentAccounts.getText();
				List<Account> accountList = AccountMgr.getAccountList();
				for (Account account : accountList) {
					if (accountName.equals(account.getSiteName())) {
						int categoryId = 0;
						if (null != categoryMapping) {
							categoryId = categoryMapping.get(categoriesCombo.getSelectionIndex());
						}
						String categoryValue = categoriesCombo.getText();
						String title = titleText.getText();
						String content = assembleBlogContent();
						String link = null;
						try {
							link = BlogProviderMgr.getBlogProvider(account.getAccountType()).publishAsDraft(account, categoryValue, title, content);
						} catch (Exception e) {
							e.printStackTrace();
							log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
							MessageDialog.openInformation(shell, "Publish Blog Fail", e.getMessage());
							return;
						}
						MessageDialog.openInformation(shell, "Publish Blog Success", link);
					}
				}
			}
			
		});
		
		asBlog.addListener(SWT.MouseDown, new Listener() {
			
			public void handleEvent(Event event) {
				if (0 > currentAccounts.getSelectionIndex()) {
					MessageDialog.openInformation(shell, "Publish Blog", "No Account Selected");
					return;
				}
				String accountName = currentAccounts.getText();
				List<Account> accountList = AccountMgr.getAccountList();
				for (Account account : accountList) {
					if (accountName.equals(account.getSiteName())) {
						//int categoryId = categoryMapping.get(currentAccounts.getSelectionIndex());
						String categoryValue = categoriesCombo.getText();
						String title = titleText.getText();
						String content = assembleBlogContent();
						String link;
						try {
							link = BlogProviderMgr.getBlogProvider(account.getAccountType()).publish(account, categoryValue, title, content);
						} catch (Exception e) {
							e.printStackTrace();
							log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
							MessageDialog.openInformation(shell, "Publish Blog Fail", e.getMessage());
							return;
						}
						MessageDialog.openInformation(shell, "Publish Blog Success", link);
					}
				}
			}
		});
	}*/
	
	private String assembleBlogContent() {
		String header = this.headerText.getText();
		String tail = this.tailText.getText();
		
		StringBuffer sb = new StringBuffer("");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(this.blogFile));
			String line = br.readLine();
			while (null != line) {
				sb.append(line);
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String code = sb.append("").toString();
		
		return header + code + tail;
	}
	/**
	 * 
	 * @param parent
	 */
	private void createTitleLine(Composite parent) {
		// create the title line container
		Composite titleContainer = new Composite(parent, SWT.NONE);
		titleContainer.setSize(WIDTH, LINE_HEIGHT);
		
		RowLayout titleLayout = new RowLayout(SWT.HORIZONTAL);
		titleLayout.justify = true;
		titleLayout.center = true;
		titleContainer.setLayout(titleLayout);
		
		/*RowData titleRowData = new RowData();
		titleRowData.height = BlogEditor.LINE_HEIGHT;
		titleRowData.width = BlogEditor.WIDTH;
		titleContainer.setLayoutData(titleRowData);*/
		
		Label titleLabel = new Label(titleContainer, SWT.NONE);
		titleLabel.setText("Title: ");
		titleLabel.setSize(90, LINE_HEIGHT);
		titleLabel.setFont(pageFont);
		RowData titleRowData = new RowData();
		titleRowData.width = 90;
		titleRowData.height = LINE_HEIGHT;
		titleLabel.setLayoutData(titleRowData);
		
		titleText = new Text(titleContainer, SWT.SINGLE | SWT.BORDER | SWT.CENTER);
		titleText.setSize(830, LINE_HEIGHT);
		titleText.setFont(titleFont);
		RowData rw = new RowData();
		rw.width = 830;
		rw.height = LINE_HEIGHT;
		titleText.setLayoutData(rw);
	}
	
	protected Control createDialogArea(Composite parent) {
		// create metadata area
		this.createMetaDataArea(parent);
		
		// title line
		this.createTitleLine(parent);
		
		// header area
		headerText = new StyledText(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		headerText.setEditable(true);
		headerText.setSize(WIDTH, 100);
		headerText.setFont(textFont);
		
		GridLayout gly = new GridLayout();
		headerText.setLayout(gly);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 100;
		gridData.widthHint = WIDTH;
		gridData.minimumHeight = 100;
		gridData.minimumWidth = WIDTH;
		gridData.minimumHeight = 60;
		gridData.horizontalSpan = 2; //TODO check if need win + linux
		headerText.setLayoutData(gridData);
		
		// Browser part
		Browser browser = new Browser(parent, SWT.NONE);
		browser.setSize(WIDTH, 420);
		browser.setUrl("file:///" + this.blogFile.toString());
		
	    gly = new GridLayout();
	    browser.setLayout(gly);
	    
	    gridData = new GridData(GridData.FILL_BOTH);
	    gridData.heightHint = 420;
	    gridData.minimumHeight = 420;
	    gridData.minimumWidth = 800;
	    gridData.verticalSpan = 3;
	    browser.setLayoutData(gridData);
	    
		// tail part
		tailText = new StyledText(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tailText.setFont(textFont);
		tailText.setEditable(true);
		tailText.setSize(WIDTH, 90);
		// tailText.setHorizontalPixel(1000);
		
		GridData tailgd = new GridData(GridData.FILL_BOTH);
		tailgd.minimumWidth = WIDTH;
		tailgd.minimumHeight = 90;
		tailText.setLayoutData(tailgd);
		
		return parent;
	}
}
