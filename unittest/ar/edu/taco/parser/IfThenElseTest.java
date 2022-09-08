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
import ar.edu.jdynalloy.ast.JAssume;
import ar.edu.jdynalloy.ast.JBlock;
import ar.edu.jdynalloy.ast.JIfThenElse;
import ar.edu.jdynalloy.ast.JSkip;
import ar.edu.taco.parser.common.JDynAlloyParserTestBase;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.PredicateFormula;

public class IfThenElseTest extends JDynAlloyParserTestBase {

	public void testJIfThenElse() throws RecognitionException, TokenStreamException {
		String text = "if FalsePred[] { assume TruePred[];} else { skip; } ;";
		JIfThenElse ifThenElse = this.initializeParser(text).jIfThenElse(this.getCtx());

		assertTrue(ifThenElse.getCondition() instanceof PredicateFormula);
		assertTrue(ifThenElse.getThen() instanceof JBlock);
		assertTrue(((JBlock)ifThenElse.getThen()).getBlock().get(0) instanceof JAssume);
		assertTrue(ifThenElse.getElse() instanceof JBlock);		
		assertTrue(((JBlock)ifThenElse.getElse()).getBlock().get(0) instanceof JSkip);
	}
	
	public void testJIfThenElseMultipleStatement() throws RecognitionException, TokenStreamException {
		String text = "if FalsePred[] { assume TruePred[];assume FalsePred[];} else { skip; assume TruePred[];} ;";
		JIfThenElse ifThenElse = this.initializeParser(text).jIfThenElse(this.getCtx());
		assertTrue(ifThenElse.getThen() instanceof JBlock);
		JBlock thenBlock = (JBlock)ifThenElse.getThen() ;
		assertEquals(2, thenBlock.getBlock().size());
	}
	
	public void testJIfThenElseMultipleStatementTwo() throws RecognitionException, TokenStreamException {
		this.getCtx().addAlloyVariable(AlloyVariable.buildAlloyVariable( "x" ));
		String text = "" +
		"   if eq[x,true]" + "  \n" + 
		"      {" + "\n" +
		"      createObject<java_lang_IndexOutOfBoundsException>[throw];" + "\n" +
		"      call Constructor[throw,throw];" + "\n" +
		"      }" + "\n" +
		"   else {" + "\n" +
		"      skip;" + "\n" +
		"      }" + "\n" +
		"   ;" + "\n" ;

		JIfThenElse ifThenElse = this.initializeParser(text).jIfThenElse(this.getCtx());
		assertTrue(ifThenElse.getThen() instanceof JBlock);
		JBlock thenBlock = (JBlock)ifThenElse.getThen() ;
		assertEquals(2, thenBlock.getBlock().size());
	}	
	
	public void testJIfThenElseMultipleStatementThree() throws RecognitionException, TokenStreamException {
		this.getCtx().addAlloyVariable(AlloyVariable.buildAlloyVariable( "x" ));
		String text = "" +
		"if lte[index,sshr[thiz.size,1]] {" + "\n" + 
		          " var i:Int;" + "\n" +
		          " i:=1;" + "\n" +
		          " value:=thiz.head;" + "\n" +
		          "} " +
		          "else { skip; }; \n";
		          
		
		JIfThenElse ifThenElse = this.initializeParser(text).jIfThenElse(this.getCtx());
		assertTrue(ifThenElse.getThen() instanceof JBlock);
		JBlock thenBlock = (JBlock)ifThenElse.getThen() ;
		assertEquals(3, thenBlock.getBlock().size());
	}	
	   
}
