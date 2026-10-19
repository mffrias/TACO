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
package ar.edu.taco.jml.loop;

import java.util.ArrayList;
import java.util.List;

import org.jmlspecs.checker.JmlLoopStatement;
import org.jmlspecs.checker.JmlRelationalExpression;
import org.jmlspecs.checker.JmlVariantFunction;
import org.multijava.mjc.CClassType;
import org.multijava.mjc.CNumericType;
import org.multijava.mjc.CType;
import org.multijava.mjc.CTypeVariable;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JBreakStatement;
import org.multijava.mjc.JCastExpression;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JIfStatement;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JLoopStatement;
import org.multijava.mjc.JNewObjectExpression;
import org.multijava.mjc.JOrdinalLiteral;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JThisExpression;
import org.multijava.mjc.JThrowStatement;
import org.multijava.mjc.JVariableDeclarationStatement;
import org.multijava.mjc.JVariableDefinition;
import org.multijava.mjc.JWhileStatement;
import org.multijava.util.compiler.JavaStyleComment;
import org.multijava.util.compiler.UnpositionedError;

import ar.edu.taco.jml.utils.ASTUtils;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

/**
 * Evaluates the condition of each {@code while} loop into a fresh variable: {@code while (c) B}
 * becomes {@code boolean ws_<n>; ws_<n> = c; while (ws_<n>) { B; ws_<n> = c; }}, the declaration
 * and the first assignment placed before the loop in the enclosing block. The last assignment is
 * left out when {@code B} ends with {@code break} ({@link LastStatementCollector}).
 *
 * <p>For an annotated loop with variant functions ({@code decreasing}), it also checks the first
 * one: each iteration starts by storing its value in {@code variant_<n>} and throws a
 * {@code RuntimeException} if that value is negative, or if at the end of the iteration the
 * variant is not smaller than {@code variant_<n>}. The variant functions are then removed from
 * the loop.
 *
 * <p>{@code ASTSimplifierManager} runs it after {@link LSBlockVisitor}.
 * {@link WhileRemoverSimplifier} runs earlier and unrolls every {@code while} loop it reaches,
 * dropping its variant functions, so in the current pipeline this pass finds no {@code while}
 * loop and only copies the AST. Unclear: whether it is still needed in that order.
 *
 * <p>Static state: the counters in {@code ws_<n>} and {@code variant_<n>} are static fields,
 * never reset, so the names keep growing over all the analyses run in one JVM (Stryker runs
 * many).
 */
public class WhileBlockVisitor extends JmlAstClonerStatementVisitor {

	private static int variantFunctionIndex = 0;

	private static int variableNameIndex = 0;

	/**
	 * Returns the statements that the loops rewritten by this instance need before them (the
	 * declaration and first assignment of each {@code ws_<n>}); the enclosing block's
	 * {@link #visitBlockStatement} inserts them.
	 *
	 * @return the list itself, not a copy
	 */
	public List<JStatement> getNewWhileStatements() {
		return newStatements;
	}

	/**
	 * Replaces the list of statements to insert before the loop.
	 *
	 * @param newStatements the new list
	 */
	public void setNewWhileStatements(List<JStatement> newStatements) {
		this.newStatements = newStatements;
	}

	private List<JStatement> newStatements;

	/** Creates the pass with an empty list of statements to insert. */
	public WhileBlockVisitor() {
		newStatements = new ArrayList<JStatement>();
	}

	/**
	 * Returns a fresh name {@code ws_<n>}, incrementing the static counter (see the class
	 * comment).
	 *
	 * @return the new name
	 */
	public String createNewWhileVariableName() {
		WhileBlockVisitor.variableNameIndex++;
		String s = "ws_" + variableNameIndex;
		return s;
	}	

	/**
	 * Rewrites each statement of the block with a new instance and inserts, before it, the
	 * statements that instance collected ({@link #getNewWhileStatements()}). Resets this
	 * instance's own list after each statement.
	 *
	 * @param self the block
	 */
	@Override
	public void visitBlockStatement(JBlock self) {
		List<JStatement> declarationList = new ArrayList<JStatement>();
		List<JStatement> statementList = new ArrayList<JStatement>();
		for (int i = 0; i < self.body().length; i++) {
			JStatement statement = self.body()[i];
			WhileBlockVisitor visitor = new WhileBlockVisitor();
			statement.accept(visitor);
			statementList.addAll(visitor.getNewWhileStatements());
			statementList.add((JStatement) visitor.getStack().pop());
			// reset statements
			newStatements = new ArrayList<JStatement>();
		}

		JStatement[] statements = new JStatement[declarationList.size() + statementList.size()];
		int i = 0;
		for (JStatement statement : declarationList) {
			assert (statement != null);
			statements[i] = statement;
			i++;
		}
		for (JStatement statement : statementList) {
			assert (statement != null);
			statements[i] = statement;
			i++;
		}
		for (int j = 0; j < statements.length; j++) {
			JStatement statement = statements[j];
			assert (statement != null);
		}
		assert (statements != null);
		JBlock newSelf = new JBlock(self.getTokenReference(), statements, self.getComments());
		this.getStack().push(newSelf);
	}

