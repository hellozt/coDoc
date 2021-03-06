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
package edu.pdx.svl.coDoc.cdt.internal.core.dom.parser.c;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import edu.pdx.svl.coDoc.cdt.core.dom.ast.ASTNodeProperty;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.ASTVisitor;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTFileLocation;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTNode;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTProblem;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTTranslationUnit;
import edu.pdx.svl.coDoc.cdt.internal.core.parser.ParserMessages;

/**
 * @author jcamelon
 */
public class CASTProblem extends CASTNode implements IASTProblem {

	private IASTNode parent;

	private ASTNodeProperty property;

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTNode#getParent()
	 */
	public IASTNode getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTNode#setParent(edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTNode)
	 */
	public void setParent(IASTNode node) {
		this.parent = node;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTNode#getPropertyInParent()
	 */
	public ASTNodeProperty getPropertyInParent() {
		return property;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTNode#setPropertyInParent(edu.pdx.svl.coDoc.cdt.core.dom.ast.ASTNodeProperty)
	 */
	public void setPropertyInParent(ASTNodeProperty property) {
		this.property = property;
	}

	private final char[] arg;

	private final int id;

	private final boolean isError;

	private final boolean isWarning;

	private String message = null;

	public CASTProblem(int id, char[] arg, boolean warn, boolean error) {
		this.id = id;
		this.arg = arg;
		this.isWarning = warn;
		this.isError = error;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.IProblem#getID()
	 */
	public int getID() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.IProblem#isError()
	 */
	public boolean isError() {
		return isError;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.IProblem#isWarning()
	 */
	public boolean isWarning() {
		return isWarning;
	}

	protected static final Map errorMessages;
	static {
		errorMessages = new HashMap();
		errorMessages
				.put(
						new Integer(IASTProblem.PREPROCESSOR_POUND_ERROR),
						ParserMessages
								.getString("ScannerProblemFactory.error.preproc.error")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(
								IASTProblem.PREPROCESSOR_INCLUSION_NOT_FOUND),
						ParserMessages
								.getString("ScannerProblemFactory.error.preproc.inclusionNotFound")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(
								IASTProblem.PREPROCESSOR_DEFINITION_NOT_FOUND),
						ParserMessages
								.getString("ScannerProblemFactory.error.preproc.definitionNotFound")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(IASTProblem.PREPROCESSOR_INVALID_MACRO_DEFN),
						ParserMessages
								.getString("ScannerProblemFactory.error.preproc.invalidMacroDefn")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(
								IASTProblem.PREPROCESSOR_INVALID_MACRO_REDEFN),
						ParserMessages
								.getString("ScannerProblemFactory.error.preproc.invalidMacroRedefn")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(
								IASTProblem.PREPROCESSOR_UNBALANCE_CONDITION),
						ParserMessages
								.getString("ScannerProblemFactory.error.preproc.unbalancedConditional")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(
								IASTProblem.PREPROCESSOR_CONDITIONAL_EVAL_ERROR),
						ParserMessages
								.getString("ScannerProblemFactory.error.preproc.conditionalEval")); //$NON-NLS-1$
		errorMessages.put(new Integer(
				IASTProblem.PREPROCESSOR_MACRO_USAGE_ERROR), ParserMessages
				.getString("ScannerProblemFactory.error.preproc.macroUsage")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(IASTProblem.PREPROCESSOR_CIRCULAR_INCLUSION),
						ParserMessages
								.getString("ScannerProblemFactory.error.preproc.circularInclusion")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(IASTProblem.PREPROCESSOR_INVALID_DIRECTIVE),
						ParserMessages
								.getString("ScannerProblemFactory.error.preproc.invalidDirective")); //$NON-NLS-1$
		errorMessages.put(new Integer(
				IASTProblem.PREPROCESSOR_MACRO_PASTING_ERROR), ParserMessages
				.getString("ScannerProblemFactory.error.preproc.macroPasting")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(
								IASTProblem.PREPROCESSOR_MISSING_RPAREN_PARMLIST),
						ParserMessages
								.getString("ScannerProblemFactory.error.preproc.missingRParen")); //$NON-NLS-1$       
		errorMessages
				.put(
						new Integer(IASTProblem.PREPROCESSOR_INVALID_VA_ARGS),
						ParserMessages
								.getString("ScannerProblemFactory.error.preproc.invalidVaArgs")); //$NON-NLS-1$       
		errorMessages
				.put(
						new Integer(IASTProblem.SCANNER_INVALID_ESCAPECHAR),
						ParserMessages
								.getString("ScannerProblemFactory.error.scanner.invalidEscapeChar")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(IASTProblem.SCANNER_UNBOUNDED_STRING),
						ParserMessages
								.getString("ScannerProblemFactory.error.scanner.unboundedString")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(IASTProblem.SCANNER_BAD_FLOATING_POINT),
						ParserMessages
								.getString("ScannerProblemFactory.error.scanner.badFloatingPoint")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(IASTProblem.SCANNER_BAD_HEX_FORMAT),
						ParserMessages
								.getString("ScannerProblemFactory.error.scanner.badHexFormat")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(IASTProblem.SCANNER_BAD_OCTAL_FORMAT),
						ParserMessages
								.getString("ScannerProblemFactory.error.scanner.badOctalFormat")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(IASTProblem.SCANNER_BAD_DECIMAL_FORMAT),
						ParserMessages
								.getString("ScannerProblemFactory.error.scanner.badDecimalFormat")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(IASTProblem.SCANNER_ASSIGNMENT_NOT_ALLOWED),
						ParserMessages
								.getString("ScannerProblemFactory.error.scanner.assignmentNotAllowed")); //$NON-NLS-1$        
		errorMessages
				.put(
						new Integer(IASTProblem.SCANNER_DIVIDE_BY_ZERO),
						ParserMessages
								.getString("ScannerProblemFactory.error.scanner.divideByZero")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(IASTProblem.SCANNER_MISSING_R_PAREN),
						ParserMessages
								.getString("ScannerProblemFactory.error.scanner.missingRParen")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(IASTProblem.SCANNER_EXPRESSION_SYNTAX_ERROR),
						ParserMessages
								.getString("ScannerProblemFactory.error.scanner.expressionSyntaxError")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(IASTProblem.SCANNER_ILLEGAL_IDENTIFIER),
						ParserMessages
								.getString("ScannerProblemFactory.error.scanner.illegalIdentifier")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(
								IASTProblem.SCANNER_BAD_CONDITIONAL_EXPRESSION),
						ParserMessages
								.getString("ScannerProblemFactory.error.scanner.badConditionalExpression")); //$NON-NLS-1$        
		errorMessages
				.put(
						new Integer(IASTProblem.SCANNER_UNEXPECTED_EOF),
						ParserMessages
								.getString("ScannerProblemFactory.error.scanner.unexpectedEOF")); //$NON-NLS-1$
		errorMessages
				.put(
						new Integer(IASTProblem.SCANNER_BAD_CHARACTER),
						ParserMessages
								.getString("ScannerProblemFactory.error.scanner.badCharacter")); //$NON-NLS-1$
		errorMessages.put(new Integer(IASTProblem.SYNTAX_ERROR), ParserMessages
				.getString("ParserProblemFactory.error.syntax.syntaxError")); //$NON-NLS-1$
	}

	protected final static String AST_PROBLEM_PATTERN = "BaseProblemFactory.astProblemPattern"; //$NON-NLS-1$

	public String getMessage() {
		if (message != null)
			return message;

		String msg = (String) errorMessages.get(new Integer(id));
		if (msg == null)
			msg = ""; //$NON-NLS-1$

		if (arg != null) {
			msg = MessageFormat.format(msg, new Object[] { new String(arg) });
		}

		String file = null;
		int offset = 0;
		IASTFileLocation f = getFileLocation();
		if (f == null) {
			file = ""; //$NON-NLS-1$
			offset = 0;
		} else {
			file = f.getFileName();
			offset = f.getNodeOffset();
		}

		Object[] args = new Object[] { msg, file, new Integer(offset) }; //$NON-NLS-1$        
		message = ParserMessages.getFormattedString(AST_PROBLEM_PATTERN, args);
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.IASTProblem#checkCategory(int)
	 */
	public boolean checkCategory(int bitmask) {
		return ((id & bitmask) != 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.parser.IASTProblem#getArguments()
	 */
	public String getArguments() {
		return arg != null ? String.valueOf(arg) : ""; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTNode#getTranslationUnit()
	 */
	public IASTTranslationUnit getTranslationUnit() {
		if (this instanceof IASTTranslationUnit)
			return (IASTTranslationUnit) this;
		IASTNode node = getParent();
		while (!(node instanceof IASTTranslationUnit) && node != null) {
			node = node.getParent();
		}
		return (IASTTranslationUnit) node;
	}

	public boolean accept(ASTVisitor action) {
		if (action.shouldVisitProblems) {
			switch (action.visit(this)) {
			case ASTVisitor.PROCESS_ABORT:
				return false;
			case ASTVisitor.PROCESS_SKIP:
				return true;
			default:
				break;
			}
		}
		return true;
	}
}
