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
package edu.pdx.svl.coDoc.cdt.core.dom.ast.cpp;

import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTExpression;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IVariable;

/**
 * @author Doug Schaefer
 */
public interface ICPPTemplateNonTypeParameter extends ICPPTemplateParameter,
		IVariable {

	/**
	 * The default value for this parameter.
	 * 
	 * @return
	 */
	public IASTExpression getDefault();

}
