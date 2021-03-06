/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package edu.pdx.svl.coDoc.cdt.core.parser;

import java.util.Map;

import edu.pdx.svl.coDoc.cdt.core.parser.ast.IASTFactory;
import edu.pdx.svl.coDoc.cdt.core.parser.util.CharArrayObjectMap;
import edu.pdx.svl.coDoc.cdt.internal.core.parser.scanner2.ILocationResolver;

/**
 * @author jcamelon
 * 
 */
public interface IScanner {

	public static final int tPOUNDPOUND = -6;

	public static final int tPOUND = -7;

	public void setOffsetBoundary(int offset);

	public void setContentAssistMode(int offset);

	public void setASTFactory(IASTFactory f);

	public IMacro addDefinition(char[] key, char[] value);

	public IMacro addDefinition(char[] name, char[][] params, char[] expansion);

	public void addDefinition(IMacro macro);

	public Map getDefinitions();

	public String[] getIncludePaths();

	public IToken nextToken() throws EndOfFileException;

	public int getCount();

	public boolean isOnTopContext();

	public CharArrayObjectMap getRealDefinitions();

	public void cancel();

	public char[] getMainFilename();

	public ILocationResolver getLocationResolver();
}
