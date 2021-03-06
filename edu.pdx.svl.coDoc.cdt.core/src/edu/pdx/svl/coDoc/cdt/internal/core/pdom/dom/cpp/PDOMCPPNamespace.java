/*******************************************************************************
 * Copyright (c) 2006 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * QNX - Initial API and implementation
 *******************************************************************************/

package edu.pdx.svl.coDoc.cdt.internal.core.pdom.dom.cpp;

import edu.pdx.svl.coDoc.cdt.core.CCorePlugin;
import edu.pdx.svl.coDoc.cdt.core.dom.IPDOMVisitor;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.DOMException;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTFunctionCallExpression;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTIdExpression;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTName;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTNode;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IBinding;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IScope;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.cpp.ICPPNamespace;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.cpp.ICPPNamespaceScope;
import edu.pdx.svl.coDoc.cdt.internal.core.pdom.PDOM;
import edu.pdx.svl.coDoc.cdt.internal.core.pdom.db.BTree;
import edu.pdx.svl.coDoc.cdt.internal.core.pdom.db.IBTreeVisitor;
import edu.pdx.svl.coDoc.cdt.internal.core.pdom.dom.PDOMBinding;
import edu.pdx.svl.coDoc.cdt.internal.core.pdom.dom.PDOMNamedNode;
import edu.pdx.svl.coDoc.cdt.internal.core.pdom.dom.PDOMNode;
import edu.pdx.svl.coDoc.cdt.internal.core.pdom.dom.PDOMNotImplementedError;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Doug Schaefer
 * 
 */
