package com.tianxiaohui.qingpublish.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.tianxiaohui.qingpublish.ui.AccountSetupDialog;

public class SetupAccount implements IObjectActionDelegate {
	private Shell shell;
	@Override
	public void run(IAction action) {
		AccountSetupDialog asd = new AccountSetupDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		asd.open();
		/*MessageDialog.openInformation(shell, "Publish Blog", "setup account!");
		SetupPage sp = new SetupPage("setup");
		//sp.createControl(parent)
		
		sp.createControl(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		try {
			//PlatformUI.getWorkbench().getActiveWorkbenchWindow().openPage(null);
			//PlatformUI.getWorkbench().getHelpSystem().setHelp(this.getClass(), "");
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
		System.out.println("done");*/
		//PlatformUI.getWorkbench().getActiveWorkbenchWindow().openPage(s)

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
