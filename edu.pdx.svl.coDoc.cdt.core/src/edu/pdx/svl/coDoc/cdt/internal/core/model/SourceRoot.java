/*******************************************************************************
 * Copyright (c) 2000, 2004 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *******************************************************************************/
package edu.pdx.svl.coDoc.cdt.internal.core.model;

import edu.pdx.svl.coDoc.cdt.core.model.ICElement;
import edu.pdx.svl.coDoc.cdt.core.model.ISourceEntry;
import edu.pdx.svl.coDoc.cdt.core.model.ISourceRoot;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/**
 * SourceRoot
 */
public class SourceRoot extends CContainer implements ISourceRoot {

	ISourceEntry sourceEntry;

	/**
	 * @param parent
	 * @param res
	 */
	public SourceRoot(ICElement parent, IResource res, ISourceEntry entry) {
		super(parent, res);
		sourceEntry = entry;
		IPath path = getPath();
		IPath cpath = getParent().getPath();
		if (path.segmentCount() > cpath.segmentCount()) {
			IPath p = path.removeFirstSegments(cpath.segmentCount());
			setElementName(p.toString());
		}
	}

	public ISourceEntry getSourceEntry() {
		return sourceEntry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.model.ISourceRoot#isOnclasspath(edu.pdx.svl.coDoc.cdt.core.model.ICElement)
	 */
	public boolean isOnSourceEntry(ICElement element) {
		IPath path = element.getPath();
		return this.isOnSourceEntry(path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.model.ISourceRoot#isOnSourceEntry(org.eclipse.core.resources.IResource)
	 */
	public boolean isOnSourceEntry(IResource res) {
		IPath path = res.getFullPath();
		return isOnSourceEntry(path);
	}

	public boolean isOnSourceEntry(IPath path) {
		if (sourceEntry.getPath().isPrefixOf(path)) {
			return true;
		}
		return false;
	}

}
