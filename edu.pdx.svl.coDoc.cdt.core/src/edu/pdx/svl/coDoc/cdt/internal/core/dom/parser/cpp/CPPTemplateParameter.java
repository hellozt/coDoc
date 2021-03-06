/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 *******************************************************************************/
/*
 * Created on Mar 11, 2005
 */
package edu.pdx.svl.coDoc.cdt.internal.core.dom.parser.cpp;

import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTName;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTNode;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IScope;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IType;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.cpp.ICPPDelegate;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.cpp.ICPPTemplateParameter;
import edu.pdx.svl.coDoc.cdt.core.parser.util.ArrayUtil;
import edu.pdx.svl.coDoc.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.core.runtime.PlatformObject;

/**
 * @author aniefer
 */
public class CPPTemplateParameter extends PlatformObject implements
		ICPPTemplateParameter, ICPPInternalBinding {
	private IASTName[] declarations;

	public CPPTemplateParameter(IASTName name) {
		declarations = new IASTName[] { name };
	}

	public Object clone() {
		IType t = null;
		try {
			t = (IType) super.clone();
		} catch (CloneNotSupportedException e) {
			// not going to happen
		}
		return t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.dom.ast.IBinding#getName()
	 */
	public String getName() {
		return declarations[0].toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.dom.ast.IBinding#getNameCharArray()
	 */
	public char[] getNameCharArray() {
		return declarations[0].toCharArray();
	}

	public IASTName getPrimaryDeclaration() {
		return declarations[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.dom.ast.IBinding#getScope()
	 */
	public IScope getScope() {
		return CPPVisitor.getContainingScope(getPrimaryDeclaration());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.dom.ast.cpp.ICPPBinding#getQualifiedName()
	 */
	public String[] getQualifiedName() {
		return new String[] { getName() };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.dom.ast.cpp.ICPPBinding#getQualifiedNameCharArray()
	 */
	public char[][] getQualifiedNameCharArray() {
		return new char[][] { getNameCharArray() };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.dom.ast.cpp.ICPPBinding#isGloballyQualified()
	 */
	public boolean isGloballyQualified() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.internal.core.dom.parser.cpp.ICPPInternalBinding#getDeclarations()
	 */
	public IASTNode[] getDeclarations() {
		return declarations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.internal.core.dom.parser.cpp.ICPPInternalBinding#getDefinition()
	 */
	public IASTNode getDefinition() {
		if (declarations != null && declarations.length > 0)
			return declarations[0];
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.internal.core.dom.parser.cpp.ICPPInternalBinding#createDelegate(edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTName)
	 */
	public ICPPDelegate createDelegate(IASTName name) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.internal.core.dom.parser.cpp.ICPPInternalBinding#addDefinition(edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTNode)
	 */
	public void addDefinition(IASTNode node) {
		addDeclaration(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.internal.core.dom.parser.cpp.ICPPInternalBinding#addDeclaration(edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTNode)
	 */
	public void addDeclaration(IASTNode node) {
		if (!(node instanceof IASTName))
			return;
		IASTName name = (IASTName) node;
		if (declarations == null)
			declarations = new IASTName[] { name };
		else {
			if (declarations.length > 0 && declarations[0] == node)
				return;
			// keep the lowest offset declaration in [0]
			if (declarations.length > 0
					&& ((ASTNode) node).getOffset() < ((ASTNode) declarations[0])
							.getOffset()) {
				declarations = (IASTName[]) ArrayUtil.prepend(IASTName.class,
						declarations, name);
			} else {
				declarations = (IASTName[]) ArrayUtil.append(IASTName.class,
						declarations, name);
			}
		}
	}

	public void removeDeclaration(IASTNode node) {
		if (declarations != null) {
			for (int i = 0; i < declarations.length; i++) {
				if (node == declarations[i]) {
					if (i == declarations.length - 1)
						declarations[i] = null;
					else
						System.arraycopy(declarations, i + 1, declarations, i,
								declarations.length - 1 - i);
					return;
				}
			}
		}
	}
}
