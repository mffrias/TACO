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
package ar.edu.taco.jml.cast;

import org.multijava.mjc.CArrayType;
import org.multijava.mjc.CType;
import org.multijava.mjc.JArrayAccessExpression;
import org.multijava.mjc.JCastExpression;
import org.multijava.mjc.JParenthesedExpression;

import ar.edu.taco.utils.jml.JmlAstClonerExpressionVisitor;

/**
 * Copies an expression and wraps every array access in a cast to its element type:
 * {@code a[i]} becomes {@code ((T) a[i])}, where {@code T} is the type of the access or, if the
 * access has none, the element type of the array's type. Accesses nested in the array or the
 * index are cast too. Everything else is copied by {@link JmlAstClonerExpressionVisitor}; the
 * result is on top of {@code getArrayStack()}. {@link CastSClassVisitor} applies it to the
 * predicates of the specifications.
 */
public class CastSExpressionVisitor extends JmlAstClonerExpressionVisitor {
	/** Visits the given array access expression. */
	public void visitArrayAccessExpression(/* @non_null */JArrayAccessExpression self) {
	    super.visitArrayAccessExpression(self);
	    CType type;
	    if (self.getType() == null) {
		CArrayType arrayType = (CArrayType) self.prefix().getType();
		type = arrayType.getBaseType();
	    } else {
		type = self.getType();
	    }
	    JArrayAccessExpression newSelf = (JArrayAccessExpression) this.getArrayStack().pop();
	    JCastExpression castExpression = new JCastExpression(newSelf.getTokenReference(), newSelf, type);
	    JParenthesedExpression parenthesedExpression = new JParenthesedExpression(newSelf.getTokenReference(),castExpression);
	    this.getArrayStack().push(parenthesedExpression);
	}
	
}

