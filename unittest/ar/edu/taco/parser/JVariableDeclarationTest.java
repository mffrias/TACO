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
package ar.edu.taco.parser;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import ar.edu.jdynalloy.ast.JVariableDeclaration;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.parser.common.JDynAlloyParserTestBase;

public class JVariableDeclarationTest extends JDynAlloyParserTestBase {

	public void testJVariableDeclarationFormula() throws RecognitionException, TokenStreamException {
		String text = "var i:Int; fake fake fake to reach 7 char lookhaed";
		JVariableDeclaration variableDeclaration = this.initializeParser(text).jVariableDeclarationStatement(this.getCtx());
		assertEquals("i", variableDeclaration.getVariable().getVariableId().getString());

		JType type = variableDeclaration.getType();
		assertEquals("Int", type.singletonFrom());
	}

	public void testVariableDeclarationUpdatesContext() throws RecognitionException, TokenStreamException {
		String text = "var x:java_lang_Object+null; fake fake fake to reach 7 char lookhaed";
		JVariableDeclaration variableDeclaration = this.initializeParser(text).jVariableDeclarationStatement(this.getCtx());
		assertTrue(getCtx().isVariableName("x"));
	}
}
