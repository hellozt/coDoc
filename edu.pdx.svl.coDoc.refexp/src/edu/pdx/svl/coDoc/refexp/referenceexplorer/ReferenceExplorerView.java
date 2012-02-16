package edu.pdx.svl.coDoc.refexp.referenceexplorer;

import java.io.File;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;


import edu.pdx.svl.coDoc.refexp.Global;
import edu.pdx.svl.coDoc.refexp.XML.SimpleXML;
import edu.pdx.svl.coDoc.refexp.preferences.PreferencesView;
import edu.pdx.svl.coDoc.refexp.referencemodel.*;
import edu.pdx.svl.coDoc.refexp.referenceexplorer.provider.*;
import edu.pdx.svl.coDoc.refexp.referenceexplorer.provider.LabelProvider;
import edu.pdx.svl.coDoc.refexp.referenceexplorer.edit.*;
import edu.pdx.svl.coDoc.refexp.view.EditView;
import edu.pdx.svl.coDoc.refexp.view.Help;

public class ReferenceExplorerView extends ViewPart implements ISelectionListener, Listener, IResourceChangeListener {
	public static final String ID = "edu.pdx.svl.coDoc.refexp.referenceexplorer.ReferenceExplorerView";
	private static TreeViewer treeViewer;
	private static TableViewer tableViewer;
	private final int NUM_HORIZONTAL_ELEMENTS = 11; // max num elements in a row
	Button checkButton;
	IWorkbenchPart workbenchPart;
	String activeEditorFileName;
	Button selectPDFButton;
	Button openActivePDF;
	PreferencesView preferencesView;
	Composite parent;
	String searchTextStr;
	Text searchText;
	Combo combo;
	
	
	public Button getSelectPDFButton() {
		return selectPDFButton;
	}
	
//	public void createPartControl() {
//		createPartControl(parent);
//	}
	@Override
	public void createPartControl(Composite parent) {
		Global.INSTANCE.referenceExplorerView = this;
		this.parent = parent;
		
		//NH - I split the setup into several parts...
		createSearchBarAndButtons(parent);
		createTreeViewer(parent);
		createWorkbenchListener();
		createResourceChangeListener();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}
	
	

	private void createSearchBarAndButtons(Composite parent) {
		GridLayout layout = new GridLayout(NUM_HORIZONTAL_ELEMENTS, false);
		parent.setLayout(layout);
		

		// setup search bar
		createSearchBar(parent);
		
		// setup buttons
		createSearchTypeComboBox(parent);
		createSelectPDFButton(parent);
		createOpenActivePDF(parent);
		createAddButton(parent);
		createEditButton(parent);
		createDeleteButton(parent);
		createPreferencesButton(parent);
		createHelpButton(parent);
		createAllSourcesCheckBox(parent);
	}

