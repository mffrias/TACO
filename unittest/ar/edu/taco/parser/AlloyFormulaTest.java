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
import ar.edu.taco.parser.common.JDynAlloyParserTestBase;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprIntLiteral;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.AndFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.EqualsFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.ImpliesFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.NotFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.OrFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.PredicateCallAlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.PredicateFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.QuantifiedFormula;

public class AlloyFormulaTest extends JDynAlloyParserTestBase {

	public void testPredicateFormula() throws RecognitionException, TokenStreamException {
		AlloyFormula alloyFormula = this.initializeParser("equ[0,1]").alloyFormula(this.getCtx());
		assertTrue("Instance of PredicateFormula", alloyFormula instanceof PredicateFormula);
		PredicateFormula predicateFormula = (PredicateFormula) alloyFormula;
		assertEquals("equ", predicateFormula.getPredicateId());
		assertEquals(2, predicateFormula.getParameters().size());
	}
	
	public void testQuantifiedFormula() throws RecognitionException, TokenStreamException {
		AlloyFormula alloyFormula = this.initializeParser("some i:Int+null | { equ[i,1] }").alloyFormula(this.getCtx());
		assertTrue(alloyFormula instanceof QuantifiedFormula);
		QuantifiedFormula quantifiedFormula = (QuantifiedFormula) alloyFormula; 
		
		assertTrue(this.getCtx().isVariableName("i"));
		
		assertEquals("i", quantifiedFormula.getNames().get(0));
	}
	
	public void testCallSpec() throws RecognitionException, TokenStreamException {
		AlloyFormula alloyFormula = this.initializeParser("callSpec has[fun_reach[thiz.head,ar_edu_dynjml4alloy_jml_LinkList_inner_Value,next],has_return_0,thiz.tail]").alloyFormula(this.getCtx());
		assertTrue(alloyFormula instanceof PredicateCallAlloyFormula);
		PredicateCallAlloyFormula predicateCallAlloyFormula = (PredicateCallAlloyFormula) alloyFormula;
		assertEquals("has", predicateCallAlloyFormula.getProgramId() );
		assertEquals(3, predicateCallAlloyFormula.getArguments().size());
		assertEquals(false, predicateCallAlloyFormula.isSuperCall());
		assertEquals(false, predicateCallAlloyFormula.isStatic());
	}	
	
	public void testAndFormula() throws RecognitionException, TokenStreamException {
		AlloyFormula alloyFormula = this.initializeParser("callSpec aFunction[param1,has_return_0,thiz.tail] and equ[has_return_0,1]").alloyFormula(this.getCtx());
		assertTrue(alloyFormula instanceof AndFormula);
		AndFormula andFormula = (AndFormula) alloyFormula;
		assertTrue(andFormula.getLeft() instanceof PredicateCallAlloyFormula);
		assertTrue(andFormula.getRight() instanceof PredicateFormula);
	}
	
	public void testAndFormulaList() throws RecognitionException, TokenStreamException {
		AlloyFormula alloyFormula = this.initializeParser(" equ[has_return_0,1] and equ[has_return_0,1] and equ[has_return_0,1]").alloyFormula(this.getCtx());
		assertTrue(alloyFormula instanceof AndFormula);
		AndFormula andFormula = (AndFormula) alloyFormula;
		assertTrue(andFormula.getLeft() instanceof AndFormula);
		assertTrue(andFormula.getRight() instanceof PredicateFormula);
	}
	

	public void testOrFormula() throws RecognitionException, TokenStreamException {
		AlloyFormula alloyFormula = this.initializeParser("callSpec aFunction[param1,has_return_0,thiz.tail] or equ[has_return_0,1]").alloyFormula(this.getCtx());
		assertTrue(alloyFormula instanceof OrFormula);
		OrFormula orFormula = (OrFormula) alloyFormula;
		assertTrue(orFormula.getLeft() instanceof PredicateCallAlloyFormula);
		assertTrue(orFormula.getRight() instanceof PredicateFormula);
	}
	
	public void testOrFormulaList() throws RecognitionException, TokenStreamException {
		AlloyFormula alloyFormula = this.initializeParser(" equ[has_return_0,1] or equ[has_return_0,1] or equ[has_return_0,1]").alloyFormula(this.getCtx());
		assertTrue(alloyFormula instanceof OrFormula);
		OrFormula orFormula = (OrFormula) alloyFormula;
		assertTrue(orFormula.getLeft() instanceof OrFormula);
		assertTrue(orFormula.getRight() instanceof PredicateFormula);
	}
	
	public void testImpliesFormula() throws RecognitionException, TokenStreamException {
		AlloyFormula alloyFormula = this.initializeParser("callSpec aFunction[param1,has_return_0,thiz.tail] implies (some i:Int+null | { equ[i,1] }) and equ[has_return_0,1]").alloyFormula(this.getCtx());
		assertTrue(alloyFormula instanceof ImpliesFormula);
		ImpliesFormula impliesFormula = (ImpliesFormula) alloyFormula;
		assertTrue(impliesFormula.getLeft() instanceof PredicateCallAlloyFormula);
		assertTrue(impliesFormula.getRight() instanceof AndFormula);
	}
	
	public void testImpliesFormulaList() throws RecognitionException, TokenStreamException {
		AlloyFormula alloyFormula = this.initializeParser("equ[has_return_0,1] implies equ[has_return_0,1] implies equ[has_return_0,1]").alloyFormula(this.getCtx());
		assertTrue(alloyFormula instanceof ImpliesFormula);
		ImpliesFormula impliesFormula = (ImpliesFormula) alloyFormula;
		assertTrue(impliesFormula.getLeft() instanceof ImpliesFormula);
		assertTrue(impliesFormula.getRight() instanceof PredicateFormula);
	}	

	public void testKeywordPrefixTest() throws RecognitionException, TokenStreamException {
		
		AlloyFormula alloyFormula = this.initializeParser("andres[or_,1]").alloyFormula(this.getCtx());
		
	}

	public void testNotFormula() throws RecognitionException, TokenStreamException {
		AlloyFormula alloyFormula = this.initializeParser("not equ[has_return_0,1]").alloyFormula(this.getCtx());
		assertTrue(alloyFormula instanceof NotFormula);
		NotFormula impliesFormula = (NotFormula) alloyFormula;
		assertTrue(impliesFormula.getFormula() instanceof PredicateFormula);
	}	

	public void testEqualsFormula() throws RecognitionException, TokenStreamException {
		this.getCtx().addAlloyVariable(AlloyVariable.buildAlloyVariable("x"));
		AlloyFormula alloyFormula = this.initializeParser(" x = 1").alloyFormula(this.getCtx());
		assertTrue(alloyFormula instanceof EqualsFormula);
		EqualsFormula equalsFormula = (EqualsFormula) alloyFormula;
		assertTrue(equalsFormula.getLeft() instanceof ExprVariable);
		assertTrue(equalsFormula.getRight() instanceof ExprIntLiteral);
	}

	public void testEqualsFormulaWithProblems() throws RecognitionException, TokenStreamException {
		this.getCtx().addAlloyVariable(AlloyVariable.buildAlloyVariable("es_var__1"));
		AlloyFormula alloyFormula = this.initializeParser(" es_var__1 = true").alloyFormula(this.getCtx());
		assertTrue(alloyFormula instanceof EqualsFormula);
		EqualsFormula equalsFormula = (EqualsFormula) alloyFormula;
		assertTrue(equalsFormula.getLeft() instanceof ExprVariable);
		assertTrue(equalsFormula.getRight() instanceof ExprConstant);
	}

}
