package com.tianxiaohui.qingpublish.ui;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
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

import com.tianxiaohui.qingpublish.Account;
import com.tianxiaohui.qingpublish.AccountMgr;

public class AccountPanel extends Dialog {

	private Shell shell;
	public  AccountPanel(Shell shell) {
		super(shell);
		this.shell = shell;
	}

	protected Control createContents(Composite parent) {
		parent.getShell().setText("Account Management");
		
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
	
	private void drawAccountList(final Table accountsTable) {
		List<Account> accountList = AccountMgr.getAccountList();
		System.out.println("accountList(): " + accountList);
		if (null != accountList) {
			TableItem ti = null;
			for (Account oneAccount : accountList) {
				ti = new TableItem(accountsTable, SWT.NONE);
				ti.setText(new String[] {String.valueOf(oneAccount.getId()), String.valueOf(oneAccount.getAccountType()), oneAccount.getSiteName(), oneAccount.getUserName(),
						oneAccount.getServerUrl(), oneAccount.isDefaulted() ? "Y" : "N" });
			}
		}
	}
	
	protected Control createDialogArea(Composite parent) {
		//super.createDialogArea(parent);
		Composite container = new Composite(parent, SWT.NONE);
		container.setSize(980, 700);
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		container.setLayout(gl);
		
		
		Label accountsLabel = new Label(container, SWT.BORDER_SOLID);
		accountsLabel.setText("Accounts:");
		accountsLabel.setSize(500, 20);
		GridData algd = new GridData();
		algd.widthHint = 500;
		accountsLabel.setLayoutData(algd);
		
		final Table accountsTable = new Table(container, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		GridData atgd = new GridData(SWT.FILL, SWT.FILL, true, true);
		atgd.minimumWidth = 600;
		atgd.widthHint = 700;
		accountsTable.setLayoutData(atgd);
		accountsTable.setHeaderVisible(true);
		accountsTable.setLinesVisible (true);
		accountsTable.setLayout(new FillLayout());
		//accountsTable.setItemCount(4);
		accountsTable.setRedraw(true);
		//accountsTable.setItemCount(6);
		/*TableColumnLayout layout = new TableColumnLayout();
		accountsTable.setLayout( layout );*/
		TableColumn seq = new TableColumn(accountsTable, SWT.NONE);
		TableColumn accountType = new TableColumn(accountsTable, SWT.NONE);
		TableColumn account = new TableColumn(accountsTable, SWT.NONE);
		TableColumn accountName = new TableColumn(accountsTable, SWT.NONE);
		TableColumn accountSite = new TableColumn(accountsTable, SWT.NONE);
		TableColumn accountDefault = new TableColumn(accountsTable, SWT.NONE);
		seq.setText("Seq");
		seq.setWidth(32);
		accountType.setText("Type");
		accountType.setWidth(92);
		account.setText("Account");
		account.setWidth(180);
		accountName.setText("User Name");
		accountName.setWidth(100);
		accountSite.setText("Website URL");
		accountSite.setWidth(120);
		accountDefault.setText("Default");
		accountDefault.setWidth(50);
		
		this.drawAccountList(accountsTable);
		
		Composite actions = new Composite(container, SWT.NONE);
		RowLayout rl = new RowLayout();
		rl.center = true;
		actions.setLayout(rl);
		Button add = new Button(actions, SWT.NONE);
		add.setText("Add Account");
		Listener listener = new Listener () {
			public void handleEvent (Event e) {
				AddAccount aad = new AddAccount(shell);
				aad.open();
				accountsTable.clearAll();
				accountsTable.update();
				accountsTable.redraw();
				accountsTable.pack();
				drawAccountList(accountsTable);
			}
		};
		add.addListener(SWT.MouseDown, listener);
		
		final Button edit = new Button(actions, SWT.NONE);
		edit.setText("Edit Account");
		edit.setEnabled(false);
		edit.addListener(SWT.MouseDown, new Listener(){
			public void handleEvent(Event event) {
				
			}
		});
		
		final Button delete = new Button(actions, SWT.NONE);
		delete.setText("Delete");
		delete.setEnabled(false);
		delete.addListener(SWT.MouseDown, new Listener(){
			public void handleEvent(Event event) {
				
				System.out.println ("in delete : " + accountsTable.getSelectionCount());
				System.out.println ("in delete : " + accountsTable.getSelectionIndex());
				System.out.println ("in delete8 : " + accountsTable.getSelectionIndices());
				TableItem[] tis = accountsTable.getSelection();
				
				for (TableItem ti : tis) {
					System.out.println ("in delete5 : " + ti.getText(0));
					try {
						AccountMgr.deleteAccount(Integer.valueOf(ti.getText()));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				accountsTable.remove(accountsTable.getSelectionIndex());
				accountsTable.clearAll();
				accountsTable.update();
				accountsTable.redraw();
				accountsTable.pack();
				drawAccountList(accountsTable);
			}
		});

		accountsTable.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				edit.setEnabled(true);
				delete.setEnabled(true);
				/*
				if ((SWT.SELECTED == event.detail) && event.item instanceof TableItem) {
					TableItem ti = (TableItem) event.item;
					System.out.println(ti.getText(1));
					selectedAccount.delete(0, selectedAccount.length());
					selectedAccount.append(ti.getText(1));
				}*/
			}
		});
		//container.pack();
		
		return container;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