	private void createSearchBar(Composite parent) {
		Label searchLabel = new Label(parent, SWT.NONE);
		searchLabel.setText("Search: ");
		searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		searchText.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
//				int code = e.keyCode;
//				System.out.println(code);
				
				//the enter/return key has the key code of 13
				if (e.keyCode == 13 || e.keyCode == 16777296) {
					searchTextStr = searchText.getText();
					displayListOfTextSelectionReferencesForSearchString();
				}
			}
		});
	}
	
	private void createSearchTypeComboBox(Composite parent) {
		combo = new Combo (parent, SWT.READ_ONLY);
		combo.setItems (new String [] {"All Data", "Source Text", "Source File", "Project", "PDF File", "PDF Text", "PDF Page", "Comments"});
		combo.setToolTipText("Search Categories.");
		combo.select(0);
		combo.addSelectionListener( new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchTextStr = searchText.getText();
				displayListOfTextSelectionReferencesForSearchString();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
	}
	
	private void createAllSourcesCheckBox(Composite parent) {
		checkButton = new Button(parent, SWT.CHECK);
		checkButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshTreeViewer();
			}
		});
		checkButton.setText("All Sources");
		checkButton.setSelection(true);
		checkButton.setToolTipText("When checked, all saved references are shown.\nWhen unchecked, only references for the currently\nactive source file are shown.");
	}
	
	private void refreshTreeViewer() {
		searchText.setText("");
		treeViewer.setInput(Global.INSTANCE.references);
		TreeContentProvider tcp = (TreeContentProvider)treeViewer.getContentProvider();
		if (checkButton.getSelection() == true) {
			tcp.allSources = true;
		} else {
			tcp.allSources = false;
		}
		treeViewer.refresh();
		treeViewer.expandToLevel(4);
	}
	
	private void createSelectPDFButton(Composite parent) {

		selectPDFButton = new Button(parent, SWT.PUSH);
		selectPDFButton.setText("Select Active PDF");
		
		selectPDFButton.setToolTipText("Selects the current PDF for new references.\nCurrent PDF:\n" + 
				PDFManager.INSTANCE.getCurrentPdfFile().description());
		
		selectPDFButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PDFManager.INSTANCE.openFileInAcrobat();
				selectPDFButton.setToolTipText("Current PDF:\n" +
						PDFManager.INSTANCE.getCurrentPdfFile().description());
				openActivePDF.setToolTipText("Opens the currently active PDF for new references.\nCurrent PDF:\n" + 
						PDFManager.INSTANCE.getCurrentPdfFile().description());
			}
		});
	}
	
	private void createOpenActivePDF(Composite parent) {
		openActivePDF = new Button(parent, SWT.PUSH);
		openActivePDF.setText("Open Active PDF");
		openActivePDF.setToolTipText("Opens the currently active PDF for new references.\nCurrent PDF:\n" + 
				PDFManager.INSTANCE.getCurrentPdfFile().description());
		openActivePDF.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PDFManager.INSTANCE.openActivePDFInAcrobat();
			}
		});
	}
	
	private void createPreferencesButton(Composite parent) {
		Button preferencesButton = new Button(parent, SWT.PUSH);
		preferencesButton.setText("Preferences");
		preferencesView = new PreferencesView();
		preferencesButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				preferencesView.open();
			}
		});
		preferencesView.setParentView(this);
	}

	private void createAddButton(Composite parent) {
		Button addButton = new Button(parent, SWT.PUSH);
		addButton.setText("Add");
		
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Global.INSTANCE.references.addReference();
			}
		});
	}
	
	private void createEditButton(Composite parent) {
		Button editButton = new Button(parent, SWT.PUSH);
		editButton.setText("Edit");
		
		editButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean refNotSelected = true;
				ISelection selection = treeViewer.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					IStructuredSelection sel = (IStructuredSelection) selection;
					for (Iterator<Reference> iterator = sel.iterator(); iterator.hasNext();) {
						Reference refToEdit = iterator.next();
						refNotSelected = false;
						(new EditView(new Shell(), refToEdit)).open();
					}
					treeViewer.refresh();
				} 
				if (refNotSelected == true) {
					MessageDialog.openError(null, "Alert",  "You must select a reference to be able to edit it!");
				}
			}
		});
	}
	
	private void createDeleteButton(Composite parent) {
		Button ok = new Button(parent, SWT.PUSH);
		ok.setText("Delete");
		ok.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = treeViewer.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					References refs = Global.INSTANCE.references;
					IStructuredSelection sel = (IStructuredSelection) selection;
					for (Iterator<Reference> iterator = sel.iterator(); iterator.hasNext();) {
						Reference refToDelete = iterator.next();
						refs.deleteReference(refToDelete);
					}
					treeViewer.refresh();
				}
			}
		});
	}

	private void createHelpButton(Composite parent) {
		Button ok = new Button(parent, SWT.PUSH);
		ok.setText("Help");
		ok.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				Help.openHelpBrowser();
			}
		});
	}


	private void createTreeViewer(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| /* NH */SWT.FULL_SELECTION | SWT.BORDER);

		treeViewer.setAutoExpandLevel(4);
		Tree tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(false);

		TreeViewerColumn viewerColumn = createTreeViewerColumn(
				"Source References", 300);
		viewerColumn.setLabelProvider(new LabelProvider());

		viewerColumn = createTreeViewerColumn("Type", 90);
		viewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Reference) {
					return ((Reference)element).getType();
				} else {
					return "";
				}
			}
		});

		viewerColumn = createTreeViewerColumn("Specification Text", 320);
		viewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Reference) {
					Reference r = (Reference)element;
					return r.pdfDescription();
				} else {
					return "";
				}
			}
		});
		
		viewerColumn = createTreeViewerColumn("PDF File", 145);
		viewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Reference) {
					Reference r = (Reference)element;
					PDFFile pdfFile = r.getPdfFile();
					return pdfFile.getFileName();
				} else {
					return "";
				}
			}
		});

		viewerColumn = createTreeViewerColumn("Page", 50);
		viewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Reference) {
					Reference r = (Reference)element;
					return r.pdfPage();
				} else {
					return "";
				}
			}
		});

		viewerColumn = createTreeViewerColumn("Comments", 263);
		viewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Reference) {
					Reference r = (Reference)element;
					return r.getComment();
				} else {
					return "";
				}
			}
		});
		viewerColumn.setEditingSupport(new EditComment(treeViewer));
		// end NH

		TreeContentProvider contentProvider = new TreeContentProvider();
		treeViewer.setContentProvider(contentProvider);
		
