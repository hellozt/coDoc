/**********************************************************************
 * Copyright (c) 2002, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package edu.pdx.svl.coDoc.cdt.core.model;

import edu.pdx.svl.coDoc.cdt.core.parser.ast.ASTAccessVisibility;

/**
 * Common protocol for C elements that can be members of types. This set
 * consists of <code>IType</code>, <code>IMethod</code>,
 * <code>IField</code>.
 */
public interface IMember extends IDeclaration {

	/**
	 * Returns the member's visibility V_PRIVATE = 0 V_PROTECTED = 1 V_PUBLIC =
	 * 2
	 * 
	 * @return int
	 */
	public ASTAccessVisibility getVisibility() throws CModelException;

}
