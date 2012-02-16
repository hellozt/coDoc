package edu.pdx.svl.coDoc.refexp.referencemodel;

import java.util.Vector;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public abstract class Reference {
	
	@ElementList
	protected Vector<Reference> children = new Vector<Reference>();

	/**
	 * Any given reference may have a comment associated with it.
	 */
	@Element(required=false)
	protected String comment;
	
	@Element
	protected PDFFile pdfFile;
	
	@Element(required=false)
	protected PDFSelection pdfSelection;
	

	/**
	 * By default a new reference is linked to the currently selected PDF File.
	 */
	public Reference() {
		pdfFile = PDFManager.INSTANCE.getCurrentPdfFile();
	}

	
	// The name of the type of reference.
	public abstract String getType();
	
	//description of reference to be used in Source Reference Tree Item
	public abstract String description();

	
	// getters and setters
	public Vector<Reference> getChildrenList() {
		return children;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}


	public PDFFile getPdfFile() {
		return pdfFile;
	}


	public void setPdfFile(PDFFile pdfFile) {
		this.pdfFile = pdfFile;
	}


	public PDFSelection getPdfSelection() {
		return pdfSelection;
	}


	public void setPdfSelection(PDFSelection pdfSelection) {
		this.pdfSelection = pdfSelection;
	}
	
	public String pdfDescription() {
		if (pdfSelection != null) {
			return pdfSelection.description();
		}
		return "";
	}
	
	public String pdfPage() {
		if (pdfSelection != null) {
			return pdfSelection.page();
		}
		return "";
	}

}
