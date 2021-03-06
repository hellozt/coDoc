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
package edu.pdx.svl.coDoc.cdt.core.dom;

import edu.pdx.svl.coDoc.cdt.core.parser.IScannerInfo;

/**
 * This interface represents a parser configuration as specified by the client
 * to the parser service.
 * 
 * @author jcamelon
 */
public interface IParserConfiguration {

	/**
	 * @return IScannerInfo representing the build information required to
	 *         parse.
	 */
	public IScannerInfo getScannerInfo();

	// TODO this may change
	/**
	 * @return String representing dialect name for the language
	 */
	public String getParserDialect();

}
