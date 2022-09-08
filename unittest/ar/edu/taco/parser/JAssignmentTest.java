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

import java.util.HashSet;
import java.util.Set;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import ar.edu.jdynalloy.ast.JAssignment;
import ar.edu.jdynalloy.ast.JBlock;
import ar.edu.jdynalloy.ast.JStatement;
import ar.edu.jdynalloy.parser.JDynAlloyProgramParseContext;
import ar.edu.taco.parser.common.JDynAlloyParserTestBase;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;

public class JAssignmentTest extends JDynAlloyParserTestBase {

    public void testJAssignment() throws RecognitionException, TokenStreamException {

	Set<AlloyVariable> vars = new HashSet<AlloyVariable>();
	vars.add(AlloyVariable.buildAlloyVariable("x"));
	JDynAlloyProgramParseContext ctx = new JDynAlloyProgramParseContext(vars, new HashSet<AlloyVariable>(), true);
	this.setCtx(ctx);

	String text = "x:=add[1,1];";
	JAssignment assignment = this.initializeParser(text).jAssignment(this.getCtx());
	assertTrue(assignment.getLvalue() instanceof ExprVariable);
	ExprVariable left = (ExprVariable) assignment.getLvalue();
	assertEquals("x", left.getVariable().getVariableId().getString());

	// assertEquals("i",
	// createObject.getLvalue().getVariableId().getString());
    }

    public void testDeclareAndAssign() throws RecognitionException, TokenStreamException {
	String text = "{ var value:ar_edu_dynjml4alloy_jml_LinkList_inner_Value+null; " + "value:=thiz.head; } ";
	JBlock assignment = this.initializeParser(text).jBlock(this.getCtx());
    }

    public void testDeclareAndAssignWithCall() throws RecognitionException, TokenStreamException {
	String text = "{" 
	    + " var value:java_lang_Map+null; " 
	    + "var value:ar_diego_Something+null; "
	    + " var k:Int; " 
	    + " var v:Int; " 
	    + " var Map_entries:java_lang_Map->Int; "	    
	    + " ar_diego_Something.value:=fun_map_put[thiz.Map_entries,k,v];"
	    + "} ";
	JBlock assignment = this.initializeParser(text).jBlock(this.getCtx());
    }

    public void testDeclareAndAssignWithCall2() throws RecognitionException, TokenStreamException {
	String text = "thiz.Map_entries:=fun_map_put[thiz.Map_entries,k,v];";
	JStatement assignment = this.initializeParser(text).jStatement(this.getCtx());
    }    
    
}
