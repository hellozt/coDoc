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
import edu.pdx.svl.coDoc.cdt.core.parser.ITokenDuple;
import edu.pdx.svl.coDoc.cdt.core.parser.ast.ASTUtil;
import edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTExpression;
import edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTTypeId;

/**
 * @author jcamelon
 * 
 */
public class ASTNewExpression extends ASTExpression {

	private final IASTTypeId typeId;

	private final IASTNewExpressionDescriptor newDescriptor;

	/**
	 * @param kind
	 * @param references
	 * @param newDescriptor
	 * @param typeId
	 */
	public ASTNewExpression(Kind kind, List references,
			IASTNewExpressionDescriptor newDescriptor, IASTTypeId typeId) {
		super(kind, references);
		this.newDescriptor = newDescriptor;
		this.typeId = typeId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTExpression#getTypeId()
	 */
	public IASTTypeId getTypeId() {
		return typeId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTExpression#getNewExpressionDescriptor()
	 */
	public IASTNewExpressionDescriptor getNewExpressionDescriptor() {
		return newDescriptor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.internal.core.parser.ast.complete.ASTExpression#processCallbacks(edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementRequestor)
	 */
	protected void processCallbacks(ISourceElementRequestor requestor) {
		super.processCallbacks(requestor);
		typeId.acceptElement(requestor);
		newDescriptor.acceptElement(requestor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.internal.core.parser.ast.complete.ASTExpression#findNewDescriptor(edu.pdx.svl.coDoc.cdt.core.parser.ITokenDuple)
	 */
	public IASTExpression findNewDescriptor(ITokenDuple finalDuple) {
		if (((ASTTypeId) typeId).getTokenDuple().contains(finalDuple))
			return this;
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTExpression#freeReferences(edu.pdx.svl.coDoc.cdt.core.parser.ast.IReferenceManager)
	 */
	public void freeReferences() {
		super.freeReferences();
		typeId.freeReferences();
	}

	public String toString() {
		return ASTUtil.getExpressionString(this);
	}

	public ASTExpression findOwnerExpressionForIDExpression(ITokenDuple duple) {
		ASTTypeId ti = (ASTTypeId) getTypeId();
		ITokenDuple typeDuple = ti.getTokenDuple();

		if (typeDuple.equals(duple))
			return this;
		// check subduple
		if (typeDuple.contains(duple))
			return this;

		// else, check the parameters
		ASTExpression ownerExpression = null;
		ASTNewDescriptor nd = (ASTNewDescriptor) getNewExpressionDescriptor();
		List newInitializerExpressions = nd.getNewInitializerExpressionsList();
		int size = newInitializerExpressions.size();
		for (int i = 0; i < size; i++) {
			ASTExpression expressionList = (ASTExpression) newInitializerExpressions
					.get(i);
			ownerExpression = expressionList
					.findOwnerExpressionForIDExpression(duple);
			if (ownerExpression != null) {
				break;
			}
		}

		return ownerExpression;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableElement#setStartingOffsetAndLineNumber(int,
	 *      int)
	 */
	public void setStartingOffsetAndLineNumber(int offset, int lineNumber) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableElement#setEndingOffsetAndLineNumber(int,
	 *      int)
	 */
	public void setEndingOffsetAndLineNumber(int offset, int lineNumber) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableElement#getStartingOffset()
	 */
	public int getStartingOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableElement#getEndingOffset()
	 */
	public int getEndingOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableElement#getStartingLine()
	 */
	public int getStartingLine() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableElement#getEndingLine()
	 */
	public int getEndingLine() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableElement#getFilename()
	 */
	public char[] getFilename() {
		// TODO Auto-generated method stub
		return null;
	}
}
