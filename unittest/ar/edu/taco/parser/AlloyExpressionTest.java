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
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprIntersection;

public class AlloyExpressionTest extends JDynAlloyParserTestBase {

	public void testPredicateFormula() throws RecognitionException, TokenStreamException {
		AlloyExpression alloyExpression = this.initializeParser("Int & null").termExpression(this.getCtx());
		assertTrue("Instance of PredicateFormula", alloyExpression instanceof ExprIntersection);
		ExprIntersection exprIntersection = (ExprIntersection) alloyExpression;
		assertEquals(new ExprConstant(null,"Int"), exprIntersection.getLeft());
		assertEquals(new ExprConstant(null, "null"), exprIntersection.getRight());
	}
		
}
