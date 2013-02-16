package com.tianxiaohui.qingpublish.popup.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.tianxiaohui.qingpublish.Account;
import com.tianxiaohui.qingpublish.AccountMgr;
import com.tianxiaohui.qingpublish.blog.BlogProvider;
import com.tianxiaohui.qingpublish.blog.BlogProviderMgr;
import com.tianxiaohui.qingpublish.ui.AccountSetupDialog;

public class MyBlogPublisher implements IObjectActionDelegate {

	private Shell shell;
	
	/**
	 * Constructor for Action1.
	 */
	public MyBlogPublisher() {
		super();
		//http://www.eclipse.org/swt/snippets/
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	// http://augustli.wordpress.com/2010/08/07/eclipse-plugin-adding-custom-editor-context-menu-to-process-selected-text-or-whole-file/
	public void run(IAction action) {
		/*BlogEditor be = new BlogEditor();
		IWorkbenchWindow dwindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();  
		IWorkbenchPage wbPage = dwindow.getActivePage();
		try {
			wbPage.openEditor(be.getEditorInput(), "com.tianxiaohui.qingpublish.BlogEditor");
			MessageDialog.openInformation(shell, "Publish Blog", "opened!");
		} catch (PartInitException e1) {
			e1.printStackTrace();
		}*/
		
		Account defaultAccount = AccountMgr.getDefaultAccount();
		if (null == defaultAccount) {
			MessageDialog.openInformation(shell, "Publish Blog", "No default account set!");
			new AccountSetupDialog(shell).open();
			return;
		}
		
		IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		if (editorPart instanceof ITextEditor) {
			IEditorSite iEditorSite = editorPart.getEditorSite();
			if (null != iEditorSite) {
				ISelectionProvider selectionProvider = iEditorSite.getSelectionProvider();
				if (selectionProvider != null) {
					ISelection iSelection = selectionProvider.getSelection();
					if (!iSelection.isEmpty()) {
						//String selectedText = ((ITextSelection) iSelection).getText();
						int length = ((ITextSelection) iSelection).getLength();
						/*System.out.println("text selected: " + selectedText);
						System.out.println("text length: " + length);*/
						if (length < 1) {
							//MessageDialog.openInformation(shell, "Delte", "New Action was executed.");
							MessageDialog.openInformation(shell, "Publish Blog", "You select nothing to publish!");
							return;
						}
					}
				}
			} else {
				MessageDialog.openInformation(shell, "Publish Blog", "Not supported!");
			}
			
			if (editorPart instanceof ITextEditor) {
				ITextEditor textEditor = (ITextEditor) editorPart;
				
				// COPY THE SELECTED
				System.out.println("textEditor.getAction(IWorkbenchCommandConstants.EDIT_COPY)" + textEditor.getAction(IWorkbenchCommandConstants.EDIT_COPY));
				System.out.println("textEditor.getAction(copy)" + textEditor.getAction("copy"));
				textEditor.getAction("copy").run();
				Clipboard clipBoard = new Clipboard(Display.getCurrent());
				Object rtf = clipBoard.getContents(RTFTransfer.getInstance());

				// IF NO select, then choose all
				if (null == rtf || rtf.toString().length() < 1) {
					textEditor.getAction(
							IWorkbenchCommandConstants.EDIT_SELECT_ALL).run();
					textEditor.getAction(IWorkbenchCommandConstants.EDIT_COPY)
							.run();
					rtf = clipBoard.getContents(RTFTransfer.getInstance());
				}

				String rtfText = rtf.toString();
				System.out.println("rtf I get: " + rtfText);

				BufferedReader br = null;
				StringWriter sw = null;
				RTFEditorKit rtfKit = new RTFEditorKit();
				HTMLEditorKit htmlKit = new HTMLEditorKit();
				//htmlKit.setStyleSheet(arg0)

				try {
					//br = new BufferedReader(new StringReader(rtfText));
					br = new BufferedReader(new StringReader(StringEscapeUtils.escapeHtml(rtfText)));
					StyledDocument doc = (StyledDocument) rtfKit
							.createDefaultDocument();
					rtfKit.read(br, doc, 0);

					sw = new StringWriter();
					htmlKit.write(sw, doc, 0, doc.getLength());

					System.out.println("finally I got " + sw.toString());

					String html = sw.toString();
					int bodyStart = html.indexOf("<body>");
					int bodyEnd = html.lastIndexOf("</body>");
					String startPart = html.substring(0, bodyStart + 6);
					String endPart = html.substring(bodyEnd, html.length());
					String result = html.replace(startPart, "").replace(endPart, "");
					//System.out.println("at the end: what I got : " + result);

					IFileEditorInput input = (IFileEditorInput) editorPart.getEditorInput();
					IFile file = input.getFile();
					IProject activeProject = file.getProject();
					String activeProjectName = activeProject.getName();
					
					String title = activeProjectName + " - " + file.getName();
					//Object rst = BlogPublisher.publish(title, result.replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "==").replaceAll("\\s{12}", " ").replaceAll("\\s{6}", "").replaceAll("\\s{4}", " ").replaceAll("\\s{2}", "").replaceAll("==", "  ").replaceAll("</p>\\s+<p", "</p><p"));
					String content = result.replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "==").replaceAll("\\s{12}", " ").replaceAll("\\s{6}", "").replaceAll("\\s{4}", " ").replaceAll("\\s{2}", "").replaceAll("==", "  ").replaceAll("</p>\\s+<p", "</p><p");
					BlogProvider blogProvider = BlogProviderMgr.getBlogProvider(defaultAccount.getAccountType());
					Object rst = blogProvider.publish(defaultAccount, null, title, "<pre><code>" + content + "</code></pre>");
					
					MessageDialog.openInformation(shell, "Publish Blog", "done: " + rst.toString());
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openInformation(shell, "Publish Blog", "Failure: " + e.getLocalizedMessage());
				} finally {
					if (null != br) {
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					if (null != sw) {
						try {
							sw.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				MessageDialog.openInformation(shell, "Publish Blog", "Not supported!");
			}
		} else {
			MessageDialog.openInformation(shell, "Publish Blog", "Not supported!");
		}
	}
	
	public void run2(IAction action) {/*
		IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		if (editorPart instanceof ITextEditor) {
			ITextEditor textEditor = (ITextEditor) editorPart;
			
			// COPY THE SELECTED 
			textEditor.getAction(IWorkbenchCommandConstants.EDIT_COPY).run();
			Clipboard clipBoard = new Clipboard(Display.getCurrent());
			Object rtf = clipBoard.getContents(RTFTransfer.getInstance());
			
			// IF NO select, then choose all 
			if (null == rtf || rtf.toString().length() < 1) {
				textEditor.getAction(IWorkbenchCommandConstants.EDIT_SELECT_ALL).run();
				textEditor.getAction(IWorkbenchCommandConstants.EDIT_COPY).run();
				rtf = clipBoard.getContents(RTFTransfer.getInstance());
			}
			
			String rtfText = rtf.toString();
			System.out.println("rtf I get: " + rtfText);
			
			BufferedReader br = null;
			StringWriter sw = null;
			RTFEditorKit rtfKit = new RTFEditorKit();
			HTMLEditorKit htmlKit = new HTMLEditorKit();
			
			try {
			br = new BufferedReader(new StringReader(rtfText));
			StyledDocument doc = (StyledDocument) rtfKit.createDefaultDocument();
			rtfKit.read(br, doc, 0);
			
			sw = new StringWriter();
			htmlKit.write(sw, doc, 0, doc.getLength());
			
			System.out.println("finally I got " + sw.toString());
			
			
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (null != br) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (null != sw) {
					try {
						sw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	*/}
	public void run1(IAction action) {/*
		MessageDialog.openInformation(
			shell,
			"QingPublish",
			"Publish As Blog was executed.");
		
		for (int i = 0; i < 10; i++) {
			System.out.println("-------------------------");
		}
		
		IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		if (editorPart instanceof ITextEditor) {
			ITextEditor textEditor = (ITextEditor) editorPart;
			System.out.println("textEditor.getSelectionProvider():  " + textEditor.getSelectionProvider().getClass());
			System.out.println("textEditor.getSelectionProvider().getSelection():  " + textEditor.getSelectionProvider().getSelection().getClass());
			
			
			ITextSelection textSelection = (ITextSelection) textEditor.getSelectionProvider().getSelection();
			
			System.out.println("what I get" + textSelection.getText());
			//textEditor.getSelectionProvider()
		
		
			RTFTransfer rtfTransfer = null;
			TextTransfer.getInstance();
			//textEditor.geta
			//ActionFactory.COPY;
			;
			textEditor.getAction("selectAll").run();
			textEditor.getAction(IWorkbenchCommandConstants.EDIT_COPY).run();
			
			
			
			IEditorInput input = textEditor.getEditorInput();
			IDocumentProvider provider = textEditor.getDocumentProvider();
			IDocument document = provider.getDocument(input);

			if (null != document) {
				System.out.println("document: " + document.get());
			}
			//TransferData td = new TransferData();
			//HTMLTransfer.getInstance().javaToNative(textSelection.getText(), td);
			//System.out.println("what I get" + td.
		}
		//Transfer transfer = JavaUI.getJavaElementClipboardTransfer();
		//transfer.
		//JavaUI.openInEditor(element);
		//PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().
		
		System.out.println("getActivePage().getSelection():  " + PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection().getClass());
		System.out.println("getSelectionService().getSelection():  " + PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection().getClass());
		//PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().
		
		IViewReference[] ivrs = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
		
		for (IViewReference ivr : ivrs) {
			System.out.println("title:  " + ivr.getView(true).getTitle());
			System.out.println("title.getClass:  " + ivr.getView(true).getClass());
			System.out.println("getSelectionProvider:  " + ivr.getView(true).getViewSite().getSelectionProvider());
			//System.out.println("title:  " + ivr.getView(true).getViewSite().getSelectionProvider().getSelection());
			
			if (ivr.getView(true) instanceof SourceView) {
				SourceView sv = (SourceView) ivr.getView(true);
				System.out.println("		4	sv.getViewSite().getShell().getText():   " + sv.getViewSite().getShell().getText());
				//sv.getViewSite().gets
				final StyledText st = new StyledText(sv.getViewSite().getShell(), SWT.WRAP);
				System.out.println("		5	st.getText():   " + st.getText());
			}
			
			
			if (ivr.getView(true).getViewSite().getSelectionProvider() instanceof JavaSourceViewer) {
				JavaSourceViewer jsv = (JavaSourceViewer) ivr.getView(true).getViewSite().getSelectionProvider();
				jsv.doOperation(jsv.SELECT_ALL);
				jsv.doOperation(JavaSourceViewer.COPY);
				jsv.getTextWidget().selectAll();
				jsv.getTextWidget().copy();
				//jsv.
				System.out.println("			jsv.getTextWidget().getText():   " + jsv.getTextWidget().getText());
				
				System.out.println("			getSelectionProvider:   " + jsv.getSelectionProvider());
				System.out.println("			getSelectionProvider: getSelection  " + jsv.getSelectionProvider().getSelection());
				//System.out.println("			getSelection  " + jsv.getSelection().);
				System.out.println("			getSelectionProvider:   " + jsv);
			}
		}
		
		
		
		
		
		Clipboard clipBoard = new Clipboard(Display.getCurrent());
		System.out.println("getCurrent().getAppName() " + Display.getCurrent().getAppName());
		System.out.println("getActiveShell().getText()   " + Display.getCurrent().getActiveShell().getText());
		System.out.println("getActiveShell().getText()   " + Display.getCurrent().getActiveShell().toString());
		Display.getCurrent().getActiveShell().forceActive();
		System.out.println();
		final StyledText st = new StyledText(Display.getCurrent().getActiveShell(), SWT.WRAP);
		//st.setText("public static void main(String[] args) {");
		st.selectAll();
		st.copy();

		System.out.println("3");
		//st.getContent().
		System.out.println("st.getSelection() x, y, width, height: " + st.getBlockSelectionBounds().x + "  " + st.getBlockSelectionBounds().y + "  " + st.getBlockSelectionBounds().width + "  " + st.getBlockSelectionBounds().height);
		System.out.println("st.getSelection() : " + st.getSelection().toString());
		//st.getContent().get
		
		System.out.println("st.getText() : " + st.getText());
		
		Object rtf = clipBoard.getContents(RTFTransfer.getInstance());
		String rtfText = rtf.toString();
		System.out.println("rtf I get: " + rtfText);
		
		BufferedReader br = null;
		StringWriter sw = null;
		RTFEditorKit rtfKit = new RTFEditorKit();
		HTMLEditorKit htmlKit = new HTMLEditorKit();
		
		try {
		br = new BufferedReader(new StringReader(rtfText));
		StyledDocument doc = (StyledDocument) rtfKit.createDefaultDocument();
		rtfKit.read(br, doc, 0);
		
		sw = new StringWriter();
		htmlKit.write(sw, doc, 0, doc.getLength());
		
		System.out.println("finally I got " + sw.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (null != sw) {
				try {
					sw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	*/}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {/*
		//this.
		if (selection instanceof ITextSelection) {
			ITextSelection its = (ITextSelection) selection;
			System.out.println("ITextSelection" + its.getStartLine() + "  " + its.getText());
		}
		
		if (selection instanceof IMarkSelection) {
			IMarkSelection ims = (IMarkSelection) selection;
			System.out.println("IMarkSelection" + ims.getLength() + "  " + ims.getDocument().get());
		}
		
		
		if (selection instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) selection;
			System.out.println("ss.getFirstElement()  " +  ss.getFirstElement());
			System.out.println("ss.size()  " +  ss.size());
			for (Object obj : ss.toList()) {
				System.out.println("obj  " +  obj);
			}
		}
		System.out.println(" selection.getClass()  " +  selection.getClass());
		if (selection.isEmpty()) {
			action.setEnabled(false);
		} else {
			action.setEnabled(true);
		}
		
	*/}

}
