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
import edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTAbstractDeclaration;
import edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTExpression;
import edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTInitializerClause;
import edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTScope;
import edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTVariable;
import edu.pdx.svl.coDoc.cdt.internal.core.parser.Parser;
import edu.pdx.svl.coDoc.cdt.internal.core.parser.ast.ASTQualifiedNamedElement;
import edu.pdx.svl.coDoc.cdt.internal.core.parser.pst.ISymbol;
import edu.pdx.svl.coDoc.cdt.internal.core.parser.pst.ITypeInfo;

/**
 * @author jcamelon
 * 
 */
public class ASTVariable extends ASTSymbol implements IASTVariable {
	private final IASTExpression constructorExpression;

	private final ASTQualifiedNamedElement qualifiedName;

	private final IASTExpression bitfieldExpression;

	private final IASTInitializerClause initializerClause;

	private final IASTAbstractDeclaration abstractDeclaration;

	protected List references;

	private final char[] fn;

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableElement#getFilename()
	 */
	public char[] getFilename() {
		return fn;
	}

	/**
	 * @param newSymbol
	 * @param abstractDeclaration
	 * @param initializerClause
	 * @param bitfieldExpression
	 * @param startingOffset
	 * @param nameOffset
	 * @param references
	 * @param filename
	 */
	public ASTVariable(ISymbol newSymbol,
			IASTAbstractDeclaration abstractDeclaration,
			IASTInitializerClause initializerClause,
			IASTExpression bitfieldExpression, int startingOffset,
			int startingLine, int nameOffset, int nameEndOffset, int nameLine,
			List references, IASTExpression constructorExpression,
			boolean previouslyDeclared, char[] filename) {
		super(newSymbol);
		this.abstractDeclaration = abstractDeclaration;
		this.initializerClause = initializerClause;
		this.bitfieldExpression = bitfieldExpression;
		this.constructorExpression = constructorExpression;
		setStartingOffsetAndLineNumber(startingOffset, startingLine);
		setNameOffset(nameOffset);
		setNameEndOffsetAndLineNumber(nameEndOffset, nameLine);
		this.references = references;
		qualifiedName = new ASTQualifiedNamedElement(getOwnerScope(), newSymbol
				.getName());
		fn = filename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTVariable#isAuto()
	 */
	public boolean isAuto() {
		return symbol.getTypeInfo().checkBit(ITypeInfo.isAuto);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTVariable#isRegister()
	 */
	public boolean isRegister() {
		return symbol.getTypeInfo().checkBit(ITypeInfo.isRegister);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTVariable#isStatic()
	 */
	public boolean isStatic() {
		return symbol.getTypeInfo().checkBit(ITypeInfo.isStatic);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTVariable#isExtern()
	 */
	public boolean isExtern() {
		return symbol.getTypeInfo().checkBit(ITypeInfo.isExtern);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTVariable#isMutable()
	 */
	public boolean isMutable() {
		return symbol.getTypeInfo().checkBit(ITypeInfo.isMutable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTVariable#getAbstractDeclaration()
	 */
	public IASTAbstractDeclaration getAbstractDeclaration() {
		return abstractDeclaration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableNamedElement#getName()
	 */
	public String getName() {
		return String.valueOf(getSymbol().getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTVariable#getInitializerClause()
	 */
	public IASTInitializerClause getInitializerClause() {
		return initializerClause;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTVariable#isBitfield()
	 */
	public boolean isBitfield() {
		return (bitfieldExpression != null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTVariable#getBitfieldExpression()
	 */
	public IASTExpression getBitfieldExpression() {
		return bitfieldExpression;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTQualifiedNameElement#getFullyQualifiedName()
	 */
	public String[] getFullyQualifiedName() {
		return qualifiedName.getFullyQualifiedName();
	}

	public char[][] getFullyQualifiedNameCharArrays() {
		return qualifiedName.getFullyQualifiedNameCharArrays();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTScopedElement#getOwnerScope()
	 */
	public IASTScope getOwnerScope() {
		return (IASTScope) getSymbol().getContainingSymbol().getASTExtension()
				.getPrimaryDeclaration();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementCallbackDelegate#acceptElement(edu.pdx.svl.coDoc.cdt.core.parser.ISourceElementRequestor)
	 */
	public void acceptElement(ISourceElementRequestor requestor) {
		try {
			requestor.acceptVariable(this);
		} catch (Exception e) {
			/* do nothing */
		}
		Parser.processReferences(references, requestor);
		references = null;
		if (initializerClause != null)
			initializerClause.acceptElement(requestor);
		if (constructorExpression != null)
			constructorExpression.acceptElement(requestor);
		if (getAbstractDeclaration() != null)
			getAbstractDeclaration().acceptElement(requestor);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTVariable#getConstructorExpression()
	 */
	public IASTExpression getConstructorExpression() {
		return constructorExpression;
	}

	private int startingLineNumber, startingOffset, endingLineNumber,
			endingOffset, nameStartOffset, nameEndOffset, nameLineNumber;

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableElement#getStartingLine()
	 */
	public final int getStartingLine() {
		return startingLineNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableElement#getEndingLine()
	 */
	public final int getEndingLine() {
		return endingLineNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableNamedElement#getNameLineNumber()
	 */
	public final int getNameLineNumber() {
		return nameLineNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableElement#setStartingOffset(int)
	 */
	public final void setStartingOffsetAndLineNumber(int offset, int lineNumber) {
		startingOffset = offset;
		startingLineNumber = lineNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableElement#setEndingOffset(int)
	 */
	public final void setEndingOffsetAndLineNumber(int offset, int lineNumber) {
		endingOffset = offset;
		endingLineNumber = lineNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableElement#getStartingOffset()
	 */
	public final int getStartingOffset() {
		return startingOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableElement#getEndingOffset()
	 */
	public final int getEndingOffset() {
		return endingOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableNamedElement#getNameOffset()
	 */
	public final int getNameOffset() {
		return nameStartOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableNamedElement#setNameOffset(int)
	 */
	public final void setNameOffset(int o) {
		nameStartOffset = o;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableNamedElement#getNameEndOffset()
	 */
	public final int getNameEndOffset() {
		return nameEndOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableNamedElement#setNameEndOffset(int)
	 */
	public final void setNameEndOffsetAndLineNumber(int offset, int lineNumber) {
		nameEndOffset = offset;
		nameLineNumber = lineNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTOffsetableNamedElement#getNameCharArray()
	 */
	public char[] getNameCharArray() {
		return getSymbol().getName();
	}
}
