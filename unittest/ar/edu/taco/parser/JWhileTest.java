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

import java.util.ArrayList;
import java.util.List;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import ar.edu.jdynalloy.ast.JAssignment;
import ar.edu.jdynalloy.ast.JBlock;
import ar.edu.jdynalloy.ast.JSkip;
import ar.edu.jdynalloy.ast.JWhile;
import ar.edu.taco.parser.common.JDynAlloyParserTestBase;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.PredicateFormula;

public class JWhileTest extends JDynAlloyParserTestBase {

	public void testJWhileWithOutInvariant() throws RecognitionException, TokenStreamException {
		this.getCtx().addAlloyVariable(AlloyVariable.buildAlloyVariable("i"));
		String text = "while lt[i,10] { i:=add[i,1]; skip; };";
		JWhile aWhile = this.initializeParser(text).jWhile(this.getCtx());

		// condition
		assertTrue(aWhile.getCondition() instanceof PredicateFormula);
		PredicateFormula condition = (PredicateFormula) aWhile.getCondition();
		assertEquals("lt", condition.getPredicateId());

		// body
		assertTrue(aWhile.getBody() instanceof JBlock);
		JBlock body = (JBlock) aWhile.getBody();
		assertEquals(2, body.getBlock().size());
		assertTrue(body.getBlock().get(0) instanceof JAssignment);
		assertTrue(body.getBlock().get(1) instanceof JSkip);

		// invariant
		assertEquals(null, aWhile.getLoopInvariant());

	}

	public void testJWhileWithInvariant() throws RecognitionException, TokenStreamException {
		this.getCtx().addAlloyVariable(AlloyVariable.buildAlloyVariable("i"));
		String text = "while lt[i,10] loop_invariant lt[0,i] { skip; };";
		JWhile aWhile = this.initializeParser(text).jWhile(this.getCtx());

		// invariant
		List<AlloyExpression> parameters = new ArrayList<AlloyExpression>();
		parameters.add(ExprConstant.buildExprConstant("0"));
		parameters.add(ExprVariable.buildExprVariable("i"));
		AlloyFormula invariantFormulaExcepected = new PredicateFormula(null, "lt", parameters);

	}

}
