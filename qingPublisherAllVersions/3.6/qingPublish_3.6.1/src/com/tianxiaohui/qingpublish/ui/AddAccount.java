package com.tianxiaohui.qingpublish.ui;

import java.util.Set;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.tianxiaohui.qingpublish.Account;
import com.tianxiaohui.qingpublish.AccountMgr;
import com.tianxiaohui.qingpublish.Activator;
import com.tianxiaohui.qingpublish.blog.BlogProvider;

public class AddAccount extends Dialog {
	private static ILog log = Activator.getDefault().getLog();
	private Shell shell = null;
	
	private Combo typeCompo;
	private Text userNameText;
	private Text pwdText;
	private Text serverUrl;
	private Button isDefault;
	
	private static int LINE_HEIGHT = 22;
	
	protected AddAccount(Shell parentShell) {
		super(parentShell);
		shell = parentShell;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.getShell().setText("Add New Account");
		
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout cgl = new GridLayout(2, false);
		container.setLayout(cgl);
		
		//////////////////////
		GridData gd = new GridData();
		gd.minimumWidth = 150;
		gd.widthHint = 150;
		gd.minimumHeight = LINE_HEIGHT;
		gd.heightHint = LINE_HEIGHT;
		//////////////////////
		
		Label typeLabel = new Label(container, SWT.NONE);
		typeLabel.setText("Type");
		
		typeCompo = new Combo(container, SWT.READ_ONLY);
		typeCompo.setLayoutData(gd);
		Set<String> blogTypes = BlogProvider.getBlogTypeList();
		if (null != blogTypes) {
			for (String type : blogTypes) {
				typeCompo.add(type);
			}
		}
		
		Label userNameLabel = new Label(container, SWT.NONE);
		userNameLabel.setText("User Name");
		
		userNameText = new Text(container, SWT.NONE);
		userNameText.setSize(120, LINE_HEIGHT);
		userNameText.setLayoutData(gd);
		
		Label pwdLabel = new Label(container, SWT.NONE);
		pwdLabel.setText("Password");
		
		pwdText = new Text(container, SWT.PASSWORD);
		pwdText.setLayoutData(gd);
		
		Label siteLabel = new Label(container, SWT.NONE);
		siteLabel.setText("Site URL");
		serverUrl = new Text(container, SWT.NONE);
		serverUrl.setLayoutData(gd);
		
		isDefault = new Button (container, SWT.CHECK);
		isDefault.setText("Default");
		
		return super.createDialogArea(parent);
	}

	
	@Override
	protected void okPressed() {
		Account account = new Account();
		
		account.setAccountType(this.typeCompo.getText());
		account.setUserName(this.userNameText.getText());
		account.setUserPwd(this.pwdText.getText());
		account.setServerUrl(this.serverUrl.getText());
		account.setDefaulted(isDefault.getSelection());
		//TODO fix me
		//account.setOtherConfig(otherConfig);
		
		BlogProvider provider;
		try {
			provider = BlogProvider.getBlogProvider(account.getAccountType());
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		String siteName = null;
		try {
			System.out.println(account.getUserName());
			System.out.println(account.getUserPwd());
			System.out.println(account.getServerUrl());
			System.out.println(account.getUserName());
			System.out.println(account.getUserPwd());
			System.out.println(account.getServerUrl());
			siteName = provider.getSiteName(account);
			account.setSiteName(siteName);
		} catch (Exception e) {
			//e.printStackTrace();
			log.log(new Status(Status.ERROR, Activator.PLUGIN_ID, Status.OK, e.getMessage(), e));
			MessageDialog.openInformation(shell, "Test Site Fail", e.getMessage() + ", use site url.");
			//return;
		}
		//account.setSiteName(siteName);
		
		try {
			AccountMgr.addAccount(account);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		MessageDialog.openInformation(shell, "Add Account Success!", account.getSiteName());
		
		super.okPressed();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display ();
	Shell shell = new Shell (display);
	shell.setLayout (new FillLayout ());
	new Label (shell, SWT.SEPARATOR | SWT.HORIZONTAL);
	new Label (shell, SWT.SEPARATOR | SWT.VERTICAL);
	shell.setSize (200, 200);
	shell.open ();
	while (!shell.isDisposed ()) {
		if (!display.readAndDispatch ()) display.sleep ();
	}
	display.dispose ();}

}
