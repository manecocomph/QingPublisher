package com.tianxiaohui.qingpublish.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.tianxiaohui.qingpublish.Account;
import com.tianxiaohui.qingpublish.AccountMgr;

public class AddAccountDialog extends Dialog {

	private Combo typeCompo;
	private Text userNameText;
	private Text pwdText;
	private Text serverUrl;
	
	protected AddAccountDialog(Shell parentShell) {
		super(parentShell);
	}

	/*@Override
	protected Control createContents(Composite parent) {
		// create the top level composite for the dialog
		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);
		// initialize the dialog units
		initializeDialogUnits(composite);
		// create the dialog area and button bar
		dialogArea = createDialogArea(composite);
		buttonBar = createButtonBar(composite);
				
		return composite;
	}*/
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout cgl = new GridLayout(2, false);
		container.setLayout(cgl);
		
		Label typeLabel = new Label(container, SWT.NONE);
		typeLabel.setText("Type");
		typeCompo = new Combo(container, SWT.NONE);
		typeCompo.add("wordpress", 0);
		typeCompo.add("blogger", 0);
		Label userNameLabel = new Label(container, SWT.NONE);
		userNameLabel.setText("User Name");
		userNameText = new Text(container, SWT.NONE);
		Label pwdLabel = new Label(container, SWT.NONE);
		pwdLabel.setText("Password");
		pwdText = new Text(container, SWT.NONE);
		Label siteLabel = new Label(container, SWT.NONE);
		siteLabel.setText("Site URL");
		serverUrl = new Text(container, SWT.NONE);
		
		return super.createDialogArea(parent);
	}

	
	@Override
	protected void okPressed() {
		Account account = new Account();
		
		//TODO fix me
		account.setAccountType(this.typeCompo.getSelectionIndex());
		account.setUserName(this.userNameText.getText());
		account.setUserPwd(this.pwdText.getText());
		account.setServerUrl(this.serverUrl.getText());
		//TODO fix me
		account.setDefaulted(true);
		//TODO fix me
		//account.setOtherConfig(otherConfig);
		//account.setSiteName(siteName);
		
		AccountMgr.addAccount(account);
		
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