//		TableContentProvider contentProvider = new TableContentProvider();
//		treeViewer.setContentProvider(contentProvider);
		
		treeViewer.setInput(Global.INSTANCE.references);
//		treeViewer.setInput(Global.INSTANCE.references.findReferencesForActiveSourceFile());

		tree.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				selectTextInTextEditor();
				selectTextInAcrobat();
			}
		});
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				ISelection selection = treeViewer.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					IStructuredSelection sel = (IStructuredSelection) selection;
					for (Iterator<Reference> iterator = sel.iterator(); iterator.hasNext();) {
						Reference ref = iterator.next();
						PDFFile pdfFile = ref.getPdfFile();
						if (pdfFile != null) {
							PDFManager.INSTANCE.openFileInAcrobatForced(pdfFile);
							PDFSelection pdfs = ref.getPdfSelection();
							if (pdfs != null) {
								//We have to sleep, because Acrobat needs time to load before making a selection.
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e1) {
									System.out.println("cannot sleep for creating acrobat selection");
									e1.printStackTrace();
								}
								pdfs.selectTextInAcrobat();
							}
						}
					}
				}
				setFocus();
			}
		});

		// Layout the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = NUM_HORIZONTAL_ELEMENTS;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		treeViewer.getControl().setLayoutData(gridData);
	}

	// NH
	private TreeViewerColumn createTreeViewerColumn(String title, int bound) {
		final TreeViewerColumn viewerColumn = new TreeViewerColumn(treeViewer,
				SWT.NONE);
		final TreeColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}
	

	@SuppressWarnings("unchecked")
	private void selectTextInTextEditor() {
		ISelection selection = treeViewer.getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			for (Iterator<Reference> iterator = sel.iterator(); iterator.hasNext();) {
				Reference ref = iterator.next();
				if (ref instanceof TextSelectionReference) {
					TextSelectionReference tsr = (TextSelectionReference)ref;
					int offset = tsr.getOffset();
					int length = tsr.getLength();
					
					
					
					//new way in which we open the right source file	
					File fileToOpen = new File(tsr.getSourceFileReference().getFilePath());
					 
					if (fileToOpen.exists() && fileToOpen.isFile()) {
					    IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
					    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					 
					    try {
					        IDE.openEditorOnFileStore( page, fileStore );
					    } catch ( PartInitException e ) {
					        //Put your exception handler here if you wish to
					    }
					}
					
					ISelectionProvider selProv = workbenchPart.getSite().getSelectionProvider();
					TextSelection newSelection = new TextSelection(offset,length);
					selProv.setSelection(newSelection);
					
				}
			}
		}
		setFocus();
	}
	


	@SuppressWarnings("unchecked")
	private void selectTextInAcrobat() {

//		if (PDFManager.INSTANCE.isAcrobatOpened() == true) {
			ISelection selection = treeViewer.getSelection();
			if (selection != null && selection instanceof IStructuredSelection) {
				IStructuredSelection sel = (IStructuredSelection) selection;
				for (Iterator<Reference> iterator = sel.iterator(); iterator.hasNext();) {
					Reference ref = iterator.next();
					PDFSelection pdfs = ref.getPdfSelection();
					if (pdfs != null) {
						pdfs.selectTextInAcrobat();
					}
				}
			}
//		}
	}

	public void handleEvent(Event event) {
		MessageDialog.openError(null, "Alert",  "handleEvent(Event event)");
		
	}
	
	private TextSelection currentTextSelection;
	public TextSelectionReference getCurrentTextSelectionReference() {
		if (currentTextSelection == null) {
			MessageDialog.openError(null, "Alert",  "Warning:\nYou have not selected any text in your source file.\nNo reference has been saved.");
			return null;
		}

		int length = currentTextSelection.getLength();
		if (length == 0) {
			MessageDialog.openError(null, "Alert",  "Warning:\nYou have not selected any text in your source file.\nNo reference has been saved.");
			return null;
		}
		int offset = currentTextSelection.getOffset();
		String text = currentTextSelection.getText();
		
		TextSelectionReference tsr = new TextSelectionReference();
		tsr.setOffset(offset);
		tsr.setLength(length);
		tsr.setText(text);
		tsr.fetchAcrobatData();
		
		PDFSelection pdfSel = tsr.getPdfSelection();
		if (pdfSel == null) {
			MessageDialog.openError(null, "Alert", "Warning:\nYou do not have an open PDF file in Acrobat from which you will create a reference.\nNo reference has been saved.");
			return null;
		}
		String pdfTxt = pdfSel.getText();
		if (pdfTxt == null || pdfTxt.equals("")) {
			MessageDialog.openError(null, "Alert", "Warning:\nYou have not selected any text in your PDF file.\nNo reference has been saved.");
			return null;
		}
		
		tsr.setPdfFile(PDFManager.INSTANCE.getCurrentPdfFile());

		return tsr;
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		Global.INSTANCE.workbenchPart = part;
		workbenchPart = part;

		if (selection == null) return;
		
//		if (selection instanceof TreeSelection) {
//			MessageDialog.openInformation(null, "Tree Selection", "Tree Selection");
//			System.out.println("Tree Selection");
//			TreeSelection ts = (TreeSelection)selection;
//		}
		
		if (selection instanceof TextSelection) {
			currentTextSelection = (TextSelection)selection;
		}

		IEditorPart editorPart = part.getSite().getPage().getActiveEditor();
		if (editorPart != null) {
			IEditorInput iEditorInput = editorPart.getEditorInput();
			FileEditorInput fei = (FileEditorInput)iEditorInput;
			FileEditorInput oldFei = Global.INSTANCE.activeFileEditorInput;
			Global.INSTANCE.activeFileEditorInput = fei;
			
			if (checkButton.getSelection() == false && fei != oldFei) {
				searchText.setText("");
				treeViewer.setInput(Global.INSTANCE.references);
				TreeContentProvider tcp = (TreeContentProvider)treeViewer.getContentProvider();
				if (checkButton.getSelection() == true) {
					tcp.allSources = true;
				} else {
					tcp.allSources = false;
				}
				treeViewer.refresh();
				treeViewer.expandToLevel(4);
			}
		}
		
		
//		IEditorReference[] editors = part.getSite().getPage().getEditorReferences();
//		editors[0].getName();
//		
//		IViewReference[] views = part.getSite().getPage().getViewReferences();
//		views[0].getTitle();
		
//		IViewReference outlineView = views[6];
//		IViewPart ov = outlineView.getView(true);
//		ContentOutline contentOutline = (ContentOutline)ov;
	}
	
	private void createWorkbenchListener() {
		getSite().getPage().addSelectionListener(this);
		selectionChanged(getSite().getPart(), getSite().getPage().getSelection());
	}
	
	private void createResourceChangeListener() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IResourceChangeListener listener = this;
		workspace.addResourceChangeListener(listener);
	}
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
//		System.out.println("Resource Changed!");
		
		//some possible constants that a flag or kind could be...
