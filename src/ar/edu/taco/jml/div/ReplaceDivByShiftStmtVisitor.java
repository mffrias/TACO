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

import java.util.ArrayList;
import java.util.List;

import org.jmlspecs.checker.JmlAssertStatement;
import org.jmlspecs.checker.JmlAssignmentStatement;
import org.jmlspecs.checker.JmlAssumeStatement;
import org.jmlspecs.checker.JmlPredicate;
import org.jmlspecs.checker.JmlSpecExpression;
import org.jmlspecs.checker.JmlVariableDefinition;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JExpressionStatement;
import org.multijava.mjc.JIfStatement;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JReturnStatement;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JThrowStatement;
import org.multijava.mjc.JVariableDeclarationStatement;
import org.multijava.mjc.JVariableDefinition;
import org.multijava.mjc.JWhileStatement;

import ar.edu.taco.jml.utils.ASTUtils;
import ar.edu.taco.jml.utils.SpecSimplifierClassBaseVisitor;

public class ReplaceDivByShiftStmtVisitor extends SpecSimplifierClassBaseVisitor {

	@Override
	public void visitBlockStatement(JBlock self) {
		List<JStatement> declarationList = new ArrayList<JStatement>();
		List<JStatement> statementList = new ArrayList<JStatement>();

		for (int i = 0; i < self.body().length; i++) {
			JStatement statement = self.body()[i];

			{
				ReplaceDivByShiftStmtVisitor visitor = new ReplaceDivByShiftStmtVisitor();
				statement.accept(visitor);

				JStatement aStatement = (JStatement) visitor.getStack().pop();

				// If the statement is a Local variable declaration, we are
				// going to skip it.
				if (!(aStatement instanceof JExpressionStatement) || !(((JExpressionStatement) aStatement).expr() instanceof JLocalVariableExpression)) {
					statementList.add(aStatement);
				}
			}
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


	@Override
	public void visitIfStatement(/* @non_null */JIfStatement self) {

		self.thenClause().accept(this);
		JStatement newThen = (JStatement) this.getStack().pop();
		JStatement newElse = null;
		if (self.elseClause() != null) {
			self.elseClause().accept(this);
			newElse = (JStatement) this.getStack().pop();
		}

		ReplaceDivByShiftExprVisitor conditionSimplifierVisitor = new ReplaceDivByShiftExprVisitor();
		self.cond().accept(conditionSimplifierVisitor);
		JExpression condition = conditionSimplifierVisitor.getArrayStack().pop();

		JIfStatement newIfStatement = ASTUtils.createIfStatement(condition, newThen, newElse, self.getComments());

		this.getStack().push(newIfStatement);

	}

	@Override
	public void visitWhileStatement(JWhileStatement self) {
		self.body().accept(this);
		JStatement newBody = (JStatement) this.getStack().pop();

		ReplaceDivByShiftExprVisitor conditionSimplifierVisitor = new ReplaceDivByShiftExprVisitor();
		self.cond().accept(conditionSimplifierVisitor);
		JExpression condition = conditionSimplifierVisitor.getArrayStack().pop();

		JWhileStatement newJWhileStatement = new JWhileStatement(self.getTokenReference(), condition, newBody, self.getComments());

		this.getStack().push(newJWhileStatement);
	}

	@Override
	public void visitVariableDeclarationStatement(JVariableDeclarationStatement self) {

		JVariableDefinition[] newVars = new JVariableDefinition[self.getVars().length];
		for (int i = 0; i < self.getVars().length; i++) {
			JVariableDefinition variableDefinition = self.getVars()[i];
			variableDefinition.accept(this);
			newVars[i] = (JVariableDefinition) getStack().pop();
		}

		JVariableDeclarationStatement newSelf = new JVariableDeclarationStatement(self.getTokenReference(), newVars, self.getComments());
		this.getStack().push(newSelf);
	}

	@Override
	public void visitJmlVariableDefinition(JmlVariableDefinition self) {
		ReplaceDivByShiftExprVisitor conditionSimplifierVisitor = new ReplaceDivByShiftExprVisitor();
		self.expr().accept(conditionSimplifierVisitor);
		JmlVariableDefinition newSelf = new JmlVariableDefinition(self.getTokenReference(), self.modifiers(), self.getType(), self.ident(),
				conditionSimplifierVisitor.getArrayStack().pop());
		getStack().push(newSelf);

	}

	@Override
	public void visitVariableDefinition(JVariableDefinition self) {
		ReplaceDivByShiftExprVisitor conditionSimplifierVisitor = new ReplaceDivByShiftExprVisitor();
		JExpression newExpr = null;
		if (self.expr() != null) {
			self.expr().accept(conditionSimplifierVisitor);
			newExpr = conditionSimplifierVisitor.getArrayStack().pop();
		}
		JVariableDefinition newSelf = new JVariableDefinition(self.getTokenReference(), self.modifiers(), self.getType(), self.ident(), newExpr);
		getStack().push(newSelf);

	}

	@Override
	public void visitJmlAssignmentStatement(JmlAssignmentStatement self) {

		self.assignmentStatement().accept(this);
		JExpressionStatement newExpressionStatement = (JExpressionStatement) this.getStack().pop();
		JmlAssignmentStatement newAssignamentStatement = new JmlAssignmentStatement(newExpressionStatement);
		getStack().push(newAssignamentStatement);

	}

	@Override
	public void visitExpressionStatement(JExpressionStatement self) {
		ReplaceDivByShiftExprVisitor visitor = new ReplaceDivByShiftExprVisitor();
		self.expr().accept(visitor);
		JExpression newExpression = visitor.getArrayStack().pop();
		JExpressionStatement newExpressionStatement = new JExpressionStatement(self.getTokenReference(), newExpression, self.getComments());
		getStack().push(newExpressionStatement);

	}

	@Override
	public void visitReturnStatement(JReturnStatement self) {
		ReplaceDivByShiftExprVisitor exprSimplifierVisitor = new ReplaceDivByShiftExprVisitor();
		JExpression expr = null;

		if (self.expr() != null) {
			self.expr().accept(exprSimplifierVisitor);
			expr = exprSimplifierVisitor.getArrayStack().pop();
		}

		JReturnStatement newSelf = new JReturnStatement(self.getTokenReference(), expr, self.getComments());

		this.getStack().push(newSelf);
	}

	@Override
	public void visitJmlAssertStatement(JmlAssertStatement self) {
		ReplaceDivByShiftExprVisitor exprSimplifierVisitor = new ReplaceDivByShiftExprVisitor();
		JExpression expr = null;

		self.predicate().specExpression().expression().accept(exprSimplifierVisitor);
		expr = exprSimplifierVisitor.getArrayStack().pop();
		JmlPredicate jmlPredicate = new JmlPredicate(new JmlSpecExpression(expr));
		JmlAssertStatement newSelf = new JmlAssertStatement(self.getTokenReference(), self.isRedundantly(), jmlPredicate, self.throwMessage(),
				self.getComments());

		this.getStack().push(newSelf);
	}

	@Override
	public void visitJmlAssumeStatement(JmlAssumeStatement self) {
		ReplaceDivByShiftExprVisitor exprSimplifierVisitor = new ReplaceDivByShiftExprVisitor();
		JExpression expr = null;

		self.predicate().specExpression().expression().accept(exprSimplifierVisitor);
		expr = exprSimplifierVisitor.getArrayStack().pop();
		JmlPredicate jmlPredicate = new JmlPredicate(new JmlSpecExpression(expr));
		JmlAssumeStatement newSelf = new JmlAssumeStatement(self.getTokenReference(), self.isRedundantly(), jmlPredicate, self.throwMessage(),
				self.getComments());

		this.getStack().push(newSelf);
	}

	@Override
	public void visitThrowStatement(JThrowStatement self) {
		ReplaceDivByShiftExprVisitor exprSimplifierVisitor = new ReplaceDivByShiftExprVisitor();
		JExpression expr = null;

		if (self.expr() != null) {
			self.expr().accept(exprSimplifierVisitor);
			expr = exprSimplifierVisitor.getArrayStack().pop();
		}

		JThrowStatement newSelf = new JThrowStatement(self.getTokenReference(), expr, self.getComments());

		this.getStack().push(newSelf);
	}

	@Override
	protected JmlPredicate simplifyPredicateSupport(JmlPredicate predicate) {
		ReplaceDivByShiftExprVisitor expressionVisitor = new ReplaceDivByShiftExprVisitor();
		predicate.specExpression().expression().accept(expressionVisitor);
		JExpression expr = expressionVisitor.getArrayStack().pop();

		JmlPredicate newPredicate = new JmlPredicate(new JmlSpecExpression(expr));
		return newPredicate;
	}

}
