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

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import ar.edu.jdynalloy.ast.JDynAlloyMutator;
import ar.edu.jdynalloy.ast.JAssert;
import ar.edu.jdynalloy.ast.JAssignment;
import ar.edu.jdynalloy.ast.JAssume;
import ar.edu.jdynalloy.ast.JIfThenElse;
import ar.edu.jdynalloy.ast.JProgramCall;
import ar.edu.jdynalloy.ast.JStatement;
import ar.edu.jdynalloy.ast.JWhile;
import ar.edu.jdynalloy.binding.JBindingKey;
import ar.edu.jdynalloy.factory.JDynAlloyFactory;
import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.jdynalloy.factory.JPredicateFactory;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.simplejml.builtin.JNullPointerException;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprComprehension;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprIfCondition;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprIntersection;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprSum;
import ar.uba.dc.rfm.alloy.ast.expressions.ExpressionVisitor;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.JFormulaVisitor;
import ar.uba.dc.rfm.alloy.ast.formulas.OrFormula;

public class NullDerefVisitor extends JDynAlloyMutator {

	private static class NullDerefSafetyVisitor extends ExpressionVisitor {

		public List<AlloyExpression> getUnsafeExpressions() {
			return unsafeExpressions;
		}

		private final List<AlloyExpression> unsafeExpressions = new LinkedList<AlloyExpression>();

		@Override
		public Object visit(ExprJoin n) {
			// TODO: Add block list of expressions that we know are not null
			unsafeExpressions.add(n.getLeft());
			return super.visit(n);
		}

		//		private JFormulaVisitor formulaVisitor;

		@Override
		public Object visit(ExprComprehension e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object visit(ExprIfCondition e) {
			return super.visit(e);
		}

		@Override
		public Object visit(ExprSum e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object visit(ExprIntersection e) {
			return super.visit(e);
		}

	}

	private final IdentityHashMap<JProgramCall, JBindingKey> callBindings;

	public NullDerefVisitor(
			IdentityHashMap<JProgramCall, JBindingKey> callBindings) {
		super(TacoConfigurator.getInstance().getUseJavaArithmetic());
		this.callBindings = callBindings;
	}

	@Override
	public Object visit(JAssert n) {
		JAssert freshAssert = (JAssert) super.visit(n);
		return visitStatement(freshAssert, freshAssert.getCondition());
	}

	private JStatement buildNullDerefBlock(
			List<AlloyExpression> unsafeExpressions, JStatement statement) {
		Vector<AlloyFormula> v = new Vector<AlloyFormula>();

		for (AlloyExpression unsafeExpr : unsafeExpressions) {
			AlloyFormula isEmptyOrNull = JPredicateFactory
					.isEmptyOrNull(unsafeExpr);
			v.add(isEmptyOrNull);
		}
		AlloyFormula nullDeRefCondition = OrFormula.buildOrFormula(v
				.toArray(new AlloyFormula[] {}));

		String null_pointer_exception_literal = JNullPointerException
				.getInstance().getModule().getLiteralSingleton()
				.getSignatureId();
		JAssignment assign_throw = JDynAlloyFactory.assign(
				JExpressionFactory.THROW_EXPRESSION, ExprConstant
				.buildExprConstant(null_pointer_exception_literal));

		JStatement nullDerefBlock = JDynAlloyFactory.ifThenElse(
				nullDeRefCondition, assign_throw, statement);

		return nullDerefBlock;
	}

	private List<AlloyExpression> collectUnsafeExpressions(AlloyFormula formula) {
		NullDerefSafetyVisitor exprVisitor = new NullDerefSafetyVisitor();
		formula.accept(new JFormulaVisitor(exprVisitor));
		return exprVisitor.getUnsafeExpressions();
	}

	private List<AlloyExpression> collectUnsafeExpressions(
			AlloyExpression expression) {
		NullDerefSafetyVisitor exprVisitor = new NullDerefSafetyVisitor();
		expression.accept(exprVisitor);
		return exprVisitor.getUnsafeExpressions();
	}

	@Override
	public Object visit(JAssignment n) {
		JAssignment freshAssigment = (JAssignment) super.visit(n);
		List<AlloyExpression> exprs = Arrays.asList(freshAssigment.getLvalue(),
				freshAssigment.getRvalue());
		return visitStatement(freshAssigment, exprs);
	}

	@Override
	public Object visit(JAssume n) {
		JAssume freshAssume = (JAssume) super.visit(n);
		return visitStatement(freshAssume, freshAssume.getCondition());
	}

	@Override
	public Object visit(JIfThenElse n) {
		JIfThenElse freshIfThenElse = (JIfThenElse) super.visit(n);
		return visitStatement(freshIfThenElse, freshIfThenElse.getCondition());
	}

	@Override
	public Object visit(JProgramCall oldProgramCall) {
		JProgramCall freshProgramCall = (JProgramCall) super
				.visit(oldProgramCall);

		updateBinding(oldProgramCall, freshProgramCall);

		return visitStatement(freshProgramCall, freshProgramCall.getArguments());
	}

	private boolean updateBinding(JProgramCall oldProgramCall,
			JProgramCall newProgramCall) {
		if (callBindings.containsKey(oldProgramCall)) {
			JBindingKey value = callBindings.get(oldProgramCall);
			callBindings.remove(oldProgramCall);
			callBindings.put(newProgramCall, value);
			return true;
		} else
			return false;
	}

	@Override
	public Object visit(JWhile n) {
		JWhile freshWhile = (JWhile) super.visit(n);
		return visitStatement(freshWhile, freshWhile.getCondition());
	}

	private JStatement visitStatement(JStatement statement, AlloyFormula formula) {
		List<AlloyExpression> unsafeExpressions = collectUnsafeExpressions(formula);
		return buildBlock(statement, unsafeExpressions);
	}

	private JStatement visitStatement(JStatement statement,
			List<AlloyExpression> exprs) {
		List<AlloyExpression> unsafeExprs = new LinkedList<AlloyExpression>();
		for (AlloyExpression expr : exprs) {
			unsafeExprs.addAll(collectUnsafeExpressions(expr));
		}

		if (statement instanceof JProgramCall) {
			JProgramCall programCall = (JProgramCall)statement;
			if (!programCall.isStatic()) {
				AlloyExpression receiver = programCall.getReceiver();
				unsafeExprs.add(receiver);
			}
		}
		// TODO: Add unsafe receiver expression if stmt is non-static program call
		return buildBlock(statement, unsafeExprs);
	}

	private JStatement buildBlock(JStatement statement,
			List<AlloyExpression> unsafeExprs) {
		if (!unsafeExprs.isEmpty()) {
			JStatement nullDerefBlock = buildNullDerefBlock(unsafeExprs,
					statement);
			return nullDerefBlock;
		} else
			return statement;
	}

}
