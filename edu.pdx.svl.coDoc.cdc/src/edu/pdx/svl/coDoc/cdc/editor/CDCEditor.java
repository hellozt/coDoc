package edu.pdx.svl.coDoc.cdc.editor;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorLauncher;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiEditor;

import edu.pdx.svl.coDoc.cdc.XML.SimpleXML;

class TempCodeEditorInput implements IStorageEditorInput
{
	public boolean exists()
	{
		return true;
	}
	public ImageDescriptor getImageDescriptor()
	{
		return null;
	}
	public IPersistableElement getPersistable()
	{
		return null;
	}
	public String getName()
	{
		return "=FAKE.c=";
	}
	public String getToolTipText()
	{
		return "Fake code file, open real one.";
	}
	public Object getAdapter(Class adapter)
	{
		return null;
	}
	@Override
	public IStorage getStorage() throws CoreException {
		return new TempCodeStorage();
	}
	private final class TempCodeStorage implements IStorage {
		@Override
		public InputStream getContents() throws CoreException {
			return new StringBufferInputStream("This is a fake code file. Open the real code file when you need it.");
		}
		@Override
		public IPath getFullPath() {
			return null;
		}
		@Override
		public String getName() {
			return TempCodeEditorInput.this.getName();
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
		@Override
		public Object getAdapter(Class adapter) {
			return null;
		}
	}
}

class TempPdfEditorInput implements IStorageEditorInput
{
	public boolean exists()
	{
		return true;
	}
	public ImageDescriptor getImageDescriptor()
	{
		return null;
	}
	public IPersistableElement getPersistable()
	{
		return null;
	}
	public String getName()
	{
		return "=FAKE.pdf=";
	}
	public String getToolTipText()
	{
		return "Fake pdf file, open real one.";
	}
	public Object getAdapter(Class adapter)
	{
		return null;
	}
	@Override
	public IStorage getStorage() throws CoreException {
		return new TempPdfStorage();
	}
	private final class TempPdfStorage implements IStorage {
		@Override
		public InputStream getContents() throws CoreException {
			//String content = "This is a fake pdf file. Open the real pdf file when you need it.";
			String content = "This is a fake pdf file. \nOpen the real pdf file when you need it.";
			String head = "%PDF-1.4\n";
			String obj1 = "1 0 obj\n" + "<<\n" + "/Type /Catalog\n" 
							+ "/Pages 3 0 R\n" + "/Outlines 2 0 R\n" 
							+ ">>\n" + "endobj\n";
			String obj2 = "2 0 obj\n" + "<<\n" + "/Type /Outlines\n" 
							+ "/Count 0\n" + ">>\n" + "endobj\n";
			//content += "/Kids [4 0 R 10 0 R]\n";
			String obj3 = "3 0 obj\n" + "<<\n" + "/Type /Pages\n"
							+ "/Count 1\n" + "/Kids [4 0 R]\n" + ">>\n" 
							+ "endobj\n";
			String obj4 = "4 0 obj\n" + "<<\n" + "/Type /Page\n" + "/Parent 3 0 R\n" 
			        		+ "/Resources << /Font << /F1 7 0 R >> /ProcSet 6 0 R>>\n"
			        		+ "/MediaBox [0 0 612 792]\n" + "/Contents 5 0 R\n"
			        		+ ">>\n" + "endobj\n";
			String stream = String.format("BT\n/F1 24 Tf\n100 500 Td (%s) Tj\nET\n", content);
			String obj5 = "5 0 obj\n" + String.format("<< /Length %d >>\n",stream.length()) // from BT to ET
							+ "stream\n" + stream + "endstream\n" + "endobj\n";
			String obj6 = "6 0 obj\n" + "[/PDF /Text]\n" + "endobj\n";
			String obj7 = "7 0 obj\n" + "<<\n" + "/Type /Font\n" + "/Subtype /Type1\n"
							+ "/Name /F1\n" + "/BaseFont /Helvetica\n" + ">>\n" + "endobj\n";
			String table1 = "xref\n" + "0 8\n" + "0000000000 65535 f\n";
			String table2 = "0000000009 00000 n\n" // catalog
							+ String.format("%010d 00000 n\n", (head+obj1).length()) // 74
							+ String.format("%010d 00000 n\n", (head+obj1+obj2).length()) // 120
							+ String.format("%010d 00000 n\n", (head+obj1+obj2+obj3).length()) // 179
							+ String.format("%010d 00000 n\n", (head+obj1+obj2+obj3+obj4).length()) // 322
							+ String.format("%010d 00000 n\n", (head+obj1+obj2+obj3+obj4+obj5).length()) // 415
							+ String.format("%010d 00000 n\n", (head+obj1+obj2+obj3+obj4+obj5+obj6).length()); // 445
			String tail = "trailer\n" + "<<\n" + "/Size 8\n" // # of objs
							+ "/Root 1 0 R\n"
							+ ">>\n" + "startxref\n"
							+ String.format("%d\n", (head+obj1+obj2+obj3+obj4+obj5+obj6+obj7+table1).length()) // offset of ref table(553)
							+ "%%EOF\n";
			return new StringBufferInputStream(head+obj1+obj2+obj3+obj4+obj5+obj6+obj7+table1+table2+tail);
		}
		@Override
		public IPath getFullPath() {
			return null;
		}
		@Override
		public String getName() {
			return TempPdfEditorInput.this.getName();
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
		@Override
		public Object getAdapter(Class adapter) {
			return null;
		}
	}
}

class NewCDCFileWizardPage extends WizardNewFileCreationPage {
	public NewCDCFileWizardPage(IStructuredSelection selection) {
		super("NewCDCFileWizardPage",selection);
		setTitle("CDC File");
		setDescription("Create a new CDC File");
		setFileExtension("cdc");
		//setFileName("");
	}
    @Override
    protected InputStream getInitialContents() {
        try {
    		CDCModel cdcModel = new CDCModel();
    		IWorkspace workspace = ResourcesPlugin.getWorkspace();
    		IWorkspaceRoot workspaceroot = workspace.getRoot();
    		IPath workspacerootpath = workspaceroot.getLocation();
    		String filepath = workspacerootpath.toString()+File.separatorChar+"temp0000.000";
    		SimpleXML.writeCDCModel(cdcModel, filepath);
    		File file = new File(filepath);
    		byte[] buf = new byte[(int) file.length()];
    		FileInputStream fin = new FileInputStream(file);
    	    int offset = 0;
    	    int numRead = 0;
    	    while (offset < buf.length && (numRead=fin.read(buf, offset, buf.length-offset)) >= 0) {
    	        offset += numRead;
    	    }
    		fin.close();
    		file.delete();
    		return new ByteArrayInputStream(buf);
            // return Activator.getDefault().getBundle().getEntry("/resources/newFileContents.config").openStream();
        } catch (IOException e) {
            return null; // ignore and create empty comments
        }
    }
}

class NewCDCFileWizard extends Wizard implements INewWizard {
    private IStructuredSelection selection;
    private NewCDCFileWizardPage newFileWizardPage;
    private IWorkbench workbench;
    private IPath cdcFilename = null;

