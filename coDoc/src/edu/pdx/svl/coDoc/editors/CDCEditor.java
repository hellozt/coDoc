package edu.pdx.svl.coDoc.editors;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.GZIPInputStream;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.INavigationLocationProvider;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import edu.pdx.svl.coDoc.poppler.OutlineNode;
import edu.pdx.svl.coDoc.poppler.PDFDestination;
import com.sun.pdfview.PDFObject;

import edu.pdx.svl.coDoc.editors.PDFPageViewer.IPDFEditor;
import edu.pdx.svl.coDoc.editors.StatusLinePageSelector.IPageChangeListener;
import edu.pdx.svl.coDoc.poppler.PopplerJNI;


public class CDCEditor extends EditorPart implements IResourceChangeListener, INavigationLocationProvider, IPageChangeListener, IPDFEditor
{

	public static final String ID = "edu.pdx.svl.coDoc.editors.CDCEditor"; // editor id, plugin.xml
	public static final String CONTEXT_ID = "PDFViewer.editors.contextid"; // key binding, plugin.xml

	public static final int FORWARD_SEARCH_OK = 0;
	public static final int FORWARD_SEARCH_NO_SYNCTEX = -1;
	public static final int FORWARD_SEARCH_FILE_NOT_FOUND = -2;
	public static final int FORWARD_SEARCH_POS_NOT_FOUND = -3;
	public static final int FORWARD_SEARCH_UNKNOWN_ERROR = -4;

	static final String PDFPOSITION_ID = "PDFPosition"; //$NON-NLS-1$
	
	private PopplerJNI poppler;
	public int currentPage;
	public int pageNumbers;

	private ScrolledComposite scc;
	private ScrolledComposite sc;
	
	public PDFPageViewer pv;
	private PDFFileOutline outline;
	private StatusLinePageSelector position;

	public CDCEditor() {
		super();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException 
	{
		setSite(site);
		setInput(input);
		setPartName(input.getName());
		poppler = new PopplerJNI();
		readPdfFile();
	}
	
	public void readPdfFile() throws PartInitException{
		IEditorInput input = getEditorInput();
		String pathname = null;
		URI uri = null;
		if (input instanceof FileStoreEditorInput) {
			uri = ((FileStoreEditorInput)input).getURI();
		}
		else if ((input instanceof IFileEditorInput)) {
			uri = ((IFileEditorInput) input).getFile().getLocationURI();
		}
		else {
			throw new PartInitException("Messages.PDFEditor_ErrorMsg1");
		}
		pathname = uri.toString();
    	//poppler.document_new_from_file("file:///home/derek/Data Check and Restore Manual.pdf", null);
		poppler.document_new_from_file(pathname, null);
		pageNumbers = poppler.document_get_n_pages();
		currentPage = 1;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if(event.getType() == IResourceChangeEvent.POST_CHANGE){
			try {

				if (!(getEditorInput() instanceof IFileEditorInput)) return;

				final IFile currentfile = ((IFileEditorInput) getEditorInput()).getFile();
				if (event.getDelta().findMember(currentfile.getFullPath()) != null){
					readPdfFile();
					//derek final OutlineNode n = f.getOutline();
					Display.getDefault().asyncExec(new Runnable() {										
						@Override
						public void run() {
							if (pv != null && !pv.isDisposed()) {
								showPage(currentPage);
								//derek if (outline != null) outline.setInput(n);		
								pv.redraw();
							}
						}
					});
				}
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //derek catch (IOException e) {
				// TODO Auto-generated catch block
			//derek e.printStackTrace();
			//derek }
		}				
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		if (sc != null) sc.dispose();
		if (pv != null) pv.dispose();
		if (outline != null) outline.dispose();
		
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		if (position != null) position.removePageChangeListener(this);

    	poppler.document_release_page();
    	poppler.document_close();
		poppler = null;
		
		position = null;
		outline = null;
		pv = null;
		sc = null;
	}

	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());
		
		scc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		
		sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		pv = new PDFPageViewer(sc, this);
		//pv = new PDFPageViewerAWT(sc, this);
		sc.setContent(pv);
		// Speed up scrolling when using a wheel mouse
		ScrollBar vBar = sc.getVerticalBar();
		vBar.setIncrement(10);

		IStatusLineManager statusLineM = getEditorSite().getActionBars().getStatusLineManager();
		IContributionItem[] items = statusLineM.getItems();
		for (IContributionItem item : items) {
			if (PDFPOSITION_ID.equals(item.getId())) {
				position = (StatusLinePageSelector) item;
				position.setPageChangeListener(this);
			}
		}
		if (position == null) {
			position = new StatusLinePageSelector(PDFPOSITION_ID, 15);
			position.setPageChangeListener(this);
			statusLineM.add(position);
		}
		position.setPageInfo(1, 1);

		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

		showPage(currentPage);
		
		initKeyBindingContext();
	}

