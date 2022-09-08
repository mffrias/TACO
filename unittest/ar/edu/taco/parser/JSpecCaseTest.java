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
import ar.edu.jdynalloy.ast.JSpecCase;
import ar.edu.taco.parser.common.JDynAlloyParserTestBase;
import ar.uba.dc.rfm.alloy.AlloyVariable;

public class JSpecCaseTest extends JDynAlloyParserTestBase {

	public void testJAssume() throws RecognitionException, TokenStreamException {
		this.getCtx().addAlloyField(AlloyVariable.buildAlloyVariable("x"));
		String text = "  SpecCase #1 { " + "requires { TruePred[] } " + "ensures { FalsePred[] } " + "requires { equ[0,1] } " + "modifies {x} " + "} ";
		JSpecCase specCase = this.initializeParser(text).jSpecCase(this.getCtx());
		assertEquals(2, specCase.getRequires().size());
		assertEquals(1, specCase.getEnsures().size());
		assertEquals(1, specCase.getModifies().size());
	}
}
