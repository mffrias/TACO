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

import org.jmlspecs.checker.Constants;
import org.multijava.mjc.CType;
import org.multijava.mjc.JBinaryExpression;
import org.multijava.mjc.JBooleanLiteral;
import org.multijava.mjc.JEqualityExpression;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JInstanceofExpression;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.mjc.JParenthesedExpression;
import org.multijava.mjc.JSuperExpression;
import org.multijava.mjc.JThisExpression;
import org.multijava.mjc.JUnaryExpression;
import org.multijava.mjc.JUnaryPromote;

import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.jdynalloy.factory.JSignatureFactory;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoNotImplementedYetException;
import ar.edu.taco.simplejml.ExpressionVisitor;
import ar.edu.taco.simplejml.JmlBaseExpressionVisitor.Instant;
import ar.edu.taco.simplejml.JmlExpressionVisitor;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.EqualsFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.NotFormula;

/**
 * Helpers of {@code ExpressionVisitor} and its JML subclasses for parts of expressions: the
 * receiver of a call, binary and unary operators, conditions, and conversions between the
 * Java-arithmetic types. They translate sub-expressions by making them accept the visitor
 * passed in and reading its result ({@code getAlloyExpression()} or
 * {@code getAlloyFormula()}), so they change that visitor's stack. No state of their own.
 */
public class ExpressionSolver {

	/**
	 * Returns the receiver of a call as an Alloy expression: {@code thiz} for {@code this}
	 * ({@code thiz'}, the primed variable, if the visitor is a {@code JmlExpressionVisitor} in
	 * the pre-state); a variable named {@code super} for {@code super}; otherwise the prefix
	 * translated by the visitor. Unclear: giving the primed {@code thiz'} in the pre-state is the
	 * opposite of {@code JmlExpressionVisitor.visitThisExpression} and of
	 * {@link JmlExpressionSolver#buildInstantVariable}, and since
	 * {@code JmlExpressionVisitor} overrides {@code visitMethodCallExpression} without calling
	 * this method, whether that branch is ever reached is an open question.
	 *
	 * @param expressionVisitor the visitor translating the call
	 * @param expression the call's prefix, or null
	 * @return the receiver, or null if {@code expression} is null
	 */
	public static AlloyExpression getLeftExpression(
			ExpressionVisitor expressionVisitor, JExpression expression) {
		AlloyExpression leftExpression = null;
		if (expression instanceof JThisExpression) {
			if (expressionVisitor instanceof JmlExpressionVisitor
					&& ((JmlExpressionVisitor) expressionVisitor).getInstant()
					.equals(Instant.PRE_INSTANT)) {
				leftExpression = JExpressionFactory.PRIMED_THIS_EXPRESSION;
			} else {
				leftExpression = JExpressionFactory.THIS_EXPRESSION;
			}
		} else if (expression instanceof JSuperExpression) {
			// QQ: SUPER EXPRESSION: Como tenemos que tratar al super??????
			// Respuesta: Hay que crear una palabra reservada como THIZ en
			// DynAlloy
			leftExpression = ExprVariable.buildExprVariable(new AlloyVariable(
					"super"));
		} else if (expression != null) {
			expression.accept(expressionVisitor);
			leftExpression = expressionVisitor.getAlloyExpression();
		}

		return leftExpression;
	}



	/**
	 * 
	 * @param theExpre is the expression whose innermost type we want to retrieve
	 * @return the innermost type of JExpression theExpre
	 * This method is required because getter "getType" does some sort of autoboxing that we want to avoid
	 */
	public static CType getType(JExpression theExpre){
		if (theExpre instanceof JUnaryPromote){
			return getType(((JUnaryPromote)theExpre).expr());
		} else if (theExpre instanceof JParenthesedExpression) {
			return getType(((JParenthesedExpression)theExpre).expr());
		} else
			return theExpre.getType();
	}


