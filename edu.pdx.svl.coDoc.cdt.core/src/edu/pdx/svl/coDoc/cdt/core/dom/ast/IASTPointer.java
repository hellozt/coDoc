/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 *******************************************************************************/
package edu.pdx.svl.coDoc.cdt.core.dom.ast;

/**
 * This represents the good ol' * pointer operator.
 * 
 * @author Doug Schaefer
 */
public interface IASTPointer extends IASTPointerOperator {

	// Qualifiers applied to the pointer type
	/**
	 * Is this a const pointer?
	 * 
	 * @return boolean
	 */
	public boolean isConst();

	/**
	 * Is this a volatile pointer?
	 * 
	 * @return boolean
	 */
	public boolean isVolatile();

	/**
	 * Set this to be a const pointer (true/false).
	 * 
	 * @param value -
	 *            the value
	 */
	public void setConst(boolean value);

	/**
	 * Set this to be a volatile pointer (true/false).
	 * 
	 * @param value -
	 *            the value
	 */
	public void setVolatile(boolean value);

}
