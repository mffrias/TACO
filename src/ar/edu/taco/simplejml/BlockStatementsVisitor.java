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

import static ar.uba.dc.rfm.alloy.AlloyVariable.buildAlloyVariable;
import static ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable.buildExprVariable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.jmlspecs.checker.JmlAssertStatement;
import org.jmlspecs.checker.JmlAssignmentStatement;
import org.jmlspecs.checker.JmlAssumeStatement;
import org.jmlspecs.checker.JmlLoopInvariant;
import org.jmlspecs.checker.JmlLoopStatement;
import org.jmlspecs.checker.JmlRelationalExpression;
import org.jmlspecs.checker.JmlSetStatement;
import org.jmlspecs.checker.JmlSpecExpression;
import org.jmlspecs.checker.JmlVariantFunction;
import org.multijava.mjc.CClassType;
import org.multijava.mjc.CNumericType;
import org.multijava.mjc.CType;
import org.multijava.mjc.CTypeVariable;
import org.multijava.mjc.Constants;
import org.multijava.mjc.JAddExpression;
import org.multijava.mjc.JArrayAccessExpression;
import org.multijava.mjc.JAssertStatement;
import org.multijava.mjc.JAssignmentExpression;
import org.multijava.mjc.JBreakStatement;
import org.multijava.mjc.JCatchClause;
import org.multijava.mjc.JCompoundAssignmentExpression;
import org.multijava.mjc.JDivideExpression;
import org.multijava.mjc.JDoStatement;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JExpressionStatement;
import org.multijava.mjc.JIfStatement;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.mjc.JMinusExpression;
import org.multijava.mjc.JModuloExpression;
import org.multijava.mjc.JMultExpression;
import org.multijava.mjc.JNewArrayExpression;
import org.multijava.mjc.JNewObjectExpression;
import org.multijava.mjc.JOrdinalLiteral;
import org.multijava.mjc.JParenthesedExpression;
import org.multijava.mjc.JPostfixExpression;
import org.multijava.mjc.JReturnStatement;
import org.multijava.mjc.JStringLiteral;
import org.multijava.mjc.JSwitchGroup;
import org.multijava.mjc.JSwitchStatement;
import org.multijava.mjc.JThisExpression;
import org.multijava.mjc.JThrowStatement;
import org.multijava.mjc.JTryCatchStatement;
import org.multijava.mjc.JTryFinallyStatement;
import org.multijava.mjc.JUnaryPromote;
import org.multijava.mjc.JVariableDeclarationStatement;
import org.multijava.mjc.JVariableDefinition;
import org.multijava.mjc.JWhileStatement;
import org.multijava.util.compiler.JavaStyleComment;
import org.multijava.util.compiler.UnpositionedError;

import ar.edu.jdynalloy.ast.JAssert;
import ar.edu.jdynalloy.ast.JAssignment;
import ar.edu.jdynalloy.ast.JAssume;
import ar.edu.jdynalloy.ast.JIfThenElse;
import ar.edu.jdynalloy.ast.JLoopInvariant;
import ar.edu.jdynalloy.ast.JSkip;
import ar.edu.jdynalloy.ast.JStatement;
import ar.edu.jdynalloy.ast.JVariableDeclaration;
import ar.edu.jdynalloy.ast.JavaPrimitiveIntValueArrayFactory;
import ar.edu.jdynalloy.ast.AlloyIntArrayFactory;
import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.jdynalloy.factory.JPredicateFactory;
import ar.edu.jdynalloy.factory.JSignatureFactory;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
import ar.edu.taco.TacoNotImplementedYetException;
import ar.edu.taco.jml.loop.HasBreakStatementVisitor;
import ar.edu.taco.jml.utils.LabelUtils;
import ar.edu.taco.simplejml.JmlBaseExpressionVisitor.Instant;
import ar.edu.taco.simplejml.builtin.AuxiliaryConstantsFactory;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveIntegerValue;
import ar.edu.taco.simplejml.builtin.AuxiliaryConstantsFactory.AddAuxiliaryConstants;
import ar.edu.taco.simplejml.builtin.AuxiliaryConstantsFactory.DivAuxiliaryConstants;
import ar.edu.taco.simplejml.builtin.AuxiliaryConstantsFactory.MinusAuxiliaryConstants;
import ar.edu.taco.simplejml.builtin.AuxiliaryConstantsFactory.MultAuxiliaryConstants;
import ar.edu.taco.simplejml.helpers.BlockStatementSolver;
import ar.edu.taco.simplejml.helpers.CTypeAdapter;
import ar.edu.taco.simplejml.helpers.ExpressionSolver;
import ar.edu.taco.simplejml.helpers.JavaOperatorSolver;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprIfCondition;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprIntLiteral;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.AndFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.EqualsFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.NotFormula;

public class BlockStatementsVisitor extends JDynAlloyASTVisitor {

	private Stack<AlloyExpression> expressions = new Stack<AlloyExpression>();

//	private AlloyTyping varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures = new AlloyTyping();
	
//	private List<AlloyFormula> predsEncodingValueOfArithmeticOperationsInRequiresAndEnsures = new ArrayList<AlloyFormula>();

//	public AlloyTyping varsAndTheirTypeFromMathOperations = new AlloyTyping();
		
//	public List<AlloyFormula> predsFromMathOperations = new ArrayList<AlloyFormula>();

	
	
	
	private Stack<ExprVariable> whileIndices = new Stack<ExprVariable>();

	private static int variantFunctionIndex = 0;

	/**
	 * @return the varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures
	 */
	public AlloyTyping getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures() {
		return varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures;
	}

	/**
	 * @param varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures the varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures to set
	 */
	public void setVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures(
			AlloyTyping varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures) {
		this.varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures = varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures;
	}



	int ifLabelCount = 10000;
	// int whileLabelCount = 10000;
	private static Logger log = Logger.getLogger(BlockStatementsVisitor.class);

	/**
	 * @return a JStatement that represent a JAlloyProgram.
	 */
	public JStatement getJAlloyProgram() {
		if (this.programBuffer.toJAlloyProgram() != null) {
			return this.programBuffer.toJAlloyProgram();
		} else {
			return new JSkip();
		}
	}