    public NewCDCFileWizard() {
        setWindowTitle("New CDC File");
    } 

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        this.selection = selection;
    }
    
    @Override
    public void addPages() {
        newFileWizardPage = new NewCDCFileWizardPage(selection);
        addPage(newFileWizardPage);
    }
    
    @Override
    public boolean performFinish() {
        IFile file = newFileWizardPage.createNewFile();
        if (file != null) {
        	//cdcFilename = file.getFullPath();
        	//cdcFilename = file.getLocation();
        	//cdcFilename = file.getProjectRelativePath();
        	cdcFilename = file.getRawLocation();
            return true;
        } else {
            return false;
        }
    }
    
    public IPath getCDCFilename() {
    	return cdcFilename;
    }
}

public class CDCEditor implements IEditorLauncher
{
	private static IEditorPart entryeditor = null;
	private static IPath path = null;
	private static IPath cdcfilepath = null;

	public CDCEditor() {
		super();
	}
	
	/*
	 * This give you the directory of the first project.
	 * 
	 * This is to be replaced by getActiveProjectDirectory()
	 */
	public static String getProjectDirectory() {
		String dir = null;
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = workspaceRoot.getProjects();
		if (projects.length != 0) {
			IProject proj = projects[0];
			IPath path = proj.getFullPath();
			File filePath = path.toFile();
			String relativeDir = filePath.getPath() + '\\';
			String workspaceDir = workspaceRoot.getLocation().toFile().getAbsolutePath();
			dir = workspaceDir + relativeDir;
			return dir;
		} else {
			return "";
		}
	}
	
