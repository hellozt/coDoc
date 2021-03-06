/*******************************************************************************
 * Copyright (c) 2002, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Rational Software - Initial API and implementation
 *******************************************************************************/
package edu.pdx.svl.coDoc.cdt.internal.core.parser.ast.complete;

import edu.pdx.svl.coDoc.cdt.internal.core.parser.pst.ISymbol;
import edu.pdx.svl.coDoc.cdt.internal.core.parser.pst.ISymbolOwner;

/**
 * @author jcamelon
 * 
 */
public class ASTSymbolOwner extends ASTNode implements ISymbolOwner {
	protected ISymbol symbol;

	/**
	 * 
	 */
	public ASTSymbolOwner(ISymbol symbol) {
		this.symbol = symbol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.internal.core.parser.pst.ISymbolOwner#getSymbol()
	 */
	public ISymbol getSymbol() {
		return symbol;
	}

	public void setSymbol(ISymbol symbol) {
		this.symbol = symbol;
	}

}
