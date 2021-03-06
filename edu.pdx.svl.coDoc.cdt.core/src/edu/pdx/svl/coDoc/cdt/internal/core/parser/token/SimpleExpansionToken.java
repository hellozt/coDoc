/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

/*
 * Created on Sep 27, 2004
 */
package edu.pdx.svl.coDoc.cdt.internal.core.parser.token;

import edu.pdx.svl.coDoc.cdt.core.parser.IToken;

/**
 * @author aniefer
 */
public class SimpleExpansionToken extends SimpleToken implements IToken {
	public SimpleExpansionToken(int t, int macroOffset, int macroLength,
			char[] f, int l) {
		super(t, macroOffset + macroLength, f, l);
		setOffsetAndLength(macroOffset, macroLength);
	}

	protected void setOffsetAndLength(int macroOffset, int macroLength) {
		offset = macroOffset;
		length = macroLength;
	}

	protected int length;

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.pdx.svl.coDoc.cdt.internal.core.parser.token.AbstractToken#getLength()
	 */
	public final int getLength() {
		return length;
	}
}