	public IEditorPart getOpenedEntryEditorTop(IPath path) {
		IEditorPart editor = null;
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow workbenchwindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = workbenchwindow.getActivePage();
		//IWorkbenchPage workbenchPage = getEditorSite().getPage();
		//IEditorReference[] editorrefs = workbenchPage.getEditorReferences();
		IEditorReference[] editorrefs = workbenchPage.findEditors(null,"edu.pdx.svl.coDoc.cdc.editor.EntryEditor",IWorkbenchPage.MATCH_ID);
		for(IEditorReference er : editorrefs) {
			if(((EntryEditor) er.getEditor(false)).getCDCFilepath().equals(path)) {
				// already open
				editor = er.getEditor(false);
				workbenchPage.bringToTop(editor);
				workbenchPage.activate(editor);
			}
		}

		return editor;
	}
	
	// not practical here
	// will always get "edu.pdx.svl.coDoc.cdc.editor.CDCEditor"
	public IEditorPart getActiveEntryEditor() {
		IEditorPart editor = null;
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow workbenchwindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = workbenchwindow.getActivePage();
		//IWorkbenchPage workbenchPage = getEditorSite().getPage();
		//IEditorReference[] editorrefs = workbenchPage.getEditorReferences();
		IEditorPart activeeditor = workbenchPage.getActiveEditor();
		if(activeeditor instanceof EntryEditor) {
			editor = activeeditor;
		} else {
			// check if it's the child of EntryEditor
			IEditorReference[] editorrefs = workbenchPage.findEditors(null,"edu.pdx.svl.coDoc.cdc.editor.EntryEditor",IWorkbenchPage.MATCH_ID);
			for(IEditorReference er : editorrefs) {
				IEditorPart innerEditors[] = ((EntryEditor) er.getEditor(false)).getInnerEditors();
				for(IEditorPart ep : innerEditors) {
					if(ep == activeeditor) {
						editor = er.getEditor(false);
						break;
					}
				}
				if(editor != null) {
					break;
				}
			}
		}
		return editor;
	}
	
	public IEditorPart openEntryEditor(IPath codepath, IPath specpath) {
		EntryEditorInput entryEditorInput = null;
		IEditorPart editor = null;
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceroot = workspace.getRoot();
		
		if((codepath!=null)&&(specpath!=null)) {
			IFile codefile = (IFile) workspaceroot.getFile(codepath);
			final FileEditorInput codeEditorInput = new FileEditorInput(codefile);
			IFile specfile = (IFile) workspaceroot.getFile(specpath);
			final FileEditorInput specEditorInput = new FileEditorInput(specfile);
			
			String editorID[] = {"edu.pdx.svl.coDoc.cdt.ui.editor.CEditor","edu.pdx.svl.coDoc.poppler.editor.PDFEditor"};
			IEditorInput editorInput[] = {codeEditorInput,specEditorInput};
			entryEditorInput = new EntryEditorInput(editorID, editorInput);
		} else if((codepath!=null)&&(specpath==null)) {
			IFile codefile = (IFile) workspaceroot.getFile(codepath);
			final FileEditorInput codeEditorInput = new FileEditorInput(codefile);

			String editorID[] = {"edu.pdx.svl.coDoc.cdt.ui.editor.CEditor"};
			IEditorInput editorInput[] = {codeEditorInput};
			entryEditorInput = new EntryEditorInput(editorID, editorInput);
		} else if((codepath==null)&&(specpath!=null)) {
			IFile specfile = (IFile) workspaceroot.getFile(specpath);
			final FileEditorInput specEditorInput = new FileEditorInput(specfile);

			String editorID[] = {"edu.pdx.svl.coDoc.poppler.editor.PDFEditor"};
			IEditorInput editorInput[] = {specEditorInput};
			entryEditorInput = new EntryEditorInput(editorID, editorInput);
		} else {
			entryEditorInput = null;
			assert(false);
		}
		
		try 
		{
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow workbenchwindow = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage workbenchPage = workbenchwindow.getActivePage();
			editor = workbenchPage.openEditor(entryEditorInput, "edu.pdx.svl.coDoc.cdc.editor.EntryEditor");
		} 
		catch (PartInitException e) 
		{
			e.printStackTrace();
		}
		return editor;
	}

