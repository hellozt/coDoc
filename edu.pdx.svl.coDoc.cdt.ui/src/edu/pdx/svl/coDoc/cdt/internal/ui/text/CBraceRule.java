package edu.pdx.svl.coDoc.cdt.internal.ui.text;

/*******************************************************************************
 * Copyright (c) 2005 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - initial API and implementation
 *******************************************************************************/
import org.eclipse.jface.text.rules.IToken;

/**
 * Braces rule.
 * 
 * @author P.Tomaszewski
 */
public class CBraceRule extends SingleCharRule {

	/**
	 * Creates new rule.
	 * 
	 * @param token
	 *            Style token.
	 */
	public CBraceRule(IToken token) {
		super(token);
	}

	/**
	 * @see edu.pdx.svl.coDoc.cdt.internal.ui.text.SingleCharRule#isRuleChar(int)
	 */
	protected boolean isRuleChar(int ch) {
		return ch == '{' || ch == '}' || ch == '[' || ch == ']' || ch == '('
				|| ch == ')';
	}

}
