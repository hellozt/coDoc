/*******************************************************************************
 * Copyright (c) 2002, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Rational Software - Initial API and implementation
 *******************************************************************************/
package edu.pdx.svl.coDoc.cdt.internal.core.model;

import edu.pdx.svl.coDoc.cdt.core.model.CoreModel;
import edu.pdx.svl.coDoc.cdt.core.model.ICContainer;
import edu.pdx.svl.coDoc.cdt.core.model.ICElement;
import edu.pdx.svl.coDoc.cdt.core.model.ICElementDelta;
import edu.pdx.svl.coDoc.cdt.core.model.CModelException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;

/**
 * This class is used by <code>CModelManager</code> to convert
 * <code>IResourceDelta</code>s into <code>ICElementDelta</code>s. It also
 * does some processing on the <code>CElement</code>s involved (e.g. closing
 * them or updating classpaths).
 */
public class DeltaProcessor {

	/**
	 * The <code>CElementDelta</code> corresponding to the
	 * <code>IResourceDelta</code> being translated.
	 */
	protected CElementDelta fCurrentDelta;

	/*
	 * The C element that was last created (see createElement(IResource). This
	 * is used as a stack of C elements (using getParent() to pop it, and using
	 * the various get*(...) to push it.
	 */
	ICElement currentElement;

	static final ICElementDelta[] NO_DELTA = new ICElementDelta[0];

	public static boolean VERBOSE = false;

	// Hold on the element bein renamed.
	ICElement movedFromElement = null;

	/**
	 * Creates the create corresponding to this resource. Returns null if none
	 * was found.
	 */
	protected ICElement createElement(IPath path) {
		return CModelManager.getDefault().create(path);
	}

	/**
	 * Creates the create corresponding to this resource. Returns null if none
	 * was found.
	 */
	protected ICElement createElement(IResource resource) {
		if (resource == null) {
			return null;
		}

		CModelManager manager = CModelManager.getDefault();

		boolean shouldProcess = true;

		// Check for C nature or if the was a CNature
		if (!(resource instanceof IWorkspaceRoot)) {
			IProject project = resource.getProject();
			if (!(CoreModel.hasCNature(project) || CoreModel
					.hasCCNature(project))) {
				shouldProcess = false;
				CModel root = manager.getCModel();
				CModelInfo rootInfo = (CModelInfo) manager.peekAtInfo(root);
				if (rootInfo != null) {
					ICElement[] celements = rootInfo.getChildren();
					for (int i = 0; i < celements.length; i++) {
						IResource r = celements[i].getResource();
						if (project.equals(r)) {
							shouldProcess = true;
						}
					}
				}
			}
		}

		if (!shouldProcess) {
			return null;
		}
		ICElement celement = manager.create(resource, null);
		return celement;
	}

	/**
	 * Adds the given child handle to its parent's cache of children.
	 */
	protected void addToParentInfo(Openable child) throws CModelException {
		Openable parent = (Openable) child.getParent();
		if (parent != null && parent.isOpen()) {
			CElementInfo info = parent.getElementInfo();
			// Check if the element exits
			if (!info.includesChild(child)) {
				info.addChild(child);
			}
		}
	}

	/**
	 * Removes the given element from its parents cache of children. If the
	 * element does not have a parent, or the parent is not currently open, this
	 * has no effect.
	 */
	private void removeFromParentInfo(ICElement child) throws CModelException {
		CModelManager factory = CModelManager.getDefault();

		// Remove the child from the parent list.
		ICElement parent = child.getParent();
		if (parent != null && parent instanceof Parent
				&& factory.peekAtInfo(parent) != null) {
			((Parent) parent).removeChild(child);
		}
	}

	/**
	 * Release the Element and cleaning.
	 */
	protected void releaseCElement(ICElement celement) throws CModelException {
		CModelManager factory = CModelManager.getDefault();
		removeFromParentInfo(celement);
		factory.releaseCElement(celement);
	}

	/**
	 * Processing for an element that has been added:
	 * <ul>
	 * <li>If the element is a project, do nothing, and do not process
	 * children, as when a project is created it does not yet have any natures -
	 * specifically a java nature.
	 * <li>If the elemet is not a project, process it as added (see
	 * <code>basicElementAdded</code>.
	 * </ul>
	 */
	protected void elementAdded(ICElement element, IResourceDelta delta)
			throws CModelException {

		if (element instanceof Openable) {
			addToParentInfo((Openable) element);
		}
		if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {
			// ICElement movedFromElement =
			// createElement(delta.getMovedFromPath());
			if (movedFromElement == null) {
				movedFromElement = createElement(delta.getMovedFromPath());
			}
			fCurrentDelta.movedTo(element, movedFromElement);
			movedFromElement = null;
		} else {
			fCurrentDelta.added(element);
		}
	}