	@Override
	// open policies:
	//     cdc file: not open -> open
	//               open -> brint to front
	//     c file:
	//     pdf file:
	//               active editor not cdc editor -> create and open
	//               active editor is cdc editor -> open
	public void open(IPath path) 
	{
		this.path = path;
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceroot = workspace.getRoot();
		IPath workspacerootpath = workspaceroot.getLocation();
		
		if(path.getFileExtension().equals("cdc")) {
			cdcfilepath = path;
			IEditorPart editor = getOpenedEntryEditorTop(path);
			if(editor == null) {
				String filename = path.toString();
				CDCModel cdcModel = SimpleXML.readCDCModel(filename);
				if(cdcModel != null) {
					IPath codepath = null;
					IPath specpath = null;
					String codeFilename = cdcModel.getLastOpenedCodeFilename();
					if(codeFilename != null) {
						codepath = new Path(codeFilename);
						codepath = codepath.removeFirstSegments(1); // remove "project:"
					}
					String specFilename = cdcModel.getLastOpenedSpecFilename();
					if(specFilename != null) {
						specpath = new Path(specFilename);
						specpath = specpath.removeFirstSegments(1);
					}
					entryeditor = openEntryEditor(codepath, specpath);
				} else {
					//MessageDialog.openInformation(null, "Error", "\n\tUnrecognized CDC file!");
					MessageDialog.openError(null, "Error", "\n\tUnrecognized CDC file!");
				}
			}
		} else if (path.getFileExtension().equals("pdf")) {
			IEditorPart editor = getActiveEntryEditor();
			if(editor == null) {
		        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		        NewCDCFileWizard wizard = new NewCDCFileWizard();
		        wizard.init(PlatformUI.getWorkbench(), new StructuredSelection());
				WizardDialog dialog = new WizardDialog(shell, wizard);
				//dialog.create();
				dialog.open();
				if(wizard.getCDCFilename() != null) {
					cdcfilepath = wizard.getCDCFilename();
					CDCModel cdcModel = SimpleXML.readCDCModel(cdcfilepath.toString());
					path = path.makeRelativeTo(workspacerootpath);
					cdcModel.setLastOpenedSpecFilename("project:///"+path.toString());
					SimpleXML.writeCDCModel(cdcModel, cdcfilepath.toString());
					entryeditor = openEntryEditor(null, path);
				}
			} else {
				path = path.makeRelativeTo(workspacerootpath);
				IFile specfile = (IFile) workspaceroot.getFile(path);
				final FileEditorInput specEditorInput = new FileEditorInput(specfile);
				EntryEditorInput entryEditorInput = (EntryEditorInput) editor.getEditorInput();
				if(entryEditorInput.getEditors()[entryEditorInput.getEditors().length-1].equals("edu.pdx.svl.coDoc.poppler.editor.PDFEditor")) {
					entryEditorInput.getInput()[entryEditorInput.getInput().length-1] = specEditorInput;
				} else {
					String editorID[] = {entryEditorInput.getEditors()[0], "edu.pdx.svl.coDoc.poppler.editor.PDFEditor"};
					IEditorInput editorInput[] = {entryEditorInput.getInput()[0], specEditorInput};
					entryEditorInput = new EntryEditorInput(editorID, editorInput);
				}
				cdcfilepath = ((EntryEditor) editor).getCDCFilepath();
				CDCModel cdcModel = SimpleXML.readCDCModel(cdcfilepath.toString());
				cdcModel.setLastOpenedSpecFilename("project:///"+path.toString());
				SimpleXML.writeCDCModel(cdcModel, cdcfilepath.toString());
				IWorkbench workbench = PlatformUI.getWorkbench();
				IWorkbenchWindow workbenchwindow = workbench.getActiveWorkbenchWindow();
				IWorkbenchPage workbenchPage = workbenchwindow.getActivePage();
				workbenchPage.reuseEditor((IReusableEditor) editor, entryEditorInput);
				entryeditor = editor;
			}
		} else {
			IEditorPart editor = getActiveEntryEditor();
			if(editor == null) {
		        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		        NewCDCFileWizard wizard = new NewCDCFileWizard();
		        wizard.init(PlatformUI.getWorkbench(), new StructuredSelection());
				WizardDialog dialog = new WizardDialog(shell, wizard);
				//dialog.create();
				dialog.open();
				if(wizard.getCDCFilename() != null) {
					cdcfilepath = wizard.getCDCFilename();
					CDCModel cdcModel = SimpleXML.readCDCModel(cdcfilepath.toString());
					path = path.makeRelativeTo(workspacerootpath);
					cdcModel.setLastOpenedCodeFilename("project:///"+path.toString());
					SimpleXML.writeCDCModel(cdcModel, cdcfilepath.toString());
					entryeditor = openEntryEditor(path, null);
				}
			} else {
				path = path.makeRelativeTo(workspacerootpath);
				IFile codefile = (IFile) workspaceroot.getFile(path);
				final FileEditorInput codeEditorInput = new FileEditorInput(codefile);
				EntryEditorInput entryEditorInput = (EntryEditorInput) editor.getEditorInput();
				if(entryEditorInput.getEditors()[0].equals("edu.pdx.svl.coDoc.cdt.ui.editor.CEditor")) {
					entryEditorInput.getInput()[0] = codeEditorInput;
				} else {
					String editorID[] = {"edu.pdx.svl.coDoc.cdt.ui.editor.CEditor", entryEditorInput.getEditors()[0]};
					IEditorInput editorInput[] = {codeEditorInput, entryEditorInput.getInput()[0]};
					entryEditorInput = new EntryEditorInput(editorID, editorInput);
				}
				cdcfilepath = ((EntryEditor) editor).getCDCFilepath();
				CDCModel cdcModel = SimpleXML.readCDCModel(cdcfilepath.toString());
				cdcModel.setLastOpenedCodeFilename("project:///"+path.toString());
				SimpleXML.writeCDCModel(cdcModel, cdcfilepath.toString());
				IWorkbench workbench = PlatformUI.getWorkbench();
				IWorkbenchWindow workbenchwindow = workbench.getActiveWorkbenchWindow();
				IWorkbenchPage workbenchPage = workbenchwindow.getActivePage();
				workbenchPage.reuseEditor((IReusableEditor) editor, entryEditorInput);
				entryeditor = editor;
			}
		}
	}
	