	/**
	 * Rewrites the body, then pushes {@code while (ws_<n>) { body; ws_<n> = cond; }} and adds the
	 * declaration of {@code ws_<n>} and the assignment {@code ws_<n> = cond;} to
	 * {@link #getNewWhileStatements()}. The assignment is left out of the body when the body ends
	 * with {@code break}. The same assignment object is used before the loop and in the body.
	 *
	 * @param self the loop
	 */
	@Override
	public void visitWhileStatement(JWhileStatement self) {
		self.body().accept(this);
		JStatement newBody = (JStatement) this.getStack().pop();
		JWhileStatement whileStatement = null;
		String cond = createNewWhileVariableName();
		JVariableDefinition variableDefinition = new JVariableDefinition(self.getTokenReference(), 0, self.cond().getType(), cond, null);
		JVariableDeclarationStatement variableDeclarationStatement = new JVariableDeclarationStatement(self.getTokenReference(), variableDefinition,
				new JavaStyleComment[0]);
		getNewWhileStatements().add(variableDeclarationStatement);
		JLocalVariableExpression condReference = new JLocalVariableExpression(self.getTokenReference(), variableDefinition);
		JStatement assignamentStatement = ASTUtils.createAssignamentStatement(condReference, self.cond());
		getNewWhileStatements().add(assignamentStatement);
		LastStatementCollector lsc = new LastStatementCollector();
		newBody.accept(lsc);
		if (lsc.lastStatementClass != JBreakStatement.class){
			JBlock generatedBlock = ASTUtils.createBlockStatement(newBody, assignamentStatement);
			whileStatement = new JWhileStatement(self.getTokenReference(), condReference, generatedBlock, self.getComments());
		} else {
			whileStatement = new JWhileStatement(self.getTokenReference(), condReference, newBody, self.getComments());
		}
		this.getStack().push(whileStatement);
	}

	/**
	 * Rewrites an annotated {@code while} loop. With variant functions, first adds the checks of
	 * the first one to the body (see the class comment). Pushes
	 * {@code { <ws_n declaration> <ws_n assignment> <annotated loop> }}, the loop rewritten by
	 * {@link #visitWhileStatement} and annotated with the original loop invariants and no variant
	 * functions, and clears the list of statements to insert.
	 *
	 * @param self the annotated loop. Its loop must be a {@code while} loop
	 *     ({@code ClassCastException} otherwise), and its first variant function, if any, of a
	 *     numeric type.
	 */
	@Override
	public void visitJmlLoopStatement(JmlLoopStatement self) {

		JWhileStatement loopStatement = (JWhileStatement)self.loopStmt();
		//Will modify the while loop so that it handles the variant function.

		JStatement whileBody = loopStatement.body();
		
		JmlVariantFunction[] varFunctions = self.variantFunctions();
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

			JNewObjectExpression theExpre = new JNewObjectExpression(
					self.getTokenReference(), 
					theExceptionType,
					new JThisExpression(self.getTokenReference()), 
					new JExpression[]{});

			JThrowStatement throwStmt = new JThrowStatement(
					self.getTokenReference(), 
					theExpre,
					new JavaStyleComment[]{});
			JExpression expreZero = new JOrdinalLiteral(loopStatement.getTokenReference(), 0, (CNumericType)type);
			JExpression ifCond = new JmlRelationalExpression(loopStatement.getTokenReference(), OPE_LT, variableVariantFunction.expr(), expreZero);
			JStatement theIf = new JIfStatement(loopStatement.getTokenReference(), ifCond, throwStmt, null, null);
			JExpression ifCond2 = new JmlRelationalExpression(loopStatement.getTokenReference(), OPE_GE, varFunctions[0].specExpression(), new JLocalVariableExpression(loopStatement.getTokenReference(), variableVariantFunction));
			JThrowStatement throwStmt2 = new JThrowStatement(
					loopStatement.getTokenReference(), 
					new JNewObjectExpression(
							loopStatement.getTokenReference(), 
							theExceptionType,
							new JThisExpression(loopStatement.getTokenReference()), 
							new JExpression[]{}),
					new JavaStyleComment[]{});
			org.multijava.mjc.JStatement theIf2 = new JIfStatement(loopStatement.getTokenReference(), ifCond2, throwStmt2, null, null);

			org.multijava.mjc.JStatement newLoopBody = new org.multijava.mjc.JBlock(loopStatement.getTokenReference(), new org.multijava.mjc.JStatement[]{theVariantFunctionVariableDeclaration, theIf, whileBody, theIf2}, new JavaStyleComment[]{});
			JWhileStatement newLoopStatement = new JWhileStatement(loopStatement.getTokenReference(), loopStatement.cond(), newLoopBody, loopStatement.getComments());
			newLoopStatement.accept(this);
		} else {
			loopStatement.accept(this);
		}

//		self.loopStmt().accept(this);
		JStatement newStatement = (JStatement) this.getStack().pop();
		JmlLoopStatement jmlLoopStatement = new JmlLoopStatement(self.getTokenReference(), self.loopInvariants(), new JmlVariantFunction[]{}, newStatement, self.getComments());
		JStatement[] blockStatements = new JStatement[this.newStatements.size() + 1];
		for (int i = 0; i< this.newStatements.size(); i++){
			blockStatements[i] = this.newStatements.get(i);
		}
		blockStatements[this.newStatements.size()] = jmlLoopStatement;
		this.newStatements = new ArrayList<JStatement>();
		JBlock newBlockIncludingNewStatements = new JBlock(self.getTokenReference(), blockStatements, self.getComments());
		this.getStack().push(newBlockIncludingNewStatements);
	}
}
