package com.tianxiaohui.qingpublish.ui;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import com.tianxiaohui.qingpublish.Account;
import com.tianxiaohui.qingpublish.AccountMgr;

public class AccountSetupDialog extends Dialog {

	public  AccountSetupDialog(Shell shell) {
		super(shell);
	}

	@Override
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
		//buttonBar = createButtonBar(composite);
				
		return composite;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		//super.createDialogArea(parent);
		Composite container = new Composite(parent, SWT.NONE);
		container.setSize(800, 700);
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		container.setLayout(gl);
		
		Label accountsLabel = new Label(container, SWT.BORDER_SOLID);
		accountsLabel.setText("Accounts:");
		accountsLabel.setSize(500, 20);
		GridData algd = new GridData();
		algd.widthHint = 500;
		accountsLabel.setLayoutData(algd);
		
		Table accountsTable = new Table(container, SWT.VIRTUAL | SWT.BORDER);
		GridData atgd = new GridData();
		atgd.widthHint = 500;
		accountsTable.setLayoutData(atgd);
		accountsTable.setHeaderVisible(true);
		//accountsTable.setItemCount(6);
		TableColumn accountType = new TableColumn(accountsTable, SWT.NONE);
		TableColumn account = new TableColumn(accountsTable, SWT.NONE);
		TableColumn accountName = new TableColumn(accountsTable, SWT.NONE);
		TableColumn accountSite = new TableColumn(accountsTable, SWT.NONE);
		accountType.setText("Type");
		accountType.setWidth(90);
		account.setText("Account");
		account.setWidth(250);
		accountName.setText(" user Name");
		accountName.setWidth(150);
		accountSite.setText("Website URL");
		accountSite.setWidth(50);
		
		List<Account> accountList = AccountMgr.getAccountList();
		System.out.println("accountList(): " + accountList);
		if (null != accountList) {
			TableItem ti = null;
			for (Account oneAccount : accountList) {
				ti = new TableItem(accountsTable, SWT.NONE);
				ti.setText(new String[] {String.valueOf(oneAccount.getAccountType()), oneAccount.getClass().toString(), oneAccount.getUserName(),
						oneAccount.getSiteName() });
			}
			accountType.pack();
			account.pack();
			accountName.pack();
			accountSite.pack();
		}
		
		Composite actions = new Composite(container, SWT.NONE);
		RowLayout rl = new RowLayout();
		rl.center = true;
		actions.setLayout(rl);
		Button add = new Button(actions, SWT.NONE);
		add.setText("Add Account");
		Listener listener = new Listener () {
			public void handleEvent (Event e) {
				AddAccountDialog aad = new AddAccountDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				aad.open();
				close();
			}
		};
		add.addListener(SWT.MouseDown, listener);
		
		Button edit = new Button(actions, SWT.NONE);
		edit.setText("Edit Account");
		edit.setEnabled(false);
		
		Button delete = new Button(actions, SWT.NONE);
		delete.setText("Delete");
		delete.setEnabled(false);
		
		return container;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