	@Override
	public void visitAssignmentExpression(JAssignmentExpression jAssignmentExpression) {
		jAssignmentExpression.accept(prettyPrint);
		log.debug("Visiting: " + jAssignmentExpression.getClass().getName());
		log.debug("Statement: " + prettyPrint.getPrettyPrint());

		ExpressionVisitor expressionVisitor = new ExpressionVisitor();

		jAssignmentExpression.left().accept(expressionVisitor);
		AlloyExpression leftSide = expressionVisitor.getAlloyExpression();
		expressionVisitor.setLeftAssignmentExpression(leftSide);

		// If the variable which is going to be modified is an instance
		// variable, then it should be added into de modified variables list
		if (leftSide instanceof ExprJoin && ((ExprJoin) leftSide).getLeft().equals(JExpressionFactory.THIS_EXPRESSION)) {
			this.instanceModifiedVariables.add(leftSide);
		}

		Object rightSide = null;

		if (jAssignmentExpression.right() instanceof JMinusExpression) {

			JMinusExpression minusExpression = (JMinusExpression) jAssignmentExpression.right();

			this.visitMinusExpression(minusExpression);

			rightSide = expressions.pop();
		} else if (jAssignmentExpression.right() instanceof JAddExpression) {

			JAddExpression addExpression = (JAddExpression) jAssignmentExpression.right();

			this.visitAddExpression(addExpression);

			rightSide = expressions.pop();
		} else if (jAssignmentExpression.right() instanceof JDivideExpression) {

			JDivideExpression divExpression = (JDivideExpression) jAssignmentExpression.right();

			this.visitDivideExpression(divExpression);

			rightSide = expressions.pop();

		} else if (jAssignmentExpression.right() instanceof JMultExpression) {

			JMultExpression multExpression = (JMultExpression) jAssignmentExpression.right();

			this.visitMultExpression(multExpression);

			rightSide = expressions.pop();

		} else if (jAssignmentExpression.right() instanceof JModuloExpression) {

			JModuloExpression moduloExpression = (JModuloExpression) jAssignmentExpression.right();

			this.visitModuloExpression(moduloExpression);

			rightSide = expressions.pop();

		} else {

			jAssignmentExpression.right().accept(expressionVisitor);
			if ((jAssignmentExpression.right() instanceof JMethodCallExpression) || jAssignmentExpression.right() instanceof JNewObjectExpression
					|| jAssignmentExpression.right() instanceof JNewArrayExpression) {
				rightSide = expressionVisitor.getAlloyProgram();
			} else {
				rightSide = expressionVisitor.getAlloyExpression();
			}
		}

		JStatement jStatement;

		if ((TacoConfigurator.getInstance().getUseJavaArithmetic() == true)
				&& (jAssignmentExpression.left() instanceof JArrayAccessExpression || jAssignmentExpression.right() instanceof JArrayAccessExpression)) {

			AlloyExpression left_side_expr = (AlloyExpression) leftSide;
			AlloyExpression right_side_expr = (AlloyExpression) rightSide;
			if (jAssignmentExpression.left() instanceof JArrayAccessExpression) {

				jStatement = JavaPrimitiveIntValueArrayFactory.array_write_stmt(left_side_expr, right_side_expr);
			} else {

				jStatement = JavaPrimitiveIntValueArrayFactory.array_read_stmt(left_side_expr, right_side_expr);

			}
		} else if ((TacoConfigurator.getInstance().getUseJavaArithmetic() == false)
				&& (jAssignmentExpression.left() instanceof JArrayAccessExpression || jAssignmentExpression.right() instanceof JArrayAccessExpression)){

			AlloyExpression left_side_expr = (AlloyExpression) leftSide;
			AlloyExpression right_side_expr = (AlloyExpression) rightSide;
			if (jAssignmentExpression.left() instanceof JArrayAccessExpression) {

				jStatement = AlloyIntArrayFactory.array_write_stmt(left_side_expr, right_side_expr);
			} else {

				jStatement = AlloyIntArrayFactory.array_read_stmt(left_side_expr, right_side_expr);
			}

		} else {
			if (rightSide instanceof AlloyExpression) {
				CTypeAdapter type_Adapter = new CTypeAdapter();
				CType leftSideType = jAssignmentExpression.left().getType();
				CType rightSideType = getType(jAssignmentExpression.right());
				JType left_alloy_type = type_Adapter.translate(leftSideType);
				JType right_alloy_type = type_Adapter.translate(rightSideType);
				if (leftSideType != rightSideType){
					AlloyExpression newRightSide = ExpressionSolver.getCastingExpression(left_alloy_type, right_alloy_type, (AlloyExpression)rightSide);
					jStatement = new JAssignment(leftSide, newRightSide);
				} else {
					jStatement = new JAssignment(leftSide, (AlloyExpression) rightSide);
				}


				//				jStatement = new JAssignment(leftSide, (AlloyExpression) rightSide);
			} else if (rightSide instanceof AlloyFormula) {
				jStatement = (new JIfThenElse((AlloyFormula) rightSide, new JAssignment(leftSide, JExpressionFactory.TRUE_EXPRESSION), new JAssignment(
						leftSide, JExpressionFactory.FALSE_EXPRESSION), LabelUtils.nextIfLabel()));
			} else if (rightSide instanceof JStatement /* AlloyProgram */) {
				jStatement = (JStatement) rightSide;
			} else
				throw new RuntimeException("Illegal condition");
		}


		AlloyFormula breakReached;
		if (!this.whileIndices.isEmpty() && this.whileIndices.peek() != null){
			breakReached = new EqualsFormula(
					this.whileIndices.firstElement(),
					JExpressionFactory.FALSE_EXPRESSION);
		} else {
			breakReached = new EqualsFormula(
					JExpressionFactory.TRUE_EXPRESSION,
					JExpressionFactory.TRUE_EXPRESSION);
		}
		if (this.isTryCatchBlock) {
			programBuffer.openIf(new AndFormula (BlockStatementSolver.getTryCatchSurrounderCondition(),breakReached));
		}

		programBuffer.appendProgram(jStatement);

		if (this.isTryCatchBlock) {
			programBuffer.closeIf();
		}
	}

	@Override
	public void visitCatchClause(JCatchClause jCatchClause) {
		AlloyExpression throwAlloyExpression = JExpressionFactory.THROW_EXPRESSION;

		// CTypeAdapter cTypeAdapter = new CTypeAdapter();
		// JType jType =
		// cTypeAdapter.translate(jCatchClause.exception().getType());
		// String signatureId = jType.toString();

		ExpressionVisitor expressionVisitor = new ExpressionVisitor();
		jCatchClause.exception().accept(expressionVisitor);

		JVariableDeclaration declaredVariable = (JVariableDeclaration) expressionVisitor.getAlloyProgram();
		JType jType = declaredVariable.getType();
		String signatureId = jType.toString();

		AlloyFormula alloyFormula = JPredicateFactory.instanceOf(throwAlloyExpression, signatureId);
		programBuffer.openIf(alloyFormula);

		declaredVariable.getType();
		programBuffer.appendProgram(declaredVariable);
		programBuffer.assign(declaredVariable.getVariable(), throwAlloyExpression);
		programBuffer.assign(JExpressionFactory.THROW_EXPRESSION, JExpressionFactory.NULL_EXPRESSION);

		BlockStatementsVisitor catchBlockScopeTranslator = new BlockStatementsVisitor();
		for (org.multijava.mjc.JStatement aStatement : jCatchClause.body().body()) {
			aStatement.accept(catchBlockScopeTranslator);
		}

		programBuffer.appendProgram(catchBlockScopeTranslator.getJAlloyProgram());

		programBuffer.closeIf();
	}