	public static IPath getLatestPath() {
		return cdcfilepath;
	}
	
	public static void main(String[] args) {
		/*String content = "";
		content += "%PDF-1.4\n";
		content += "1 0 obj\n";
		content += "<<\n";
		content += "/Type /Catalog\n";
		content += "/Pages 3 0 R\n";
		content += "/Outlines 2 0 R\n";
		content += ">>\n";
		content += "endobj\n";
		content += "2 0 obj\n";
		content += "<<\n";
		content += "/Type /Outlines\n";
		content += "/Count 0\n";
		content += ">>\n";
		content += "endobj\n";
		content += "3 0 obj\n";
		content += "\n";
		content += "<<\n";
		content += "/Type /Pages\n";
		content += "/Count 1\n";
		//content += "/Kids [4 0 R 10 0 R]\n";
		content += "/Kids [4 0 R]\n";
		content += ">>\n";
		content += "\n";
		content += "endobj\n";
		content += "4 0 obj\n";
		content += "\n";
		content += "<<\n";
		content += "/Type /Page\n";
		content += "/Parent 3 0 R\n";
		content += "/Resources << /Font << /F1 7 0 R >> /ProcSet 6 0 R>>\n";
		content += "/MediaBox [0 0 612 792]\n";
		content += "/Contents 5 0 R\n";
		content += "\n";
		content += ">>\n";
		content += "\n";
		content += "endobj\n";
		content += "5 0 obj\n";
		content += "<< /Length 44 >>\n"; // from BT to ET
		content += "stream\n";
		content += "BT\n"; // begin of Text Obj
		content += "/F1 24 Tf\n"; // True font Obj, font F1, size 24
		content += "100 500 Td (Hello World) Tj\n";
		content += "ET\n";
		content += "endstream\n";
		content += "endobj\n";
		content += "6 0 obj\n";
		content += "\n";
		content += "[/PDF /Text]\n";
		content += "\n";
		content += "endobj\n";
		content += "7 0 obj\n";
		content += "<<\n";
		content += "/Type /Font\n";
		content += "/Subtype /Type1\n";
		content += "/Name /F1\n";
		content += "/BaseFont /Helvetica\n";
		content += ">>\n";
		content += "endobj\n";
		content += "xref\n";
		content += "0 8\n";
		content += "0000000000 65535 f\n";
		content += "0000000009 00000 n\n"; // catalog
		content += "0000000074 00000 n\n";
		content += "0000000120 00000 n\n";
		content += "0000000179 00000 n\n";
		content += "0000000322 00000 n\n";
		content += "0000000415 00000 n\n";
		content += "0000000445 00000 n\n";
		content += "trailer\n";
		content += "<<\n";
		content += "/Size 8\n"; // # of objs
		content += "/Root 1 0 R\n"; // root obj
		content += ">>\n";
		content += "startxref\n";
		content += "553\n"; // offset of ref table
		content += "%%EOF\n";*/
		String content = "Hello world.";
		String head = "%PDF-1.4\n";
		String obj1 = "1 0 obj\n" + "<<\n" + "/Type /Catalog\n" 
						+ "/Pages 3 0 R\n" + "/Outlines 2 0 R\n" 
						+ ">>\n" + "endobj\n";
		String obj2 = "2 0 obj\n" + "<<\n" + "/Type /Outlines\n" 
						+ "/Count 0\n" + ">>\n" + "endobj\n";
		//content += "/Kids [4 0 R 10 0 R]\n";
		String obj3 = "3 0 obj\n" + "<<\n" + "/Type /Pages\n"
						+ "/Count 1\n" + "/Kids [4 0 R]\n" + ">>\n" 
						+ "endobj\n";
		String obj4 = "4 0 obj\n" + "<<\n" + "/Type /Page\n" + "/Parent 3 0 R\n" 
		        		+ "/Resources << /Font << /F1 7 0 R >> /ProcSet 6 0 R>>\n"
		        		+ "/MediaBox [0 0 612 792]\n" + "/Contents 5 0 R\n"
		        		+ ">>\n" + "endobj\n";
		String stream = String.format("BT\n/F1 24 Tf\n100 700 Td (%s) Tj\nET\n", content);
		String obj5 = "5 0 obj\n" + String.format("<< /Length %d >>\n",stream.length()) // from BT to ET
						+ "stream\n" + stream + "endstream\n" + "endobj\n";
		String obj6 = "6 0 obj\n" + "[/PDF /Text]\n" + "endobj\n";
		String obj7 = "7 0 obj\n" + "<<\n" + "/Type /Font\n" + "/Subtype /Type1\n"
						+ "/Name /F1\n" + "/BaseFont /Helvetica\n" + ">>\n" + "endobj\n";
		String table1 = "xref\n" + "0 8\n" + "0000000000 65535 f\n";
		String table2 = "0000000009 00000 n\n" // catalog
						+ String.format("%010d 00000 n\n", (head+obj1).length()) // 74
						+ String.format("%010d 00000 n\n", (head+obj1+obj2).length()) // 120
						+ String.format("%010d 00000 n\n", (head+obj1+obj2+obj3).length()) // 179
						+ String.format("%010d 00000 n\n", (head+obj1+obj2+obj3+obj4).length()) // 322
						+ String.format("%010d 00000 n\n", (head+obj1+obj2+obj3+obj4+obj5).length()) // 415
						+ String.format("%010d 00000 n\n", (head+obj1+obj2+obj3+obj4+obj5+obj6).length()); // 445
		String tail = "trailer\n" + "<<\n" + "/Size 8\n" // # of objs
						+ "/Root 1 0 R\n"
						+ ">>\n" + "startxref\n"
						+ String.format("%d\n", (head+obj1+obj2+obj3+obj4+obj5+obj6+obj7+table1).length()) // offset of ref table(553)
						+ "%%EOF\n";
		content = head+obj1+obj2+obj3+obj4+obj5+obj6+obj7+table1+table2+tail;
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("test.pdf"));
			out.write(content);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
}
