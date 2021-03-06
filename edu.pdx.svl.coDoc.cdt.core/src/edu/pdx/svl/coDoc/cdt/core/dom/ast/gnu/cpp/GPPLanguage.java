/*******************************************************************************
 * Copyright (c) 2005 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * QNX - Initial API and implementation
 *******************************************************************************/

package edu.pdx.svl.coDoc.cdt.core.dom.ast.gnu.cpp;

import java.util.ArrayList;
import java.util.List;

import edu.pdx.svl.coDoc.cdt.core.CCorePlugin;
import edu.pdx.svl.coDoc.cdt.core.dom.CDOM;
import edu.pdx.svl.coDoc.cdt.core.dom.ICodeReaderFactory;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.ASTCompletionNode;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTName;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTNode;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.IASTTranslationUnit;
import edu.pdx.svl.coDoc.cdt.core.dom.ast.cpp.CPPASTVisitor;
import edu.pdx.svl.coDoc.cdt.core.model.ICProject;
import edu.pdx.svl.coDoc.cdt.core.model.IContributedModelBuilder;
import edu.pdx.svl.coDoc.cdt.core.model.ILanguage;
import edu.pdx.svl.coDoc.cdt.core.model.ITranslationUnit;
import edu.pdx.svl.coDoc.cdt.core.model.IWorkingCopy;
import edu.pdx.svl.coDoc.cdt.core.parser.CodeReader;
import edu.pdx.svl.coDoc.cdt.core.parser.IScanner;
import edu.pdx.svl.coDoc.cdt.core.parser.IScannerInfo;
import edu.pdx.svl.coDoc.cdt.core.parser.IScannerInfoProvider;
import edu.pdx.svl.coDoc.cdt.core.parser.ParserFactory;
import edu.pdx.svl.coDoc.cdt.core.parser.ParserLanguage;
import edu.pdx.svl.coDoc.cdt.core.parser.ParserMode;
import edu.pdx.svl.coDoc.cdt.core.parser.ParserUtil;
import edu.pdx.svl.coDoc.cdt.core.parser.ScannerInfo;
import edu.pdx.svl.coDoc.cdt.internal.core.dom.SavedCodeReaderFactory;
import edu.pdx.svl.coDoc.cdt.internal.core.dom.parser.ISourceCodeParser;
import edu.pdx.svl.coDoc.cdt.internal.core.dom.parser.cpp.GNUCPPSourceParser;
import edu.pdx.svl.coDoc.cdt.internal.core.dom.parser.cpp.GPPParserExtensionConfiguration;
import edu.pdx.svl.coDoc.cdt.internal.core.parser.scanner2.DOMScanner;
import edu.pdx.svl.coDoc.cdt.internal.core.parser.scanner2.GPPScannerExtensionConfiguration;
import edu.pdx.svl.coDoc.cdt.internal.core.parser.scanner2.IScannerExtensionConfiguration;
import edu.pdx.svl.coDoc.cdt.internal.core.pdom.PDOM;
import edu.pdx.svl.coDoc.cdt.internal.core.pdom.PDOMCodeReaderFactory;
import edu.pdx.svl.coDoc.cdt.internal.core.pdom.dom.IPDOMLinkageFactory;
import edu.pdx.svl.coDoc.cdt.internal.core.pdom.dom.cpp.PDOMCPPLinkageFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.PlatformObject;

/**
 * @author Doug Schaefer
 * 
 */
public class GPPLanguage extends PlatformObject implements ILanguage {

	protected static final GPPScannerExtensionConfiguration CPP_GNU_SCANNER_EXTENSION = new GPPScannerExtensionConfiguration();

	public static final String ID = CCorePlugin.PLUGIN_ID + ".g++"; //$NON-NLS-1$

	private static final GPPLanguage myDefault = new GPPLanguage();

	public static GPPLanguage getDefault() {
		return myDefault;
	}

	public String getId() {
		return ID;
	}

	public Object getAdapter(Class adapter) {
		if (adapter == IPDOMLinkageFactory.class)
			return new PDOMCPPLinkageFactory();
		else
			return super.getAdapter(adapter);
	}

	public IASTTranslationUnit getASTTranslationUnit(ITranslationUnit file,
			int style) throws CoreException {
		ICodeReaderFactory fileCreator;
		if ((style & (ILanguage.AST_SKIP_INDEXED_HEADERS | ILanguage.AST_SKIP_ALL_HEADERS)) != 0) {
			PDOM pdom = (PDOM) CCorePlugin.getPDOMManager().getPDOM(
					file.getCProject()).getAdapter(PDOM.class);
			fileCreator = new PDOMCodeReaderFactory(pdom);
		} else
			fileCreator = SavedCodeReaderFactory.getInstance();

		return getASTTranslationUnit(file, fileCreator, style);
	}