//		int added = IResourceDelta.ADDED;
//		int changed = IResourceDelta.CHANGED;
//		int copiedFrom = IResourceDelta.COPIED_FROM;
//		int movedFrom = IResourceDelta.MOVED_FROM;
//		int movedTo = IResourceDelta.MOVED_TO;
//		int localChanged = IResourceDelta.LOCAL_CHANGED;
//		int noChange = IResourceDelta.NO_CHANGE;
//		int open = IResourceDelta.OPEN;
//		int removed = IResourceDelta.REMOVED;
//		int removedPhantom = IResourceDelta.REMOVED_PHANTOM;
//		int replaced = IResourceDelta.REPLACED;
		
		References references = Global.INSTANCE.references;
		
		IResourceDelta workspaceIrd = event.getDelta(); //workspace
		if (workspaceIrd == null) return;
		
		IResourceDelta[] projectsChanged = workspaceIrd.getAffectedChildren();
		for (IResourceDelta projectIrd : projectsChanged) {
			int projectIrdKind = projectIrd.getKind(); //project
			int projectIrdFlag = projectIrd.getFlags();
			
			//project is renamed or moved
			if (projectIrdKind == IResourceDelta.REMOVED && projectIrdFlag == IResourceDelta.MOVED_TO) {
				IPath projectFullPath = projectIrd.getFullPath();
				IPath projectMovedToPath = projectIrd.getMovedToPath();
				references.resourceDeltaMoveProject(projectFullPath, projectMovedToPath);
				break;
			}
			//project is removed
			else if (projectIrdKind == IResourceDelta.REMOVED && projectIrdFlag == IResourceDelta.NO_CHANGE) {
				IPath projectFullPath = projectIrd.getFullPath();
				references.resourceDeltaRemoveProject(projectFullPath);
				break;
			}
			//new project imported, we need to scan to see if there are saved references
			else if (projectIrdKind == IResourceDelta.ADDED && projectIrdFlag == IResourceDelta.OPEN) {
				Global.INSTANCE.references = SimpleXML.read();
				break;
			}
			
			IResourceDelta[] filesChanged = projectIrd.getAffectedChildren();
			for (IResourceDelta fileIrd : filesChanged) {
				int fileIrdKind = fileIrd.getKind(); //file
				int fileIrdFlag = fileIrd.getFlags();
				
				//old file renamed or moved
				if (fileIrdKind == IResourceDelta.REMOVED && fileIrdFlag == IResourceDelta.MOVED_TO) {
					IPath fileIrdFullPath = fileIrd.getFullPath();
					IPath fileIrdMovedToPath = fileIrd.getMovedToPath();
					references.resourceDeltaMoveFile(fileIrdFullPath, fileIrdMovedToPath);
				}
				//file removed
				else if (fileIrdKind == IResourceDelta.REMOVED && fileIrdFlag == IResourceDelta.NO_CHANGE) {
					IPath fileIrdFullPath = fileIrd.getFullPath();
					references.resourceDeltaRemoveFile(fileIrdFullPath);
				}
			}
		}
		refreshTreeViewer();
	}

	public static TreeViewer getTreeViewer() {
		return treeViewer;
	}
	
	public void displayListOfTextSelectionReferencesForSelectionInActiveEditor() {
		References references = Global.INSTANCE.references;
		TextSelectionReference currentTextSelection = getCurrentTextSelectionReference();
		Vector<Reference> matches = references.findReferencesContainingTextSelectionInActiveEditor(currentTextSelection);
		checkButton.setSelection(false);
		treeViewer.setInput(matches);
		treeViewer.refresh();
	}

	public void displayListOfTextSelectionReferencesForSearchString() {
		if (searchTextStr == "") {
			refreshTreeViewer();
			return;
		}
		References references = Global.INSTANCE.references;
		Vector<Reference> matches = null;
		
		int comboSelection = combo.getSelectionIndex();
		if (comboSelection == 0) {
			if (checkButton.getSelection() == true ) {
				matches = references.findAllReferences(searchTextStr);
			} else {
				matches = references.findAllReferencesForActiveSourceFile(searchTextStr);
			}
		} else if (comboSelection == 1) {
			if (checkButton.getSelection() == true ) {
				matches = references.findSourceTextReferences(searchTextStr);
			} else {
				matches = references.findSourceTextReferencesForActiveSourceFile(searchTextStr);
			}
		} else if (comboSelection == 2) {
			matches = references.findSourceFileReferences(searchTextStr);
			checkButton.setSelection(true);
		} else if (comboSelection == 3) {
			matches = references.findProjectReferences(searchTextStr);
			checkButton.setSelection(true);
		} else if (comboSelection == 4) {
			if (checkButton.getSelection() == true ) {
				matches = references.findPDFFileReferences(searchTextStr);
			} else {
				matches = references.findPDFFileReferencesForActiveSourceFile(searchTextStr);
			}
		} else if (comboSelection == 5) {
			if (checkButton.getSelection() == true ) {
				matches = references.findPDFTextReferences(searchTextStr);
			} else {
				matches = references.findPDFTextReferencesForActiveSourceFile(searchTextStr);
			}
		} else if (comboSelection == 6) {
			if (checkButton.getSelection() == true ) {
				matches = references.findPDFPageReferences(searchTextStr);
			} else {
				matches = references.findPDFPageReferencesForActiveSourceFile(searchTextStr);
			}
		} else {
			if (checkButton.getSelection() == true ) {
				matches = references.findCommentReferences(searchTextStr);
			} else {
				matches = references.findCommentReferencesForActiveSourceFile(searchTextStr);
			}
		} 
		
		
//		checkButton.setSelection(false);
		treeViewer.setInput(matches);
		treeViewer.refresh();
		treeViewer.setAutoExpandLevel(4);
	}
}