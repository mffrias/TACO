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


import static ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable.buildExprVariable;

import java.util.Stack;

import ar.edu.jdynalloy.ast.JIfThenElse;
import ar.edu.jdynalloy.ast.JSkip;
import ar.edu.jdynalloy.ast.JStatement;
import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.taco.jml.utils.LabelUtils;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.AndFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.EqualsFormula;

/**
 * @author ggasser
 * 
 */
public class BlockStatementSolver {
	
	
	/**
	 * Returns the statement guarded so that it does not run after an exception or a
	 * {@code return}: when {@code isTryCatchBlock} holds,
	 * {@code if (throw = null and exit_stmt_reached = false) jStatement else skip}, labelled with
	 * {@code LabelUtils.nextIfLabel()}; otherwise the statement itself. In
	 * {@code BlockStatementsVisitor}, its caller, the flag is always true (it starts true in
	 * {@code JmlAstTransverseStatementVisitor} and nothing sets it to false), so every
	 * statement is guarded.
	 *
	 * @param jStatement the translated statement
	 * @param isTryCatchBlock whether to guard it
	 * @return the guarded statement, or {@code jStatement}
	 */
	public static JStatement getSurroundedStatement(JStatement jStatement,
			boolean isTryCatchBlock) {
		JStatement returnStatement = jStatement;
		AlloyFormula alloyFormula = getTryCatchSurrounderCondition();
		if (isTryCatchBlock) {
			returnStatement = new JIfThenElse(alloyFormula, jStatement,
					new JSkip(), LabelUtils.nextIfLabel());
		}
		return returnStatement;
	}


	/**
	 * Returns the flag variable for a loop that contains a {@code break}:
	 * {@code break_reached_0} if no enclosing loop has one, otherwise
	 * {@code break_reached_<n+1>}, where {@code break_reached_<n>} is the flag of the innermost
	 * enclosing loop that has one. The stack is not changed, so calls with the same stack give
	 * the same name; {@code BlockStatementsVisitor} relies on that.
	 *
	 * @param prevs the flags of the enclosing loops, innermost on top; null for a loop without a
	 *        {@code break}. Each flag's name must end in {@code _<number>}
	 * @return a new variable expression
	 */
	public static ExprVariable getNextBreakReachedName(Stack<ExprVariable> prevs){
		int idx = prevs.size()-1;
		while (idx >= 0 && prevs.get(idx) == null){
			idx--;
		}
		String newName = null;
		if (idx < 0){
			 newName = JExpressionFactory.BREAK_REACHED_VARIABLE.getVariableId().getString() + "_0";
		} else {
			String prevName = prevs.get(idx).getVariable().getVariableId().getString();
			int initIndex = prevName.lastIndexOf("_");
			int prevNumber = Integer.valueOf(prevName.substring(initIndex+1));
			newName = JExpressionFactory.BREAK_REACHED_VARIABLE.getVariableId().getString() + "_" + (prevNumber+1);
		}
		return new ExprVariable(new AlloyVariable(newName));
	}


	/**
	 * Returns {@code theVar = false}: the loop's {@code break} has not been reached.
	 *
	 * @param theVar a loop's {@code break_reached_<n>} flag
	 * @return the formula
	 */
	public static AlloyFormula getBreakReachedCondition(ExprVariable theVar) {
		AlloyFormula brc = new EqualsFormula(theVar, JExpressionFactory.FALSE_EXPRESSION);
		return brc;
	}

	/**
	 * Returns {@code throw = null and exit_stmt_reached = false}: no exception thrown, and no
	 * {@code return} or {@code throw} reached. It guards statements
	 * ({@link #getSurroundedStatement}) and loops in {@code BlockStatementsVisitor}.
	 *
	 * @return a new formula
	 */
	public static AlloyFormula getTryCatchSurrounderCondition() {
		AlloyFormula nullThrowFormula = new EqualsFormula(
				JExpressionFactory.THROW_EXPRESSION,
				JExpressionFactory.NULL_EXPRESSION);

		AlloyFormula exitReachedFormula = new EqualsFormula(
				JExpressionFactory.EXIT_REACHED_EXPRESSION,
				JExpressionFactory.FALSE_EXPRESSION);
		

		exitReachedFormula = new AndFormula(nullThrowFormula,
				exitReachedFormula);
		return exitReachedFormula;
	}

	/**
	 * Returns {@code return = null}. Nothing calls it.
	 *
	 * @return a new formula
	 */
	public static AlloyFormula getReturnSurrounderCondition() {
		AlloyFormula nullReturnFormula = new EqualsFormula(
				JExpressionFactory.RETURN_EXPRESSION,
				JExpressionFactory.NULL_EXPRESSION);

		return nullReturnFormula;
	}
}
