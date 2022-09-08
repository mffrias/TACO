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
import ar.edu.jdynalloy.ast.JProgramCall;
import ar.edu.taco.parser.common.JDynAlloyParserTestBase;
import ar.uba.dc.rfm.alloy.AlloyVariable;

public class JProgramCallTest extends JDynAlloyParserTestBase {

	public void testJProgramCall() throws RecognitionException, TokenStreamException {
		this.getCtx().addAlloyVariable(AlloyVariable.buildAlloyVariable("a"));
		this.getCtx().addAlloyVariable(AlloyVariable.buildAlloyVariable("throw"));
		String text = "call Constructor[a,throw,4];";
		JProgramCall programCall = this.initializeParser(text).jProgramCall(this.getCtx());
		assertEquals("Constructor", programCall.getProgramId());
		assertEquals(3, programCall.getArguments().size());
		assertEquals(false, programCall.isSuperCall());
	}

	public void testJProgramCallSuper() throws RecognitionException, TokenStreamException {
		this.getCtx().addAlloyVariable(AlloyVariable.buildAlloyVariable("aObject"));
		this.getCtx().addAlloyVariable(AlloyVariable.buildAlloyVariable("throw"));
		String text = "super call aMethod[Object,throw,4];";
		JProgramCall programCall = this.initializeParser(text).jProgramCall(this.getCtx());
		assertEquals("aMethod", programCall.getProgramId());
		assertEquals(3, programCall.getArguments().size());
		assertEquals(true, programCall.isSuperCall());
	}

}