	//requires useJavaArithmetic == true
	/**
	 * Returns {@code alloyExpression} converted from {@code right_alloy_type} to
	 * {@code left_alloy_type} with the Java-arithmetic conversion functions: char to int, char
	 * to long and int to long (widening), int to char, long to char and long to int (narrowing).
	 * For any other pair, the same type included, returns the expression unchanged. Assumes
	 * {@code useJavaArithmetic}, as the comment above says. Used by {@code BlockStatementsVisitor}
	 * for assignments, initializers and returns.
	 *
	 * @param left_alloy_type the type to convert to
	 * @param right_alloy_type the expression's type
	 * @param alloyExpression the expression
	 * @return the converted expression, or {@code alloyExpression}
	 */
	public static AlloyExpression getCastingExpression(JType left_alloy_type, JType right_alloy_type,
			AlloyExpression alloyExpression) {
		if (left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE) &&
				right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE)){
			return (AlloyExpression) JExpressionFactory.fun_java_primitive_char_value_to_int_value(alloyExpression);
		} else if (left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE) &&
				right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE)){
			return (AlloyExpression) JExpressionFactory.fun_java_primitive_char_value_to_long_value(alloyExpression);
		} else if (left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE) &&
				right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE)){
			return (AlloyExpression) JExpressionFactory.fun_java_primitive_int_value_to_long_value(alloyExpression);
		} else if (left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE) &&
				right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE)){
			return (AlloyExpression) JExpressionFactory.fun_java_primitive_int_value_to_char_value(alloyExpression);
		} else if (left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE) &&
				right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE)){
			return (AlloyExpression) JExpressionFactory.fun_java_primitive_long_value_to_char_value(alloyExpression);
		} else if (left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE) &&
				right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE)){
			return (AlloyExpression) JExpressionFactory.fun_java_primitive_long_value_to_int_value(alloyExpression);
		} else {
			return alloyExpression;
		}

	}


	/**
	 * Translates a binary expression with the given operator. For {@code ==>}, {@code <==} and
	 * {@code <==>} each operand becomes a formula (an expression {@code e} becomes
	 * {@code e = true}), and
	 * {@link JavaOperatorSolver#getAlloyBinaryFormula(AlloyFormula, AlloyFormula, int)}
	 * combines them. For any other operator both operands must translate to
	 * expressions: an operand that is a {@code JmlSpecExpression} is translated by a new
	 * {@code JmlExpressionVisitor} (pre-state, no compilation units), a method call by the
	 * visitor's {@code visitMethodCallExpression}, anything else by accepting the visitor. Then
	 * {@code <:} gives {@code instanceOf}, and every other operator goes to
	 * {@link JavaOperatorSolver#getAlloyBinaryExpression} with the operands' JDynAlloy types
	 * ({@link CTypeAdapter} of {@link #getType}). For a method call as the right operand the
	 * visitor is cast to {@code JmlExpressionVisitor} (not for the left one), so with a plain
	 * {@code ExpressionVisitor} that case throws a {@code ClassCastException}.
	 *
	 * @param expressionVisitor the visitor translating the enclosing expression
	 * @param binaryExpression the expression
	 * @param operator the operator, a JML {@code Constants.OPE_...} value
	 * @return an {@code AlloyExpression} or an {@code AlloyFormula}, depending on the operator
	 * @throws TacoNotImplementedYetException if an operand of a non-logical operator does not
	 *         translate to an expression
	 */
	public static Object getBinaryExpression(
			ExpressionVisitor expressionVisitor,
			JBinaryExpression binaryExpression, int operator) {

		//		CType left_type = binaryExpression.left().getType();
		//		CType right_type = binaryExpression.right().getType();

		CType left_type = getType(binaryExpression.left());
		CType right_type = getType(binaryExpression.right());

		CTypeAdapter type_adapter = new CTypeAdapter();

		JType left_alloy_type = type_adapter.translate(left_type);
		JType right_alloy_type = type_adapter.translate(right_type);

		if (operator == Constants.OPE_IMPLIES
				|| operator == Constants.OPE_BACKWARD_IMPLIES
				|| operator == Constants.OPE_EQUIV) {

			AlloyFormula leftSide = null;
			binaryExpression.left().accept(expressionVisitor);
			if (expressionVisitor.isAlloyExpression()) {
				leftSide = new EqualsFormula(expressionVisitor
						.getAlloyExpression(),
						JExpressionFactory.TRUE_EXPRESSION);
			} else {
				leftSide = expressionVisitor.getAlloyFormula();
			}

			AlloyFormula rightSide = null;
			binaryExpression.right().accept(expressionVisitor);
			if (expressionVisitor.isAlloyExpression()) {
				rightSide = new EqualsFormula(expressionVisitor
						.getAlloyExpression(),
						JExpressionFactory.TRUE_EXPRESSION);
			} else {
				rightSide = expressionVisitor.getAlloyFormula();
			}

			return JavaOperatorSolver.getAlloyBinaryFormula(leftSide,
					rightSide, operator);

		} else {

			AlloyExpression leftExpression = null;
			@SuppressWarnings("unused")
			AlloyFormula leftFormula = null;
			JExpression leftSourceExpression = binaryExpression.left();

			if (leftSourceExpression instanceof org.jmlspecs.checker.JmlSpecExpression) {
				JmlExpressionVisitor translatorToAlloy = new JmlExpressionVisitor();
				leftSourceExpression.accept(translatorToAlloy);
				leftExpression = translatorToAlloy.getAlloyExpression();
			} else {
				if (leftSourceExpression instanceof JMethodCallExpression/* && expressionVisitor instanceof JmlExpressionVisitor*/) {
					// method call within annotation
					(expressionVisitor).visitMethodCallExpression((JMethodCallExpression)binaryExpression.left());
					leftExpression = expressionVisitor.getAlloyExpression();
				} else {

					leftSourceExpression.accept(expressionVisitor);
					leftExpression = expressionVisitor.getAlloyExpression();
				}
			}

			AlloyExpression rightExpression = null;
			@SuppressWarnings("unused")
			AlloyFormula rightFormula = null;
			JExpression rightSourceExpression = binaryExpression.right();

			if (rightSourceExpression instanceof org.jmlspecs.checker.JmlSpecExpression) {
				JmlExpressionVisitor translatorToAlloy = new JmlExpressionVisitor();
				rightSourceExpression.accept(translatorToAlloy);
				rightExpression = translatorToAlloy.getAlloyExpression();
			} else {
				if (binaryExpression.right() instanceof JMethodCallExpression) {
					// method call within annotation
					((JmlExpressionVisitor) expressionVisitor).visitMethodCallExpression((JMethodCallExpression)binaryExpression.right());
					rightExpression = expressionVisitor.getAlloyExpression();
				} else {
					binaryExpression.right().accept(expressionVisitor);
					rightExpression = expressionVisitor.getAlloyExpression();
				}
			}


			// EXPRESSION op EXPRESSION
			if (leftExpression != null && rightExpression != null) {
				if (Constants.OPE_SUBTYPE == operator) {
					return JavaOperatorSolver.getAlloyBinaryFormula(
							leftExpression, rightExpression, operator);
				} else {

					AlloyExpression[] expressions = new AlloyExpression[] {
							leftExpression, rightExpression };
					JType[] expression_types = new JType[] { left_alloy_type,
							right_alloy_type };

					return JavaOperatorSolver.getAlloyBinaryExpression(
							expression_types, expressions, operator);
				}
			} else {
				// FORMULA op FORMULA
				// EXPRESSION op FORMULA
				// FORMULA op EXPRESSIOn
				throw new TacoNotImplementedYetException();
			}

		}

	}

	/**
	 * Translates a unary expression. For minus: with {@code useJavaArithmetic}, the int or long
	 * negation function, and for any other operand type
	 * {@link JavaOperatorSolver#getAlloyUnaryExpression} (int negation again); without it,
	 * Alloy's integer negation. For other operators, an operand that translates to an expression
	 * goes to {@link JavaOperatorSolver#getAlloyUnaryExpression}, and one that translates to a
	 * formula is negated for {@code !}.
	 *
	 * @param expressionVisitor the visitor translating the enclosing expression
	 * @param jUnaryExpression the expression
	 * @param operator the operator, a JML {@code Constants.OPE_...} value
	 * @return an {@code AlloyExpression} or an {@code AlloyFormula}
	 * @throws TacoNotImplementedYetException for a formula operand and an operator other than
	 *         {@code !}
	 */
	public static Object getUnaryExpression(
			ExpressionVisitor expressionVisitor,
			JUnaryExpression jUnaryExpression, int operator) {

		if (operator == Constants.OPE_MINUS) {

			jUnaryExpression.expr().accept(expressionVisitor);

			AlloyExpression alloyExpression = expressionVisitor
					.getAlloyExpression();

			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true){
				if (jUnaryExpression.expr().getType().getIdent().equals("int")){
					return JExpressionFactory
							.fun_java_primitive_integer_value_negate(alloyExpression);
				} 
				
				if (jUnaryExpression.expr().getType().toString().equals("long")){
					return JExpressionFactory
							.fun_java_primitive_long_value_negate(alloyExpression);
				}
				return JavaOperatorSolver.getAlloyUnaryExpression(alloyExpression,
						operator);
			} else {
				return JExpressionFactory.alloy_int_negate(alloyExpression);
			}
		} else {
			jUnaryExpression.expr().accept(expressionVisitor);
			@SuppressWarnings("unused")
			AlloyFormula alloyFormula = null;
			if (expressionVisitor.isAlloyExpression()) {
				AlloyExpression alloyExpression = expressionVisitor
						.getAlloyExpression();
				//				alloyFormula = new EqualsFormula(alloyExpression,
				//						JExpressionFactory.TRUE_EXPRESSION);

				return JavaOperatorSolver.getAlloyUnaryExpression(alloyExpression,
						operator);
			} else {
				alloyFormula = expressionVisitor.getAlloyFormula();
				if (operator == Constants.OPE_LNOT){
					return JavaOperatorSolver.getAlloyUnaryFormula(alloyFormula,
							operator);
				} else

					// This code was commented after the changes made to the JML
					// expression simplifier.
					// if (alloyFormula instanceof QuantifiedFormula) {
					// alloyFormula =
					// JmlExpressionSolver.getQuantifiedFormulaForUnaryExpression(expressionVisitor,
					// jUnaryExpression, operator);
					// }
					throw new TacoNotImplementedYetException("Not implemented yet at getUnaryExpression");

			}
		}
	}

	/**
	 * Translates the condition of an {@code if}, a loop or an assertion into a formula, after
	 * removing one level of parentheses. Binary, equality and {@code instanceof} expressions and
	 * method calls are taken from the visitor as formulas; the literal {@code true} becomes
	 * {@code e = e} and {@code false} {@code not (e = e)}, where {@code e} is the literal's
	 * translation; anything else (names, unary expressions) becomes {@code e = true} if it
	 * translates to an expression {@code e}, or the formula itself.
	 *
	 * @param expressionVisitor the visitor to translate with
	 * @param condition the condition
	 * @return the formula
	 */
	public static AlloyFormula getConditionAsAlloyFormula(
			ExpressionVisitor expressionVisitor, JExpression condition) {
		if (condition instanceof JParenthesedExpression) {
			condition = ((JParenthesedExpression) condition).expr();
		}
		condition.accept(expressionVisitor);
		AlloyFormula alloyFormula;
		if (condition instanceof JBinaryExpression
				|| condition instanceof JMethodCallExpression
				|| condition instanceof JEqualityExpression) {
			alloyFormula = expressionVisitor.getAlloyFormula();
		} else if (condition instanceof JBooleanLiteral) {
			JBooleanLiteral booleanCondition = (JBooleanLiteral) condition;
			if (booleanCondition.booleanValue()) {
				AlloyExpression alloyExpression = expressionVisitor
						.getAlloyExpression();
				alloyFormula = new EqualsFormula(alloyExpression,
						alloyExpression);
			} else {
				AlloyExpression alloyExpression = expressionVisitor
						.getAlloyExpression();
				alloyFormula = new NotFormula(new EqualsFormula(
						alloyExpression, alloyExpression));
			}
		} else if (condition instanceof JInstanceofExpression) {
			alloyFormula = expressionVisitor.getAlloyFormula();
		} else /* is a SingleNameReference or a unaryExpression */{
			if (expressionVisitor.isAlloyExpression()) {
				AlloyExpression alloyExpression = expressionVisitor
						.getAlloyExpression();
				alloyFormula = new EqualsFormula(alloyExpression,
						JExpressionFactory.TRUE_EXPRESSION);
			} else {
				alloyFormula = expressionVisitor.getAlloyFormula();
			}
		}
		return alloyFormula;
	}

	/**
	 * Returns whether the type is {@code java.lang.Exception} or one of its subclasses, by
	 * loading the class by name in TACO's own JVM ({@code Class.forName}); a class that cannot be
	 * loaded counts as not an exception, and so does an {@code Error}. {@code ExpressionVisitor}
	 * uses it, with {@code newExceptionsAreLiterals}, to translate {@code new} of an exception
	 * into the exception's literal.
	 *
	 * @param ctype a class type
	 * @return true if the class loads and is an {@code Exception}
	 */
	public static boolean isDescendentOfException(CType ctype) {
		try {
			Class<?> exceptionClass = Class.forName("java.lang.Exception");
			Class<?> aClass = Class.forName(ctype.getCClass().getJavaName());

			return exceptionClass.isAssignableFrom(aClass);

		} catch (ClassNotFoundException e) {
			// if not found, we assume not descendant of exception
			return false;
		}

	}

	/**
	 * Returns {@code literalValue} reduced to what {@code bitwidth} bits can hold: its absolute
	 * value modulo 2^(bitwidth-1), with its sign (so a multiple of 2^(bitwidth-1) becomes 0). Its
	 * calls in {@code ExpressionVisitor} are commented out.
	 *
	 * @param literalValue the literal
	 * @param bitwidth the integer bit width
	 * @return the bounded value
	 */
	public static int preventBitwidthOverflow(int literalValue, int bitwidth) {

		// max count of int represented by alloy
		int intSetCardinality = (int) Math.pow(2, bitwidth);

		int sign = (literalValue > 0 ? 1 : -1);

		int boundedValueWithoutSign = Math.abs(literalValue)
				% (intSetCardinality / 2);
		int boundedValue = boundedValueWithoutSign * sign;

		return boundedValue;
	}



}
