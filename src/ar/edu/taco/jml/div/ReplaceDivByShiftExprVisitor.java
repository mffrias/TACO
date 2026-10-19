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
package ar.edu.taco.jml.div;

import org.multijava.mjc.CNumericType;
import org.multijava.mjc.CType;
import org.multijava.mjc.Constants;
import org.multijava.mjc.JDivideExpression;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JOrdinalLiteral;
import org.multijava.mjc.JShiftExpression;

import ar.edu.taco.utils.jml.JmlAstClonerExpressionVisitor;

/**
 * Replaces expression ''int / 2'' with ''int >> 1''
 * 
 * @author jgaleotti
 * 
 */
public class ReplaceDivByShiftExprVisitor extends JmlAstClonerExpressionVisitor {

	private boolean isNumberLiteral2(JExpression expr) {
		if (expr instanceof JOrdinalLiteral) {
			JOrdinalLiteral ordinalLiteral = (JOrdinalLiteral) expr;
			Number number = ordinalLiteral.numberValue();
			if (number.intValue() == 2) {
				return true;
			}
		}
		return false;
	}

	private boolean isIntegerValue(JExpression expr) {
		CType typeExpr = expr.getType();
		if (typeExpr instanceof CNumericType) {
			CNumericType numericType = (CNumericType) typeExpr;
			if (numericType.getTypeID() == Constants.TID_INT) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Rewrites {@code e / 2} as {@code e >> 1} when {@code e} has type {@code int} and the
	 * divisor is an integer literal ({@code JOrdinalLiteral}) whose value is 2, after rewriting
	 * {@code e}; any other division is copied
	 * ({@link JmlAstClonerExpressionVisitor#visitDivideExpression}). The two differ when
	 * {@code e} is negative and odd: in Java {@code -3 / 2} is {@code -1}, {@code -3 >> 1} is
	 * {@code -2}. Nothing uses this class (see {@link ReplaceDivByShiftStmtVisitor}).
	 *
	 * @param self the division
	 */
	@Override
	public void visitDivideExpression(JDivideExpression self) {

		if (isIntegerValue(self.left()) && isNumberLiteral2(self.right())) {

			self.left().accept(this);
			JExpression left = this.getArrayStack().pop();

			JOrdinalLiteral literalIntegerOne = createNumberOne(self);

			JShiftExpression shiftExpr = new JShiftExpression(self.getTokenReference(), Constants.OPE_SR, left, literalIntegerOne);
			this.getArrayStack().push(shiftExpr);
		} else {
			super.visitDivideExpression(self);
		}
	}

	private JOrdinalLiteral createNumberOne(JDivideExpression self) {
		JOrdinalLiteral numberOne = new JOrdinalLiteral(self.getTokenReference(), new Integer(1), (CNumericType)self.getType());
		return numberOne;
	}

}
