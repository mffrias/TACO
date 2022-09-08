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
import ar.edu.jdynalloy.ast.JObjectInvariant;
import ar.edu.taco.parser.common.JDynAlloyParserTestBase;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.PredicateFormula;

public class JObjectInvariantTest extends JDynAlloyParserTestBase {

	public void testObjectInvariant() throws RecognitionException, TokenStreamException {
		JObjectInvariant invariant = this.initializeParser("object_invariant equ[0,1]").jObjectInvariant(this.getCtx());
		AlloyFormula alloyFormula = invariant.getFormula();
		assertTrue("Instance of PredicateFormula", alloyFormula instanceof PredicateFormula);
		PredicateFormula predicateFormula = (PredicateFormula) alloyFormula;
		assertEquals("equ", predicateFormula.getPredicateId());
		assertEquals(2, predicateFormula.getParameters().size());
	}
	
}