public class PDOMCPPNamespace extends PDOMBinding implements ICPPNamespace,
		ICPPNamespaceScope {

	private static final int INDEX_OFFSET = PDOMBinding.RECORD_SIZE + 0;

	protected static final int RECORD_SIZE = PDOMBinding.RECORD_SIZE + 4;

	public PDOMCPPNamespace(PDOM pdom, PDOMNode parent, IASTName name)
			throws CoreException {
		super(pdom, parent, name);
	}

	public PDOMCPPNamespace(PDOM pdom, int record) {
		super(pdom, record);
	}

	protected int getRecordSize() {
		return RECORD_SIZE;
	}

	public int getNodeType() {
		return PDOMCPPLinkage.CPPNAMESPACE;
	}

	public BTree getIndex() throws CoreException {
		return new BTree(pdom.getDB(), record + INDEX_OFFSET);
	}

	public void accept(final IPDOMVisitor visitor) throws CoreException {
		super.accept(visitor);
		getIndex().accept(new IBTreeVisitor() {
			public int compare(int record) throws CoreException {
				return 1;
			};

			public boolean visit(int record) throws CoreException {
				PDOMBinding binding = pdom.getBinding(record);
				if (binding != null) {
					if (visitor.visit(binding))
						binding.accept(visitor);
				}
				return true;
			};
		});
	}

	public void addChild(PDOMNamedNode child) throws CoreException {
		getIndex().insert(child.getRecord(), child.getIndexComparator());
	}

	public String[] getQualifiedName() throws DOMException {
		throw new PDOMNotImplementedError();
	}

	public char[][] getQualifiedNameCharArray() throws DOMException {
		throw new PDOMNotImplementedError();
	}

	public boolean isGloballyQualified() throws DOMException {
		throw new PDOMNotImplementedError();
	}

	public IBinding[] getMemberBindings() throws DOMException {
		throw new PDOMNotImplementedError();
	}

	public ICPPNamespaceScope getNamespaceScope() throws DOMException {
		return this;
	}

	public void addUsingDirective(IASTNode directive) throws DOMException {
		throw new PDOMNotImplementedError();
	}

	public IASTNode[] getUsingDirectives() throws DOMException {
		// TODO
		return new IASTNode[0];
	}

	public void addBinding(IBinding binding) throws DOMException {
		throw new PDOMNotImplementedError();
	}

	public void addName(IASTName name) throws DOMException {
		throw new PDOMNotImplementedError();
	}

	public IBinding[] find(String name) throws DOMException {
		throw new PDOMNotImplementedError();
	}

	public void flushCache() throws DOMException {
		throw new PDOMNotImplementedError();
	}

	private static final class FindBinding extends PDOMNamedNode.NodeFinder {
		PDOMBinding pdomBinding;

		final int[] desiredType;

		public FindBinding(PDOM pdom, char[] name, int desiredType) {
			this(pdom, name, new int[] { desiredType });
		}

		public FindBinding(PDOM pdom, char[] name, int[] desiredType) {
			super(pdom, name);
			this.desiredType = desiredType;
		}

		public boolean visit(int record) throws CoreException {
			if (record == 0)
				return true;
			PDOMBinding tBinding = pdom.getBinding(record);
			if (!tBinding.hasName(name))
				// no more bindings with our desired name
				return false;
			int nodeType = tBinding.getNodeType();
			for (int i = 0; i < desiredType.length; ++i)
				if (nodeType == desiredType[i]) {
					// got it
					pdomBinding = tBinding;
					return false;
				}

			// wrong type, try again
			return true;
		}
	}

	public IBinding getBinding(IASTName name, boolean resolve)
			throws DOMException {
		try {
			if (name instanceof ICPPASTQualifiedName) {
				IASTName lastName = ((ICPPASTQualifiedName) name).getLastName();
				return lastName != null ? lastName.resolveBinding() : null;
			}
			IASTNode parent = name.getParent();
			if (parent instanceof ICPPASTQualifiedName) {
				IASTName[] names = ((ICPPASTQualifiedName) parent).getNames();
				if (name == names[names.length - 1]) {
					parent = parent.getParent();
				} else {
					IASTName nsname = null;
					for (int i = 0; i < names.length - 2; ++i) {
						if (name != names[i])
							nsname = names[i];
					}
					// make sure we're the namespace they're talking about
					if (nsname != null && !equals(pdom.resolveBinding(nsname)))
						return null;

					// Look up the name
					FindBinding visitor = new FindBinding(pdom, name
							.toCharArray(), new int[] {
							PDOMCPPLinkage.CPPCLASSTYPE,
							PDOMCPPLinkage.CPPNAMESPACE,
							PDOMCPPLinkage.CPPFUNCTION,
							PDOMCPPLinkage.CPPVARIABLE });
					getIndex().accept(visitor);
					return visitor.pdomBinding;
				}
			}
			if (parent instanceof IASTIdExpression) {
				// reference
				IASTNode eParent = parent.getParent();
				if (eParent instanceof IASTFunctionCallExpression) {
					FindBinding visitor = new FindBinding(pdom, name
							.toCharArray(), PDOMCPPLinkage.CPPFUNCTION);
					getIndex().accept(visitor);
					return visitor.pdomBinding;
				} else {
					FindBinding visitor = new FindBinding(
							pdom,
							name.toCharArray(),
							(name.getParent() instanceof ICPPASTQualifiedName && ((ICPPASTQualifiedName) name
									.getParent()).getLastName() != name) ? PDOMCPPLinkage.CPPNAMESPACE
									: PDOMCPPLinkage.CPPVARIABLE);
					getIndex().accept(visitor);
					return visitor.pdomBinding;
				}
			} else if (parent instanceof IASTNamedTypeSpecifier) {
				FindBinding visitor = new FindBinding(pdom, name.toCharArray(),
						PDOMCPPLinkage.CPPCLASSTYPE);
				getIndex().accept(visitor);
				return visitor.pdomBinding;
			}
			return null;
		} catch (CoreException e) {
			CCorePlugin.log(e);
			return null;
		}
	}

	public IScope getParent() throws DOMException {
		// TODO
		return null;
	}

	public IASTNode getPhysicalNode() throws DOMException {
		throw new PDOMNotImplementedError();
	}

	public IASTName getScopeName() throws DOMException {
		throw new PDOMNotImplementedError();
	}

	public boolean isFullyCached() throws DOMException {
		return true;
	}

	public void removeBinding(IBinding binding) throws DOMException {
		throw new PDOMNotImplementedError();
	}

	public void setFullyCached(boolean b) throws DOMException {
		throw new PDOMNotImplementedError();
	}

}
