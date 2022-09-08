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

public class ExpressionSolver {

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
		} else {
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
