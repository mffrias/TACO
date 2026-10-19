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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import ar.edu.jdynalloy.ast.*;
import ar.edu.taco.simplejml.builtin.*;
import ar.uba.dc.rfm.alloy.util.VarCollector;
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
import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.jdynalloy.factory.JPredicateFactory;
import ar.edu.jdynalloy.factory.JSignatureFactory;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.jdynalloy.xlator.JTypeHelper;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
import ar.edu.taco.TacoNotImplementedYetException;
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

	private Object inputToFix = null;


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

	/**
	 * Creates a translator that starts in the pre-state; see
	 * {@link #JmlExpressionVisitor(Instant)}.
	 */
	public JmlExpressionVisitor() {
		this(Instant.PRE_INSTANT);
	}


	/**
	 * Creates a translator that starts in the pre-state and passes the compilation units on to the
	 * visitors it creates for {@code ensures} clauses (nothing else in this class reads them); see
	 * {@link #JmlExpressionVisitor(Instant)}. {@code helpers.MethodDeclarationSolver} uses it for
	 * a method's specification.
	 *
	 * @param compilationUnits the simplified compilation units of the run
	 */
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

	/**
	 * Creates a translator of JML expressions and specifications into Alloy, starting in the given
	 * state. It is the JML counterpart of this package's {@code ExpressionVisitor}: each
	 * expression it visits, and each {@code requires}, {@code ensures} and {@code signals} clause,
	 * leaves one result on the inherited stack, an expression or a formula
	 * ({@code getAlloyExpression()}, {@code getAlloyFormula()}), while the parts of a
	 * specification are collected: spec cases and {@code assignable} locations in
	 * {@link #getJmlMethodDeclarationResult()}, read by {@code helpers.MethodDeclarationSolver};
	 * invariants, constraints and {@code represents} clauses in
	 * {@link #getJmlClassDeclarationResult()}, read by {@link JDynAlloyASTVisitor}.
	 * {@link BlockStatementsVisitor} uses it for JML {@code assert} and {@code assume} statements,
	 * loop invariants and ghost initializers.
	 *
	 * <p>The state decides how names are translated: in the pre-state ({@code PRE_INSTANT}) local
	 * variables, fields, {@code \result} and {@code this} are unprimed; in the post-state
	 * ({@code POST_INSTANT}, used for {@code ensures}) they are primed ({@code x'}, {@code f'},
	 * {@code return'}, {@code thiz'}), except the variables of an enclosing quantifier. With
	 * {@code useJavaArithmetic}, arithmetic operations become fresh variables defined by
	 * predicates (see {@link #visitAddExpression}).
	 *
	 * <p>This class adds no static state, but it registers literals in the singletons of
	 * {@code builtin} ({@code JavaPrimitiveIntegerValue} and the like) and numbers fresh variables
	 * with the counters of {@code builtin.JMLAuxiliaryConstantsFactory}, all kept between runs in
	 * one JVM.
	 *
	 * @param instant the state to start in
	 */
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




	/**
	 * Translates an equality ({@code ==} or {@code !=}) as {@code ExpressionVisitor} does, but
	 * entirely in the pre-state when one side is {@code \old(m(...))}, a call inside {@code \old};
	 * the state is restored afterwards. The sides it picks out are not used: the whole equality is
	 * translated.
	 *
	 * @param jEqualityExpression the equality
	 * @throws ClassCastException when the left side is {@code \old(m(...))} and the right side is
	 *     not an {@code \old} (the code casts the right side where it means the left)
	 */
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
	/**
	 * Translates {@code a + b} and pushes the result. With {@code useJavaArithmetic}, when no
	 * operand mentions a variable of an enclosing quantifier, the sum is a fresh variable
	 * ({@code SK_jml_<predicate>_ARG_result_<n>}, from
	 * {@code builtin.JMLAuxiliaryConstantsFactory}) defined by a predicate over the operands, the
	 * result and an overflow variable. The predicate and the two variables go to the
	 * requires/ensures lists
	 * ({@code getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures()} and
	 * {@code getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures()}, the variables
	 * marked as coming from the contract) when {@link #isContractTranslation()}, and to the
	 * math-operations lists ({@link #getPredsFromMathOperations()},
	 * {@link #getVarsAndTheirTypeFromMathOperations()}) otherwise. The operands may be both
	 * {@code int}, {@code long}, {@code float} or {@code char}, or {@code int} and {@code char} in
	 * either order. With a quantified variable, or without {@code useJavaArithmetic}, the sum is a
	 * function application ({@code helpers.ExpressionSolver.getBinaryExpression}).
	 *
	 * @param jAddExpression the sum
	 * @throws TacoException with {@code useJavaArithmetic}, for other operand types
	 */
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

			//Here we need to check that the variables involved in the expressions being added
			//were not quantified. For this, the visitor keeps a field "notAllowsPrimedState" that
			//contains the names of the quantified variables.
			//So we need to use an expression visitor to collect the variable names in the left and right
			//expressions, and we need to check these names do not intersect notAllowsPrimedState.
			//If they do, we just return the function call from the corresponding type rather than abstracting
			//the addition out of the specification.

			VarCollector varsInExpression = new VarCollector();
			right_add_expr.accept(varsInExpression);
			left_add_expr.accept(varsInExpression);

			boolean expressionsInvolveQuantifiedVariables = false;
			for (AlloyVariable theVar : varsInExpression.getVariables()){
				if (this.notAllowsPrimedState.contains(theVar.getVariableId().getString())){
					expressionsInvolveQuantifiedVariables = true;
				}
			}

			if (!expressionsInvolveQuantifiedVariables) {


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

					if (this.isContractTranslation) {
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

					if (this.isContractTranslation) {
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

					if (this.isContractTranslation) {
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

					if (this.isContractTranslation) {
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
					if (this.isContractTranslation) {
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
					if (this.isContractTranslation) {
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
				Object binaryExpression = ExpressionSolver.getBinaryExpression(this, jAddExpression, Constants.OPE_PLUS );
				rvalue = (AlloyExpression) binaryExpression;
			}

		} else {

			Object binaryExpression;
			binaryExpression = ExpressionSolver.getBinaryExpression(this, jAddExpression, Constants.OPE_PLUS);
			rvalue = (AlloyExpression) binaryExpression;

		}

		this.getStack().push(rvalue);

	}



	/**
	 * Translates {@code a - b} as {@link #visitAddExpression} translates a sum, for the same
	 * operand types.
	 *
	 * @param jMinusExpression the difference
	 * @throws TacoException with {@code useJavaArithmetic}, for other operand types
	 */
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

			VarCollector varsInExpression = new VarCollector();
			right_minus_expr.accept(varsInExpression);
			left_minus_expr.accept(varsInExpression);

			boolean expressionsInvolveQuantifiedVariables = false;
			for (AlloyVariable theVar : varsInExpression.getVariables()){
				if (this.notAllowsPrimedState.contains(theVar.getVariableId().getString())){
					expressionsInvolveQuantifiedVariables = true;
				}
			}

			if (!expressionsInvolveQuantifiedVariables) {

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
					if (this.isContractTranslation) {
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

					if (this.isContractTranslation) {
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
					if (this.isContractTranslation) {
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
					if (this.isContractTranslation) {
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
					if (this.isContractTranslation) {
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
					if (this.isContractTranslation) {
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
				Object binaryExpression = ExpressionSolver.getBinaryExpression(this, jMinusExpression, Constants.OPE_MINUS );
				rvalue = (AlloyExpression) binaryExpression;
			}
		} else {

			Object binaryExpression;
			binaryExpression = ExpressionSolver.getBinaryExpression(this, jMinusExpression, Constants.OPE_MINUS);
			rvalue = (AlloyExpression) binaryExpression;

		}

		this.getStack().push(rvalue);

	}




	//mfrias 23/07/2013
	/**
	 * Translates {@code a * b} as {@link #visitAddExpression} translates a sum. The operands may
	 * be both {@code int}, {@code long} or {@code float}, or {@code char} on the left and
	 * {@code int} or {@code long} on the right.
	 *
	 * @param jMultExpression the product
	 * @throws TacoException with {@code useJavaArithmetic}, for other operand types
	 */
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


			VarCollector varsInExpression = new VarCollector();
			right_mul_expr.accept(varsInExpression);
			left_mul_expr.accept(varsInExpression);

			boolean expressionsInvolveQuantifiedVariables = false;
			for (AlloyVariable theVar : varsInExpression.getVariables()){
				if (this.notAllowsPrimedState.contains(theVar.getVariableId().getString())){
					expressionsInvolveQuantifiedVariables = true;
				}
			}

			if (!expressionsInvolveQuantifiedVariables) {


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
					if (this.isContractTranslation) {
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
					if (this.isContractTranslation) {
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
					if (this.isContractTranslation) {
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
					if (this.isContractTranslation) {
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
					if (this.isContractTranslation) {
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

		} else {

			Object binaryExpression;
			binaryExpression = ExpressionSolver.getBinaryExpression(this, jMultExpression, Constants.OPE_STAR);
			rvalue = (AlloyExpression) binaryExpression;

		}

		this.getStack().push(rvalue);

	}





	//mfrias 31/07/2013
	/**
	 * Translates {@code a / b} as {@link #visitAddExpression} translates a sum, with a quotient
	 * variable (the result) and a remainder variable instead of a result and an overflow variable,
	 * for the operand types of {@link #visitMultExpression}.
	 *
	 * @param jDivideExpression the quotient
	 * @throws TacoException with {@code useJavaArithmetic}, for other operand types
	 */
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

			VarCollector varsInExpression = new VarCollector();
			right_div_expr.accept(varsInExpression);
			left_div_expr.accept(varsInExpression);

			boolean expressionsInvolveQuantifiedVariables = false;
			for (AlloyVariable theVar : varsInExpression.getVariables()){
				if (this.notAllowsPrimedState.contains(theVar.getVariableId().getString())){
					expressionsInvolveQuantifiedVariables = true;
				}
			}

			if (!expressionsInvolveQuantifiedVariables) {

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
					if (this.isContractTranslation) {
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
					if (this.isContractTranslation) {
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
					if (this.isContractTranslation) {
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
					if (this.isContractTranslation) {
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
					if (this.isContractTranslation) {
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
		} else {

			Object binaryExpression;
			binaryExpression = ExpressionSolver.getBinaryExpression(this, jDivideExpression, Constants.OPE_SLASH);
			rvalue = (AlloyExpression) binaryExpression;

		}

		this.getStack().push(rvalue);

	}




	//mfrias 31/07/2013
	/**
	 * Translates {@code a % b} like {@link #visitDivideExpression}, with the remainder variable as
	 * the result, for operands both {@code int} or both {@code long}, or {@code char} on the left
	 * and {@code int} or {@code long} on the right. Unlike the other operations, it does not test
	 * for quantified variables. For {@code int % int} both variables are typed
	 * {@code JavaPrimitiveLongValue}, and in a contract translation no predicate is added for
	 * them, so the remainder is unconstrained there.
	 *
	 * @param jModuloExpression the remainder
	 * @throws TacoException with {@code useJavaArithmetic}, for other operand types, {@code float}
	 *     included
	 */
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








	/**
	 * Translates a field access {@code e.f} into {@code E.f}, with {@code f'} in the post-state. A
	 * static field becomes {@code ClassFields.<E>_<f>} (primed in the post-state), {@code <E>}
	 * being the translated prefix as text: the class's name for a type name such as {@code pkg.C}.
	 * If the prefix translated to a formula (a quantified formula, for a call), the formula is
	 * pushed back and its first quantified variable is used as the prefix.
	 *
	 * @param jClassFieldExpression the field access
	 */
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

	/**
	 * Adds the locations of an {@code assignable} clause to the list of
	 * {@link #getJmlMethodDeclarationResult()}, which the spec cases translated later also get
	 * (see {@code JmlBaseExpressionVisitor.JmlMethodDeclarationResult}). {@code \nothing} clears
	 * the list and {@code \everything} adds "modifies everything". {@code objectState} gives
	 * {@code thiz.g} for each field {@code g} in the module's {@code objectState} data group,
	 * {@code e.objectState} the {@code objectState} fields of the class of {@code e}
	 * ({@code getModulesObjectState()}), and {@code e.*} the non-static fields of its class
	 * ({@code getModulesNoStaticFields()}); any other location is translated by
	 * {@link #visitJmlStoreRefExpression}, in the pre-state.
	 *
	 * @param jmlAssignableClause the clause
	 * @throws TacoException if the class of {@code e} in {@code e.objectState} or {@code e.*} has
	 *     not been translated before (it must be a relevant class)
	 * @throws TacoNotImplementedYetException for {@code \not_specified}
	 * @throws UnsupportedOperationException for another kind of location
	 */
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

	/**
	 * Translates a {@code constraint} into a formula of {@link #getJmlClassDeclarationResult()}
	 * (static or not), in the visitor's current state: the pre-state, as
	 * {@link JDynAlloyASTVisitor} creates the visitor, unless an earlier {@code \old} left it in
	 * the post-state ({@link #visitJmlOldExpression}). The call {@code this.setInstant(instant)}
	 * sets the state to itself. Unclear: a constraint relates two states, but here its operands
	 * before the first {@code \old} are translated in the pre-state and those after it in the
	 * post-state; whether that is intended is an open question.
	 *
	 * @param jmlConstraint the constraint
	 */
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

	/**
	 * Translates an {@code ensures} clause, with a new visitor in the post-state, into
	 * {@code throw' = null => E}, or {@code throw' != null => E} while the normal-behavior flag is
	 * cleared ({@link #isNormalBehavior()}, see {@link #visitJmlExceptionalBehaviorSpec}), and
	 * pushes it. The arithmetic variables and predicates of {@code E} are added to this visitor's
	 * lists. An {@code ensures_redundantly} clause pushes {@code true = true} instead, and its
	 * formula goes to a list that nothing reads.
	 *
	 * @param jmlEnsuresClause the clause
	 * @throws IllegalArgumentException for {@code ensures \not_specified}
	 */
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

	/**
	 * Translates an {@code exceptional_behavior} spec case after clearing the normal-behavior flag
	 * ({@link #isNormalBehavior()}). Only {@code normal_behavior} sets it again, so a later
	 * lightweight spec case of the same specification is translated as exceptional too
	 * ({@link #visitJmlEnsuresClause}, {@link #visitJmlSignalsOnlyClause}).
	 *
	 * @param jmlExceptionalBehaviorSpec the spec case
	 */
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

	/**
	 * Translates the spec case of an {@code exceptional_behavior}: its {@code requires} clauses
	 * and the formulas its body leaves on the stack ({@code ensures}, {@code signals},
	 * {@code signals_only}) become a {@code JSpecCase} of
	 * {@link #getJmlMethodDeclarationResult()}. Unlike the other spec cases, it gets no
	 * {@code modifies} at all (its {@code assignable} clauses are still collected, for the spec
	 * cases translated later) and never the input to fix.
	 *
	 * @param jmlExceptionalSpecCase the spec case
	 */
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

	/**
	 * Records a {@code model} field, with its translated type, as a model field (static or not) of
	 * {@link #getJmlClassDeclarationResult()}; other fields are ignored. Unclear:
	 * {@link JDynAlloyASTVisitor} visits model fields with itself, not with this visitor, so
	 * nothing seems to reach this override; whether it is still needed is an open question.
	 *
	 * @param jmlFieldDeclaration the field declaration
	 */
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

	/**
	 * Translates a lightweight spec case (heavyweight {@code behavior} spec cases are not
	 * supported: {@code JmlBaseVisitor.visitJmlBehaviorSpec} throws) into a {@code JSpecCase} of
	 * {@link #getJmlMethodDeclarationResult()}: its {@code requires} clauses; the formulas its
	 * body leaves on the stack, or {@code throw' = null} if it has no body; and as
	 * {@code modifies} every location collected so far, after adding "modifies everything" to them
	 * if this spec case has no {@code assignable} clause (so the spec cases translated later get
	 * it too).
	 *
	 * <p>With an input to fix ({@link #setInputToFix}) and at least one {@code requires}, the
	 * input becomes one more {@code requires}, built by reflection: a parameter {@code x} whose
	 * value is a number gets {@code x = <literal>} (with {@code useJavaArithmetic}, the literal of
	 * an {@code int}, {@code long} or {@code float}, registered in its singleton; otherwise an
	 * Alloy integer, for an {@code int} only); the fields of an object are named
	 * {@code x.<pkg_Class>_<f>} ({@code getDeclaredFields()}: static fields included, inherited
	 * ones not), and each value found in them, a number or {@code null}, is fixed once, at the
	 * first location found with it. A number already found elsewhere gives
	 * {@code <that location> = x} for a parameter and nothing for a field; so locations that share
	 * a value (by {@code equals}), aliasing between objects (parameters included), a {@code null}
	 * parameter, and fields of the other primitive types are not constrained. jDynAlloy, given the
	 * same input, drops every {@code requires} and applies the input itself (docs/SPEC-OUTPUT.md,
	 * "inputToFix"; docs/OPEN-QUESTIONS.md, item 12). Unclear: spec cases without
	 * {@code requires}, and {@code normal_behavior} and {@code exceptional_behavior} ones, never
	 * get the input; whether that is intended is an open question, if this {@code requires} is
	 * still needed.
	 *
	 * @param jmlGenericSpecCase the spec case
	 */
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
			// Here we hardcode inputToFix as an AlloyFormula which is added as a requires clause.
			if (inputToFix != null){
				AlloyFormula outcome = processInputToFix((HashMap<String, Object>) inputToFix, TacoConfigurator.getInstance().getUseJavaArithmetic());
				JPrecondition jPrecondition = new JPrecondition(outcome);
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

	/**
	 * Translates an informal description {@code (* ... *)} as {@code true = true}.
	 *
	 * @param jmlInformalExpression the description
	 */
	@Override
	public void visitJmlInformalExpression(
			JmlInformalExpression jmlInformalExpression) {
		super.getStack().push(
				new EqualsFormula(JExpressionFactory.TRUE_EXPRESSION,
						JExpressionFactory.TRUE_EXPRESSION));
	}

	/**
	 * Translates an {@code invariant} into a formula of {@link #getJmlClassDeclarationResult()}
	 * (static or not), with {@link #isContractTranslation()} false, so that its arithmetic goes to
	 * the math-operations lists ({@link #getVarsAndTheirTypeFromMathOperations()}), from which
	 * {@link JDynAlloyASTVisitor} makes fields of the class. The flag stays false afterwards.
	 *
	 * @param jmlInvariant the invariant
	 */
	@Override
	public void visitJmlInvariant(JmlInvariant jmlInvariant) {
		JmlPredicate jmlPredicate = jmlInvariant.predicate();
		JmlSpecExpression jmlSpecExpression = jmlPredicate.specExpression();
		JExpression jExpression = jmlSpecExpression.expression();
		this.isContractTranslation = false; //this is importante because if set to true will store the collected variables as coming from pre and postcondtions.
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

	/**
	 * Translates the spec case of a {@code normal_behavior} like a lightweight one
	 * ({@link #visitJmlGenericSpecCase}), except that a spec case without {@code assignable} adds
	 * no "modifies everything" (its {@code modifies} are the locations collected so far, possibly
	 * none) and that the input to fix is never added.
	 *
	 * @param jmlNormalSpecCase the spec case
	 */
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

	/**
	 * Translates {@code \old(e)} as {@code e} in the pre-state. Afterwards the visitor is in the
	 * post-state, whatever state it was in before, so in a formula translated in the pre-state the
	 * operands after an {@code \old} are translated in the post-state.
	 *
	 * @param jmlOldExpression the expression
	 */
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

	/**
	 * Translates {@code \reach(e, T, f, ...)} into {@code fun_reach[E, T, f + ...]}, with the
	 * fields primed in the post-state, or into its {@code JMLObjectSet} form
	 * ({@code JExpressionFactory.reach_JMLObjectSet}) when {@code JMLObjectSetToAlloySet} is off.
	 *
	 * @param jmlReachExpression the expression
	 */
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


	/**
	 * Records a {@code represents} clause in the relation form ({@code \such_that}) in
	 * {@link #getJmlClassDeclarationResult()}: the translated location, its type, and the formula
	 * of the predicate. The function form is not supported.
	 *
	 * @param jmlRepresentsDecl the clause
	 * @throws IllegalArgumentException if the location was not type-checked
	 * @throws RuntimeException for the function form
	 */
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

	/**
	 * Translates a {@code requires} clause, with a new visitor in the pre-state (an expression
	 * {@code e} becomes {@code e = true}), adds its arithmetic variables and predicates to this
	 * visitor's lists, and pushes the formula.
	 *
	 * @param jmlRequiresClause the clause
	 * @throws IllegalArgumentException for {@code requires \not_specified}
	 */
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






	private AlloyFormula processInputToFix(HashMap<String, Object> inputToFix, boolean isJavaArithmetic) {

		HashMap<Object, AlloyExpression> mapConcreteToExpre = new HashMap<Object, AlloyExpression>();
		HashMap<Object, AlloyExpression> mapConcreteToActualName = new HashMap<Object, AlloyExpression>();

//	    AlloyFormula fixedInputFormula = null;

		for (String s : inputToFix.keySet()){
			if (!s.equals("throw") && !s.equals("return") && !s.startsWith("customvar")){
				if (isJavaArithmetic) {

					if (inputToFix.get(s) != null && (inputToFix.get(s).getClass().getName().equals("java.lang.Integer") || inputToFix.get(s).getClass().getName().equals("int"))) {
						processIntegerToFix((Integer) inputToFix.get(s), mapConcreteToExpre);
					} else {
						if (inputToFix.get(s) != null && (inputToFix.get(s).getClass().getName().equals("java.lang.Long") || inputToFix.get(s).getClass().getName().equals("long"))) {
							processLongToFix((Long) inputToFix.get(s), mapConcreteToExpre);
						} else {
							if (inputToFix.get(s) != null && (inputToFix.get(s).getClass().getName().equals("java.lang.Float") || inputToFix.get(s).getClass().getName().equals("float"))) {
								processFloatToFix((Float) inputToFix.get(s), mapConcreteToExpre);
							} else {
								if ((inputToFix.get(s) == null) || !inputToFix.get(s).getClass().isPrimitive()) {
									if (inputToFix.get(s) == null) {
										if (s.equals("thiz")){
											AlloyExpression exp = new ExprConstant("null", "null");
											mapConcreteToExpre.put(null, exp);
										}
//										AlloyExpression exp = new ExprConstant("null", "null");
//										mapConcreteToExpre.put(null, exp);
									} else {
										processObjectToFix((Object) inputToFix.get(s), mapConcreteToExpre);
									}
								}
							}
						}
					}
				} else {
					if (inputToFix.get(s) != null && (inputToFix.get(s).getClass().getName().equals("java.lang.Integer") || inputToFix.get(s).getClass().getName().equals("int"))) {
						processAlloyIntegerToFix((Integer) inputToFix.get(s), mapConcreteToExpre);
					} else {
						if ((inputToFix.get(s) == null) || !inputToFix.get(s).getClass().isPrimitive()) {
							if (inputToFix.get(s) == null) {
								if (s.equals("thiz")){
									AlloyExpression exp = new ExprConstant("null", "null");
									mapConcreteToExpre.put(null, exp);
								}
//										AlloyExpression exp = new ExprConstant("null", "null");
//										mapConcreteToExpre.put(null, exp);
							} else {
								processObjectToFix((Object) inputToFix.get(s), mapConcreteToExpre);
							}
						}
					}
				}

			}

		}

		for (String s : inputToFix.keySet()){
			Object o = inputToFix.get(s);
			if (o != null && !mapConcreteToActualName.keySet().contains(o)) {
				ExprVariable e = new ExprVariable(new AlloyVariable(s));
				mapConcreteToActualName.put(inputToFix.get(s), e);
				if (inputToFix.get(s) != null && isNotNumericType(inputToFix.get(s).getClass())) {
					processObjectToFixActualName(inputToFix.get(s), e, mapConcreteToActualName);
				}
			} else {
				if (o == null && s.equals("thiz")){
					mapConcreteToActualName.put(null, new ExprConstant("null", "null"));
				}
			}
		}


		AlloyFormula theOutcomeFormula = null;

		Set<Object> alreadyVisited = new HashSet<>();
		for (String s : inputToFix.keySet()){
			Object o = inputToFix.get(s);
			AlloyExpression name = mapConcreteToActualName.get(o);
			AlloyExpression alloyValue = mapConcreteToExpre.get(o);
			alreadyVisited.add(o);
			if (!alloyValue.toString().contains("/null")) {
				AlloyFormula af;
				if (name.toString().equals(s)) {
					af = new EqualsFormula(name, alloyValue);
				} else {
					ExprVariable ev = new ExprVariable(new AlloyVariable(s));
					af = new EqualsFormula(name, ev);
				}
				if (theOutcomeFormula == null) {
					theOutcomeFormula = af;
				} else {
					theOutcomeFormula = new AndFormula(theOutcomeFormula, af);
				}
			} else {
				if (o != null) {
					AlloyFormula af = processObjectToFormula(o, mapConcreteToExpre, mapConcreteToActualName, alreadyVisited);
					if (theOutcomeFormula == null) {
						theOutcomeFormula = af;
					} else {
						theOutcomeFormula = new AndFormula(theOutcomeFormula, af);
					}
				}
			}
		}

		return theOutcomeFormula;

//		HashMap<Object, AlloyExpression> mapConcreteToExpre = new HashMap<Object, AlloyExpression>();
//		String[] metSplit = this.specFromMethod.split("_");
//		String methodName = metSplit[metSplit.length - 1];
//		if (inputToFix2.get("thiz") != null) {
//			Method[] methods = inputToFix2.get("thiz").getClass().getDeclaredMethods();
//			int numPars = 0;
//			Method theMethod = null;
//			for (Method m : methods) {
//				if (m.getName().equals(methodName)) {
//					numPars = m.getParameterTypes().length;
//					theMethod = m;
//					break;
//				}
//			}
//		}
//
//		AlloyFormula fixedInputFormula = null;
//		int index = 0;
//		for (JVariableDeclaration vd : formalParameterNames){
//			if (!vd.getVariable().getVariableId().getString().equals("throw")
//					&& !vd.getVariable().getVariableId().getString().equals("return")
//					&& !vd.getVariable().getVariableId().getString().startsWith("customvar")){
//
//				if (isJavaArithmetic) {
//					if (vd.getType().toString().equals("JavaPrimitiveIntegerValue")) {
//						AlloyFormula af = new EqualsFormula(
//								new ExprVariable(vd.getVariable()),
//								new ExprConstant("JavaPrimitiveIntegerValue",
//										JavaPrimitiveIntegerValue.getInstance().toJavaPrimitiveIntegerLiteral(((Integer)(inputToFix2.get(vd.getVariable().getVariableId().getString()))).intValue(), true).getConstantId()
//								)
//						);
//						if (fixedInputFormula == null)
//							fixedInputFormula = af;
//						else
//							fixedInputFormula = new AndFormula(fixedInputFormula, af);
//					}
//					if (vd.getType().toString().equals("JavaPrimitiveLongValue")) {
//						AlloyFormula af = new EqualsFormula(
//								new ExprVariable(vd.getVariable()),
//								new ExprConstant("JavaPrimitiveLongValue",
//										JavaPrimitiveLongValue.getInstance().toJavaPrimitiveLongLiteral(((Long)(inputToFix2.get(vd.getVariable().getVariableId().getString()))).longValue(), true).getConstantId()
//								)
//						);
//						if (fixedInputFormula == null)
//							fixedInputFormula = af;
//						else
//							fixedInputFormula = new AndFormula(fixedInputFormula, af);
//
//					}
//					if (vd.getType().toString().equals("JavaPrimitiveFloatValue")) {
//						AlloyFormula af = new EqualsFormula(
//								new ExprVariable(vd.getVariable()),
//								new ExprConstant("JavaPrimitiveFloatValue",
//										JavaPrimitiveFloatValue.getInstance().toJavaPrimitiveFloatLiteral(((Float)(inputToFix2.get(vd.getVariable().getVariableId().getString()))).floatValue(), true).getConstantId()
//								)
//						);
//						if (fixedInputFormula == null)
//							fixedInputFormula = af;
//						else
//							fixedInputFormula = new AndFormula(fixedInputFormula, af);
//
//					}
//					if (vd.getType().toString().equals("JavaPrimitiveCharValue")) {
//						AlloyFormula af = new EqualsFormula(
//								new ExprVariable(vd.getVariable()),
//								new ExprConstant("JavaPrimitiveCharValue",
//										JavaPrimitiveCharValue.getInstance().toJavaPrimitiveCharLiteral(((Character)(inputToFix2.get(vd.getVariable().getVariableId().getString()))).charValue(), true).getConstantId()
//								)
//						);
//						if (fixedInputFormula == null)
//							fixedInputFormula = af;
//						else
//							fixedInputFormula = new AndFormula(fixedInputFormula, af);
//
//					}
//
//				} else {
//					if (vd.getType().toString().equals("Int")){
//						AlloyFormula af = new EqualsFormula(
//								new ExprVariable(vd.getVariable()),
//								new ExprConstant("Int", inputToFix2.get(vd.getVariable().getVariableId().getString()).toString())
//						);
//						if (fixedInputFormula == null)
//							fixedInputFormula = af;
//						else
//							fixedInputFormula = new AndFormula(fixedInputFormula, af);
//					}
//
//				}
//
//				if (!mapConcreteToExpre.containsKey(inputToFix2.get(vd.getVariable().getVariableId().getString()))) {
//					AlloyFormula f = null;
//					if (inputToFix2.get(vd.getVariable().getVariableId().getString()) != null){
//						mapConcreteToExpre.put(inputToFix2.get(vd.getVariable().getVariableId().getString()), new ExprVariable(vd.getVariable()));
//						f = processInputToFixToFormula(inputToFix2.get(vd.getVariable().getVariableId().getString()), mapConcreteToExpre, isJavaArithmetic);
//					} else {
//						f = new EqualsFormula(new ExprVariable(vd.getVariable()), new ExprConstant(vd.getType().toString(), "null"));
//					}
//					if (fixedInputFormula == null)
//						fixedInputFormula = f;
//					else
//						fixedInputFormula = new AndFormula(fixedInputFormula, f);
//				} else {
//					AlloyFormula aff = new EqualsFormula(new ExprVariable(vd.getVariable()), mapConcreteToExpre.get(inputToFix2.get(vd.getVariable().getVariableId().getString())));
//					if (fixedInputFormula == null)
//						fixedInputFormula = aff;
//					else
//						fixedInputFormula = new AndFormula(fixedInputFormula, aff);
//				}
//
//				index++;
//
//			}
//		}
//
//		Object[] obs = mapConcreteToExpre.keySet().toArray();
//
//		for (int index1 = 0; index1 < obs.length; index1++){
//			for (int index2 = index1+1; index2 < obs.length; index2++){
//				if (obs[index1] == obs[index2]){
//					AlloyFormula f = new EqualsFormula(mapConcreteToExpre.get(obs[index1]), mapConcreteToExpre.get(obs[index2]));
//					if (fixedInputFormula == null)
//						fixedInputFormula = f;
//					else
//						fixedInputFormula = new AndFormula(fixedInputFormula, f);
//				} else {
//					AlloyFormula f = new NotFormula(new EqualsFormula(mapConcreteToExpre.get(obs[index1]), mapConcreteToExpre.get(obs[index2])));
//					if (fixedInputFormula == null)
//						fixedInputFormula = f;
//					else
//						fixedInputFormula = new AndFormula(fixedInputFormula, f);
//				}
//			}
//		}
//		Vector<Object> resultVec = new Vector<Object>();
//		resultVec.add(fixedInputFormula);
//		return resultVec;

	}

	private AlloyFormula processObjectToFormula(Object o, HashMap<Object, AlloyExpression> mapConcreteToExpre, HashMap<Object, AlloyExpression> mapConcreteToActualName, Set<Object> alreadyVisited) {
		AlloyFormula af = null;
		for (Field f : o.getClass().getDeclaredFields()){
			Object dest = null;
			try {
				f.setAccessible(true);
				dest = f.get(o);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			if (!alreadyVisited.contains(dest)) {
				alreadyVisited.add(dest);
				if (dest != null && isNotNumericType(dest.getClass())) {
					AlloyFormula theFormula = processObjectToFormula(dest, mapConcreteToExpre, mapConcreteToActualName, alreadyVisited);
					if (theFormula != null) {
						if (af == null) {
							af = theFormula;
						} else {
							af = new AndFormula(af, theFormula);
						}
					}
				} else {
					if (dest == null) {
						AlloyFormula equals = new EqualsFormula(mapConcreteToActualName.get(dest), mapConcreteToExpre.get(dest));
						if (af == null) {
							af = equals;
						} else {
							af = new AndFormula(equals, af);
						}
					} else {
						AlloyFormula equals = new EqualsFormula(mapConcreteToActualName.get(dest), mapConcreteToExpre.get(dest));
						if (af == null) {
							af = equals;
						} else {
							af = new AndFormula(equals, af);
						}
					}
				}
			}
		}
		return af;
	}

	private boolean isNotNumericType(Class<?> aClass) {
		return !aClass.isPrimitive() && !Number.class.isAssignableFrom(aClass);
	}

	private void processObjectToFixActualName(Object o, AlloyExpression e, HashMap<Object, AlloyExpression> mapConcreteToActualName) {
		//We know o is Object
		for (Field f : o.getClass().getDeclaredFields()){
			try {
				f.setAccessible(true);
				Object newO = f.get(o);
				if (o != null && !mapConcreteToActualName.keySet().contains(newO)) {
					String fullyqualifiedfieldname = (f.getDeclaringClass().getName() + "_" +  f.getName()).replace('.', '_');
					AlloyExpression theName = new ExprJoin(e, new ExprVariable(new AlloyVariable(fullyqualifiedfieldname)));
					mapConcreteToActualName.put(newO, theName);
					if (newO != null && isNotNumericType(newO.getClass())) {
						processObjectToFixActualName(newO, theName, mapConcreteToActualName);
					}
				}
			} catch (IllegalAccessException ae){
				ae.printStackTrace();
			}
		}
	}

	private void processAlloyIntegerToFix(Integer i, HashMap<Object, AlloyExpression> mapConcreteToExpre) {
		if (!mapConcreteToExpre.keySet().contains(i)) {
			ExprIntLiteral alloyInt = new ExprIntLiteral(i);
//			ExprConstant alloyInt = new ExprConstant("Int", i.toString());
			mapConcreteToExpre.put(i, alloyInt);
		}
	}

	private void processObjectToFix(Object o, HashMap<Object, AlloyExpression> mapConcreteToExpre) {
		if (!mapConcreteToExpre.keySet().contains(o)) {
			if (o == null){
				mapConcreteToExpre.put(o, new ExprConstant("null", "null"));
				return;
			}
			mapConcreteToExpre.put(o, new ExprConstant(o.getClass().getName(), "null"));
			for (Field f : o.getClass().getDeclaredFields()){
				try {
					f.setAccessible(true);
					if (f.getType().getName().equals("int") || f.getType().getName().equals("java.lang.Integer")){
						processIntegerToFix((Integer)f.get(o), mapConcreteToExpre);
					} else {
						if (f.getType().getName().equals("float") || f.getType().getName().equals("java.lang.Float")){
							processFloatToFix((Float)f.get(o), mapConcreteToExpre);
						} else {
							if (f.getType().getName().equals("long") || f.getType().getName().equals("java.lang.Long")){
								processLongToFix((Long)f.get(o), mapConcreteToExpre);
							} else {
								if (!f.getType().isPrimitive()) {
									processObjectToFix(f.get(o), mapConcreteToExpre);
								}
							}
						}
					}
					//add others
				} catch (IllegalAccessException e){
					e.printStackTrace();
				}
			}
		}
	}

	private String getModuleId(Object o) {
		return o.getClass().getName();
	}

	private void processFloatToFix(Float aFloat, HashMap<Object, AlloyExpression> mapConcreteToExpre) {
		if (!mapConcreteToExpre.keySet().contains(aFloat)) {
			ExprConstant alloyFloat = new ExprConstant("JavaPrimitiveFloatValue", JavaPrimitiveFloatValue.getInstance().toJavaPrimitiveFloatLiteral((Float) aFloat, false).getConstantId());
			mapConcreteToExpre.put(aFloat, alloyFloat);
		}
	}

	private void processLongToFix(Long aLong, HashMap<Object, AlloyExpression> mapConcreteToExpre) {
		if (!mapConcreteToExpre.keySet().contains(aLong)) {
			ExprConstant alloyLong = new ExprConstant("JavaPrimitiveLongValue", JavaPrimitiveLongValue.getInstance().toJavaPrimitiveLongLiteral((Long) aLong, false).getConstantId());
			mapConcreteToExpre.put(aLong, alloyLong);
		}
	}

	private void processIntegerToFix(Integer i, HashMap<Object, AlloyExpression> mapConcreteToExpre) {
		if (!mapConcreteToExpre.keySet().contains(i)) {
			if (TacoConfigurator.getInstance().getUseJavaArithmetic()) {
				ExprConstant alloyInteger = new ExprConstant("JavaPrimitiveIntegerValue", JavaPrimitiveIntegerValue.getInstance().toJavaPrimitiveIntegerLiteral((Integer) i, false).getConstantId());
				mapConcreteToExpre.put(i, alloyInteger);
			} else {
				ExprIntLiteral alloyInteger = new ExprIntLiteral(i);
				mapConcreteToExpre.put(i, alloyInteger);
			}
		}
	}


	/**
	 * Pushes {@code \result} as {@code return}, or {@code return'} in the post-state.
	 *
	 * @param jmlResultExpression the expression
	 */
	@Override
	public void visitJmlResultExpression(JmlResultExpression jmlResultExpression) {
		String returnVarId = JExpressionFactory.RETURN_VARIABLE.toString();
		ExprVariable returnExpr = JmlExpressionSolver.buildInstantVariable(
				returnVarId, instant);

		super.getStack().push(returnExpr);
	}

	/**
	 * Translates {@code signals (E e) P} into {@code instanceOf[throw', E] => P}, with {@code E}
	 * the exception class's signature, and pushes it ({@code \not_specified} gives the left side
	 * alone). {@code P} is translated in the visitor's current state, which for a method's
	 * specification is the pre-state unless an {@code \old} left it in the post-state, and the
	 * variable {@code e} is not bound.
	 *
	 * @param jmlSignalsClause the clause
	 */
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

	/**
	 * Translates {@code signals_only E1, ...} into
	 * {@code throw' = null and (instanceOf[throw', E1Lit] or ...)} while the normal-behavior flag
	 * is set ({@link #isNormalBehavior()}: by default, and in {@code normal_behavior} spec cases)
	 * and into {@code throw' != null and (...)} otherwise, and pushes it. Each {@code EiLit} is
	 * the literal signature of the exception class, not the class's signature as in
	 * {@link #visitJmlSignalsClause}. Pushes nothing if the clause lists no exceptions.
	 *
	 * @param jmlSignalsOnlyClause the clause
	 */
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

	/**
	 * Translates a JML quantifier: {@code \forall T x; R; B} into {@code all x: T | R => B},
	 * {@code \exists} into {@code some x: T | R and B}, and {@code \sum} into an Alloy sum
	 * ({@code ExprSum}) of the body, without the range. The bound is {@code T} without
	 * {@code null}, unless {@code quantifierIncludesNull} is set. While the range and body are
	 * translated, the quantified names are in {@code notAllowsPrimedState}, so they stay unprimed
	 * and arithmetic on them is a function application, not a fresh variable.
	 *
	 * <p>With {@code useJavaArithmetic}, in a {@code \forall} or {@code \exists}, the fresh
	 * variables that {@code R} and {@code B} add to the requires/ensures lists (in a contract
	 * translation) are quantified inside it, {@code all v... | preds and bounds => F}, and do not
	 * stay in the lists: those are set aside before and restored after. For {@code \sum} they are
	 * cleared before and not restored, so what the clause had collected before the sum is lost.
	 * Each bound limits a result variable to {@code numericRangeLower..numericRangeUpper}
	 * ({@link TacoConfigurator#getLowerBound()}, {@link TacoConfigurator#getUpperBound()}), and
	 * for a quantifier over an {@code int} or {@code long} variable the literals of that interval
	 * are registered in the {@code JavaPrimitiveIntegerValue} or {@code JavaPrimitiveLongValue}
	 * singleton (static state).
	 *
	 * @param jmlSpecQuantifiedExpression the quantified expression
	 * @throws TacoNotImplementedYetException for other quantifiers ({@code \min}, {@code \max},
	 *     {@code \product}, {@code \num_of})
	 */
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


	/**
	 * Translates the expression of a store reference (an {@code assignable} location) with a new
	 * visitor in the pre-state, and pushes it.
	 *
	 * @param jmlStoreRefExpression the store reference
	 */
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

	/**
	 * Pushes {@code \type(T)} as the constant naming {@code T}'s signature, without {@code null}.
	 *
	 * @param jmlTypeExpression the expression
	 */
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


	/**
	 * Translates a call inside a JML expression into an Alloy function application, not a program
	 * call, and pushes it. Some calls have fixed translations: {@code isEmpty} and
	 * {@code int_size} on {@code JMLObjectSet} and {@code JMLObjectSequence}; {@code has} and
	 * {@code get}, whatever the receiver's type; {@code contains} and {@code size} on
	 * {@code java.util.Set} or {@code HashSet} (over the primed set contents in the post-state);
	 * and {@code Float.isNaN}, only with {@code useJavaArithmetic}. Any other call becomes
	 * {@code m[receiver, args...]}, named by the bare method name. The receiver is the translated
	 * prefix, {@code none} for a call without one; a {@code this} prefix goes through
	 * {@link #visitThisExpression}, not through {@code helpers.ExpressionSolver.getLeftExpression}
	 * as in {@code ExpressionVisitor.visitMethodCallExpression} (see docs/OPEN-QUESTIONS.md, item
	 * 25). {@code int_size} registers the literals 0 to 2^(b-1) - 1, {@code b} the configured bit
	 * width, in the {@code JavaPrimitiveIntegerValue} singleton (static state), even without Java
	 * arithmetic.
	 *
	 * @param jMethodCall the call
	 * @throws TacoException for {@code isEmpty}, {@code int_size}, {@code contains}, {@code size}
	 *     or {@code isNaN} on another receiver type, and for {@code isNaN} without
	 *     {@code useJavaArithmetic}
	 */
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

	/**
	 * Translates a local variable or parameter: unprimed in the pre-state and for the variables of
	 * an enclosing quantifier, primed ({@code x'}, marked as coming from the contract) otherwise.
	 *
	 * @param jLocalVariableExpression the variable
	 */
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

	/**
	 * Pushes the receiver: {@code thiz} in the pre-state (as {@code ExpressionVisitor} does),
	 * {@code thiz'} in the post-state. For the receiver of a call translated with this visitor in
	 * the pre-state, {@code helpers.ExpressionSolver.getLeftExpression} gives the opposite; see
	 * docs/OPEN-QUESTIONS.md, item 25.
	 *
	 * @param jThisExpression the expression
	 */
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



	/**
	 * Returns the typing of the fresh variables of the arithmetic operations translated while
	 * {@link #isContractTranslation()} was false (invariants; JML expressions in method bodies);
	 * in a contract translation they go to
	 * {@code getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures()} instead. The field
	 * is inherited from {@link JDynAlloyASTVisitor}, which replaces it before each invariant
	 * ({@code setVarsAndTheirTypeFromMathOperations}) and makes fields of the class from its
	 * variables; {@link BlockStatementsVisitor} reads it only for ghost initializers.
	 *
	 * @return the typing (not a copy)
	 */
	public AlloyTyping getVarsAndTheirTypeFromMathOperations(){
		return this.varsAndTheirTypeFromMathOperations;
	}

	/**
	 * Returns the predicates that define the variables of
	 * {@link #getVarsAndTheirTypeFromMathOperations()}.
	 *
	 * @return the list (not a copy)
	 */
	public List<AlloyFormula> getPredsFromMathOperations(){
		return predsFromMathOperations;
	}



	public void setPredsFromMathOperations(List<AlloyFormula> l){
		this.predsFromMathOperations = l; 
	}


	public void setVarsAndTheirTypeFromMathOperations(AlloyTyping at){
		this.varsAndTheirTypeFromMathOperations = at; 
	}



	/**
	 * Pushes an integer literal: an {@code int} or {@code long} value, with
	 * {@code useJavaArithmetic}, as the literal registered in the
	 * {@code JavaPrimitiveIntegerValue} or {@code JavaPrimitiveLongValue} singleton (static state,
	 * kept between runs in one JVM); otherwise as an Alloy integer ({@code negate} for a negative
	 * one). Boolean and string ordinal literals are translated as such. Unlike
	 * {@code ExpressionVisitor}'s, it has no case for {@code char} values.
	 *
	 * @param jOrdinalLiteral the literal
	 * @throws TacoException for another literal type ({@code char} with {@code useJavaArithmetic}
	 *     included)
	 * @throws TacoNotImplementedYetException for an ordinal literal of another kind
	 */
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


	/**
	 * Sets the input to fix, which {@link #visitJmlGenericSpecCase} adds as a {@code requires} to
	 * each lightweight spec case that has one. {@code helpers.MethodDeclarationSolver} sets it,
	 * for the method under analysis only, in the second run of {@code checkAndRunSpecIfFaulty}
	 * ({@code TacoMain.isCheckAndRunSpecAssertionSupport}); the visitors that this one creates for
	 * single clauses do not get it. See docs/SPEC-OUTPUT.md.
	 *
	 * @param inputToFix a {@code HashMap<String, Object>} with the receiver under {@code "thiz"}
	 *     and one entry per parameter, or {@code null}
	 */
	public void setInputToFix(Object inputToFix) {
		this.inputToFix = inputToFix;
	}
}