	@Override
	public void visitCompoundAssignmentExpression(JCompoundAssignmentExpression jCompoundAssignmentExpression) {
		jCompoundAssignmentExpression.accept(prettyPrint);
		log.debug("Visiting: " + jCompoundAssignmentExpression.getClass().getName());
		log.debug("Statement: " + prettyPrint.getPrettyPrint());

		ExpressionVisitor expressionVisitor = new ExpressionVisitor();

		// Get the left side of the expression
		jCompoundAssignmentExpression.left().accept(expressionVisitor);
		AlloyExpression leftSide = expressionVisitor.getAlloyExpression();

		jCompoundAssignmentExpression.right().accept(expressionVisitor);
		AlloyExpression rightSide = expressionVisitor.getAlloyExpression();

		// Create an array of alloy expression types

		CType left_type = jCompoundAssignmentExpression.left().getType();
		CType right_type = jCompoundAssignmentExpression.right().getType();

		CTypeAdapter ctype_adapter = new CTypeAdapter();

		JType left_alloy_type = ctype_adapter.translate(left_type);
		JType right_alloy_type = ctype_adapter.translate(right_type);

		JType[] expression_types = new JType[] { left_alloy_type, right_alloy_type };

		// Create an array of AlloyExpression
		AlloyExpression[] alloyExpressions = new AlloyExpression[] { leftSide, rightSide };

		AlloyExpression finalAlloyExpression = (AlloyExpression) JavaOperatorSolver.getAlloyBinaryExpression(expression_types, alloyExpressions,
				jCompoundAssignmentExpression.oper());

		JStatement jStatement = new JAssignment(leftSide, finalAlloyExpression);
		jStatement = BlockStatementSolver.getSurroundedStatement(jStatement, this.isTryCatchBlock);
		programBuffer.appendProgram(jStatement);

	}



	@Override
	public void visitAssertStatement(JAssertStatement self){
		self.accept(prettyPrint);
		log.debug("Visiting: " + self.getClass().getName());
		log.debug("Statement:\n" + prettyPrint.getPrettyPrint());

		ExpressionVisitor expressionVisitor = new ExpressionVisitor();
		JExpression cond = self.predicate();
		AlloyFormula alloyFormula = ExpressionSolver.getConditionAsAlloyFormula(expressionVisitor, cond);
		programBuffer.assertion(alloyFormula);
	}


	@Override
	public void visitIfStatement(JIfStatement jIfStatement) {
		jIfStatement.accept(prettyPrint);
		log.debug("Visiting: " + jIfStatement.getClass().getName());
		log.debug("Statement:\n" + prettyPrint.getPrettyPrint());

		if (this.isTryCatchBlock) {
			programBuffer.openIf(BlockStatementSolver.getTryCatchSurrounderCondition());
		}

		ExpressionVisitor expressionVisitor = new ExpressionVisitor();
		JExpression cond = jIfStatement.cond();
		//		cond.accept(expressionVisitor);
		//		AlloyFormula alloyFormula = expressionVisitor.getAlloyFormula();
		AlloyFormula alloyFormula = ExpressionSolver.getConditionAsAlloyFormula(expressionVisitor, cond);
		ArrayList<AlloyFormula> al = new ArrayList<AlloyFormula>();
		if (expressionVisitor.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures() != null){
			for (AlloyFormula af : expressionVisitor.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures()){
				al.add(af);
			}
		}
		if (this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures() != null){
			this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().addAll(al);
		} else {
			this.setPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures(al);
		}
		programBuffer.openIf(alloyFormula);

		jIfStatement.thenClause().accept(this);
		programBuffer.switchToElseIf();
		if (jIfStatement.elseClause() != null) {
			jIfStatement.elseClause().accept(this);
		} else {
			programBuffer.skip();
		}

		programBuffer.closeIf();

		if (this.isTryCatchBlock) {
			programBuffer.closeIf();
		}
	}

	@Override
	public void visitJmlAssertStatement(JmlAssertStatement jmlAssertStatement) {
		jmlAssertStatement.accept(prettyPrint);
		log.debug("Visiting: " + jmlAssertStatement.getClass().getName());
		log.debug("Statement: " + prettyPrint.getPrettyPrint());

		JmlExpressionVisitor jmlExpressionVisitor = new JmlExpressionVisitor(Instant.PRE_INSTANT);
		jmlAssertStatement.accept(jmlExpressionVisitor);

		AlloyFormula assertFormula = jmlExpressionVisitor.getAlloyFormula();
		JAssert jAssert = new JAssert(assertFormula);

		JStatement jStatement = BlockStatementSolver.getSurroundedStatement(jAssert, this.isTryCatchBlock);

		programBuffer.appendProgram(jStatement);

	}

	@Override
	public void visitJmlAssignmentStatement(JmlAssignmentStatement jmlAssignamentStatement) {
		jmlAssignamentStatement.accept(prettyPrint);
		log.debug("Visiting: " + jmlAssignamentStatement.getClass().getName());
		log.debug("Statement: " + prettyPrint.getPrettyPrint());

		JExpressionStatement expressionStatement = jmlAssignamentStatement.assignmentStatement();
		JAssignmentExpression assignmentExpression = (JAssignmentExpression) expressionStatement.getExpression();

		assignmentExpression.accept(this);
	}

	
	
	
	@Override
	public void visitJmlAssumeStatement(JmlAssumeStatement jmlAssumeStatement) {
		jmlAssumeStatement.accept(prettyPrint);
		log.debug("Visiting: " + jmlAssumeStatement.getClass().getName());
		log.debug("Statement: " + prettyPrint.getPrettyPrint());

		JmlExpressionVisitor jmlExpressionVisitor = new JmlExpressionVisitor(Instant.PRE_INSTANT);
		jmlAssumeStatement.accept(jmlExpressionVisitor);

		AlloyFormula assumeFormula = jmlExpressionVisitor.getAlloyFormula();
		JAssume jAssume = new JAssume(assumeFormula);

		JStatement jStatement = BlockStatementSolver.getSurroundedStatement(jAssume, this.isTryCatchBlock);

		programBuffer.appendProgram(jStatement);
	}

