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
import ar.edu.jdynalloy.ast.JAssignment;
import ar.edu.jdynalloy.ast.JBlock;
import ar.edu.jdynalloy.ast.JProgramDeclaration;
import ar.edu.taco.parser.common.JDynAlloyParserTestBase;

public class JProgramDeclarationTest extends JDynAlloyParserTestBase {

    public void testProgramDeclaration() throws RecognitionException, TokenStreamException {
	String text = "program ar_edu_dynjml4alloy_jml_LinkList::get[" + "\n" +
				  "var thiz:ar_edu_dynjml4alloy_jml_LinkList," + "\n" +
				  "var throw:AssertionFailure+java_lang_Exception+null," + "\n" +
				  "var return:ar_edu_dynjml4alloy_jml_LinkList_inner_Value+null," + "\n" +
                  "var index:Int] " + "\n" +
					"Specification " + "\n" +
                  	"{ SpecCase #0 { requires { lt[0,index] } ensures { TruePred[] } } }" + "\n" +  
					"Implementation " + "\n" +
					"{ " + "\n" +
					"	return:=index;" + "\n" +
					"}";	
	JProgramDeclaration programDeclaration = this.initializeParser(text).jProgramDeclaration(this.getCtx());
	assertEquals("ar_edu_dynjml4alloy_jml_LinkList", programDeclaration.getSignatureId());
	assertEquals("get", programDeclaration.getProgramId());
	assertEquals(4, programDeclaration.getParameters().size());
	assertEquals(1, programDeclaration.getSpecCases().size());
	assertEquals(1, programDeclaration.getSpecCases().get(0).getEnsures().size());
	assertEquals(1, programDeclaration.getSpecCases().get(0).getRequires().size());
	assertEquals(0, programDeclaration.getSpecCases().get(0).getModifies().size());
	assertTrue(programDeclaration.getBody() instanceof JBlock);
	assertTrue(((JBlock)programDeclaration.getBody()).getBlock().get(0) instanceof JAssignment);
	assertTrue(this.getCtx().isVariableName("thiz"));
	assertTrue(this.getCtx().isVariableName("throw"));
	assertTrue(this.getCtx().isVariableName("return"));
	assertTrue(this.getCtx().isVariableName("index"));
	
	
    }
    
    public void testProgramDeclarationWithOutSpecification() throws RecognitionException, TokenStreamException {
	String text = "program ar_edu_dynjml4alloy_jml_LinkList::get[" + "\n" +
				  "var thiz:ar_edu_dynjml4alloy_jml_LinkList," + "\n" +
				  "var throw:AssertionFailure+java_lang_Exception+null," + "\n" +
				  "var return:ar_edu_dynjml4alloy_jml_LinkList_inner_Value+null," + "\n" +
                  "var index:Int] " + "\n" +
					"Implementation " + "\n" +
					"{ " + "\n" +
					"	return:=index;" + "\n" +
					"}";	
	JProgramDeclaration programDeclaration = this.initializeParser(text).jProgramDeclaration(this.getCtx());
	assertEquals("ar_edu_dynjml4alloy_jml_LinkList", programDeclaration.getSignatureId());
	assertEquals("get", programDeclaration.getProgramId());
	assertEquals(4, programDeclaration.getParameters().size());
	assertEquals(0, programDeclaration.getSpecCases().size());
	assertTrue(programDeclaration.getBody() instanceof JBlock);
	assertTrue(((JBlock)programDeclaration.getBody()).getBlock().get(0) instanceof JAssignment);
	assertTrue(this.getCtx().isVariableName("thiz"));
	assertTrue(this.getCtx().isVariableName("throw"));
	assertTrue(this.getCtx().isVariableName("return"));
	assertTrue(this.getCtx().isVariableName("index"));
	
	
    }
    
}
