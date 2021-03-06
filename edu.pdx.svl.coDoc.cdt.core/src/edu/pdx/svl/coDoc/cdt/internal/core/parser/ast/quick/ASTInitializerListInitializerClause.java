/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package edu.pdx.svl.coDoc.cdt.internal.core.parser.ast.quick;

import java.util.Iterator;
import java.util.List;

import edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTInitializerClause;
import edu.pdx.svl.coDoc.cdt.internal.core.parser.ast.EmptyIterator;

/**
 * @author jcamelon
 */
public class ASTInitializerListInitializerClause extends ASTInitializerClause
		implements IASTInitializerClause {

	private final List initializerClauses;

	/**
	 * @param kind
	 * @param initializerClauses
	 */
	public ASTInitializerListInitializerClause(Kind kind,
			List initializerClauses) {
		super(kind);
		this.initializerClauses = initializerClauses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTInitializerClause#getInitializers()
	 */
	public Iterator getInitializers() {
		if (initializerClauses == null)
			return EmptyIterator.EMPTY_ITERATOR;
		return initializerClauses.iterator();
	}
}