	/**
	 * Processing for the closing of an element - there are two cases:
	 * <ul>
	 * <li>when a project is closed (in the platform sense), the CModel reports
	 * this as if the CProject has been removed.
	 * <li>otherwise, the CModel reports this as a the element being closed
	 * (CHANGED + F_CLOSED).
	 * </ul>
	 * <p>
	 * In both cases, the children of the element are not processed. When a
	 * resource is closed, the platform reports all children as removed. This
	 * would effectively delete the classpath if we processed children.
	 */
	protected void elementClosed(ICElement element, IResourceDelta delta)
			throws CModelException {

		if (element.getElementType() == ICElement.C_PROJECT) {
			// treat project closing as removal
			elementRemoved(element, delta);
			CModelInfo rootInfo = (CModelInfo) CModelManager.getDefault()
					.getCModel().getElementInfo();
			rootInfo.setNonCResources(null);
		} else {
			fCurrentDelta.closed(element);
		}
	}

	/**
	 * Processing for the opening of an element - there are two cases:
	 * <ul>
	 * <li>when a project is opened (in the platform sense), the CModel reports
	 * this as if the CProject has been added.
	 * <li>otherwise, the CModel reports this as a the element being opened
	 * (CHANGED + F_CLOSED).
	 * </ul>
	 */
	protected void elementOpened(ICElement element, IResourceDelta delta)
			throws CModelException {

		if (element.getElementType() == ICElement.C_PROJECT) {
			// treat project opening as addition
			if (hasCNature(delta.getResource())) {
				elementAdded(element, delta);
			}
			CModelInfo rootInfo = (CModelInfo) CModelManager.getDefault()
					.getCModel().getElementInfo();
			rootInfo.setNonCResources(null);

		} else {
			fCurrentDelta.opened(element);
		}
	}

	/*
	 * Closes the given element, which removes it from the cache of open
	 * elements.
	 */
	private void close(Openable element) {
		try {
			element.close();
		} catch (CModelException e) {
			// do nothing
		}
	}

	/**
	 * Generic processing for elements with changed contents:
	 * <ul>
	 * <li>The element is closed such that any subsequent accesses will re-open
	 * the element reflecting its new structure.
	 * <li>An entry is made in the delta reporting a content change (K_CHANGE
	 * with F_CONTENT flag set).
	 * </ul>
	 */
	protected void elementChanged(ICElement element, IResourceDelta delta) {
		// For Binary/Archive We can not call close() to do the work
		// closing will remove the element from the {Binary,Archive}Container
		// We nee to clear the cache explicitely
		if (element instanceof Openable) {
			close((Openable) element);
		}
		fCurrentDelta.changed(element, ICElementDelta.F_CONTENT);
	}

