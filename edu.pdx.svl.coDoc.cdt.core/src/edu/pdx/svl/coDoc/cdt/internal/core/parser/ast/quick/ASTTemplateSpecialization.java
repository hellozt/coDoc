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
import edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTScope;
import edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTTemplateSpecialization;

/**
 * @author jcamelon
 * 
 */
public class ASTTemplateSpecialization extends ASTTemplateDeclaration implements
		IASTTemplateSpecialization {
	/**
	 * @param scope
	 */
	public ASTTemplateSpecialization(IASTScope scope, int startingOffset,
			int startingLine, char[] filename) {
		super(scope, null, startingOffset, startingLine, false, filename);
		setStartingOffsetAndLineNumber(startingOffset, startingLine);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementCallbackDelegate#accept(edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementRequestor)
	 */
	public void acceptElement(ISourceElementRequestor requestor) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementCallbackDelegate#enter(edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementRequestor)
	 */
	public void enterScope(ISourceElementRequestor requestor) {
		try {
			requestor.enterTemplateSpecialization(this);
		} catch (Exception e) {
			/* do nothing */
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementCallbackDelegate#exit(edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementRequestor)
	 */
	public void exitScope(ISourceElementRequestor requestor) {
		try {
			requestor.exitTemplateSpecialization(this);
		} catch (Exception e) {
			/* do nothing */
		}
	}
}
