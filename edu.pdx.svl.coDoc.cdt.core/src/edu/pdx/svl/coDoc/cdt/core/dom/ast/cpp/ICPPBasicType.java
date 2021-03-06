/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Dec 10, 2004
 */
package edu.pdx.svl.coDoc.cdt.core.dom.ast.cpp;

import edu.pdx.svl.coDoc.cdt.core.dom.ast.IBasicType;

/**
 * @author aniefer
 */
public interface ICPPBasicType extends IBasicType {
	// Extra types
	public static final int t_bool = ICPPASTSimpleDeclSpecifier.t_bool;

	public static final int t_wchar_t = ICPPASTSimpleDeclSpecifier.t_wchar_t;
}
