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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jmlspecs.checker.JmlAssertStatement;
import org.jmlspecs.checker.JmlAssignableClause;
import org.jmlspecs.checker.JmlAssumeStatement;
import org.jmlspecs.checker.JmlConstraint;
import org.jmlspecs.checker.JmlEnsuresClause;
import org.jmlspecs.checker.JmlExceptionalBehaviorSpec;
import org.jmlspecs.checker.JmlExceptionalSpecBody;
import org.jmlspecs.checker.JmlExceptionalSpecCase;
import org.jmlspecs.checker.JmlFieldDeclaration;
import org.jmlspecs.checker.JmlGenericSpecBody;
import org.jmlspecs.checker.JmlGenericSpecCase;
import org.jmlspecs.checker.JmlInformalExpression;
import org.jmlspecs.checker.JmlInvariant;
import org.jmlspecs.checker.JmlMapsIntoClause;
import org.jmlspecs.checker.JmlNormalBehaviorSpec;
import org.jmlspecs.checker.JmlNormalSpecBody;
import org.jmlspecs.checker.JmlNormalSpecCase;
import org.jmlspecs.checker.JmlOldExpression;
import org.jmlspecs.checker.JmlPredicate;
import org.jmlspecs.checker.JmlReachExpression;
import org.jmlspecs.checker.JmlRelationalExpression;
import org.jmlspecs.checker.JmlRepresentsDecl;
import org.jmlspecs.checker.JmlRequiresClause;
import org.jmlspecs.checker.JmlResultExpression;
import org.jmlspecs.checker.JmlSignalsClause;
import org.jmlspecs.checker.JmlSignalsOnlyClause;
import org.jmlspecs.checker.JmlSpecBodyClause;
import org.jmlspecs.checker.JmlSpecExpression;
import org.jmlspecs.checker.JmlSpecQuantifiedExpression;
import org.jmlspecs.checker.JmlStoreRef;
import org.jmlspecs.checker.JmlStoreRefExpression;
import org.jmlspecs.checker.JmlStoreRefKeyword;
import org.jmlspecs.checker.JmlTypeExpression;
import org.multijava.mjc.CClassType;
import org.multijava.mjc.CField;
import org.multijava.mjc.CMethod;
import org.multijava.mjc.CType;
import org.multijava.mjc.Constants;
import org.multijava.mjc.JAddExpression;
import org.multijava.mjc.JArrayAccessExpression;
import org.multijava.mjc.JArrayLengthExpression;
import org.multijava.mjc.JClassFieldExpression;
import org.multijava.mjc.JCompilationUnitType;
import org.multijava.mjc.JDivideExpression;
import org.multijava.mjc.JEqualityExpression;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.mjc.JMinusExpression;
import org.multijava.mjc.JModuloExpression;
import org.multijava.mjc.JMultExpression;
import org.multijava.mjc.JNameExpression;
import org.multijava.mjc.JOrdinalLiteral;
import org.multijava.mjc.JParenthesedExpression;
import org.multijava.mjc.JRelationalExpression;
import org.multijava.mjc.JThisExpression;
import org.multijava.mjc.JTypeDeclarationType;
import org.multijava.mjc.JUnaryExpression;
import org.multijava.mjc.JUnaryPromote;
import org.multijava.mjc.JVariableDefinition;

import ar.edu.jdynalloy.JDynAlloyConfig;
import ar.edu.jdynalloy.JDynAlloyException;
import ar.edu.jdynalloy.ast.AlloyIntArrayFactory;
import ar.edu.jdynalloy.ast.JModifies;
import ar.edu.jdynalloy.ast.JPostcondition;
import ar.edu.jdynalloy.ast.JPrecondition;
import ar.edu.jdynalloy.ast.JSpecCase;
import ar.edu.jdynalloy.ast.JavaPrimitiveIntValueArrayFactory;
import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.jdynalloy.factory.JPredicateFactory;
import ar.edu.jdynalloy.factory.JSignatureFactory;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.jdynalloy.xlator.JTypeHelper;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
import ar.edu.taco.TacoNotImplementedYetException;
import ar.edu.taco.simplejml.builtin.JMLAuxiliaryConstantsFactory;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveFloatValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveIntegerValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveLongValue;
import ar.edu.taco.simplejml.builtin.JMLAuxiliaryConstantsFactory.JMLAddAuxiliaryConstants;
import ar.edu.taco.simplejml.builtin.JMLAuxiliaryConstantsFactory.JMLDivAuxiliaryConstants;
import ar.edu.taco.simplejml.builtin.JMLAuxiliaryConstantsFactory.JMLMinusAuxiliaryConstants;
import ar.edu.taco.simplejml.builtin.JMLAuxiliaryConstantsFactory.JMLModuloAuxiliaryConstants;
import ar.edu.taco.simplejml.builtin.JMLAuxiliaryConstantsFactory.JMLMultAuxiliaryConstants;
import ar.edu.taco.simplejml.helpers.CTypeAdapter;
import ar.edu.taco.simplejml.helpers.ExpressionSolver;
import ar.edu.taco.simplejml.helpers.JavaClassNameNormalizer;
import ar.edu.taco.simplejml.helpers.JmlExpressionSolver;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprIntLiteral;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprSum;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.AndFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.EqualsFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.FormulaVisitor;
import ar.uba.dc.rfm.alloy.ast.formulas.ImpliesFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.NotFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.OrFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.PredicateFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.QuantifiedFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.QuantifiedFormula.Operator;
import ar.uba.dc.rfm.alloy.ast.expressions.ExpressionVisitor;


/**
 * @author elgaby
 * 
 */
public class JmlExpressionVisitor extends JmlBaseExpressionVisitor {

	private static Logger log = Logger.getLogger(BlockStatementsVisitor.class);

	private Instant instant;

	private boolean hasAssignableClause;

	private List<AlloyFormula> ensuresRedundantly = new ArrayList<AlloyFormula>();

//	private AlloyTyping varsAndTheirTypeFromMathOperations = new AlloyTyping();

//	private List<AlloyFormula> predsFromMathOperations = new ArrayList<AlloyFormula>();

	private boolean isContractTranslation = false;

	private List<JCompilationUnitType> compilationUnits = new ArrayList<JCompilationUnitType>();




	/**
	 * @return the isContractTranslation
	 */
	public boolean isContractTranslation() {
		return isContractTranslation;
	}

	/**
	 * @param isContractTranslation the isContractTranslation to set
	 */
	public void setContractTranslation(boolean isContractTranslation) {
		this.isContractTranslation = isContractTranslation;
	}

	public JmlExpressionVisitor() {
		this(Instant.PRE_INSTANT);
	}


	public JmlExpressionVisitor(List<JCompilationUnitType> compilationUnits) {
		this(Instant.PRE_INSTANT);
		this.compilationUnits = compilationUnits;
	}

	public Instant getInstant() {
		return instant;
	}

	public void setInstant(Instant aInstant) {
		this.instant = aInstant;
	}

	public JmlExpressionVisitor(Instant instant) {
		this.instant = instant;
	}



