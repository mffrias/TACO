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
import ar.edu.jdynalloy.ast.JField;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.jdynalloy.xlator.JTypeHelper;
import ar.edu.taco.parser.common.JDynAlloyParserTestBase;

public class JFieldTest extends JDynAlloyParserTestBase {

	public void testField() throws RecognitionException, TokenStreamException {
		JField field = this.initializeParser("field myInt : Int {}").jField(this.getCtx());
		

		assertEquals("myInt", field.getFieldVariable().getVariableId().getString());
		
		JType type = field.getFieldType();
		assertTrue("Type must not include null", !JTypeHelper.fromIncludesNull(type));
		assertEquals("base type must be Int", "Int",type.singletonFrom());

		assertTrue(this.getCtx().isVariableName("myInt"));
	}

	public void testField2() throws RecognitionException, TokenStreamException {
		JField field = this.initializeParser("field aSeq:(ar_edu_dynjml4alloy_jml_LinkList)->one(org_jmlspecs_models_JMLObjectSequence) {}").jField(this.getCtx());
		

		assertEquals("aSeq", field.getFieldVariable().getVariableId().getString());
		
		JType type = field.getFieldType();
		assertTrue("Type must not include null", !JTypeHelper.fromIncludesNull(type));
		assertEquals("base type must be ar_edu_dynjml4alloy_jml_LinkList", "ar_edu_dynjml4alloy_jml_LinkList",type.singletonFrom());
		assertEquals("base type must be ar_edu_dynjml4alloy_jml_LinkList", "org_jmlspecs_models_JMLObjectSequence",type.singletonTo());

		assertTrue(this.getCtx().isVariableName("aSeq"));
	}
	
}