	public IASTTranslationUnit getASTTranslationUnit(ITranslationUnit file,
			ICodeReaderFactory codeReaderFactory, int style)
			throws CoreException {
		IResource resource = file.getResource();
		ICProject project = file.getCProject();
		IProject rproject = project.getProject();

		IScannerInfo scanInfo = null;
		IScannerInfoProvider provider = CCorePlugin.getDefault()
				.getScannerInfoProvider(rproject);
		if (provider != null) {
			IResource infoResource = resource != null ? resource : rproject;
			IScannerInfo buildScanInfo = provider
					.getScannerInformation(infoResource);
			if (buildScanInfo != null)
				scanInfo = buildScanInfo;
			else if ((style & ILanguage.AST_SKIP_IF_NO_BUILD_INFO) != 0)
				return null;
			else
				scanInfo = new ScannerInfo();
		}

		CodeReader reader;
		IFile rfile = (IFile) file.getResource();
		String path = rfile != null ? rfile.getLocation().toOSString() : file
				.getPath().toOSString();
		if (file instanceof IWorkingCopy) {
			// get the working copy contents
			reader = new CodeReader(path, file.getContents());
		} else {
			reader = codeReaderFactory.createCodeReaderForTranslationUnit(path);
			if (reader == null)
				return null;
		}

		IScannerExtensionConfiguration scannerExtensionConfiguration = CPP_GNU_SCANNER_EXTENSION;

		IScanner scanner = new DOMScanner(reader, scanInfo,
				ParserMode.COMPLETE_PARSE, ParserLanguage.CPP, ParserFactory
						.createDefaultLogService(),
				scannerExtensionConfiguration, codeReaderFactory);
		// assume GCC
		ISourceCodeParser parser = new GNUCPPSourceParser(scanner,
				ParserMode.COMPLETE_PARSE, ParserUtil.getParserLogService(),
				new GPPParserExtensionConfiguration());

		// Parse
		IASTTranslationUnit ast = parser.parse();
		if ((style & AST_USE_INDEX) != 0)
			ast.setIndex(CCorePlugin.getPDOMManager().getPDOM(
					file.getCProject()));
		return ast;
	}

	public ASTCompletionNode getCompletionNode(IWorkingCopy workingCopy,
			int offset) throws CoreException {
		IResource resource = workingCopy.getResource();
		ICProject project = workingCopy.getCProject();
		IProject rproject = project.getProject();

		IScannerInfo scanInfo = null;
		IScannerInfoProvider provider = CCorePlugin.getDefault()
				.getScannerInfoProvider(rproject);
		if (provider != null) {
			IResource infoResource = resource != null ? resource : rproject;
			IScannerInfo buildScanInfo = provider
					.getScannerInformation(infoResource);
			if (buildScanInfo != null)
				scanInfo = buildScanInfo;
			else
				scanInfo = new ScannerInfo();
		}

		// TODO use the pdom once we get enough info into it
		// PDOM pdom =
		// (PDOM)CCorePlugin.getPDOMManager().getPDOM(workingCopy.getCProject()).getAdapter(PDOM.class);
		// ICodeReaderFactory fileCreator = new PDOMCodeReaderFactory(pdom);

		ICodeReaderFactory fileCreator = CDOM
				.getInstance()
				.getCodeReaderFactory(CDOM.PARSE_WORKING_COPY_WHENEVER_POSSIBLE);

		CodeReader reader = new CodeReader(resource.getLocation().toOSString(),
				workingCopy.getContents());
		IScannerExtensionConfiguration scannerExtensionConfiguration = CPP_GNU_SCANNER_EXTENSION;
		IScanner scanner = new DOMScanner(reader, scanInfo,
				ParserMode.COMPLETE_PARSE, ParserLanguage.CPP, ParserFactory
						.createDefaultLogService(),
				scannerExtensionConfiguration, fileCreator);
		scanner.setContentAssistMode(offset);

		ISourceCodeParser parser = new GNUCPPSourceParser(scanner,
				ParserMode.COMPLETION_PARSE, ParserUtil.getParserLogService(),
				new GPPParserExtensionConfiguration());

		// Run the parse and return the completion node
		parser.parse();
		ASTCompletionNode node = parser.getCompletionNode();
		if (node != null) {
			node.count = scanner.getCount();
		}
		return node;
	}

	private static class NameCollector extends CPPASTVisitor {
		{
			shouldVisitNames = true;
		}

		private List nameList = new ArrayList();

		public int visit(IASTName name) {
			nameList.add(name);
			return PROCESS_CONTINUE;
		}

		public IASTName[] getNames() {
			return (IASTName[]) nameList.toArray(new IASTName[nameList.size()]);
		}
	}

	public IASTName[] getSelectedNames(IASTTranslationUnit ast, int start,
			int length) {
		IASTNode selectedNode = ast.selectNodeForLocation(ast.getFilePath(),
				start, length);

		if (selectedNode == null)
			return new IASTName[0];

		if (selectedNode instanceof IASTName)
			return new IASTName[] { (IASTName) selectedNode };

		NameCollector collector = new NameCollector();
		selectedNode.accept(collector);
		return collector.getNames();
	}

	public IContributedModelBuilder createModelBuilder(ITranslationUnit tu) {
		// Use the default CDT model builder
		return null;
	}
}
