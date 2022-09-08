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

import java.util.List;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.taco.parser.common.JDynAlloyParserTestBase;

public class JDynAlloyModulesDeclarationTest extends JDynAlloyParserTestBase {

	public void testJDynAlloyModules() throws RecognitionException,
			TokenStreamException {
		String text = ""
			+ "module ar_edu_dynjml4alloy_jml_LinkList_inner_Value"
			+ "\n"
			+ "sig ar_edu_dynjml4alloy_jml_LinkList_inner_Value extends java_lang_Object {} {}"
			+ "\n"
				+ "module ar_edu_dynjml4alloy_jml_LinkList"
				+ "\n"
				+ "sig ar_edu_dynjml4alloy_jml_LinkList extends java_lang_Object {} {}"
				+ "\n"
				+ "\n"
				+ "field size:Int+null {}"
				+ "\n"
				+ "invariant equ[0,1]"
				+ "\n"				
				+ "represents (thiz).(size) such that equ[0,1]"
				+ "\n"
				+ "program ar_edu_dynjml4alloy_jml_LinkList::get["
				+ "\n"
				+ "var thiz:ar_edu_dynjml4alloy_jml_LinkList,"
				+ "\n"
				+ "var throw:AssertionFailure+java_lang_Exception+null,"
				+ "\n"
				+ "var return:ar_edu_dynjml4alloy_jml_LinkList_inner_Value+null,"
				+ "\n"
				+ "var index:Int] "
				+ "\n"
				+ "Specification "
				+ "\n"
				+ "{ SpecCase #0 { requires { lt[0,index] } ensures { TruePred[] } } }"
				+ "\n" + "Implementation " + "\n" + "{ " + "\n"
				+ "	return:=index;" + "\n" + "}";
		
		List<JDynAlloyModule> modulesDeclaration = this.initializeParser(text).dynJmlAlloyModules();
		
		
		assertEquals(2, modulesDeclaration.size());
		assertEquals("ar_edu_dynjml4alloy_jml_LinkList_inner_Value", modulesDeclaration.get(0).getModuleId());
		assertEquals("ar_edu_dynjml4alloy_jml_LinkList", modulesDeclaration.get(1).getModuleId());


		
	}
}
