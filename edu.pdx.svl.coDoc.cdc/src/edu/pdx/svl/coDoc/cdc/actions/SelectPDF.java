package edu.pdx.svl.coDoc.cdc.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


//import edu.pdx.svl.coDoc.refexp.editorcontextmenu.editors.SelectionAndCursor;

import edu.pdx.svl.coDoc.cdc.referencemodel.PDFManager;



public class SelectPDF implements IObjectActionDelegate {

	private Shell shell;
	
	
	/**
	 * Constructor for Action1.
	 */
	public SelectPDF() {
		super();
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
	public void run(IAction action) {
		PDFManager.INSTANCE.openFileInAcrobat();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}