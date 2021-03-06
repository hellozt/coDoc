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
package edu.pdx.svl.coDoc.cdt.internal.core.parser.ast.quick;

import edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementRequestor;
import edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTConstructorMemberInitializer;
import edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTExpression;

/**
 * @author jcamelon
 * 
 */
public class ASTConstructorMemberInitializer implements
		IASTConstructorMemberInitializer {
	/**
	 * @param string
	 * @param expressionList
	 */
	public ASTConstructorMemberInitializer(char[] name,
			IASTExpression expressionList) {
		this.name = name;
		this.expressionList = expressionList;
	}

	private final IASTExpression expressionList;

	private final char[] name;

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTConstructorMemberInitializer#getExpressionList()
	 */
	public IASTExpression getExpressionList() {
		return expressionList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTConstructorMemberInitializer#getName()
	 */
	public String getName() {
		return String.valueOf(name);
	}

	public char[] getNameCharArray() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementCallbackDelegate#acceptElement(edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementRequestor)
	 */
	public void acceptElement(ISourceElementRequestor requestor) {
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
