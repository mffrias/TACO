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
package ar.edu.taco.jml.FieldParameter;




import org.multijava.mjc.JClassFieldExpression;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JMethodCallExpression;

import ar.edu.taco.jml.fieldnames.FNExpressionVisitor;


/**
 * A {@link FNExpressionVisitor} (field renaming) whose {@link #visitMethodCallExpression} visits
 * the receiver and the arguments of a call but pushes nothing for the call itself, so the stack
 * ends up holding the copies of the receiver and of the arguments instead of a copy of the call.
 * Nothing uses it: {@code ASTSimplifierManager} does not run it, and nothing in TACO, jDynAlloy
 * or dynalloy4 refers to it.
 *
 * <p>Unclear: what it was meant for, and whether it can go; the two branches of its
 * {@code visitMethodCallExpression} (argument a field access or not) do the same thing.
 */
public class FieldParameterExpressionVisitor extends FNExpressionVisitor {

	public FieldParameterExpressionVisitor(String currentClassName) {
		super(currentClassName);
	}
	
	
	/**
	 * Visits the receiver and then each argument with this visitor, which leaves their copies
	 * on {@code getArrayStack()}, and pushes nothing for the call (see the class comment). Fails
	 * with a {@code NullPointerException} for a call without an explicit receiver.
	 *
	 * @param self the call
	 */
	public void visitMethodCallExpression(/* @non_null */ JMethodCallExpression self){
		self.prefix().accept(this);
		for (JExpression expression : self.args()) {
			if (expression instanceof JClassFieldExpression){
				expression.accept(this);
			} else {
				expression.accept(this);
			}					
		}
	}	

	
}