	public void visitJmlLoopStatement(JmlLoopStatement jmlLoopStatement) {
		jmlLoopStatement.accept(prettyPrint);
		log.debug("Visiting: " + jmlLoopStatement.getClass().getName());
		log.debug("Statement: " + prettyPrint.getPrettyPrint());

		JmlExpressionVisitor jmlExpressionVisitor = new JmlExpressionVisitor(Instant.PRE_INSTANT);

		if (jmlLoopStatement.loopInvariants() != null) {
			for (JmlLoopInvariant jmlLoopInvariant : jmlLoopStatement.loopInvariants()) {
				jmlLoopInvariant.accept(jmlExpressionVisitor);
				this.loopInvariants.push(jmlExpressionVisitor.getAlloyFormula());
			}
		}

		/** This requires the loopStatement to be while loop. Otherwise, a class cast exception will be thrown */
		JWhileStatement loopStatement = (JWhileStatement)jmlLoopStatement.loopStmt();
		org.multijava.mjc.JStatement whileBody = loopStatement.body();

		JmlVariantFunction[] varFunctions = jmlLoopStatement.variantFunctions();
		if (varFunctions.length > 0){
			CType type = varFunctions[0].specExpression().getApparentType();
			String newVarName = "variant_" + variantFunctionIndex;
			variantFunctionIndex++;
			JVariableDefinition variableVariantFunction = new JVariableDefinition(loopStatement.getTokenReference(), 0, type, newVarName, varFunctions[0].specExpression());
			JVariableDeclarationStatement theVariantFunctionVariableDeclaration = new JVariableDeclarationStatement(loopStatement.getTokenReference(), variableVariantFunction, new JavaStyleComment[]{});

			CClassType theExceptionType = new CTypeVariable("java.lang.RuntimeException", new CClassType[]{});
			try {
				theExceptionType.checkType(null);
			} catch (UnpositionedError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			JThrowStatement throwStmt = new JThrowStatement(
					loopStatement.getTokenReference(), 
					new JNewObjectExpression(
							jmlLoopStatement.getTokenReference(), 
							theExceptionType,
							new JThisExpression(jmlLoopStatement.getTokenReference()), 
							new JExpression[]{}),
					new JavaStyleComment[]{});
			JExpression expreZero = new JOrdinalLiteral(loopStatement.getTokenReference(), 0, (CNumericType)type);
			JExpression ifCond = new JmlRelationalExpression(jmlLoopStatement.getTokenReference(), OPE_LT, variableVariantFunction.expr(), expreZero);
			org.multijava.mjc.JStatement theIf = new JIfStatement(loopStatement.getTokenReference(), ifCond, throwStmt, null, null);
			JExpression ifCond2 = new JmlRelationalExpression(jmlLoopStatement.getTokenReference(), OPE_GE, varFunctions[0].specExpression(), new JLocalVariableExpression(jmlLoopStatement.getTokenReference(), variableVariantFunction));
			JThrowStatement throwStmt2 = new JThrowStatement(
					loopStatement.getTokenReference(), 
					new JNewObjectExpression(
							jmlLoopStatement.getTokenReference(), 
							theExceptionType,
							new JThisExpression(jmlLoopStatement.getTokenReference()), 
							new JExpression[]{}),
					new JavaStyleComment[]{});
			org.multijava.mjc.JStatement theIf2 = new JIfStatement(loopStatement.getTokenReference(), ifCond2, throwStmt2, null, null);

			org.multijava.mjc.JStatement newLoopBody = new org.multijava.mjc.JBlock(jmlLoopStatement.getTokenReference(), new org.multijava.mjc.JStatement[]{theVariantFunctionVariableDeclaration, theIf, whileBody, theIf2}, new JavaStyleComment[]{});
			JWhileStatement newLoopStatement = new JWhileStatement(jmlLoopStatement.getTokenReference(), loopStatement.cond(), newLoopBody, jmlLoopStatement.getComments());
			newLoopStatement.accept(this);
		} else {
			jmlLoopStatement.loopStmt().accept(this);
		}
	}

	@Override
	public void visitJmlSetStatement(JmlSetStatement jmlSetStatement) {
		jmlSetStatement.assignmentExpression().accept(this);
	}

	@Override
	public void visitMethodCallExpression(JMethodCallExpression jMethodCallExpression) {
		jMethodCallExpression.accept(prettyPrint);
		log.debug("Visiting: " + jMethodCallExpression.getClass().getName());
		log.debug("Statement: " + prettyPrint.getPrettyPrint());

		ExpressionVisitor expressionVisitor = new ExpressionVisitor();
		jMethodCallExpression.accept(expressionVisitor);

		if (this.isTryCatchBlock) {
			programBuffer.openIf(BlockStatementSolver.getTryCatchSurrounderCondition());
		}

		programBuffer.appendProgram(expressionVisitor.getAlloyProgram());

		if (this.isTryCatchBlock) {
			programBuffer.closeIf();
		}	
	}

	@Override
	public void visitPostfixExpression(JPostfixExpression jPostfixExpression) {
		jPostfixExpression.accept(prettyPrint);
		log.debug("Visiting: " + jPostfixExpression.getClass().getName());
		log.debug("Statement: " + prettyPrint.getPrettyPrint());

		ExpressionVisitor expressionVisitor = new ExpressionVisitor();

		// Get the left side of the expression
		jPostfixExpression.expr().accept(expressionVisitor);
		AlloyExpression leftSide = expressionVisitor.getAlloyExpression();

		// Create an array of AlloyExpression
		AlloyExpression[] alloyExpressions = new AlloyExpression[2];
		alloyExpressions[0] = leftSide;
		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
			alloyExpressions[1] = JavaPrimitiveIntegerValue.getInstance().toJavaPrimitiveIntegerLiteral(1,false);
		} else {
			alloyExpressions[1] = new ExprIntLiteral(1);
		}

		AlloyExpression finalAlloyExpression = null;
		switch (jPostfixExpression.oper()) {
		case Constants.OPE_POSTINC: {
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
				finalAlloyExpression = JExpressionFactory.fun_java_primitive_integer_value_add(alloyExpressions);
			} else {
				finalAlloyExpression = JExpressionFactory.alloy_int_add(alloyExpressions);
			}
			break;
		}
		case Constants.OPE_POSTDEC:
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
				finalAlloyExpression = JExpressionFactory.fun_java_primitive_integer_value_sub(alloyExpressions);
			} else {
				finalAlloyExpression = JExpressionFactory.alloy_int_sub(alloyExpressions);
			}
		}

		JStatement jStatement = new JAssignment(leftSide, finalAlloyExpression);
		jStatement = BlockStatementSolver.getSurroundedStatement(jStatement, this.isTryCatchBlock);
		programBuffer.appendProgram(jStatement);
	}

	@Override
	public void visitReturnStatement(JReturnStatement returnStatement) {
		returnStatement.accept(prettyPrint);
		log.debug("Visiting: " + returnStatement.getClass().getName());
		log.debug("Statement: " + prettyPrint.getPrettyPrint());

		if (returnStatement.expr() != null) {
			ExpressionVisitor expressionVisitor = new ExpressionVisitor();
			returnStatement.expr().accept(expressionVisitor);
			AlloyVariable returnVariable = AlloyVariable.buildAlloyVariable("return");
			AlloyExpression returnValue = null;
			if (expressionVisitor.isAlloyExpression()) {
				returnValue = expressionVisitor.getAlloyExpression();
				if (returnStatement.expr().getApparentType().isPrimitive() && ExpressionSolver.getType(returnStatement.expr()).isPrimitive() && returnStatement.expr().getApparentType() != ExpressionSolver.getType(returnStatement.expr())){
					String fromType = ExpressionSolver.getType(returnStatement.expr()).toString();
					String toType = returnStatement.expr().getApparentType().toString();
					returnValue = castExpression(returnValue, fromType, toType);
				}
			} else {
				returnValue = new ExprIfCondition(expressionVisitor.getAlloyFormula(), JExpressionFactory.TRUE_EXPRESSION, JExpressionFactory.FALSE_EXPRESSION);
			}

			if (this.isTryCatchBlock) {
				programBuffer.openIf(BlockStatementSolver.getTryCatchSurrounderCondition());
			}

			programBuffer.assign(returnVariable, returnValue);
			programBuffer.assign(JExpressionFactory.EXIT_REACHED_VARIABLE, JExpressionFactory.TRUE_EXPRESSION);

			if (this.isTryCatchBlock) {
				programBuffer.closeIf();
			}
		}

		// this.isReturnPresent.push(true);
	}

	@Override
	public void visitSwitchStatement(JSwitchStatement switchStatement) {
		switchStatement.accept(prettyPrint);
		log.debug("Visiting: " + switchStatement.getClass().getName());
		log.debug("Statement: " + prettyPrint.getPrettyPrint());

		if (this.isTryCatchBlock) {
			programBuffer.openIf(BlockStatementSolver.getTryCatchSurrounderCondition());
		}

		ExpressionVisitor expressionVisitor = new ExpressionVisitor();
		JExpression expr = switchStatement.expr();
		expr.accept(expressionVisitor);
		AlloyFormula alloyFormula = expressionVisitor.getAlloyFormula();
		ArrayList<AlloyFormula> al = new ArrayList<AlloyFormula>();
		if (expressionVisitor.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures() != null){
			for (AlloyFormula af : expressionVisitor.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures()){
				al.add(af);
			}
		}
		if (this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures() != null){
			this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().addAll(al);
		} else {
			this.setPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures(al);
		}
//		programBuffer.openIf(alloyFormula);

		
		for (JSwitchGroup j : switchStatement.groups()){
			j.accept(this);
		}
		
//		programBuffer.switchToElseIf();
//		if (jIfStatement.elseClause() != null) {
//			jIfStatement.elseClause().accept(this);
//		} else {
//			programBuffer.skip();
//		}
//
//		programBuffer.closeIf();

		if (this.isTryCatchBlock) {
			programBuffer.closeIf();
		}
	}

	@Override
	public void visitSwitchGroup(JSwitchGroup switchGroup) {
		switchGroup.accept(prettyPrint);
		log.debug("Visiting: " + switchGroup.getClass().getName());
		log.debug("Statement: " + prettyPrint.getPrettyPrint());

		if (this.isTryCatchBlock) {
			programBuffer.openIf(BlockStatementSolver.getTryCatchSurrounderCondition());
		}

		org.multijava.mjc.JStatement[] js = switchGroup.getStatements();
		for (org.multijava.mjc.JStatement j : js){
			j.accept(this);
		}
		
		if (this.isTryCatchBlock) {
			programBuffer.closeIf();
		}
	

	}

	private AlloyExpression castExpression(AlloyExpression returnValue, String fromType, String toType) {
		if (fromType.equals("char") && toType.equals("int")){ 
			return (AlloyExpression) JExpressionFactory.fun_java_primitive_char_value_to_int_value(returnValue);
		}
		if (fromType.equals("char") && toType.equals("long")){ 
			return (AlloyExpression) JExpressionFactory.fun_java_primitive_char_value_to_long_value(returnValue);
		}
		if (fromType.equals("int") && toType.equals("long")){ 
			return (AlloyExpression) JExpressionFactory.fun_java_primitive_int_value_to_long_value(returnValue);
		}
		if (fromType.equals("int") && toType.equals("char")){ 
			return (AlloyExpression) JExpressionFactory.fun_java_primitive_int_value_to_char_value(returnValue);
		}
		if (fromType.equals("long") && toType.equals("char")){ 
			return (AlloyExpression) JExpressionFactory.fun_java_primitive_long_value_to_char_value(returnValue);
		}
		if (fromType.equals("long") && toType.equals("int")){ 
			return (AlloyExpression) JExpressionFactory.fun_java_primitive_long_value_to_int_value(returnValue);
		}

		return null;


	}

	@Override
	public void visitThrowStatement(JThrowStatement jThrowStatement) {
		jThrowStatement.accept(prettyPrint);
		log.debug("Visiting: " + jThrowStatement.getClass().getName());
		log.debug("Statement: " + prettyPrint.getPrettyPrint());

		ExpressionVisitor expressionVisitor = new ExpressionVisitor();
		AlloyVariable lvalue = JExpressionFactory.THROW_VARIABLE;
		expressionVisitor.setLeftAssignmentExpression(ExprVariable.buildExprVariable(lvalue));

		jThrowStatement.expr().accept(expressionVisitor);
		// if it is an then I need to assign that expression to the Throw
		// variable, otherwise there is a
		// object creation statement, so I need to add the corresponding object
		// creation and constructor call statements to the program.
		if (expressionVisitor.isAlloyExpression()) {
			AlloyExpression rvalue = expressionVisitor.getAlloyExpression();
			programBuffer.assign(lvalue, rvalue);
			programBuffer.assign(JExpressionFactory.EXIT_REACHED_VARIABLE, JExpressionFactory.TRUE_EXPRESSION);
		} else {
			JStatement objectCreationStatements = expressionVisitor.getAlloyProgram();
			programBuffer.appendProgram(objectCreationStatements);
		}
	}

	@Override
	public void visitTryCatchStatement(JTryCatchStatement jTryCatchStatement) {
		jTryCatchStatement.accept(prettyPrint);
		log.debug("Visiting: " + jTryCatchStatement.getClass().getName());
		log.debug("Statement: " + prettyPrint.getPrettyPrint());

		BlockStatementsVisitor blockScopeTranslator = new BlockStatementsVisitor();
		// blockScopeTranslator.isTryCatchBlock = true;
		jTryCatchStatement.tryClause().accept(blockScopeTranslator);
		// blockScopeTranslator.isTryCatchBlock = false;

		programBuffer.appendProgram(blockScopeTranslator.getJAlloyProgram());

		AlloyFormula condition = new EqualsFormula(JExpressionFactory.THROW_EXPRESSION, JExpressionFactory.NULL_EXPRESSION);
		AlloyFormula notCondition = new NotFormula(condition);

		programBuffer.openIf(notCondition);

		for (int x = 0; x < jTryCatchStatement.catchClauses().length; x++) {
			jTryCatchStatement.catchClauses()[x].accept(this);

		}

		programBuffer.closeIf();

	}

	@Override
	public void visitTryFinallyStatement(JTryFinallyStatement jTryFinallyStatement) {
		jTryFinallyStatement.accept(prettyPrint);
		log.debug("Visiting: " + jTryFinallyStatement.getClass().getName());
		log.debug("Statement: " + prettyPrint.getPrettyPrint());

		jTryFinallyStatement.tryClause().accept(this);

		if (jTryFinallyStatement.finallyClause() != null) {
			BlockStatementsVisitor blockStatementsTranslator = new BlockStatementsVisitor();
			jTryFinallyStatement.finallyClause().accept(blockStatementsTranslator);
			programBuffer.appendProgram(blockStatementsTranslator.getJAlloyProgram());
		}
	}

	@Override
	public void visitVariableDefinition(JVariableDefinition jVariableDefinition) {
		jVariableDefinition.accept(prettyPrint);
		log.debug("Visiting: " + jVariableDefinition.getClass().getName());
		log.debug("Statement: " + prettyPrint.getPrettyPrint());

		// Create an AlloyVariable from variable name
		AlloyVariable alloy_variable = buildAlloyVariable(jVariableDefinition.ident());

		// extract the variable type and convert it to an Alloy variable type.
		CTypeAdapter cTypeAdapter = new CTypeAdapter();
		JType variableType = cTypeAdapter.translate(jVariableDefinition.getType());

		// Declare the variable into Alloy program buffer.
		programBuffer.declare(alloy_variable, variableType);

		// If the declared variable has a initial value assigned, the we need to
		// parse this value,
		// for that purpose we use the ExpressionVisitior.
		if (jVariableDefinition.expr() != null) {
			if (this.isTryCatchBlock) {
				programBuffer.openIf(BlockStatementSolver.getTryCatchSurrounderCondition());
			}

			ExpressionVisitor expressionVisitor = new ExpressionVisitor();

			AlloyExpression variableAsExpression = AlloyExpression.asAlloyExpression(Collections.singletonList(alloy_variable)).get(0);
			expressionVisitor.setLeftAssignmentExpression(variableAsExpression);

			// If the variable which is going to be modified is an instance
			// variable, then it should be added into de modified variables list
			if (variableAsExpression instanceof ExprJoin && ((ExprJoin) variableAsExpression).getLeft().equals(JExpressionFactory.THIS_EXPRESSION)) {
				this.instanceModifiedVariables.add(variableAsExpression);
			}

			if (jVariableDefinition.expr() instanceof JMinusExpression) {

				JMinusExpression minusExpression = (JMinusExpression) jVariableDefinition.expr();

				this.visitMinusExpression(minusExpression);

				AlloyExpression initializer = expressions.pop();
				programBuffer.assign(alloy_variable, initializer);

			} else if (jVariableDefinition.expr() instanceof JAddExpression) {

				JAddExpression addExpression = (JAddExpression) jVariableDefinition.expr();

				this.visitAddExpression(addExpression);

				AlloyExpression initializer = expressions.pop();
				programBuffer.assign(alloy_variable, initializer);

			} else if (jVariableDefinition.expr() instanceof JDivideExpression) {

				JDivideExpression divExpression = (JDivideExpression) jVariableDefinition.expr();

				this.visitDivideExpression(divExpression);

				AlloyExpression initializer = expressions.pop();
				programBuffer.assign(alloy_variable, initializer);

			} else if (jVariableDefinition.expr() instanceof JMultExpression) {

				JMultExpression multExpression = (JMultExpression) jVariableDefinition.expr();

				this.visitMultExpression(multExpression);

				AlloyExpression initializer = expressions.pop();
				programBuffer.assign(alloy_variable, initializer);

			} else if (jVariableDefinition.expr() instanceof JModuloExpression) {

				JModuloExpression moduloExpression = (JModuloExpression) jVariableDefinition.expr();

				this.visitModuloExpression(moduloExpression);

				AlloyExpression initializer = expressions.pop();
				programBuffer.assign(alloy_variable, initializer);

			} else if (jVariableDefinition.expr() instanceof JmlSpecExpression) {

				JmlSpecExpression specExpression = (JmlSpecExpression) jVariableDefinition.expr();

				JmlExpressionVisitor expreVisitor = new JmlExpressionVisitor();
				expreVisitor.visitJmlSpecExpression(specExpression);
				AlloyExpression initializer = expreVisitor.getAlloyExpression();
				programBuffer.assign(alloy_variable, initializer);

				this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().addAll(expreVisitor.getPredsFromMathOperations());


				for (AlloyVariable av : expreVisitor.getVarsAndTheirTypeFromMathOperations().getVarsInTyping())
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(av, expreVisitor.getVarsAndTheirTypeFromMathOperations().get(av));


				this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().addAll(expreVisitor.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures());

				for (AlloyVariable av : expreVisitor.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().getVarsInTyping())
					this.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().put(av, expreVisitor.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().get(av));


				for (AlloyVariable av : expreVisitor.getVarsEncodingValueOfArithmeticOperationsInObjectInvariants().getVarsInTyping())
					this.getVarsEncodingValueOfArithmeticOperationsInObjectInvariants().put(av, expreVisitor.getVarsEncodingValueOfArithmeticOperationsInObjectInvariants().get(av));

			} else {

				jVariableDefinition.expr().accept(expressionVisitor);
				// If the variable was initialized by a program call
				if (jVariableDefinition.expr() instanceof JMethodCallExpression || jVariableDefinition.expr() instanceof JNewObjectExpression
						|| jVariableDefinition.expr() instanceof JNewArrayExpression || jVariableDefinition.expr() instanceof JStringLiteral) {

					if (expressionVisitor.getPredsEncodingValueOfArithmeticOperationsInObjectInvariants() != null)
						this.getPredsEncodingValueOfArithmeticOperationsInObjectInvariants().addAll(expressionVisitor.getPredsEncodingValueOfArithmeticOperationsInObjectInvariants());
					if (expressionVisitor.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures() != null)
						this.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().addAll(expressionVisitor.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures());

					JStatement initializer = expressionVisitor.getAlloyProgram();
					programBuffer.appendProgram(initializer);
				} else {
					CTypeAdapter type_Adapter = new CTypeAdapter();
					CType leftSideType = jVariableDefinition.getType();
					CType rightSideType = getType(jVariableDefinition.expr());
					JType left_alloy_type = type_Adapter.translate(leftSideType);
					JType right_alloy_type = type_Adapter.translate(rightSideType);
					if (leftSideType != rightSideType){
						AlloyExpression initializer = ExpressionSolver.getCastingExpression(left_alloy_type, right_alloy_type, expressionVisitor.getAlloyExpression());
						programBuffer.assign(alloy_variable, initializer);
					} else {
						AlloyExpression initializer = expressionVisitor.getAlloyExpression();
						programBuffer.assign(alloy_variable, initializer);						
					}

				}
			}

			if (this.isTryCatchBlock) {
				programBuffer.closeIf();
			}
		} else {
			// mffrias10072022: check if this is the place to initialize vars with default values.
		}

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



	public void visitDoStatement(JDoStatement self){
		self.accept(prettyPrint);
		log.debug("Visiting: " + JDoStatement.class);
		log.debug("Statement: \n" + prettyPrint.getPrettyPrint());

		HasBreakStatementVisitor vis = new HasBreakStatementVisitor();
		self.body().accept(vis);

		ExpressionVisitor expressionVisitor = new ExpressionVisitor();
		self.cond().accept(expressionVisitor);
		AlloyFormula alloyFormula = expressionVisitor.getAlloyFormula();

		// this will surround the do statement with the try catch block
		// verification
		if (this.isTryCatchBlock) {
			AlloyFormula trySurrounderCondition = BlockStatementSolver.getTryCatchSurrounderCondition();
			alloyFormula = new AndFormula(alloyFormula, trySurrounderCondition);
		}
		// if the current do occurs inside another do/while that has a break statement, we must modify
		// the condition of the current do so that its execution can be prevented.
		if (!this.whileIndices.isEmpty() && this.whileIndices.peek() != null){
			alloyFormula = new AndFormula(alloyFormula, BlockStatementSolver.getBreakReachedCondition(this.whileIndices.peek()));
		}
		// if the current do contains a break, we must modify the condition so that execution of break prevents the
		// execution of the loop.
		if (vis.hasBreak){
			alloyFormula = new AndFormula(alloyFormula, BlockStatementSolver.getBreakReachedCondition(BlockStatementSolver.getNextBreakReachedName(whileIndices)));
		}

		AlloyFormula finalLoopInvariant = null;
		if (!this.loopInvariants.empty()) {
			finalLoopInvariant = this.loopInvariants.pop();
			while (!this.loopInvariants.empty()) {
				finalLoopInvariant = new AndFormula(finalLoopInvariant, this.loopInvariants.pop());
			}
		}


		if (vis.hasBreak){
			ExprVariable break_reached_ev = BlockStatementSolver.getNextBreakReachedName(whileIndices);
			programBuffer.declare(break_reached_ev.getVariable(), JSignatureFactory.BOOLEAN_TYPE);
			programBuffer.assign(break_reached_ev.getVariable(), JExpressionFactory.FALSE_EXPRESSION);
		}

		programBuffer.openDo(alloyFormula, finalLoopInvariant == null ? null : new JLoopInvariant(finalLoopInvariant));
		if (vis.hasBreak){
			this.whileIndices.push(BlockStatementSolver.getNextBreakReachedName(whileIndices));
		} else {
			this.whileIndices.push(null);
		}
		self.body().accept(this);
		this.whileIndices.pop();
		programBuffer.closeDo();
	}


	/**
	 * 1. Check if jWhileStatement contains a break statement.
	 * 2. Declare a break_reached fresh boolean variable outside the while translation, initialized to false.
	 * 3. Modify visitor so that it uses a stack of break_reached variables. Each statement inside the while body will use the top variable to enable/disable instructions.
	 */
	@Override
	public void visitWhileStatement(JWhileStatement jWhileStatement) {
		jWhileStatement.accept(prettyPrint);
		log.debug("Visiting: " + JWhileStatement.class);
		log.debug("Statement: \n" + prettyPrint.getPrettyPrint());

		HasBreakStatementVisitor vis = new HasBreakStatementVisitor();

		jWhileStatement.body().accept(vis);

		ExpressionVisitor expressionVisitor = new ExpressionVisitor();
		jWhileStatement.cond().accept(expressionVisitor);
		AlloyFormula alloyFormula = expressionVisitor.getAlloyFormula();

		// this will surround the while statement with the try catch block
		// verification
		if (this.isTryCatchBlock) {
			AlloyFormula trySurrounderCondition = BlockStatementSolver.getTryCatchSurrounderCondition();
			alloyFormula = new AndFormula(alloyFormula, trySurrounderCondition);
		}
		// if the current while occurs inside another while that has a break statement, we must modify
		// the condition of the current while so that its execution can be prevented.
		if (!this.whileIndices.isEmpty() && this.whileIndices.peek() != null){
			alloyFormula = new AndFormula(alloyFormula, BlockStatementSolver.getBreakReachedCondition(this.whileIndices.peek()));
		}
		// if the current while contains a break, we must modify the condition so that execution of break prevents the
		// execution of the loop.
		if (vis.hasBreak){
			alloyFormula = new AndFormula(alloyFormula, BlockStatementSolver.getBreakReachedCondition(BlockStatementSolver.getNextBreakReachedName(whileIndices)));
		}

		AlloyFormula finalLoopInvariant = null;
		if (!this.loopInvariants.empty()) {
			finalLoopInvariant = this.loopInvariants.pop();
			while (!this.loopInvariants.empty()) {
				finalLoopInvariant = new AndFormula(finalLoopInvariant, this.loopInvariants.pop());
			}
		}


		if (vis.hasBreak){
			ExprVariable break_reached_ev = BlockStatementSolver.getNextBreakReachedName(whileIndices);
			programBuffer.declare(break_reached_ev.getVariable(), JSignatureFactory.BOOLEAN_TYPE);
			programBuffer.assign(break_reached_ev.getVariable(), JExpressionFactory.FALSE_EXPRESSION);
		}

		programBuffer.openWhile(alloyFormula, finalLoopInvariant == null ? null : new JLoopInvariant(finalLoopInvariant));
		if (vis.hasBreak){
			this.whileIndices.push(BlockStatementSolver.getNextBreakReachedName(whileIndices));
		} else {
			this.whileIndices.push(null);
		}
		jWhileStatement.body().accept(this);
		this.whileIndices.pop();
		programBuffer.closeWhile();
	}



	@Override
	public void visitBreakStatement(JBreakStatement self){
		JStatement setBreakReachedTrue = new JAssignment(this.whileIndices.peek(), JExpressionFactory.TRUE_EXPRESSION);
		JStatement jStatement = BlockStatementSolver.getSurroundedStatement(setBreakReachedTrue, this.isTryCatchBlock);
		programBuffer.appendProgram(jStatement);
	}


	@Override
	public void visitDivideExpression(JDivideExpression divExpression) {

		AlloyExpression rvalue;
		ExpressionVisitor expressionVisitor = new ExpressionVisitor();
		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {

			divExpression.left().accept(expressionVisitor);
			AlloyExpression left_div_expr = expressionVisitor.getAlloyExpression();

			divExpression.right().accept(expressionVisitor);
			AlloyExpression right_div_expr = expressionVisitor.getAlloyExpression();

			CType left_type = divExpression.left().getType();
			CType right_type = divExpression.right().getType();

			CTypeAdapter type_Adapter = new CTypeAdapter();
			JType left_alloy_type = type_Adapter.translate(left_type);
			JType right_alloy_type = type_Adapter.translate(right_type);

			if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))) {

				DivAuxiliaryConstants divAuxiliaryConstants = AuxiliaryConstantsFactory.build_integer_divide_auxiliary_constants(left_div_expr, right_div_expr);

				for (JStatement stmt : divAuxiliaryConstants.statements.getBlock()) {
					programBuffer.appendProgram(stmt);
				}

				rvalue = divAuxiliaryConstants.result_value;
				expressions.push(rvalue);
			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))) {

				DivAuxiliaryConstants divAuxiliaryConstants = AuxiliaryConstantsFactory.build_long_divide_auxiliary_constants(left_div_expr, right_div_expr);

				for (JStatement stmt : divAuxiliaryConstants.statements.getBlock()) {
					programBuffer.appendProgram(stmt);
				}

				rvalue = divAuxiliaryConstants.result_value;

			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))) {

				DivAuxiliaryConstants divAuxiliaryConstants = AuxiliaryConstantsFactory.build_float_divide_auxiliary_constants(left_div_expr, right_div_expr);

				for (JStatement stmt : divAuxiliaryConstants.statements.getBlock()) {
					programBuffer.appendProgram(stmt);
				}

				rvalue = divAuxiliaryConstants.result_value;

			} else {
				throw new TacoException("Cannot multiply elements from types " + left_alloy_type + " and " + right_alloy_type);
			}

		} else {

			divExpression.accept(expressionVisitor);
			rvalue = expressionVisitor.getAlloyExpression();

		}

		this.expressions.push(rvalue);
	}

	@Override
	public void visitModuloExpression(JModuloExpression moduloExpression) {

		AlloyExpression rvalue;
		ExpressionVisitor expressionVisitor = new ExpressionVisitor();
		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {

			moduloExpression.left().accept(expressionVisitor);
			AlloyExpression left_rem_expr = expressionVisitor.getAlloyExpression();

			moduloExpression.right().accept(expressionVisitor);
			AlloyExpression right_rem_expr = expressionVisitor.getAlloyExpression();

			CType left_type = moduloExpression.left().getType();
			CType right_type = moduloExpression.right().getType();

			CTypeAdapter type_Adapter = new CTypeAdapter();
			JType left_alloy_type = type_Adapter.translate(left_type);
			JType right_alloy_type = type_Adapter.translate(right_type);

			if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))) {

				DivAuxiliaryConstants remainderAuxiliaryConstants = AuxiliaryConstantsFactory.build_integer_divide_auxiliary_constants(left_rem_expr,
						right_rem_expr);

				for (JStatement stmt : remainderAuxiliaryConstants.statements.getBlock()) {
					programBuffer.appendProgram(stmt);
				}

				rvalue = remainderAuxiliaryConstants.remainder;

			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))) {

				DivAuxiliaryConstants remainderAuxiliaryConstants = AuxiliaryConstantsFactory.build_long_divide_auxiliary_constants(left_rem_expr,
						right_rem_expr);

				for (JStatement stmt : remainderAuxiliaryConstants.statements.getBlock()) {
					programBuffer.appendProgram(stmt);
				}


				rvalue = remainderAuxiliaryConstants.remainder;

			} else {
				throw new TacoException("Cannot modulo elements from types " + left_alloy_type + " and " + right_alloy_type);

			}
		} else {

			moduloExpression.accept(expressionVisitor);
			rvalue = expressionVisitor.getAlloyExpression();

		}

		this.expressions.push(rvalue);

	}

	@Override
	public void visitMultExpression(JMultExpression multExpression) {

		AlloyExpression rvalue;
		ExpressionVisitor expressionVisitor = new ExpressionVisitor();
		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {

			multExpression.left().accept(expressionVisitor);
			AlloyExpression left_mul_expr = expressionVisitor.getAlloyExpression();

			multExpression.right().accept(expressionVisitor);
			AlloyExpression right_mul_expr = expressionVisitor.getAlloyExpression();

			CType left_type = multExpression.left().getType();
			CType right_type = multExpression.right().getType();

			CTypeAdapter type_Adapter = new CTypeAdapter();
			JType left_alloy_type = type_Adapter.translate(left_type);
			JType right_alloy_type = type_Adapter.translate(right_type);

			if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))) {

				MultAuxiliaryConstants mulAuxiliaryConstants = AuxiliaryConstantsFactory.build_integer_mult_auxiliary_constants(left_mul_expr, right_mul_expr);

				for (JStatement	 stmt : mulAuxiliaryConstants.statements.getBlock()) {
					programBuffer.appendProgram(stmt);
				}

				rvalue = mulAuxiliaryConstants.result_value;

			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))) {

				MultAuxiliaryConstants mulAuxiliaryConstants = AuxiliaryConstantsFactory.build_long_mult_auxiliary_constants(left_mul_expr, right_mul_expr);

				for (JStatement	 stmt : mulAuxiliaryConstants.statements.getBlock()) {
					programBuffer.appendProgram(stmt);
				}

				rvalue = mulAuxiliaryConstants.result_value;

			} else if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))) {

				MultAuxiliaryConstants mulAuxiliaryConstants = AuxiliaryConstantsFactory.build_float_mult_auxiliary_constants(left_mul_expr, right_mul_expr);

				for (JStatement	 stmt : mulAuxiliaryConstants.statements.getBlock()) {
					programBuffer.appendProgram(stmt);
				}

				rvalue = mulAuxiliaryConstants.result_value;

			} else {
				throw new TacoException("Cannot multiply elements from types " + left_alloy_type + " and " + right_alloy_type);
			}

		} else {

			multExpression.accept(expressionVisitor);
			rvalue = expressionVisitor.getAlloyExpression();

		}

		this.expressions.push(rvalue);

	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}


	//	private static CType getType(JExpression theExpre){
	//		if (theExpre instanceof JUnaryPromote){
	//			return getType(((JUnaryPromote)theExpre).expr());
	//		} else if (theExpre instanceof JParenthesedExpression) {
	//			return getType(((JParenthesedExpression)theExpre).expr());
	//		} else
	//			return theExpre.getType();
	//	}

	@Override
	public void visitAddExpression(JAddExpression addExpression) {

		AlloyExpression rvalue;
		ExpressionVisitor expressionVisitor = new ExpressionVisitor();
		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {

			addExpression.left().accept(expressionVisitor);
			AlloyExpression left_add_expr = expressionVisitor.getAlloyExpression();

			addExpression.right().accept(expressionVisitor);
			AlloyExpression right_add_expr = expressionVisitor.getAlloyExpression();

			CType left_type = addExpression.left().getType();
			CType right_type = addExpression.right().getType();

			CTypeAdapter type_Adapter = new CTypeAdapter();
			JType left_alloy_type = type_Adapter.translate(left_type);
			JType right_alloy_type = type_Adapter.translate(right_type);



			if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))) {
				AddAuxiliaryConstants addAuxiliaryConstants = AuxiliaryConstantsFactory.build_integer_add_auxiliary_constants(left_add_expr, right_add_expr);

				for (JStatement stmt : addAuxiliaryConstants.statements.getBlock()) {
					programBuffer.appendProgram(stmt);
				}

				rvalue = addAuxiliaryConstants.result_value;

			} else {
				if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))
						&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))) {

					AddAuxiliaryConstants addAuxiliaryConstants = AuxiliaryConstantsFactory.build_float_add_auxiliary_constants(left_add_expr, right_add_expr);

					for (JStatement stmt : addAuxiliaryConstants.statements.getBlock()) {
						programBuffer.appendProgram(stmt);
					}

					rvalue = addAuxiliaryConstants.result_value;

				} else {
					addExpression.accept(expressionVisitor);
					rvalue = expressionVisitor.getAlloyExpression();
				}
			}

		} else {

			addExpression.accept(expressionVisitor);
			rvalue = expressionVisitor.getAlloyExpression();

		}

		this.expressions.push(rvalue);

	}


	@Override
	public void visitMinusExpression(JMinusExpression minusExpression) {

		AlloyExpression rvalue;
		ExpressionVisitor expressionVisitor = new ExpressionVisitor();
		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {

			minusExpression.left().accept(expressionVisitor);
			AlloyExpression left_mul_expr = expressionVisitor.getAlloyExpression();

			minusExpression.right().accept(expressionVisitor);
			AlloyExpression right_mul_expr = expressionVisitor.getAlloyExpression();

			CType left_type = minusExpression.left().getType();
			CType right_type = minusExpression.right().getType();

			CTypeAdapter type_Adapter = new CTypeAdapter();
			JType left_alloy_type = type_Adapter.translate(left_type);
			JType right_alloy_type = type_Adapter.translate(right_type);

			if ((left_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))
					&& (right_alloy_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))) {

				MinusAuxiliaryConstants minusAuxiliaryConstants = AuxiliaryConstantsFactory.build_float_sub_auxiliary_constants(left_mul_expr, right_mul_expr);

				for (JStatement stmt : minusAuxiliaryConstants.statements.getBlock()) {
					programBuffer.appendProgram(stmt);
				}

				rvalue = minusAuxiliaryConstants.result_value;

			} else {

				minusExpression.accept(expressionVisitor);
				rvalue = expressionVisitor.getAlloyExpression();
			}

		} else {

			minusExpression.accept(expressionVisitor);
			rvalue = expressionVisitor.getAlloyExpression();

		}

		this.expressions.push(rvalue);

	}
}
