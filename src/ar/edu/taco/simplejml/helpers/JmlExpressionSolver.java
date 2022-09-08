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
package ar.edu.taco.simplejml.helpers;



import ar.edu.taco.simplejml.JmlBaseExpressionVisitor.Instant;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;

/**
 * @author elgaby
 * 
 */
public class JmlExpressionSolver {

	public static ExprVariable buildInstantVariable(String varName,
			Instant instant) {
		if (instant == Instant.POST_INSTANT) {
			AlloyVariable postInstantVariable = new AlloyVariable(varName, true);
			return ExprVariable.buildExprVariable(postInstantVariable);
		} else
			return ExprVariable.buildExprVariable(varName);
	}

}
