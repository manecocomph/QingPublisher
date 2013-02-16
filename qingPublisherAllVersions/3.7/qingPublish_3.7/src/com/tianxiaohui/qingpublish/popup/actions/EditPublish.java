package com.tianxiaohui.qingpublish.popup.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.tianxiaohui.qingpublish.ui.BlogEditor;

public class EditPublish implements IObjectActionDelegate {
	private Shell shell;
	
	@Override
	public void run(IAction action) {
		//MessageDialog.openInformation(shell, "Publish Blog", "in publis!");
		/*IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				//File file = new File(new Path(""), (Workspace) ResourcesPlugin.getWorkspace());
				File file = null;
				IDE.openEditor(page, file);
				//IDE.open
			} catch (PartInitException e) {
			}*/
		
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
					//textEditor.getAction(IWorkbenchCommandConstants.EDIT_SELECT_ALL).run();
					//textEditor.getAction(IWorkbenchCommandConstants.EDIT_COPY).run();
					textEditor.getAction("selectAll").run();
					textEditor.getAction("copy").run();
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

					String codeHtml = result.replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "==").replaceAll("\\s{12}", " ").replaceAll("\\s{6}", "").replaceAll("\\s{4}", " ").replaceAll("\\s{2}", "").replaceAll("==", "  ").replaceAll("</p>\\s+<p", "</p><p");
					File blogFile = new File(System.getProperty("user.home") + File.separator + ".qingBlog.html");
					BufferedWriter bw = new BufferedWriter(new FileWriter(blogFile));
					bw.write("<pre><code>" + codeHtml + "</code></pre>");
					bw.flush();
					bw.close();
					
					BlogEditor be = new BlogEditor(this.shell, blogFile);
					be.open();
					/*
					IFileEditorInput input = (IFileEditorInput) editorPart.getEditorInput();
					IFile file = input.getFile();
					IProject activeProject = file.getProject();
					String activeProjectName = activeProject.getName();
					
					String title = activeProjectName + " - " + file.getName();
					Object rst = BlogPublisher.publish(title, result.replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "==").replaceAll("\\s{12}", " ").replaceAll("\\s{6}", "").replaceAll("\\s{4}", " ").replaceAll("\\s{2}", "").replaceAll("==", "  ").replaceAll("</p>\\s+<p", "</p><p"));
					MessageDialog.openInformation(shell, "Publish Blog", "done: " + rst.toString());*/
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
			
			/*StyledText a = null;
			a.paste();*/
			///a.getSelectionProvider().get
			/*Vector cachedStyles = new Vector();
		    Point sel = text.getSelectionRange();
		    int startX = sel.x;
		    for (int i = sel.x; i <= sel.x + sel.y - 1; i++) {
		      StyleRange style = text.getStyleRangeAtOffset(i);
		      if (style != null) {
		        style.start = style.start - startX;
		        if (!cachedStyles.isEmpty()) {
		          StyleRange lastStyle = (StyleRange) cachedStyles
		              .lastElement();
		          if (lastStyle.similarTo(style)
		              && lastStyle.start + lastStyle.length == style.start) {
		            lastStyle.length++;
		          } else {
		            cachedStyles.addElement(style);
		          }
		        } else {
		          cachedStyles.addElement(style);
		        }
		      }
		    }*/
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
