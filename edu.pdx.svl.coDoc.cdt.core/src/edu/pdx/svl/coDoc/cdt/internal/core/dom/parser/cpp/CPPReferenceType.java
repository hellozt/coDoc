/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

/*
 * Created on Dec 15, 2004
 */
package edu.pdx.svl.coDoc.cdt.internal.core.dom.parser.cpp;

import edu.pdx.svl.coDoc.cdt.core.dom.ast.DOMException;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IType;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.ITypedef;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.cpp.ICPPReferenceType;
import edu.pdx.svl.coDoc.cdt.internal.core.dom.parser.ITypeContainer;

/**
 * @author aniefer
 */
public class CPPReferenceType implements ICPPReferenceType, ITypeContainer {
	IType type = null;

	/**
	 * @param type
	 * @param operator
	 */
	public CPPReferenceType(IType type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.dom.ast.cpp.ICPPReferenceType#getType()
	 */
	public IType getType() {
		return type;
	}

	public void setType(IType t) {
		type = t;
	}

	public boolean isSameType(IType obj) {
		if (obj == this)
			return true;
		if (obj instanceof ITypedef)
			return ((ITypedef) obj).isSameType(this);

		if (type == null)
			return (obj == null);

		if (obj instanceof ICPPReferenceType) {
			try {
				return type.isSameType(((ICPPReferenceType) obj).getType());
			} catch (DOMException e) {
				return false;
			}
		}
		return false;
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
}
