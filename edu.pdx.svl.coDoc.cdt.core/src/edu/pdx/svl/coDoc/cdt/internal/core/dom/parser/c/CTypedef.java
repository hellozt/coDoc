/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Rational Software - Initial API and implementation 
 *******************************************************************************/

package edu.pdx.svl.coDoc.cdt.internal.core.dom.parser.c;

import edu.pdx.svl.coDoc.cdt.core.dom.ast.DOMException;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTDeclarator;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTName;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTNode;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IScope;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IType;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.ITypedef;
import edu.pdx.svl.coDoc.cdt.internal.core.dom.parser.ITypeContainer;
import org.eclipse.core.runtime.PlatformObject;

/**
 * Created on Nov 8, 2004
 * 
 * @author aniefer
 */
public class CTypedef extends PlatformObject implements ITypedef,
		ITypeContainer {
	private final IASTName name;

	private IType type = null;

	public CTypedef(IASTName name) {
		this.name = name;
	}

	public IASTNode getPhysicalNode() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.dom.ast.ITypedef#getType()
	 */
	public IType getType() {
		if (type == null && name.getParent() instanceof IASTDeclarator)
			type = CVisitor.createType((IASTDeclarator) name.getParent());
		return type;
	}

	public void setType(IType t) {
		type = t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.dom.ast.IBinding#getName()
	 */
	public String getName() {
		return name.toString();
	}

	public char[] getNameCharArray() {
		return name.toCharArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.dom.ast.IBinding#getScope()
	 */
	public IScope getScope() {
		IASTDeclarator declarator = (IASTDeclarator) name.getParent();
		return CVisitor.getContainingScope(declarator.getParent());
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
	 * @see edu.pdx.svl.coDoc.cdt.core.dom.ast.IType#isSameType(edu.pdx.svl.coDoc.cdt.core.dom.ast.IType)
	 */
	public boolean isSameType(IType t) {
		if (t == this)
			return true;
		if (t instanceof ITypedef)
			try {
				IType temp = getType();
				if (temp != null)
					return temp.isSameType(((ITypedef) t).getType());
				return false;
			} catch (DOMException e) {
				return false;
			}

		IType temp = getType();
		if (temp != null)
			return temp.isSameType(t);
		return false;
	}
}
