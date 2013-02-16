package com.tianxiaohui.qingpublish.ui;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class SetupPage extends DialogPage {

	public SetupPage(String title) {
		super(title);
		setDescription("Blog Publisher Setup.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("Current Accounts");

		final Table table = new Table (container, SWT.VIRTUAL | SWT.BORDER);
		  table.setItemCount (100);
		 table.addListener (SWT.SetData, new Listener () {
		       public void handleEvent (Event event) {
		           TableItem item = (TableItem) event.item;
		           int index = table.indexOf (item);
		           item.setText ("Item " + index);
		           System.out.println (item.getText ());
		       }
		   }); 
		 
		 final Button addAcount = new Button(container, SWT.NORMAL);
		 addAcount.setText("Add Account");
		 final Button editAcount = new Button(container, SWT.NORMAL);
		 editAcount.setText("Edit Account");
		 
		 this.setControl(container);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