	/**
	 * Generic processing for a removed element:
	 * <ul>
	 * <li>Close the element, removing its structure from the cache
	 * <li>Remove the element from its parent's cache of children
	 * <li>Add a REMOVED entry in the delta
	 * </ul>
	 */
	protected void elementRemoved(ICElement element, IResourceDelta delta)
			throws CModelException {
		if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
			IPath movedToPath = delta.getMovedToPath();
			// create the moved to element
			ICElement movedToElement = createElement(movedToPath);
			if (movedToElement == null) {
				// moved outside
				fCurrentDelta.removed(element);
			} else {
				movedFromElement = element;
				fCurrentDelta.movedFrom(element, movedToElement);
			}
		} else {
			fCurrentDelta.removed(element);
		}
		releaseCElement(element);
	}

	/**
	 * Filters the generated <code>CElementDelta</code>s to remove those
	 * which should not be fired (because they don't represent a real change in
	 * the C Model).
	 */
	protected ICElementDelta[] filterRealDeltas(ICElementDelta[] deltas) {

		int length = deltas.length;
		ICElementDelta[] realDeltas = null;
		int index = 0;
		for (int i = 0; i < length; i++) {
			CElementDelta delta = (CElementDelta) deltas[i];
			if (delta == null) {
				continue;
			}
			if (delta.getAffectedChildren().length > 0
					|| delta.getKind() == ICElementDelta.ADDED
					|| delta.getKind() == ICElementDelta.REMOVED
					|| (delta.getFlags() & ICElementDelta.F_CLOSED) != 0
					|| (delta.getFlags() & ICElementDelta.F_OPENED) != 0
					|| delta.resourceDeltasCounter > 0) {

				if (realDeltas == null) {
					realDeltas = new ICElementDelta[length];
				}
				realDeltas[index++] = delta;
			}
		}
		if (index > 0) {
			ICElementDelta[] result = new ICElementDelta[index];
			System.arraycopy(realDeltas, 0, result, 0, index);
			return result;
		}
		return NO_DELTA;
	}

	/**
	 * Returns true if the given resource is contained in an open project with a
	 * java nature, otherwise false.
	 */
	protected boolean hasCNature(IResource resource) {
		// ensure the project has a C nature (if open)
		IProject project = resource.getProject();
		if (project.isOpen()) {
			return CoreModel.hasCNature(project);
		}
		return false;
	}

	/**
	 * Converts a <code>IResourceDelta</code> rooted in a
	 * <code>Workspace</code> into the corresponding set of
	 * <code>ICElementDelta</code>, rooted in the relevant
	 * <code>CModel</code>s.
	 */
	public ICElementDelta[] processResourceDelta(IResourceDelta changes) {

		try {
			ICElement root = CModelManager.getDefault().getCModel();
			// get the workspace delta, and start processing there.
			IResourceDelta[] deltas = changes.getAffectedChildren();
			ICElementDelta[] translatedDeltas = new CElementDelta[deltas.length];
			// System.out.println("delta.length: " + deltas.length);
			for (int i = 0; i < deltas.length; i++) {
				IResourceDelta delta = deltas[i];
				fCurrentDelta = new CElementDelta(root);
				traverseDelta(root, delta); // traverse delta
				translatedDeltas[i] = fCurrentDelta;
			}
			return filterRealDeltas(translatedDeltas);
		} finally {
		}
	}

	/**
	 * Converts an <code>IResourceDelta</code> and its children into the
	 * corresponding <code>ICElementDelta</code>s. Return whether the delta
	 * corresponds to a resource on the classpath. If it is not a resource on
	 * the classpath, it will be added as a non-java resource by the sender of
	 * this method.
	 */
	protected void traverseDelta(ICElement parent, IResourceDelta delta) {
		boolean updateChildren = true;
		try {
			IResource resource = delta.getResource();
			ICElement current = createElement(resource);
			updateChildren = updateCurrentDeltaAndIndex(current, delta);
			if (current != null) {
				parent = current;
			}
			if (updateChildren) {
				IResourceDelta[] children = delta.getAffectedChildren();
				for (int i = 0; i < children.length; i++) {
					traverseDelta(parent, children[i]);
				}
			}
		} catch (CModelException e) {
		}
		if (updateChildren) {
			IResourceDelta[] children = delta.getAffectedChildren();
			for (int i = 0; i < children.length; i++) {
				traverseDelta(parent, children[i]);
			}
		}
	}

	/*
	 * Update the current delta (ie. add/remove/change the given element) and
	 * update the correponding index. Returns whether the children of the given
	 * delta must be processed. @throws a CModelException if the delta doesn't
	 * correspond to a c element of the given type.
	 */
	private boolean updateCurrentDeltaAndIndex(ICElement element,
			IResourceDelta delta) throws CModelException {

		IResource resource = delta.getResource();

		switch (delta.getKind()) {
		case IResourceDelta.ADDED:
			if (element != null) {
				elementAdded(element, delta);
				return element instanceof ICContainer;
			}
			return false;

		case IResourceDelta.REMOVED:
			if (element != null) {
				elementRemoved(element, delta);
			}
			return element instanceof ICContainer;

		case IResourceDelta.CHANGED:
			int flags = delta.getFlags();
			if ((flags & IResourceDelta.CONTENT) != 0) {
				// content has changed
				if (element != null) {
					elementChanged(element, delta);
				}
			} else if (resource.getType() == IResource.PROJECT) {
				if ((flags & IResourceDelta.OPEN) != 0) {
					// project has been opened or closed
					IProject project = (IProject) resource;
					if (element != null) {
						if (project.isOpen()) {
							elementOpened(element, delta);
							return false;
						}
						elementClosed(element, delta);
						// Don't process children
						return false;
					}
				}
				if ((flags & IResourceDelta.DESCRIPTION) != 0) {
					IProject res = (IProject) delta.getResource();
					CModel cModel = CModelManager.getDefault().getCModel();
					boolean wasCProject = cModel.findCProject(res) != null;
					boolean isCProject = CProject.hasCNature(res);
					if (wasCProject != isCProject) {
						// project's nature has been added or removed
						if (element != null) {
							// note its resources are still visible as roots to
							// other projects
							if (isCProject) {
								elementOpened(element, delta);
							} else {
								elementRemoved(element, delta);
							}
							return true;
						}
					}
				}
			}
			return true;
		}
		return true;
	}
}
