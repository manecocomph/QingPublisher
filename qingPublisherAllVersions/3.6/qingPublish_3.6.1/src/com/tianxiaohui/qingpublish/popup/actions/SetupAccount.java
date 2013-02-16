package com.tianxiaohui.qingpublish.popup.actions;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.tianxiaohui.qingpublish.ui.AccountPanel;

public class SetupAccount implements IObjectActionDelegate {
	public void run(IAction action) {
		AccountPanel asd = new AccountPanel(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		asd.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		targetPart.getSite().getShell();
	}

}
