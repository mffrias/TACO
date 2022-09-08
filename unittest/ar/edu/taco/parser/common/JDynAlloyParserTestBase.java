/*
 * TACO: Translation of Annotated COde
 * Copyright (c) 2010 Universidad de Buenos Aires
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA,
 * 02110-1301, USA
 */
package ar.edu.taco.parser.common;

import java.io.File;
import java.io.StringReader;
import java.util.HashSet;

import junit.framework.TestCase;

import org.apache.log4j.xml.DOMConfigurator;

import antlr.ASTFactory;
import ar.edu.jdynalloy.JDynAlloyConfig;
import ar.edu.jdynalloy.parser.JDynAlloyAST;
import ar.edu.jdynalloy.parser.JDynAlloyLexer;
import ar.edu.jdynalloy.parser.JDynAlloyParser;
import ar.edu.jdynalloy.parser.JDynAlloyProgramParseContext;
import ar.uba.dc.rfm.alloy.AlloyVariable;

public abstract class JDynAlloyParserTestBase extends TestCase {

	private JDynAlloyProgramParseContext ctx;
	private static boolean initialized = false;

	public JDynAlloyParserTestBase() {
	}
	
	protected JDynAlloyParser initializeParser(String text) {
		JDynAlloyLexer lexer = new JDynAlloyLexer(new StringReader(text));
		JDynAlloyParser parser = new JDynAlloyParser(lexer);
		ASTFactory factory = new ASTFactory();
		factory.setASTNodeClass(JDynAlloyAST.class);
		parser.setASTFactory(factory);
		return parser;
	}

	@Override
	protected void setUp() throws Exception {
		ctx = new JDynAlloyProgramParseContext(new HashSet<AlloyVariable>(), new HashSet<AlloyVariable>(), true);
		if (!initialized) {
			File file = new File("config/log4j.xml");
			if (file.exists()) {
				DOMConfigurator.configure("config/log4j.xml");
			} else {
				System.err.println("File config/log4j.xml not found");
			}
			
			// Only execute one time this statement
			JDynAlloyConfig.buildConfig("unittest/unittestconfig.properties");

			initialized = true;
		}
		
		
	}

	@Override
	protected void tearDown() throws Exception {
		ctx = null;
	}

	/**
	 * @return the ctx
	 */
	public JDynAlloyProgramParseContext getCtx() {
		return ctx;
	}

	public void setCtx(JDynAlloyProgramParseContext ctx) {
		this.ctx = ctx;
	}
}
