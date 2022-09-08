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
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.parser.common.JDynAlloyParserTestBase;

public class AlloyTypeTest extends JDynAlloyParserTestBase {

	public void testJTypeSeqImage() throws RecognitionException, TokenStreamException {
		JType jType = this.initializeParser("(ar_edu_dynjml4alloy_jml_LinkList)->(seq(java_lang_Object))").alloyType();
		assertEquals("(ar_edu_dynjml4alloy_jml_LinkList)->(seq(java_lang_Object))", jType.toString());
//		ExprIntersection exprIntersection = (ExprIntersection) alloyExpression;
//		assertEquals(new ExprConstant(null,"Int"), exprIntersection.getLeft());
//		assertEquals(new ExprConstant(null, "null"), exprIntersection.getRight());
	}

	public void testJTypeTernary() throws RecognitionException, TokenStreamException {
		//JType jType = this.initializeParser("(java_util_Map)->(univ->univ)").alloyType();
	    JType jType = this.initializeParser("java_util_Map->(univ->one(univ))").alloyType();
		assertEquals("(java_util_Map)->((univ)->one(univ))", jType.toString());
	}

	public void testJTypeTernary3() throws RecognitionException, TokenStreamException {
	    JType jType = this.initializeParser("java_util_Map->(univ->lone(univ))").alloyType();
		assertEquals("(java_util_Map)->((univ)->(lone univ))", jType.toString());
	}

	public void testAlloyTypeAtom() throws RecognitionException, TokenStreamException {
		String input = "java_util_Map";
	    String s = this.initializeParser(input).typeName();
		assertEquals(input, s.trim());
	}

	public void testAlloyTypeAtom2() throws RecognitionException, TokenStreamException {
		String input = "(java_util_Map)";
	    String s = this.initializeParser(input).typeName();
		assertEquals(input, "(java_util_Map)");
	}
	
	public void testAlloyTypeAtom3() throws RecognitionException, TokenStreamException {
		String input = "lone(java_util_Map)";
	    String s = this.initializeParser(input).typeName();
		assertEquals(input, "lone(java_util_Map)");
	}	

	public void testAlloyTypeTerm1() throws RecognitionException, TokenStreamException {
		String input = "java_util_Map->Int";
	    String s = this.initializeParser(input).typeName();
		assertEquals(input, s.trim());
	}
}
