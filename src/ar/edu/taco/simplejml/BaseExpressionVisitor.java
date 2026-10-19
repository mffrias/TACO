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
package ar.edu.taco.simplejml;

import java.util.Stack;

import ar.edu.jdynalloy.ast.JStatement;
import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.jdynalloy.factory.JPredicateFactory;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprIfCondition;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

/**
 * @author elgaby
 *
 */
public class BaseExpressionVisitor extends BlockStatementsVisitor {	
	/**
	 * The stack on which the expression translators ({@link ExpressionVisitor},
	 * {@link JmlExpressionVisitor}) leave their results: Alloy expressions, Alloy formulas and
	 * JDynAlloy statements (for calls and object creation), each pushed as a one-element array. A
	 * {@code visit...} method pushes one result; the caller takes it with
	 * {@link BaseExpressionVisitor#getAlloyExpression()}, {@code getAlloyFormula()} or
	 * {@code getAlloyProgram()}.
	 */
	final static class ArrayStack {
		private final Stack<Object[]> arrayStack = new Stack<Object[]>();

		public boolean isSingleton() {
			return (arrayStack.size() == 1 && arrayStack.peek().length == 1);
		}

		public void push(Object os) {
			arrayStack.push(new Object[] { os });
		}

		public Object[] pop() {
			return arrayStack.pop();
		}

		public Object[] popReverse(int i) {
			Object[] r = new Object[i];
			for (int j = i - 1; j > -1; j--) {
				Object[] v = arrayStack.pop();
				if (v.length != 1)
					throw new IllegalStateException(
							"Cannot pop singleton over an array of length "
									+ v.length);
				r[j] = v[0];

			}
			return r;
		}

		public Object popSingleton() {
			return popReverse(1)[0];
		}

		public Object[] popReversePair() {
			return popReverse(2);
		}
		
		public Object peek() {
			return this.arrayStack.peek();
		}
		
		public boolean isEmpty() {
			return this.arrayStack.isEmpty();
		}

	}

	private final ArrayStack stack = new ArrayStack();
	private AlloyExpression leftAssignmentExpression = null;

	public AlloyExpression getLeftAssignmentExpression() {
		return leftAssignmentExpression;
	}

	/**
	 * Sets the target of the expression about to be translated: the variable or location that the
	 * program built for a call, an object creation, an array creation or a string literal assigns
	 * (its {@code return} argument, or the new object). {@link BlockStatementsVisitor} sets it
	 * before translating the right side of an assignment, an initializer or a {@code throw}. It
	 * is not reset afterwards.
	 *
	 * @param leftAssignmentExpression the translated left side, an {@code ExprVariable} for
	 *     object and array creation
	 */
	public void setLeftAssignmentExpression(AlloyExpression leftAssignmentExpression) {
		this.leftAssignmentExpression = leftAssignmentExpression;
	}

	/**
	 * Pops the last result as an expression; a formula {@code F} becomes
	 * {@code (F) => true else false}.
	 *
	 * @return the expression
	 * @throws java.util.EmptyStackException if nothing was pushed
	 * @throws ClassCastException if the result is a statement
	 */
	public AlloyExpression getAlloyExpression() {
		Object o = this.stack.popSingleton();
		if (o instanceof AlloyFormula) {
			AlloyFormula alloyFormula = (AlloyFormula) o;
			return new ExprIfCondition(alloyFormula, JExpressionFactory.TRUE_EXPRESSION, JExpressionFactory.FALSE_EXPRESSION);
		} else {
			return (AlloyExpression) o;
		}
	}
	
	/**
	 * Pops the last result as a formula; an expression {@code e} becomes
	 * {@code liftExpression[e]}, which the prelude defines as {@code e = true}.
	 *
	 * @return the formula
	 * @throws java.util.EmptyStackException if nothing was pushed
	 * @throws ClassCastException if the result is a statement
	 */
	public AlloyFormula getAlloyFormula() {
		Object o = this.stack.popSingleton();
		if (o instanceof AlloyExpression) {
		    return JPredicateFactory.liftExpression((AlloyExpression) o);   
		} else {
		    return (AlloyFormula) o;
		}			
	}
	
	/**
	 * Pops the last result as a JDynAlloy statement: the program built for a call, an object or
	 * array creation, a string literal or a formal parameter.
	 *
	 * @return the statement
	 * @throws java.util.EmptyStackException if nothing was pushed
	 * @throws ClassCastException if the result is an expression or a formula
	 */
	public JStatement getAlloyProgram() {
		return (JStatement) this.stack.popSingleton();
	}
	
	/**
	 * Returns whether the last result is an expression, leaving it on the stack.
	 *
	 * @return {@code true} for an Alloy expression
	 * @throws java.util.EmptyStackException if nothing was pushed
	 */
	public boolean isAlloyExpression() {
		Object o = this.stack.popSingleton();
		this.stack.push(o);
		return AlloyExpression.class.isAssignableFrom(o.getClass());
	}
	
	/**
	 * Returns whether the last result is a formula, leaving it on the stack.
	 *
	 * @return {@code true} for an Alloy formula
	 * @throws java.util.EmptyStackException if nothing was pushed
	 */
	public boolean isAlloyFormula() {
		Object o = this.stack.popSingleton();
		this.stack.push(o);
		return AlloyFormula.class.isAssignableFrom(o.getClass());
	}

	/**
	 * @return the stack
	 */
	public ArrayStack getStack() {
		return stack;
	}
	
	public boolean isStackEmpty() {
		return stack.isEmpty();
	}
}
