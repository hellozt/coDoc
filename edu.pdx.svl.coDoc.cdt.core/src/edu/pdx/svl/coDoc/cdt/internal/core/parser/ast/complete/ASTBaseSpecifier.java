/*******************************************************************************
 * Copyright (c) 2002, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Rational Software - Initial API and implementation
 *******************************************************************************/
package edu.pdx.svl.coDoc.cdt.internal.core.parser.ast.complete;

import java.util.List;

import edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementRequestor;
import edu.pdx.svl.coDoc.cdt.core.parser.ast.ASTAccessVisibility;
import edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTBaseSpecifier;
import edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTTypeSpecifier;
import edu.pdx.svl.coDoc.cdt.internal.core.parser.Parser;
import edu.pdx.svl.coDoc.cdt.internal.core.parser.pst.ISymbol;

/**
 * @author jcamelon
 * 
 */
public class ASTBaseSpecifier implements IASTBaseSpecifier {
	private List references;

	private final boolean isVirtual;

	private final ISymbol symbol;

	private final ASTAccessVisibility visibility;

	private final int offset;

	/**
	 * @param symbol
	 * @param b
	 * @param visibility
	 */
	public ASTBaseSpecifier(ISymbol symbol, boolean b,
			ASTAccessVisibility visibility, int nameOffset, List references) {
		isVirtual = b;
		this.visibility = visibility;
		this.symbol = symbol;
		this.offset = nameOffset;
		this.references = references;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTBaseSpecifier#getAccess()
	 */
	public ASTAccessVisibility getAccess() {
		return visibility;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTBaseSpecifier#isVirtual()
	 */
	public boolean isVirtual() {
		return isVirtual;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTBaseSpecifier#getParentClassName()
	 */
	public String getParentClassName() {
		return String.valueOf(symbol.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTBaseSpecifier#getParentClassSpecifier()
	 */
	public IASTTypeSpecifier getParentClassSpecifier() {
		return (IASTTypeSpecifier) symbol.getASTExtension()
				.getPrimaryDeclaration();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTBaseSpecifier#getNameOffset()
	 */
	public int getNameOffset() {
		return offset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementCallbackDelegate#acceptElement(edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementRequestor)
	 */
	public void acceptElement(ISourceElementRequestor requestor) {
		Parser.processReferences(references, requestor);
		references = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementCallbackDelegate#enterScope(edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementRequestor)
	 */
	public void enterScope(ISourceElementRequestor requestor) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementCallbackDelegate#exitScope(edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementRequestor)
	 */
	public void exitScope(ISourceElementRequestor requestor) {
	}
}