	@Override
	public void visitArrayAccessExpression(
			JArrayAccessExpression jArrayAccessExpression) {

		jArrayAccessExpression.accept(prettyPrint);
		log.debug("Visiting: " + jArrayAccessExpression.getClass().getName());
		log.debug("Statement: " + prettyPrint.getPrettyPrint());

		jArrayAccessExpression.prefix().accept(this);
		AlloyExpression variableReference = this.getAlloyExpression();

		jArrayAccessExpression.accessor().accept(this);

		CType prefix_ctype = jArrayAccessExpression.prefix().getType();
		JType array_type = new CTypeAdapter().translate(prefix_ctype);

		AlloyExpression arrayIndex = this.getAlloyExpression();

		if (this.getInstant() == Instant.PRE_INSTANT) {
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true){
				super.getStack().push(
						JavaPrimitiveIntValueArrayFactory.array_access(array_type, variableReference,
								arrayIndex));
			} else {
				super.getStack().push(
						AlloyIntArrayFactory.arrayAccess(array_type, variableReference,
								arrayIndex));
			}
		} else {
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true){
				super.getStack().push(
						JavaPrimitiveIntValueArrayFactory.primed_array_access(array_type, variableReference,
								arrayIndex));
			} else {
				super.getStack().push(
						AlloyIntArrayFactory.primedArrayAccess(array_type, variableReference,
								arrayIndex));
			}
		}

	}

	@Override
	public void visitArrayLengthExpression(
			JArrayLengthExpression jArrayLengthExpression) {
		jArrayLengthExpression.accept(prettyPrint);
		log.debug("Visiting: " + jArrayLengthExpression.getClass().getName());
		log.debug("Statement: " + prettyPrint.getPrettyPrint());

		jArrayLengthExpression.prefix().accept(this);
		AlloyExpression variableReference = this.getAlloyExpression();

		CType prefix_ctype = jArrayLengthExpression.prefix().getType();
		JType array_type = new CTypeAdapter().translate(prefix_ctype);

		if (this.getInstant() == Instant.PRE_INSTANT) {
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true){
				super.getStack().push(JavaPrimitiveIntValueArrayFactory.array_length(array_type, variableReference));
			} else {
				super.getStack().push(
						AlloyIntArrayFactory.arrayLength(array_type, 
								variableReference));
			}
		} else {
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true){
				super.getStack().push(JavaPrimitiveIntValueArrayFactory.primed_array_length(array_type, variableReference));
			} else {
				super.getStack().push(
						AlloyIntArrayFactory.primedArrayLength(array_type, 
								variableReference));
			}
		}
	}




	@Override
	public void visitEqualityExpression(JEqualityExpression jEqualityExpression) {
		JExpression left = jEqualityExpression.left();
		JExpression right = jEqualityExpression.right();

		Instant oldInstant = this.getInstant();

		if (right instanceof JmlOldExpression
				&& ((JmlOldExpression) right).specExpression().expression() instanceof JMethodCallExpression) {
			this.setInstant(Instant.PRE_INSTANT);
			right = ((JmlOldExpression) right).specExpression().expression();
		} else if (left instanceof JmlOldExpression
				&& ((JmlOldExpression) left).specExpression().expression() instanceof JMethodCallExpression) {
			this.setInstant(Instant.PRE_INSTANT);
			left = ((JmlOldExpression) right).specExpression().expression();
		} 

		super.visitEqualityExpression(jEqualityExpression);

		this.setInstant(oldInstant);
	}



	@Override
	public void visitJmlRelationalExpression(JmlRelationalExpression jmlRelationalExpression) {
		super.visitRelationalExpression(jmlRelationalExpression);
	}


	/**
	 * 
	 * @param theExpre is the expression whose innermost type we want to retrieve
	 * @return the innermost type of JExpression theExpre
	 * This method is required because getter "getType" does some sort of autoboxing that we want to avoid
	 */
	private static CType getType(JExpression theExpre){
		if (theExpre instanceof JUnaryPromote){
			return getType(((JUnaryPromote)theExpre).expr());
		} else if (theExpre instanceof JParenthesedExpression) {
			return getType(((JParenthesedExpression)theExpre).expr());
		} else
			return theExpre.getType();
	}


	//mfrias 23/07/2013
	@Override
	public void visitAddExpression(JAddExpression jAddExpression) {

		AlloyExpression rvalue = null;

		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {

			jAddExpression.left().accept(this);
			AlloyExpression left_add_expr = this.getAlloyExpression();
			//recover new left subexpression

			jAddExpression.right().accept(this);
			AlloyExpression right_add_expr = this.getAlloyExpression();
			//recover new right subexpression

			CType left_type = getType(jAddExpression.left());
			CType right_type = getType(jAddExpression.right());

			CTypeAdapter type_Adapter = new CTypeAdapter();
			JType left_alloy_type = type_Adapter.translate(left_type);
			JType right_alloy_type = type_Adapter.translate(right_type);

			
			if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))) {
				JMLAddAuxiliaryConstants addAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_integer_add_auxiliary_constants(left_add_expr, right_add_expr);
				rvalue = addAuxiliaryConstants.result_variable;
				AlloyVariable res = addAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable over = addAuxiliaryConstants.overflow_or_compatibility_variable.getVariable();

				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					over.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(addAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				} else {
					this.predsFromMathOperations.add(addAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				}

			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))) {
				JMLAddAuxiliaryConstants addAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_long_add_auxiliary_constants(left_add_expr, right_add_expr);
				rvalue = addAuxiliaryConstants.result_variable;
				AlloyVariable res = addAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable over = addAuxiliaryConstants.overflow_or_compatibility_variable.getVariable();

				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					over.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(addAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				} else {
					this.predsFromMathOperations.add(addAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				}

			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))) {
				JMLAddAuxiliaryConstants addAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_float_add_auxiliary_constants(left_add_expr, right_add_expr);
				rvalue = addAuxiliaryConstants.result_variable;
				AlloyVariable res = addAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable over = addAuxiliaryConstants.overflow_or_compatibility_variable.getVariable();

				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					over.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(addAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(over, JSignatureFactory.BOOLEAN_TYPE.toString());

				} else {
					this.predsFromMathOperations.add(addAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				}

			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE))) {
				JMLAddAuxiliaryConstants addAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_integer_char_add_auxiliary_constants(left_add_expr, right_add_expr);
				rvalue = addAuxiliaryConstants.result_variable;
				AlloyVariable res = addAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable over = addAuxiliaryConstants.overflow_or_compatibility_variable.getVariable();

				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					over.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(addAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(over, JSignatureFactory.BOOLEAN_TYPE.toString());

				} else {
					this.predsFromMathOperations.add(addAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				}

			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))) {

				JMLAddAuxiliaryConstants addAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_char_integer_add_auxiliary_constants(left_add_expr, right_add_expr);
				//assemble new expression

				rvalue = addAuxiliaryConstants.result_variable;
				//assemble new expression

				AlloyVariable res = addAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable over = addAuxiliaryConstants.overflow_or_compatibility_variable.getVariable();
				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					over.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(addAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				} else {
					this.predsFromMathOperations.add(addAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				}

			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE))) {

				JMLAddAuxiliaryConstants addAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_char_char_add_auxiliary_constants(left_add_expr, right_add_expr);
				//assemble new expression

				rvalue = addAuxiliaryConstants.result_variable;
				//assemble new expression

				AlloyVariable res = addAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable over = addAuxiliaryConstants.overflow_or_compatibility_variable.getVariable();
				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					over.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(addAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				} else {
					this.predsFromMathOperations.add(addAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				}


			} else {
				throw new TacoException("Cannot add elements from types " + left_alloy_type + " and " + right_alloy_type);
			}

		} else {

			Object binaryExpression;
			binaryExpression = ExpressionSolver.getBinaryExpression(this, jAddExpression, Constants.OPE_PLUS);
			rvalue = (AlloyExpression) binaryExpression;

		}

		this.getStack().push(rvalue);

	}



	@Override
	public void visitMinusExpression(JMinusExpression jMinusExpression) {

		AlloyExpression rvalue = null;


		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {

			jMinusExpression.left().accept(this);
			AlloyExpression left_minus_expr = this.getAlloyExpression();
			//recover new left subexpression

			jMinusExpression.right().accept(this);
			AlloyExpression right_minus_expr = this.getAlloyExpression();
			//recover new right subexpression

			CType left_type = getType(jMinusExpression.left());
			CType right_type = getType(jMinusExpression.right());

			CTypeAdapter type_Adapter = new CTypeAdapter();
			JType left_alloy_type = type_Adapter.translate(left_type);
			JType right_alloy_type = type_Adapter.translate(right_type);

			if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))) {

				JMLMinusAuxiliaryConstants minusAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_integer_minus_auxiliary_constants(left_minus_expr, right_minus_expr);
				rvalue = minusAuxiliaryConstants.result_variable;
				AlloyVariable res = minusAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable over = minusAuxiliaryConstants.overflow_or_compatibility_variable.getVariable();
				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					over.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(minusAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				} else {
					this.predsFromMathOperations.add(minusAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				}

			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))) {

				JMLMinusAuxiliaryConstants minusAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_long_minus_auxiliary_constants(left_minus_expr, right_minus_expr);
				//assemble new expression

				rvalue = minusAuxiliaryConstants.result_variable;
				//assemble new expression
				AlloyVariable res = minusAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable over = minusAuxiliaryConstants.overflow_or_compatibility_variable.getVariable();

				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					over.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(minusAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				} else {
					this.predsFromMathOperations.add(minusAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				}
				this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
				this.varsAndTheirTypeFromMathOperations.put(over, JSignatureFactory.BOOLEAN_TYPE.toString());

			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))) {

				JMLMinusAuxiliaryConstants minusAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_float_minus_auxiliary_constants(left_minus_expr, right_minus_expr);
				//assemble new expression

				rvalue = minusAuxiliaryConstants.result_variable;
				//assemble new expression

				AlloyVariable res = minusAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable over = minusAuxiliaryConstants.overflow_or_compatibility_variable.getVariable();
				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					over.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(minusAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				} else {
					this.predsFromMathOperations.add(minusAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				}

			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))) {

				JMLMinusAuxiliaryConstants minusAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_char_integer_minus_auxiliary_constants(left_minus_expr, right_minus_expr);
				rvalue = minusAuxiliaryConstants.result_variable;

				AlloyVariable res = minusAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable over = minusAuxiliaryConstants.overflow_or_compatibility_variable.getVariable();
				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					over.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(minusAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(over, JSignatureFactory.BOOLEAN_TYPE.toString());

				} else {
					this.predsFromMathOperations.add(minusAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				}

			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE))) {

				JMLMinusAuxiliaryConstants minusAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_integer_char_minus_auxiliary_constants(left_minus_expr, right_minus_expr);
				rvalue = minusAuxiliaryConstants.result_variable;

				AlloyVariable res = minusAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable over = minusAuxiliaryConstants.overflow_or_compatibility_variable.getVariable();
				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					over.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(minusAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(over, JSignatureFactory.BOOLEAN_TYPE.toString());

				} else {
					this.predsFromMathOperations.add(minusAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				}

			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE))) {

				JMLMinusAuxiliaryConstants minusAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_char_char_minus_auxiliary_constants(left_minus_expr, right_minus_expr);
				rvalue = minusAuxiliaryConstants.result_variable;

				AlloyVariable res = minusAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable over = minusAuxiliaryConstants.overflow_or_compatibility_variable.getVariable();
				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					over.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(minusAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(over, JSignatureFactory.BOOLEAN_TYPE.toString());

				} else {
					this.predsFromMathOperations.add(minusAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				}

			} else {
				throw new TacoException("Cannot subtract elements from types " + left_alloy_type + " and " + right_alloy_type);
			}

		} else {

			Object binaryExpression;
			binaryExpression = ExpressionSolver.getBinaryExpression(this, jMinusExpression, Constants.OPE_MINUS);
			rvalue = (AlloyExpression) binaryExpression;

		}

		this.getStack().push(rvalue);

	}




	//mfrias 23/07/2013
	@Override
	public void visitMultExpression(JMultExpression jMultExpression) {

		AlloyExpression rvalue = null;


		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {

			jMultExpression.left().accept(this);
			AlloyExpression left_mul_expr = this.getAlloyExpression();
			//recover new left subexpression

			jMultExpression.right().accept(this);
			AlloyExpression right_mul_expr = this.getAlloyExpression();
			//recover new right subexpression

			CType left_type = getType(jMultExpression.left());
			CType right_type = getType(jMultExpression.right());

			CTypeAdapter type_Adapter = new CTypeAdapter();
			JType left_alloy_type = type_Adapter.translate(left_type);
			JType right_alloy_type = type_Adapter.translate(right_type);

			if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))) {

				JMLMultAuxiliaryConstants mulAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_integer_mult_auxiliary_constants(left_mul_expr, right_mul_expr);
				rvalue = mulAuxiliaryConstants.result_variable;

				AlloyVariable res = mulAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable over = mulAuxiliaryConstants.overflow_or_compatibility_variable.getVariable();
				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					over.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(mulAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				} else {
					this.predsFromMathOperations.add(mulAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				}
			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))) {

				JMLMultAuxiliaryConstants mulAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_long_mult_auxiliary_constants(left_mul_expr, right_mul_expr);
				rvalue = mulAuxiliaryConstants.result_variable;

				AlloyVariable res = mulAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable over = mulAuxiliaryConstants.overflow_or_compatibility_variable.getVariable();
				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					over.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(mulAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				} else {
					this.predsFromMathOperations.add(mulAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				}
			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))) {

				JMLMultAuxiliaryConstants mulAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_char_int_mult_auxiliary_constants(left_mul_expr, right_mul_expr);
				rvalue = mulAuxiliaryConstants.result_variable;

				AlloyVariable res = mulAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable over = mulAuxiliaryConstants.overflow_or_compatibility_variable.getVariable();
				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					over.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(mulAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				} else {
					this.predsFromMathOperations.add(mulAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				}

			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))) {

				JMLMultAuxiliaryConstants mulAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_char_long_mult_auxiliary_constants(left_mul_expr, right_mul_expr);
				rvalue = mulAuxiliaryConstants.result_variable;

				AlloyVariable res = mulAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable over = mulAuxiliaryConstants.overflow_or_compatibility_variable.getVariable();
				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					over.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(mulAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				} else {
					this.predsFromMathOperations.add(mulAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				}
			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))) {

				JMLMultAuxiliaryConstants mulAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_float_mult_auxiliary_constants(left_mul_expr, right_mul_expr);
				rvalue = mulAuxiliaryConstants.result_variable;

				AlloyVariable res = mulAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable over = mulAuxiliaryConstants.overflow_or_compatibility_variable.getVariable();
				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					over.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(mulAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				} else {
					this.predsFromMathOperations.add(mulAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(over, JSignatureFactory.BOOLEAN_TYPE.toString());
				}
			} else {
				throw new TacoException("Cannot multiply elements from types " + left_alloy_type + " and " + right_alloy_type);
			}

		} else {

			Object binaryExpression;
			binaryExpression = ExpressionSolver.getBinaryExpression(this, jMultExpression, Constants.OPE_STAR);
			rvalue = (AlloyExpression) binaryExpression;

		}

		this.getStack().push(rvalue);

	}





	//mfrias 31/07/2013
	@Override
	public void visitDivideExpression(JDivideExpression jDivideExpression) {

		AlloyExpression rvalue = null;


		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {

			jDivideExpression.left().accept(this);
			AlloyExpression left_div_expr = this.getAlloyExpression();
			//recover new left subexpression

			jDivideExpression.right().accept(this);
			AlloyExpression right_div_expr = this.getAlloyExpression();
			//recover new right subexpression

			CType left_type = getType(jDivideExpression.left());
			CType right_type = getType(jDivideExpression.right());

			CTypeAdapter type_Adapter = new CTypeAdapter();
			JType left_alloy_type = type_Adapter.translate(left_type);
			JType right_alloy_type = type_Adapter.translate(right_type);

			if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))) {

				JMLDivAuxiliaryConstants divAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_integer_divide_auxiliary_constants(left_div_expr, right_div_expr);
				rvalue = divAuxiliaryConstants.result_variable;

				AlloyVariable quotient = divAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable remainder = divAuxiliaryConstants.remainder.getVariable();
				if (this.isContractTranslation){
					quotient.setIsVariableFromContract();
					remainder.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(divAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(quotient, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(remainder, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
				} else {
					this.predsFromMathOperations.add(divAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(quotient, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(remainder, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
				}
			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))) {

				JMLDivAuxiliaryConstants divAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_long_divide_auxiliary_constants(left_div_expr, right_div_expr);
				rvalue = divAuxiliaryConstants.result_variable;

				AlloyVariable quotient = divAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable remainder = divAuxiliaryConstants.remainder.getVariable();
				if (this.isContractTranslation){
					quotient.setIsVariableFromContract();
					remainder.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(divAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(quotient, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(remainder, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
				} else {
					this.predsFromMathOperations.add(divAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(quotient, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(remainder, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
				}
			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))) {

				JMLDivAuxiliaryConstants divAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_char_int_divide_auxiliary_constants(left_div_expr, right_div_expr);
				rvalue = divAuxiliaryConstants.result_variable;

				AlloyVariable quotient = divAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable remainder = divAuxiliaryConstants.remainder.getVariable();
				if (this.isContractTranslation){
					quotient.setIsVariableFromContract();
					remainder.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(divAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(quotient, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(remainder, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
				} else {
					this.predsFromMathOperations.add(divAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(quotient, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(remainder, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
				}
			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))) {

				JMLDivAuxiliaryConstants divAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_char_long_divide_auxiliary_constants(left_div_expr, right_div_expr);
				rvalue = divAuxiliaryConstants.result_variable;

				AlloyVariable quotient = divAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable remainder = divAuxiliaryConstants.remainder.getVariable();
				if (this.isContractTranslation){
					quotient.setIsVariableFromContract();
					remainder.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(divAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(quotient, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(remainder, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
				} else {
					this.predsFromMathOperations.add(divAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(quotient, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(remainder, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
				}
			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))) {

				JMLDivAuxiliaryConstants divAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_float_divide_auxiliary_constants(left_div_expr, right_div_expr);
				rvalue = divAuxiliaryConstants.result_variable;

				AlloyVariable quotient = divAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable remainder = divAuxiliaryConstants.remainder.getVariable();
				if (this.isContractTranslation){
					quotient.setIsVariableFromContract();
					remainder.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(divAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(quotient, JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(remainder, JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE.toString());
				} else {
					this.predsFromMathOperations.add(divAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(quotient, JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(remainder, JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE.toString());
				}
			} else {
				throw new TacoException("Cannot divide elements from types " + left_alloy_type + " and " + right_alloy_type);
			}

		} else {

			Object binaryExpression;
			binaryExpression = ExpressionSolver.getBinaryExpression(this, jDivideExpression, Constants.OPE_SLASH);
			rvalue = (AlloyExpression) binaryExpression;

		}

		this.getStack().push(rvalue);

	}




	//mfrias 31/07/2013
	@Override
	public void visitModuloExpression(JModuloExpression jModuloExpression) {

		AlloyExpression rvalue = null;


		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {

			jModuloExpression.left().accept(this);
			AlloyExpression left_mod_expr = this.getAlloyExpression();
			//recover new left subexpression

			jModuloExpression.right().accept(this);
			AlloyExpression right_mod_expr = this.getAlloyExpression();
			//recover new right subexpression

			CType left_type = getType(jModuloExpression.left());
			CType right_type = getType(jModuloExpression.right());

			CTypeAdapter type_Adapter = new CTypeAdapter();
			JType left_alloy_type = type_Adapter.translate(left_type);
			JType right_alloy_type = type_Adapter.translate(right_type);

			if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))) {

				JMLModuloAuxiliaryConstants modAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_integer_modulo_auxiliary_constants(left_mod_expr, right_mod_expr);
				rvalue = modAuxiliaryConstants.remainder;

				AlloyVariable res = modAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable rem = modAuxiliaryConstants.remainder.getVariable();
				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					rem.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(modAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(rem, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
				} else {
					this.predsFromMathOperations.add(modAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(rem, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
				}
			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))) {

				JMLModuloAuxiliaryConstants modAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_long_modulo_auxiliary_constants(left_mod_expr, right_mod_expr);
				rvalue = modAuxiliaryConstants.remainder;
				//assemble new expression

				rvalue = modAuxiliaryConstants.remainder;

				AlloyVariable res = modAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable rem = modAuxiliaryConstants.remainder.getVariable();
				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					rem.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(modAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(rem, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
				} else {
					this.predsFromMathOperations.add(modAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(rem, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
				}
			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))) {

				JMLModuloAuxiliaryConstants modAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_char_modulo_int_auxiliary_constants(left_mod_expr, right_mod_expr);
				rvalue = modAuxiliaryConstants.remainder;
				//assemble new expression

				rvalue = modAuxiliaryConstants.remainder;

				AlloyVariable res = modAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable rem = modAuxiliaryConstants.remainder.getVariable();
				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					rem.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(modAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(rem, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
				} else {
					this.predsFromMathOperations.add(modAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(rem, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE.toString());
				}
			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))) {

				JMLModuloAuxiliaryConstants modAuxiliaryConstants = JMLAuxiliaryConstantsFactory.build_char_modulo_long_auxiliary_constants(left_mod_expr, right_mod_expr);
				rvalue = modAuxiliaryConstants.remainder;
				//assemble new expression

				rvalue = modAuxiliaryConstants.remainder;

				AlloyVariable res = modAuxiliaryConstants.result_variable.getVariable();
				AlloyVariable rem = modAuxiliaryConstants.remainder.getVariable();
				if (this.isContractTranslation){
					res.setIsVariableFromContract();
					rem.setIsVariableFromContract();
					this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().add(modAuxiliaryConstants.pred);
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(res, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(rem, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
				} else {
					this.predsFromMathOperations.add(modAuxiliaryConstants.pred);
					this.varsAndTheirTypeFromMathOperations.put(res, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
					this.varsAndTheirTypeFromMathOperations.put(rem, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE.toString());
				}
			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))) {
				throw new TacoException("Cannot take remainder of elements from types " + left_alloy_type + " and " + right_alloy_type);

			} else {
				throw new TacoException("Cannot take remainder of elements from types " + left_alloy_type + " and " + right_alloy_type);
			}

		} else {

			Object binaryExpression;
			binaryExpression = ExpressionSolver.getBinaryExpression(this, jModuloExpression, Constants.OPE_PERCENT);
			rvalue = (AlloyExpression) binaryExpression;

		}

		this.getStack().push(rvalue);

	}








	@Override
	public void visitFieldExpression(JClassFieldExpression jClassFieldExpression) {
		jClassFieldExpression.prefix().accept(this);
		AlloyExpression e1 = null;
		AlloyExpression e2 = null;
		if (this.isAlloyExpression()) {
			e1 = this.getAlloyExpression();
		} else {
			// if the prefix is a formula is because is a method call, so that
			// formula MUST be a QualifiedAlloyFormula
			QuantifiedFormula quantifiedAlloyFormula = (QuantifiedFormula) this
					.getAlloyFormula();

			AlloyVariable qualifiedVariable = new AlloyVariable(
					quantifiedAlloyFormula.getNames().get(0));

			this.getStack().push(quantifiedAlloyFormula);
			e1 = ExprVariable.buildExprVariable(qualifiedVariable);
		}
		if (jClassFieldExpression.getField().isStatic()) {
			AlloyVariable ident = null;
			if (this.instant == Instant.PRE_INSTANT) {
				ident = new AlloyVariable(e1.toString() + "_"
						+ jClassFieldExpression.ident());
			} else {
				ident = new AlloyVariable(e1.toString() + "_"
						+ jClassFieldExpression.ident(), true);
			}

			e2 = ExprVariable.buildExprVariable(ident);
			e1 = ar.edu.jdynalloy.factory.JExpressionFactory.CLASS_FIELDS;
		} else {
			AlloyVariable ident = null;
			if (this.instant == Instant.PRE_INSTANT) {
				ident = new AlloyVariable(jClassFieldExpression.ident());
			} else {
				ident = new AlloyVariable(jClassFieldExpression.ident(), true);
			}

			e2 = ExprVariable.buildExprVariable(ident);
		}

		ExprJoin exprJoin = ExprJoin.join(e1, e2);
		this.getStack().push(exprJoin);
	}

	@Override
	public void visitJmlAssertStatement(JmlAssertStatement jmlAssertStatement) {
		jmlAssertStatement.predicate().accept(this);
	}

	@Override
	public void visitJmlAssignableClause(JmlAssignableClause jmlAssignableClause) {
		hasAssignableClause = true;
		if (jmlAssignableClause.storeRefs() != null) {
			for (JmlStoreRef storeRef : jmlAssignableClause.storeRefs()) {
				if (storeRef instanceof JmlStoreRefKeyword) {
					if (((JmlStoreRefKeyword) storeRef).isNothing()) {
						jmlMethodDeclarationResult.modifiables.clear();
					} else if (((JmlStoreRefKeyword) storeRef).isEverything()) {
						jmlMethodDeclarationResult.modifiables.add(JModifies
								.buildModifiesEverything());
					} else if (((JmlStoreRefKeyword) storeRef).isNotSpecified()) {
						throw new TacoNotImplementedYetException(
								"Everything keyword is not implemented yet");
					}
				} else if (storeRef instanceof JmlStoreRefExpression) {
					JmlStoreRefExpression storeRefExpression = (JmlStoreRefExpression) storeRef;
					if (storeRefExpression.getName()
							.equals(OBJECT_STATE_STRING)) {
						List<AlloyVariable> variablesInGroupClause = super
								.getBuffer().getInGroupClauses()
								.get(OBJECT_STATE_STRING);
						if (variablesInGroupClause != null) {
							for (AlloyVariable alloyVariable : variablesInGroupClause) {
								AlloyExpression variable = new ExprJoin(
										JExpressionFactory.THIS_EXPRESSION,
										new ExprVariable(alloyVariable));
								jmlMethodDeclarationResult.modifiables
								.add(new JModifies(variable));
							}
						}
					} else if (storeRefExpression.getName().contains(
							OBJECT_STATE_STRING)) {

						String classJavaName = ((JClassFieldExpression) storeRefExpression
								.expression()).prefix().getType().getCClass()
								.getJavaName();
						JavaClassNameNormalizer classNameNormalizer = new JavaClassNameNormalizer(
								classJavaName);

						String normalizedClassName = classNameNormalizer
								.getQualifiedClassName();

						if (super.getModulesObjectState().containsKey(
								normalizedClassName)) {
							AlloyExpression prefixVariable = null;

							JExpression prefix = ((JClassFieldExpression) storeRefExpression
									.expression()).prefix();
							if (prefix != null) {
								prefix.accept(this);
								prefixVariable = this.getAlloyExpression();
							}

							List<String> fieldsNames = this
									.getModulesObjectState().get(
											normalizedClassName);
							for (String fieldName : fieldsNames) {
								AlloyExpression fieldVariable = ExprVariable
										.buildExprVariable(fieldName);
								AlloyExpression variable = new ExprJoin(
										prefixVariable, fieldVariable);

								jmlMethodDeclarationResult.modifiables
								.add(new JModifies(variable));
							}

						} else {
							throw new TacoException(
									"Class "
											+ classJavaName
											+ " must be added in config as a relevant class");
						}
					} else if (storeRefExpression.getName().contains(WIDLCARD_STRING) && 
							!(storeRefExpression.expression() instanceof JArrayAccessExpression)) {
						String classJavaName = null;
						AlloyExpression prefixVariable = null;
						if (storeRefExpression.expression() instanceof JLocalVariableExpression) {
							classJavaName = ((JLocalVariableExpression) storeRefExpression
									.expression()).getType().getCClass()
									.getJavaName();
							JExpression prefix = (JLocalVariableExpression) storeRefExpression
									.expression();

							if (prefix != null) {
								prefix.accept(this);
								prefixVariable = this.getAlloyExpression();
							}
						} else if (storeRefExpression.expression() instanceof JThisExpression) {
							classJavaName = ((JThisExpression) storeRefExpression
									.expression()).getType().getCClass()
									.getJavaName();

							prefixVariable = JExpressionFactory.THIS_EXPRESSION;
						} else {
							classJavaName = ((JClassFieldExpression) storeRefExpression
									.expression()).getType().getCClass()
									.getJavaName();
							JExpression prefix = ((JClassFieldExpression) storeRefExpression
									.expression()).prefix();

							if (prefix != null) {
								prefix.accept(this);
								prefixVariable = this.getAlloyExpression();
							}

							JNameExpression sourceName = ((JClassFieldExpression) storeRefExpression
									.expression()).sourceName();
							if (sourceName != null) {
								sourceName.accept(this);
								AlloyExpression sourceExpression = this
										.getAlloyExpression();
								prefixVariable = new ExprJoin(prefixVariable,
										sourceExpression);
							}
						}

						JavaClassNameNormalizer classNameNormalizer = new JavaClassNameNormalizer(
								classJavaName);

						String normalizedClassName = classNameNormalizer
								.getQualifiedClassName();

						if (super.getModulesNoStaticFields().containsKey(
								normalizedClassName)) {
							List<String> fieldsNames = this
									.getModulesNoStaticFields().get(
											normalizedClassName);
							for (String fieldName : fieldsNames) {
								AlloyExpression variable = ExprVariable
										.buildExprVariable(fieldName);
								variable = new ExprJoin(prefixVariable,
										variable);

								jmlMethodDeclarationResult.modifiables
								.add(new JModifies(variable));
							}

						} else {
							throw new TacoException(
									"Class "
											+ classJavaName
											+ " must be added in config as a relevant class");
						}
					} else {
						storeRef.accept(this);
						AlloyExpression storeRefExpr = this
								.getAlloyExpression();

						jmlMethodDeclarationResult.modifiables
						.add(new JModifies(storeRefExpr));
					}
				} else {
					throw new UnsupportedOperationException(
							"Operation: Assignable on " + storeRef.getClass()
							+ " is not supported.");
				}
			}
		}
	}

	@Override
	public void visitJmlAssumeStatement(JmlAssumeStatement jmlAssumeStatement) {
		jmlAssumeStatement.predicate().accept(this);
	}

	@Override
	public void visitJmlConstraint(JmlConstraint jmlConstraint) {
		JmlPredicate jmlPredicate = jmlConstraint.predicate();
		JmlSpecExpression jmlSpecExpression = jmlPredicate.specExpression();
		JExpression jExpression = jmlSpecExpression.expression();

		jExpression.accept(this);
		this.setInstant(instant);
		AlloyFormula constraint = this.getAlloyFormula();

		if (jmlConstraint.isStatic()) {
			jmlClassDeclarationResult.staticConstraints.add(constraint);
		} else {
			jmlClassDeclarationResult.constraints.add(constraint);
		}
	}

	@Override
	public void visitJmlEnsuresClause(JmlEnsuresClause jmlEnsuresClause) {
		if (jmlEnsuresClause.isNotSpecified()) {
			throw new IllegalArgumentException(
					"Ensures clause is not specified.");
		}

		Instant oldInstant = this.getInstant();
		this.instant = Instant.POST_INSTANT;
		AlloyFormula finalFormula = null;
		// if (!jmlEnsuresClause.isRedundantly()) {
		JmlExpressionVisitor jmlExpressionVisitor = new JmlExpressionVisitor(this.compilationUnits);
		jmlExpressionVisitor.instant = Instant.POST_INSTANT;
		jmlExpressionVisitor.isContractTranslation = true;
		jmlEnsuresClause.predOrNot().accept(jmlExpressionVisitor);

		this.predsFromMathOperations.addAll(jmlExpressionVisitor.predsFromMathOperations);
		for (AlloyVariable av : jmlExpressionVisitor.varsAndTheirTypeFromMathOperations.varSet()){
			this.varsAndTheirTypeFromMathOperations.put(av, jmlExpressionVisitor.varsAndTheirTypeFromMathOperations.get(av));
		}
		this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().addAll(jmlExpressionVisitor.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures());
		for (AlloyVariable av : jmlExpressionVisitor.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().varSet()){
			this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(av, jmlExpressionVisitor.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().get(av));
		}



		AlloyFormula ensuresFormula = jmlExpressionVisitor.getAlloyFormula();

		if (this.isNormalBehavior()) {
			AlloyFormula throwFormula = new EqualsFormula(
					JExpressionFactory.PRIMED_THROW_EXPRESSION,
					JExpressionFactory.NULL_EXPRESSION);

			finalFormula = new ImpliesFormula(throwFormula, ensuresFormula);
		} else {
			AlloyFormula throwFormula = new NotFormula(new EqualsFormula(
					JExpressionFactory.PRIMED_THROW_EXPRESSION,
					JExpressionFactory.NULL_EXPRESSION));

			finalFormula = new ImpliesFormula(throwFormula, ensuresFormula);
		}
		// } else {
		// finalFormula =
		// JPredicateFactory.eq(JExpressionFactory.TRUE_EXPRESSION,
		// JExpressionFactory.TRUE_EXPRESSION);
		// }

		if (jmlEnsuresClause.isRedundantly()) {
			ensuresRedundantly.add(finalFormula);
			finalFormula = JPredicateFactory.eq(
					JExpressionFactory.TRUE_EXPRESSION,
					JExpressionFactory.TRUE_EXPRESSION);
		}

		jmlExpressionVisitor.isContractTranslation = false;

		this.instant = oldInstant;
		super.getStack().push(finalFormula);

	}

	@Override
	public void visitJmlExceptionalBehaviorSpec(
			JmlExceptionalBehaviorSpec jmlExceptionalBehaviorSpec) {
		this.setNormalBehavior(false);

		jmlExceptionalBehaviorSpec.specCase().accept(this);
	}

	public void visitJmlExceptionalSpecBody(
			JmlExceptionalSpecBody jmlExceptionalSpecBody) {
		if (jmlExceptionalSpecBody.isSpecClauses()) {
			for (JmlSpecBodyClause jmlSpecBodyClause : jmlExceptionalSpecBody
					.specClauses()) {
				jmlSpecBodyClause.accept(this);
			}
		}
	}

	public void visitJmlExceptionalSpecCase(
			JmlExceptionalSpecCase jmlExceptionalSpecCase) {
		List<JPrecondition> requires = new ArrayList<JPrecondition>();
		List<JPostcondition> ensures = new ArrayList<JPostcondition>();
		List<JModifies> modifies = new ArrayList<JModifies>();

		if (jmlExceptionalSpecCase.hasSpecHeader()) {
			for (JmlRequiresClause jmlRequiresClause : jmlExceptionalSpecCase
					.specHeader()) {
				jmlRequiresClause.accept(this);
				AlloyFormula alloyFormula = this.getAlloyFormula();
				JPrecondition jPrecondition = new JPrecondition(alloyFormula);
				requires.add(jPrecondition);
			}
		}

		if (jmlExceptionalSpecCase.hasSpecBody()) {
			jmlExceptionalSpecCase.specBody().accept(this);
			while (!super.getStack().isEmpty()) {
				AlloyFormula alloyFormula = this.getAlloyFormula();
				JPostcondition jPostcondition = new JPostcondition(alloyFormula);
				ensures.add(jPostcondition);
			}
		}

		JSpecCase specCase = new JSpecCase(requires, ensures, modifies);

		this.jmlMethodDeclarationResult.jSpecCases.add(specCase);
	}

	@Override
	public void visitJmlFieldDeclaration(JmlFieldDeclaration jmlFieldDeclaration) {
		if (jmlFieldDeclaration.isModel()) {
			CField field = jmlFieldDeclaration.getField();

			String ident = field.getIdent();
			CTypeAdapter adapter = new CTypeAdapter();
			JType jType = adapter.translate(field.getType());

			if (field.isStatic()) {
				jmlClassDeclarationResult.staticModelFields.put(
						new AlloyVariable(ident), jType);
			} else {
				jmlClassDeclarationResult.modelFields.put(new AlloyVariable(
						ident), jType);
			}
		}
	}

	@Override
	public void visitJmlGenericSpecBody(JmlGenericSpecBody jmlGenericSpecBody) {

		if (jmlGenericSpecBody.isSpecClauses()) {
			for (JmlSpecBodyClause jmlSpecBodyClause : jmlGenericSpecBody
					.specClauses()) {
				jmlSpecBodyClause.accept(this);
			}
		}
	}

	@Override
	public void visitJmlGenericSpecCase(JmlGenericSpecCase jmlGenericSpecCase) {
		this.hasAssignableClause = false;
		List<JPrecondition> requires = new ArrayList<JPrecondition>();
		List<JPostcondition> ensures = new ArrayList<JPostcondition>();
		List<JModifies> modifies = new ArrayList<JModifies>();

		if (jmlGenericSpecCase.hasSpecHeader()) {
			for (JmlRequiresClause jmlRequiresClause : jmlGenericSpecCase
					.specHeader()) {
				jmlRequiresClause.accept(this);
				AlloyFormula alloyFormula = this.getAlloyFormula();
				JPrecondition jPrecondition = new JPrecondition(alloyFormula);
				requires.add(jPrecondition);
			}
		}

		if (jmlGenericSpecCase.hasSpecBody()) {
			jmlGenericSpecCase.specBody().accept(this);
			while (!super.getStack().isEmpty()) {
				AlloyFormula alloyFormula = this.getAlloyFormula();
				JPostcondition jPostcondition = new JPostcondition(alloyFormula);
				ensures.add(jPostcondition);
			}
		} else {
			AlloyFormula throwFormula = new EqualsFormula(
					JExpressionFactory.PRIMED_THROW_EXPRESSION,
					JExpressionFactory.NULL_EXPRESSION);
			JPostcondition jPostcondition = new JPostcondition(throwFormula);
			ensures.add(jPostcondition);
		}

		// DPD: Default behavior for Assignable
		if (!this.hasAssignableClause) {
			jmlMethodDeclarationResult.modifiables.add(JModifies
					.buildModifiesEverything());
		}

		modifies.addAll(jmlMethodDeclarationResult.modifiables);

		JSpecCase specCase = new JSpecCase(requires, ensures, modifies);

		this.jmlMethodDeclarationResult.jSpecCases.add(specCase);
	}

	@Override
	public void visitJmlInformalExpression(
			JmlInformalExpression jmlInformalExpression) {
		super.getStack().push(
				new EqualsFormula(JExpressionFactory.TRUE_EXPRESSION,
						JExpressionFactory.TRUE_EXPRESSION));
	}

	@Override
	public void visitJmlInvariant(JmlInvariant jmlInvariant) {
		JmlPredicate jmlPredicate = jmlInvariant.predicate();
		JmlSpecExpression jmlSpecExpression = jmlPredicate.specExpression();
		JExpression jExpression = jmlSpecExpression.expression();
		this.isContractTranslation = true;
		jExpression.accept(this);
		AlloyFormula invariant = this.getAlloyFormula();

		if (jmlInvariant.isStatic()) {
			jmlClassDeclarationResult.staticInvariants.add(invariant);
		} else {
			jmlClassDeclarationResult.invariants.add(invariant);
		}
		this.isContractTranslation = false;
	}

	@Override
	public void visitJmlMapsIntoClause(JmlMapsIntoClause arg0) {
		// TODO Auto-generated method stub
		super.visitJmlMapsIntoClause(arg0);
	}

	@Override
	public void visitJmlNormalBehaviorSpec(
			JmlNormalBehaviorSpec jmlNormalBehaviorSpec) {
		this.setNormalBehavior(true);

		jmlNormalBehaviorSpec.specCase().accept(this);

	}

	@Override
	public void visitJmlNormalSpecBody(JmlNormalSpecBody jmlNormalSpecBody) {
		if (jmlNormalSpecBody.isSpecClauses()) {
			for (JmlSpecBodyClause jmlSpecBodyClause : jmlNormalSpecBody
					.specClauses()) {
				jmlSpecBodyClause.accept(this);
			}
		}
	}

	public void visitJmlNormalSpecCase(JmlNormalSpecCase jmlNormalSpecCase) {
		List<JPrecondition> requires = new ArrayList<JPrecondition>();
		List<JPostcondition> ensures = new ArrayList<JPostcondition>();
		List<JModifies> modifies = new ArrayList<JModifies>();

		if (jmlNormalSpecCase.hasSpecHeader()) {
			for (JmlRequiresClause jmlRequiresClause : jmlNormalSpecCase
					.specHeader()) {
				jmlRequiresClause.accept(this);
				AlloyFormula alloyFormula = this.getAlloyFormula();
				JPrecondition jPrecondition = new JPrecondition(alloyFormula);
				requires.add(jPrecondition);
			}
		}

		if (jmlNormalSpecCase.hasSpecBody()) {
			jmlNormalSpecCase.specBody().accept(this);
			while (!super.getStack().isEmpty()) {
				AlloyFormula alloyFormula = this.getAlloyFormula();
				JPostcondition jPostcondition = new JPostcondition(alloyFormula);
				ensures.add(jPostcondition);
			}

			// if (ensuresRedundantly.size() > 0) {
			// boolean isFirst = true;
			// AlloyFormula ensuresConjuntion = null;
			// for (int i = 0; i < ensures.size(); i++) {
			// if (isFirst) {
			// ensuresConjuntion = ensures.get(i).getFormula();
			// isFirst = false;
			// } else {
			// ensuresConjuntion = new AndFormula(ensuresConjuntion,
			// ensures.get(i).getFormula());
			// }
			// }
			//
			// isFirst = true;
			// AlloyFormula redundantEnsuresConjuntion = null;
			// for (int i = 0; i < ensuresRedundantly.size(); i++) {
			// if (isFirst) {
			// redundantEnsuresConjuntion = ensuresRedundantly.get(i);
			// isFirst = false;
			// } else {
			// redundantEnsuresConjuntion = new
			// AndFormula(redundantEnsuresConjuntion,
			// ensuresRedundantly.get(i));
			// }
			// }
			//
			// JPostcondition finalPostCondition = new JPostcondition(new
			// ImpliesFormula(ensuresConjuntion, redundantEnsuresConjuntion));
			// ensures.clear();
			// ensures.add(finalPostCondition);
			//
			// ensuresRedundantly.clear();
			// }

		} else {
			AlloyFormula throwFormula = new EqualsFormula(
					JExpressionFactory.PRIMED_THROW_EXPRESSION,
					JExpressionFactory.NULL_EXPRESSION);
			JPostcondition jPostcondition = new JPostcondition(throwFormula);
			ensures.add(jPostcondition);
		}

		modifies.addAll(jmlMethodDeclarationResult.modifiables);

		JSpecCase specCase = new JSpecCase(requires, ensures, modifies);

		this.jmlMethodDeclarationResult.jSpecCases.add(specCase);
	}

	@Override
	public void visitJmlOldExpression(JmlOldExpression jmlOldExpression) {
		this.setInstant(Instant.PRE_INSTANT);
		jmlOldExpression.specExpression().accept(this);
		if (this.isAlloyExpression()) {
			AlloyExpression expr = this.getAlloyExpression();
			super.getStack().push(expr);
		} else {
			AlloyFormula formula = this.getAlloyFormula();
			super.getStack().push(formula);
		}
		this.setInstant(Instant.POST_INSTANT);
	}

	@Override
	public void visitJmlPredicate(JmlPredicate jmlPredicate) {
		jmlPredicate.specExpression().accept(this);
	}

	@Override
	public void visitJmlReachExpression(JmlReachExpression jmlReachExpression) {
		JmlSpecExpression specExpression = jmlReachExpression.specExpression();
		CType referenceType = jmlReachExpression.referenceType();
		//		String fieldName = jmlReachExpression.storeRefExpression().getName();
		JmlStoreRefExpression[] refs = jmlReachExpression.storeRefExpressions();
		String[] fieldNames = new String[refs.length];
		for(int i = 0 ; i < refs.length ; i++){
			fieldNames[i] = refs[i].getName();
		}

		specExpression.accept(this);
		AlloyExpression head = super.getAlloyExpression();

		CTypeAdapter cTypeAdapter = new CTypeAdapter();
		JType type = cTypeAdapter.translate(referenceType);

		ExprConstant typeConstant = new ExprConstant(null,
				JTypeHelper.getBaseType(type));
		//		ExprVariable fieldVariable = JmlExpressionSolver.buildInstantVariable(
		//				fieldName, instant);

		ExprVariable[] fieldVariables = new ExprVariable[fieldNames.length]; 
		for(int i = 0 ; i < fieldNames.length ; i++){
			fieldVariables[i] = JmlExpressionSolver.buildInstantVariable(
					fieldNames[i], instant);
		}

		AlloyExpression reachExpression;
		if (JDynAlloyConfig.getInstance().getJMLObjectSetToAlloySet()) {
			reachExpression = JExpressionFactory.reach(head, typeConstant, //mfrias-mffrias-24-09-2012-aca puede estar el problema de la coma entre fields
					fieldVariables);
		} else {
			reachExpression = JExpressionFactory.reach_JMLObjectSet(head, //mfrias-mffrias-24-09-2012-aca puede estar el problema de la coma entre fields
					typeConstant, fieldVariables);
		}

		super.getStack().push(reachExpression);
	}


	@Override
	public void visitJmlRepresentsDecl(JmlRepresentsDecl jmlRepresentsDecl) {
		if (jmlRepresentsDecl.usesAbstractionRelation()) {

			JmlStoreRefExpression storeRef = jmlRepresentsDecl.storeRef();
			JExpression expr = storeRef.expression();
			if (expr == null) {
				throw new IllegalArgumentException(
						"store ref expression not type checked at "
								+ storeRef.getTokenReference());
			}

			expr.accept(this);
			AlloyExpression e = this.getAlloyExpression();

			CType ctype = expr.getType();
			CTypeAdapter typeAdapter = new CTypeAdapter();
			JType exprType = typeAdapter.translate(ctype);

			JmlPredicate predicate = jmlRepresentsDecl.predicate();
			predicate.accept(this);
			AlloyFormula alloyFormula = this.getAlloyFormula();

			jmlClassDeclarationResult.represents.add(new JmlRepresentsData(e,
					exprType, alloyFormula));

		} else if (jmlRepresentsDecl.usesAbstractionFunction()) {
			throw new RuntimeException("function abstraction is not supported.");
		} else {
			throw new RuntimeException("Unknown abstraction represents clause");
		}

	}

	@Override
	public void visitJmlRequiresClause(JmlRequiresClause jmlRequiresClause) {
		if (jmlRequiresClause.isNotSpecified()) {
			throw new IllegalArgumentException(
					"Requires clause is not specified.");
		}

		JmlExpressionVisitor jmlExpressionVisitor = new JmlExpressionVisitor(
				Instant.PRE_INSTANT);
		jmlExpressionVisitor.isContractTranslation = true;
		jmlRequiresClause.predOrNot().accept(jmlExpressionVisitor);


		AlloyFormula requireFormula = null;
		if (jmlExpressionVisitor.isAlloyFormula()) {
			requireFormula = jmlExpressionVisitor.getAlloyFormula();
		} else {
			AlloyExpression alloyExpression = jmlExpressionVisitor
					.getAlloyExpression();
			requireFormula = new EqualsFormula(alloyExpression,
					JExpressionFactory.TRUE_EXPRESSION);
		}

		// AlloyFormula finalFormula = null;
		// if (this.isNormalBehavior()) {
		// AlloyFormula throwFormula = new
		// EqualsFormula(JExpressionFactory.THROW_EXPRESSION,
		// JExpressionFactory.NULL_EXPRESSION);
		//
		// finalFormula = new AndFormula(throwFormula, requireFormula);
		// } else {
		// AlloyFormula throwFormula = new NotFormula(new
		// EqualsFormula(JExpressionFactory.THROW_EXPRESSION,
		// JExpressionFactory.NULL_EXPRESSION));
		//
		// finalFormula = new AndFormula(throwFormula, requireFormula);
		// }

		this.predsFromMathOperations.addAll(jmlExpressionVisitor.predsFromMathOperations);
		for (AlloyVariable av : jmlExpressionVisitor.varsAndTheirTypeFromMathOperations.varSet()){
			this.varsAndTheirTypeFromMathOperations.put(av, jmlExpressionVisitor.varsAndTheirTypeFromMathOperations.get(av));
		}
		this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().addAll(jmlExpressionVisitor.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures());
		for (AlloyVariable av : jmlExpressionVisitor.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().varSet()){
			this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(av, jmlExpressionVisitor.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().get(av));
		}
		jmlExpressionVisitor.isContractTranslation = false;
		super.getStack().push(requireFormula);

	}

	@Override
	public void visitJmlResultExpression(JmlResultExpression jmlResultExpression) {
		String returnVarId = JExpressionFactory.RETURN_VARIABLE.toString();
		ExprVariable returnExpr = JmlExpressionSolver.buildInstantVariable(
				returnVarId, instant);

		super.getStack().push(returnExpr);
	}

	@Override
	public void visitJmlSignalsClause(JmlSignalsClause jmlSignalsClause) {
		AlloyExpression throwExpression = JExpressionFactory.PRIMED_THROW_EXPRESSION;
		CTypeAdapter cTypeAdapter = new CTypeAdapter();

		JType jtype = cTypeAdapter.translate(
				jmlSignalsClause.type().getErasure());
		String signatureId = jtype.singletonFrom().toString();

		AlloyFormula alloyFormula = JPredicateFactory.instanceOf(
				throwExpression, signatureId);

		if (!jmlSignalsClause.isNotSpecified()) {
			jmlSignalsClause.predOrNot().accept(this);

			AlloyFormula predicate = this.getAlloyFormula();

			alloyFormula = new ImpliesFormula(alloyFormula, predicate);
		}

		super.getStack().push(alloyFormula);

	}

	@Override
	public void visitJmlSignalsOnlyClause(
			JmlSignalsOnlyClause jmlSignalsOnlyClause) {
		// FIXME: Cambiar el instanceOf(Throw', NullPointerException+null) por
		// throw = NullPointerException

		if (jmlSignalsOnlyClause.exceptions() != null) {
			AlloyExpression throwExpression = JExpressionFactory.PRIMED_THROW_EXPRESSION;
			CTypeAdapter cTypeAdapter = new CTypeAdapter();
			AlloyFormula finalOrFormula = null;
			for (CClassType cClassType : jmlSignalsOnlyClause.exceptions()) {
				JType jType = cTypeAdapter.translate(cClassType.getErasure());
				String signatureName = jType.dpdTypeNameExtract();
				String signatureId = JExpressionFactory.buildLiteralSingleton(
						signatureName).getConstantId();

				// String signatureId =
				// cTypeAdapter.translate(cClassType.getErasure()).toString();

				AlloyFormula alloyFormula = JPredicateFactory.instanceOf(
						throwExpression, signatureId);

				if (finalOrFormula == null) {
					finalOrFormula = alloyFormula;
				} else {
					finalOrFormula = new OrFormula(finalOrFormula, alloyFormula);
				}
			}

			AlloyFormula finalFormula = null;
			if (this.isNormalBehavior()) {
				AlloyFormula throwFormula = new EqualsFormula(
						JExpressionFactory.PRIMED_THROW_EXPRESSION,
						JExpressionFactory.NULL_EXPRESSION);

				finalFormula = new AndFormula(throwFormula, finalOrFormula);
			} else {
				AlloyFormula throwFormula = new NotFormula(new EqualsFormula(
						JExpressionFactory.PRIMED_THROW_EXPRESSION,
						JExpressionFactory.NULL_EXPRESSION));

				finalFormula = new AndFormula(throwFormula, finalOrFormula);
			}

			super.getStack().push(finalFormula);
		}
	}

	@Override
	public void visitJmlSpecExpression(JmlSpecExpression jmlSpecExpression) {
		jmlSpecExpression.expression().accept(this);
	}

	@Override
	public void visitJmlSpecQuantifiedExpression(
			JmlSpecQuantifiedExpression jmlSpecQuantifiedExpression) {

		List<String> quantifiedNames = new ArrayList<String>();
		List<AlloyExpression> quantifiedExpressions = new ArrayList<AlloyExpression>();
		for (JVariableDefinition varDef : jmlSpecQuantifiedExpression
				.quantifiedVarDecls()) {
			String ident = varDef.ident();
			quantifiedNames.add(ident);

			CType cType = varDef.getType();
			CTypeAdapter cTypeAdapter = new CTypeAdapter();
			JType jType = cTypeAdapter.translate(cType);

			if (TacoConfigurator.getInstance().getBoolean(
					TacoConfigurator.QUALIFIERS_INCLUDES_NULL) == true)
				quantifiedExpressions.add(jType.toAlloyExpr());
			else {
				JType nonNullType = new JType(JTypeHelper.getBaseType(jType));
				quantifiedExpressions.add(nonNullType.toAlloyExpr());
			}
		}

		this.notAllowsPrimedState.addAll(quantifiedNames);

		AlloyFormula impliesPre = null;

		//make a backup of the already registered preds and vars. The new ones, introduced by
		//the translation of the quantified formula will have a distinct treatment. 
		ArrayList<AlloyFormula> oldPredFromRequiresAndEnsures = new ArrayList<AlloyFormula>();
		oldPredFromRequiresAndEnsures.addAll(this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures());
		AlloyTyping oldVarsFromRequiresAndEnsures = new AlloyTyping();
		oldVarsFromRequiresAndEnsures.merge(this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures());
		this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().clear();
		this.varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures = new AlloyTyping();
		
		if (jmlSpecQuantifiedExpression.hasPredicate()) {
			JmlPredicate predicate = jmlSpecQuantifiedExpression.predicate();
			predicate.accept(this);
			impliesPre = super.getAlloyFormula();
		}

		JmlSpecExpression expression = jmlSpecQuantifiedExpression
				.specExpression();
		expression.accept(this);
		if (jmlSpecQuantifiedExpression.isExists()
				|| jmlSpecQuantifiedExpression.isForAll()) {

			//mfrias 22/12/2015: Quantified expressions ranging over Java integer or long values are bounded by adding appropriate literals to the model.
			//Notice that nothing is made for floating point variables.
			if (TacoConfigurator.getInstance().getUseJavaArithmetic()){
				int lower = TacoConfigurator.getInstance().getLowerBound();
				int upper = TacoConfigurator.getInstance().getUpperBound();

				if (arrayContainsAnIntegerVariable(jmlSpecQuantifiedExpression.quantifiedVarDecls())){
					for (int i = lower; i <= upper; i++){
						JavaPrimitiveIntegerValue.getInstance().toJavaPrimitiveIntegerLiteral(i, false);
					}
				} else if (arrayContainsALongVariable(jmlSpecQuantifiedExpression.quantifiedVarDecls())){
					for (int i = lower; i <= upper; i++){
						JavaPrimitiveLongValue.getInstance().toJavaPrimitiveLongLiteral(i, false);
					}				
				}

			}

			AlloyFormula impliesPost = this.getAlloyFormula();

			AlloyFormula formula = null;

			Operator operator;
			if (jmlSpecQuantifiedExpression.isExists()) {
				operator = Operator.EXISTS;
				if (impliesPre == null)
					formula = impliesPost;
				else {
					formula = new AndFormula(impliesPre, impliesPost);
				}

			} else if (jmlSpecQuantifiedExpression.isForAll()) {
				operator = Operator.FOR_ALL;

				if (impliesPre == null)
					formula = impliesPost;
				else {
					formula = new ImpliesFormula(impliesPre, impliesPost);
				}

			} else
				throw new IllegalArgumentException("Quantifier not supported "
						+ jmlSpecQuantifiedExpression.toString());


			List<String> newQuantifiedNames = new ArrayList<String>();
			List<AlloyExpression> newQuantifiedExpressions = new ArrayList<AlloyExpression>();
//			List<AlloyFormula> notQuantifiedFormulas = new ArrayList<AlloyFormula>();
			AlloyFormula alloyFormula = null;
//			Set<PredicateFormula> formulasToRemove = new HashSet<PredicateFormula>();
			for (AlloyFormula pf : getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures()){
				PredicateFormula pff = (PredicateFormula)pf;
//				if (predicateCallContainsAnyOfTheVars(pff, jmlSpecQuantifiedExpression.quantifiedVarDecls())){
//					formulasToRemove.add(pff);
					alloyFormula = alloyFormula==null ? pff : new AndFormula(alloyFormula, pff);
					for (int varIndex = 2; varIndex < pff.getParameters().size(); varIndex++){
						AlloyVariable av = new AlloyVariable(((ExprVariable)pff.getParameters().get(varIndex)).getVariable().getVariableId().getString());
						newQuantifiedNames.add(((ExprVariable)pff.getParameters().get(varIndex)).getVariable().getVariableId().getString());
						newQuantifiedExpressions.add(ExprConstant.buildExprConstant(getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().get(av)));
//						if (this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().contains(av)){
//							getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().remove(av);
//						}				

						// Keep track of types of newly quantified variables
						if (av.getVariableId().getString().contains("result") && pff.getPredicateId().contains("java_primitive_integer_value")){
							int lower = TacoConfigurator.getInstance().getLowerBound();
							int upper = TacoConfigurator.getInstance().getUpperBound();

							if (lower <= upper){
								AlloyFormula af = new EqualsFormula(ExprVariable.buildExprVariable(av), 
										JavaPrimitiveIntegerValue.getInstance().toJavaPrimitiveIntegerLiteral(lower, false)
										);
								for (int i = lower+1; i <= upper; i++){
									af = new OrFormula(af, new EqualsFormula(ExprVariable.buildExprVariable(av), 
											JavaPrimitiveIntegerValue.getInstance().toJavaPrimitiveIntegerLiteral(i, false)
											));
								}
								alloyFormula = new AndFormula(alloyFormula, af);
							}
						} else 	if (av.getVariableId().getString().contains("result") && pff.getPredicateId().contains("java_primitive_long_value")){
							int lower = TacoConfigurator.getInstance().getLowerBound();
							int upper = TacoConfigurator.getInstance().getUpperBound();

//							alloyFormula = pff;
							if (lower <= upper){
								AlloyFormula af = new EqualsFormula(ExprVariable.buildExprVariable(av), 
										JavaPrimitiveLongValue.getInstance().toJavaPrimitiveLongLiteral(lower, false));
								for (int i = lower+1; i <= upper; i++){
									af = new OrFormula(af, new EqualsFormula(ExprVariable.buildExprVariable(av), 
											JavaPrimitiveLongValue.getInstance().toJavaPrimitiveLongLiteral(i, false)));
								}
								alloyFormula = new AndFormula(alloyFormula, af);
							}
						} else 	if (av.getVariableId().getString().contains("result") && pff.getPredicateId().contains("java_primitive_float_value")){
							int lower = TacoConfigurator.getInstance().getLowerBound();
							int upper = TacoConfigurator.getInstance().getUpperBound();

//							alloyFormula = pf;
							if (lower <= upper){

								List<AlloyExpression> params1 = new ArrayList<AlloyExpression>();
								params1.add(JavaPrimitiveFloatValue.getInstance().toJavaPrimitiveFloatLiteral(lower, false));
								params1.add(ExprVariable.buildExprVariable(av));

								List<AlloyExpression> params2 = new ArrayList<AlloyExpression>();
								params2.add(ExprVariable.buildExprVariable(av));
								params2.add(JavaPrimitiveFloatValue.getInstance().toJavaPrimitiveFloatLiteral(upper, false));
								AlloyFormula af = new AndFormula(new PredicateFormula(null, "pred_java_primitive_float_value_lte", params1), new PredicateFormula(null, "pred_java_primitive_float_value_lte", params2));
								alloyFormula = new AndFormula(alloyFormula, af);
							}
						}														
					}

//				} else {						
//					notQuantifiedFormulas.add(pff);
//				}
			}
			getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().clear();
			getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().addAll(oldPredFromRequiresAndEnsures);
			varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures = new AlloyTyping();
			getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().merge(oldVarsFromRequiresAndEnsures);

			alloyFormula = alloyFormula == null ? formula : new QuantifiedFormula(Operator.FOR_ALL, newQuantifiedNames, newQuantifiedExpressions, new ImpliesFormula(alloyFormula, formula));

			QuantifiedFormula quantifiedAlloyFormula = new QuantifiedFormula(
					operator, quantifiedNames, quantifiedExpressions, alloyFormula);

			super.getStack().push(quantifiedAlloyFormula);
			
		} else if (jmlSpecQuantifiedExpression.isSum()) {
			AlloyExpression sumExpression = this.getAlloyExpression();

			ExprSum sum = new ExprSum(quantifiedNames, quantifiedExpressions,
					sumExpression);

			super.getStack().push(sum);

		} else {
			throw new TacoNotImplementedYetException(
					"Quantified operator not supported yet");
		}

		this.notAllowsPrimedState.removeAll(quantifiedNames);
	}



	//Helper method to check whether an int-typed variable is among the quantified ones.
	private boolean arrayContainsAnIntegerVariable(JVariableDefinition[] quantifiedVarDecls) {
		for (int index = 0; index < quantifiedVarDecls.length; index++){
			if (quantifiedVarDecls[index].getType().toString().equals("int")){
				return true;
			}
		}
		return false;
	}

	//Helper method to check whether a long-typed variable is among the quantified ones.
	private boolean arrayContainsALongVariable(JVariableDefinition[] quantifiedVarDecls) {
		for (int index = 0; index < quantifiedVarDecls.length; index++){
			if (quantifiedVarDecls[index].getType().toString().equals("long")){
				return true;
			}
		}
		return false;
	}



	private static boolean predicateCallOccursInSpec(PredicateFormula af, AlloyFormula pr){

		ResultVariableNamesCollectorVisitor rvncv = new ResultVariableNamesCollectorVisitor();
		FormulaVisitor fv = new FormulaVisitor(rvncv);

		pr.accept(fv);
		HashSet<String> result = ((ResultVariableNamesCollectorVisitor)fv.getDfsExprVisitor()).getResultVariableNames();

		boolean answer = false;
		if (result.contains(af.getParameters().get(2).toString()))
			answer = true;
		return answer;
	}

	private static boolean predicateCallContainsAnyOfTheVars(PredicateFormula af, JVariableDefinition[] vars){

		List<AlloyExpression> pars = af.getParameters();
		Vector<String> vecVars = new Vector<String>();
		for (AlloyExpression ae : pars){
			VariableNameCollectorVisitor vcv = new VariableNameCollectorVisitor();
			ae.accept(vcv);
			for (String s : vcv.result)
				vecVars.add(s);
		}

		boolean answer = false;
		for (JVariableDefinition jvd : vars){
			String name = jvd.ident();
			answer  = vecVars.contains(name);
			if (answer)
				return true;
		}
		return false;
	}


	@Override
	public void visitJmlStoreRefExpression(
			JmlStoreRefExpression jmlStoreRefExpression) {
		JmlExpressionVisitor jmlExpressionVisitor = new JmlExpressionVisitor(
				Instant.PRE_INSTANT);
		jmlStoreRefExpression.expression().accept(jmlExpressionVisitor);
		AlloyExpression alloyExpression = jmlExpressionVisitor
				.getAlloyExpression();
		super.getStack().push(alloyExpression);
	}

	@Override
	public void visitJmlTypeExpression(JmlTypeExpression jmlTypeExpression) {
		// type-expression ::= \type ( type )
		//
		// The operator \type can be used to introduce literals of type \TYPE in
		// expressions. An expression of the form \type(T), where T is a type
		// name,
		// has the type \TYPE. Since in JML \TYPE is the same as
		// java.lang.Class, an expression of the form \type(T) means the same
		// thing as T.class,
		// if T is a reference type. If T is a primitive type, then \type(T) is
		// equivalent to the value of the TYPE field of the corresponding
		// reference type.
		// Thus \type(boolean) equals Boolean.TYPE.

		// QQ: TYPE: El problema que tenemos aca es que por ejemplo mandan
		// \type(Object), pero no puedo obtener el qualified name
		// para la clase pasada como parametro... que hacemos, un mapping
		// hardcoded?????
		// Ademas, al igual que en \typeof estamos creando una ExprConstant para
		// devolver el tipo.

		// Respuesta: JMLForge le pide que soot le responda el tipo completo,
		// hay que ver si MJC tiene algun mecanismo similar.
		CType ctype = jmlTypeExpression.type();
		CTypeAdapter cTypeAdapter = new CTypeAdapter();
		JType jType = cTypeAdapter.translate(ctype);
		jType.setAsNonNull();

		AlloyExpression alloyExpression = ExprConstant.buildExprConstant(jType
				.toString());

		super.getStack().push(alloyExpression);
	}


	@Override
	public void visitMethodCallExpression(
			JMethodCallExpression jMethodCall) {


		String qualified_name = jMethodCall.method().toString();

		AlloyExpression prefix_expression;
		if (jMethodCall.prefix() != null) {
			jMethodCall.prefix().accept(this);
			prefix_expression = this.getAlloyExpression();
		} else {
			prefix_expression = ExprConstant.buildExprConstant("none");
		}

		Vector<AlloyExpression> args_expression = new Vector<AlloyExpression>();
		JExpression[] args = jMethodCall.args();
		for (int i = 0; i < args.length; i++) {
			JExpression jExpression = args[i];
			jExpression.accept(this);
			args_expression.add(this.getAlloyExpression());
		}

		AlloyExpression fun_expr;
		if (jMethodCall.ident().equals("isEmpty")) {
			if (qualified_name.startsWith("org.jmlspecs.models.JMLObjectSet")) {

				fun_expr = JExpressionFactory.setIsEmpty(prefix_expression);

			} else if (qualified_name
					.startsWith("org.jmlspecs.models.JMLObjectSequence")) {

				fun_expr = JExpressionFactory.listEmpty(prefix_expression);

			} else
				throw new TacoException(
						"method call within annotations not supported");

		} else if (jMethodCall.ident().equals("int_size")) {
			for (int idx = 0; idx < (int) Math.pow(2, TacoConfigurator.getInstance().getBitwidth()-1); idx++ ){
				JavaPrimitiveIntegerValue.getInstance().toJavaPrimitiveIntegerLiteral(idx, false);
			}
			if (qualified_name.startsWith("org.jmlspecs.models.JMLObjectSet")) {
				if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true ){
					fun_expr = JExpressionFactory.setSizeReturnsJavaPrimitiveIntegerValue(prefix_expression);
				} else {	
					fun_expr = JExpressionFactory.setSize(prefix_expression);
				}
			} else if (qualified_name
					.startsWith("org.jmlspecs.models.JMLObjectSequence")) {

				fun_expr = JExpressionFactory.listSize(prefix_expression);

			} else
				throw new TacoException(
						"method call within annotations not supported");

		} else if (jMethodCall.ident().equals("has")) {

			fun_expr = JExpressionFactory.setContains(prefix_expression,
					args_expression.get(0));

		} else if (jMethodCall.ident().equals("get")) {

			fun_expr = JExpressionFactory.listGet(prefix_expression,
					args_expression.get(0));

		} else if (jMethodCall.ident().equals("contains")) {
			if (qualified_name.startsWith("java.util.Set") || qualified_name.startsWith("java.util.HashSet")) {
				if (this.instant == Instant.PRE_INSTANT){
					fun_expr = JExpressionFactory.javaUtilSetContains(prefix_expression, args_expression.get(0), JExpressionFactory.JAVA_UTIL_SET_ELEMS_EXPRESSION);
				} else {
					fun_expr = JExpressionFactory.javaUtilSetContains(prefix_expression, args_expression.get(0), JExpressionFactory.PRIMED_JAVA_UTIL_SET_ELEMS_EXPRESSION);
				}
			} else {
				throw new TacoException(
						"method call within annotations not supported");
			}

		} else if (jMethodCall.ident().equals("size")) {	
			if (qualified_name.startsWith("java.util.Set") || qualified_name.startsWith("java.util.HashSet")) {
				if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true ){
					if (this.instant == Instant.PRE_INSTANT){
						fun_expr = JExpressionFactory.javaUtilSetSizeReturnsJavaPrimitiveIntegerValue(prefix_expression, JExpressionFactory.JAVA_UTIL_SET_ELEMS_EXPRESSION);
					} else {
						fun_expr = JExpressionFactory.javaUtilSetSizeReturnsJavaPrimitiveIntegerValue(prefix_expression, JExpressionFactory.PRIMED_JAVA_UTIL_SET_ELEMS_EXPRESSION);
					}
				} else {
					if (this.instant == Instant.PRE_INSTANT){
						fun_expr = JExpressionFactory.javaUtilSetSizeReturnsAlloyInt(prefix_expression, JExpressionFactory.JAVA_UTIL_SET_ELEMS_EXPRESSION);
					} else {
						fun_expr = JExpressionFactory.javaUtilSetSizeReturnsAlloyInt(prefix_expression, JExpressionFactory.PRIMED_JAVA_UTIL_SET_ELEMS_EXPRESSION);
					}
				}	
			} else {
				throw new TacoException(
						"method call within annotations not supported");
			}

		} else if (jMethodCall.ident().equals("isNaN")){
			if (qualified_name.startsWith("java.lang.Float")) {
				if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true ){
					fun_expr = JExpressionFactory.floatIsNaN(args_expression.get(0));
				} else {
					throw new TacoException(
							"Function isNaN not allowed unless JavaArithmetic is enabled.");
				}
			} else {
				throw new TacoException(
						"Function isNaN not called on type Float.");
			}

			//HERE HERE HERE
			//					} else if (jMethodCall.ident().equals("equals")) {
			//						String name = qualified_name.substring(0, qualified_name.indexOf('('));
			//						name = name.replace('.', '_');
			//						fun_expr = JExpressionFactory.buildEquals(name, prefix_expression, args_expression.get(0));
		} 
		else {
			fun_expr = JExpressionFactory.buildMethodCall(jMethodCall.ident(), prefix_expression, args_expression, qualified_name);

			//			throw new TacoException(
			//					"Method call should have been simplified before for method " + qualified_name + ".");
		}
		super.getStack().push(fun_expr);

	}

	@Override
	public void visitLocalVariableExpression(
			JLocalVariableExpression jLocalVariableExpression) {
		if (this.instant == Instant.PRE_INSTANT
				|| this.notAllowsPrimedState.contains(jLocalVariableExpression
						.ident())) {
			super.visitLocalVariableExpression(jLocalVariableExpression);
		} else {
			jLocalVariableExpression.accept(prettyPrint);
			log.debug("Visiting: "
					+ jLocalVariableExpression.getClass().getName());
			log.debug("Statement: " + prettyPrint.getPrettyPrint());

			String identifier = new String(jLocalVariableExpression.ident());
			AlloyVariable primedVariable = new AlloyVariable(identifier, true);
			primedVariable.setIsVariableFromContract();
			super.getStack().push(
					ExprVariable.buildExprVariable(primedVariable));

		}
	}

	@Override
	public void visitThisExpression(JThisExpression jThisExpression) {
		if (this.instant == Instant.PRE_INSTANT) {
			super.visitThisExpression(jThisExpression);
		} else {
			this.getStack()
			.push(ExprVariable
					.buildExprVariable(JExpressionFactory.PRIMED_THIS_VARIABLE));
		}
	}



	public AlloyTyping getVarsAndTheirTypeFromMathOperations(){
		return this.varsAndTheirTypeFromMathOperations;
	}

	public List<AlloyFormula> getPredsFromMathOperations(){
		return predsFromMathOperations;
	}



	public void setPredsFromMathOperations(List<AlloyFormula> l){
		this.predsFromMathOperations = l; 
	}


	public void setVarsAndTheirTypeFromMathOperations(AlloyTyping at){
		this.varsAndTheirTypeFromMathOperations = at; 
	}



	@Override
	public void visitOrdinalLiteral(JOrdinalLiteral jOrdinalLiteral) {

		if (jOrdinalLiteral.isLiteral()) {

			CType ctype = jOrdinalLiteral.getType();
			CTypeAdapter type_adapter = new CTypeAdapter();
			JType alloy_type = type_adapter.translate(ctype);

			AlloyExpression literalAlloyExpression;

			if (alloy_type
					.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE)) {

				int int_value = jOrdinalLiteral.numberValue().intValue();

				literalAlloyExpression = JavaPrimitiveIntegerValue
						.getInstance().toJavaPrimitiveIntegerLiteral(int_value, false);

			} else if (alloy_type
					.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE)) {

				long long_value = jOrdinalLiteral.numberValue().longValue();

				literalAlloyExpression = JavaPrimitiveLongValue.getInstance()
						.toJavaPrimitiveLongLiteral(long_value, false);

			} else if (alloy_type.equals(JSignatureFactory.ALLOY_INT)) {

				int literalValue = jOrdinalLiteral.numberValue().intValue();

				if (literalValue < 0) {

					literalAlloyExpression = JExpressionFactory
							.alloy_int_negate(new ExprIntLiteral(Math
									.abs(literalValue)));
				} else {
					literalAlloyExpression = new ExprIntLiteral(literalValue);
				}

			} else {
				throw new TacoException("unsupported ordinal type "
						+ alloy_type.toString());
			}

			super.getStack().push(literalAlloyExpression);

		} else if (jOrdinalLiteral.isBooleanLiteral()) {
			jOrdinalLiteral.getBooleanLiteral().accept(this);
		} else if (jOrdinalLiteral.isStringLiteral()) {
			jOrdinalLiteral.getStringLiteral().accept(this);
		} else {
			throw new TacoNotImplementedYetException(
					"Please verify the type of the JOrdinalLiteral and code the corresponding solution");
		}
	}







}