	private void initKeyBindingContext() {
		final IContextService service = (IContextService)
				getSite().getService(IContextService.class);

		pv.addFocusListener(new FocusListener() {
			IContextActivation currentContext = null;
			public void focusGained(FocusEvent e) {
				if (currentContext == null)
					currentContext = service.activateContext(CONTEXT_ID);
			}

			public void focusLost(FocusEvent e) {
				if (currentContext != null) {
					service.deactivateContext(currentContext);
					currentContext = null;
				}
			}
		});
	}	

	@Override
	public void pageChange(int pageNr) {
		showPage(pageNr);
		pv.setOrigin(sc.getOrigin().x, 0);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
	}


	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * Starts a forward search in the current pdf-editor. The editor
	 * searches for the SyncTeX file and displays the position given by the user.
	 * 
	 * @param file The TeX file 
	 * @param lineNr The line number in the TeX file
	 * @return One of {@link FORWARD_SEARCH_OK}, 
	 * 		{@link FORWARD_SEARCH_NO_SYNCTEX}, {@link FORWARD_SEARCH_FILE_NOT_FOUND},
	 * 		{@link FORWARD_SEARCH_POS_NOT_FOUND}, {@link FORWARD_SEARCH_UNKNOWN_ERROR}
	 */
	public int forwardSearch(String file, int lineNr) {
		int page = 1;
		showPage(page);
		pv.highlight(0, 0, 30, 4);
		Rectangle2D re = pv.convertPDF2ImageCoord(new Rectangle(0, 0, 1, 1));
		int x = sc.getOrigin().x;
		if (re.getX() < sc.getOrigin().x) x = (int)Math.round(re.getX() - 10);
		pv.setOrigin(x, (int)Math.round(re.getY() - sc.getBounds().height / 4.));
		//System.out.println("Page: "+page);
		try {
			this.getSite().getPage().openEditor(this.getEditorInput(), CDCEditor.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
			return FORWARD_SEARCH_UNKNOWN_ERROR;
		}
		this.setFocus();
		return FORWARD_SEARCH_OK;
	}

	public void reverseSearch(double pdfX, double pdfY) {
		String path="";
		IFileStore fileStore = EFS.getLocalFileSystem().fromLocalFile(new File(path));
		if (!fileStore.fetchInfo().isDirectory() && fileStore.fetchInfo().exists()) {
			IWorkbenchPage page=  this.getSite().getPage();
			try {
				IEditorPart part = IDE.openEditorOnFileStore(page, fileStore);
				if (part instanceof AbstractTextEditor) {
					AbstractTextEditor t = (AbstractTextEditor) part;
					IDocument doc = t.getDocumentProvider().getDocument(t.getEditorInput());
					t.selectAndReveal(doc.getLineOffset(2 - 1), doc.getLineLength(2 - 1));
				}
			} catch (PartInitException e) {
				e.printStackTrace();
			} catch (BadLocationException e) {
				writeStatusLineError(NLS.bind("Messages.PDFEditor_SynctexMsg3", 2 - 1));
			}
		} else {
			writeStatusLineError(NLS.bind("Messages.PDFEditor_SynctexMsg4", path));
		}

	}

	public PopplerJNI getPoppler() {
		return poppler;
	}
	
	private void showPage (PDFObject page) {
		//derek try {	
			//derek int pageNr = p.getPageNumber(page)+1;
			int pageNr = 1;
			if (pageNr < 1) pageNr = 1;
			if (pageNr > pageNumbers) pageNr = pageNumbers;
			poppler.document_get_page(pageNr);
			currentPage = pageNr;
			pv.showPage(pageNr);
			updateStatusLine();
		//derek } catch (IOException e) {
			//derek System.err.println("Messages.PDFEditor_ErrorMsg5");
		//derek }
	}

	public void showPage(int pageNr) {
		if (pageNr < 1) pageNr = 1;
		if (pageNr > pageNumbers) pageNr = pageNumbers;
		poppler.document_get_page(pageNr);
		currentPage = pageNr;
		pv.showPage(pageNr);
		updateStatusLine();
	}
	
	public void showFirstPage()
	{
		showPage(1);
		return;
	}
	
	public void showPreviousPage()
	{
		if (currentPage > 1) {
			showPage(currentPage - 1);
		}
		return;
	}
	
	public void showNextPage()
	{
		if (currentPage < pageNumbers) {
			showPage(currentPage + 1);
		}
		return;
	}
	
	public void showLastPage()
	{
		showPage(pageNumbers);
		return;
	}

	@Override
	public void setFocus() {
		sc.setFocus();
		updateStatusLine();
		position.setPageChangeListener(this);
	}

	/**
	 * Shows the given page and reveals the destination
	 * @param dest
	 */
	public void gotoAction(PDFDestination dest){
		int page = dest.getPage();
		if (page == -1) {
			return;
		}

		IWorkbenchPage wpage = getSite().getPage();
		wpage.getNavigationHistory().markLocation(this);

		showPage(page);

		Rectangle2D re = pv.convertPDF2ImageCoord(new Rectangle((int)Math.round(dest.getLeft()), (int)Math.round(dest.getTop()), 
				1, 1));
		int x = sc.getOrigin().x;
		if (re.getX() < sc.getOrigin().x) x = (int)Math.round(re.getX() - 10);
		pv.setOrigin(x, (int)Math.round(re.getY() - sc.getBounds().height / 4.));

		wpage.getNavigationHistory().markLocation(this);
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (outline == null) {
				//derek try {
					//derek OutlineNode n = f.getOutline();
					OutlineNode n = null;
					if (n == null) return null;
					outline = new PDFFileOutline(this);
					outline.setInput(n);
				//derek } catch (IOException e) {
					// TODO Auto-generated catch block
					//derek e.printStackTrace();
				//derek }
			}
			else return outline;
		}
		return super.getAdapter(required);
	}

	@Override
	public INavigationLocation createEmptyNavigationLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INavigationLocation createNavigationLocation() {
		return new PDFNavigationLocation(this);
	}

	private void updateStatusLine() {
		position.setPageInfo(currentPage, pageNumbers);
	}

	public void fitHorizontal() {
		pv.fitHorizontal();
	}

	public void fit() {
		pv.fit();
	}

	/**
	 * Writes an error message to the status line and deletes it after five seconds.
	 * @param text
	 */
	public void writeStatusLineError(String text) {
		final IStatusLineManager statusLineM = getEditorSite().getActionBars().getStatusLineManager();
		statusLineM.setErrorMessage(text);
		//FIXME: Should not be executed if there was another message in between the five secs.
		Display.getDefault().timerExec(5000, new Runnable() {

			@Override
			public void run() {
				statusLineM.setErrorMessage("");				 //$NON-NLS-1$
			}
		});
	}

	public Point getOrigin() {
		if (!sc.isDisposed()) return sc.getOrigin();
		else return null;
	}

	public void setOrigin(Point p) {
		sc.setRedraw(false);
		if (p != null) sc.setOrigin(p);
		sc.setRedraw(true);
	}

}
