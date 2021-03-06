/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Rational Software - Initial API and implementation
 *******************************************************************************/
package edu.pdx.svl.coDoc.cdt.internal.core.dom.parser.c;

import edu.pdx.svl.coDoc.cdt.core.dom.ast.ASTVisitor;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTArrayDeclarator;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTArrayModifier;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTInitializer;
import edu.pdx.svl.coDoc.cdt.core.parser.util.ArrayUtil;

/**
 * @author jcamelon
 */
public class CASTArrayDeclarator extends CASTDeclarator implements
		IASTArrayDeclarator {

	private IASTArrayModifier[] arrayMods = null;

	private int arrayModsPos = -1;

	public IASTArrayModifier[] getArrayModifiers() {
		if (arrayMods == null)
			return IASTArrayModifier.EMPTY_ARRAY;
		arrayMods = (IASTArrayModifier[]) ArrayUtil.removeNullsAfter(
				IASTArrayModifier.class, arrayMods, arrayModsPos);
		return arrayMods;

	}

	public void addArrayModifier(IASTArrayModifier arrayModifier) {
		if (arrayModifier != null) {
			arrayModsPos++;
			arrayMods = (IASTArrayModifier[]) ArrayUtil.append(
					IASTArrayModifier.class, arrayMods, arrayModifier);
		}
	}

	protected boolean postAccept(ASTVisitor action) {
		IASTArrayModifier[] mods = getArrayModifiers();
		for (int i = 0; i < mods.length; i++) {
			if (!mods[i].accept(action))
				return false;
		}
		IASTInitializer initializer = getInitializer();
		if (initializer != null)
			if (!initializer.accept(action))
				return false;
		return true;
	}
}
