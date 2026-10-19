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

import org.jmlspecs.checker.JmlAssignmentStatement;
import org.jmlspecs.checker.JmlLoopStatement;
import org.jmlspecs.checker.JmlRelationalExpression;
import org.jmlspecs.checker.JmlVariantFunction;
import org.multijava.mjc.CClassType;
import org.multijava.mjc.CNumericType;
import org.multijava.mjc.CType;
import org.multijava.mjc.CTypeVariable;
import org.multijava.mjc.JAssertStatement;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JBreakStatement;
import org.multijava.mjc.JCastExpression;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JExpressionStatement;
import org.multijava.mjc.JIfStatement;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JLoopStatement;
import org.multijava.mjc.JNewObjectExpression;
import org.multijava.mjc.JOrdinalLiteral;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JThisExpression;
import org.multijava.mjc.JThrowStatement;
import org.multijava.mjc.JUnaryExpression;
import org.multijava.mjc.JVariableDeclarationStatement;
import org.multijava.mjc.JVariableDefinition;
import org.multijava.mjc.JWhileStatement;
import org.multijava.util.compiler.JavaStyleComment;
import org.multijava.util.compiler.UnpositionedError;

import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.jml.utils.ASTUtils;
import ar.edu.taco.jml.varnames.VNWhileBlockVisitor;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;
//import javafe.ast.ThisExpr;

/**
 * Unrolls every {@code while} loop it reaches into nested {@code if} statements. With N the value
 * of {@code dynalloy.toAlloy.loopUnroll}
 * ({@link TacoConfigurator#getDynAlloyToAlloyLoopUnroll()}, 3 by default), {@code while (c) B}
 * becomes {@code { if (c) { B_N; if (c) { B_N-1; ... if (c) B_1 } } assert !c; }}, where each
 * copy {@code B_i} has the variables declared in the loop body renamed to
 * {@code <name>_<loop index>_<i>} ({@link VNWhileBlockVisitor}). An execution that needs more
 * than N iterations fails that Java {@code assert}; jDynAlloy translates a failed {@code assert}
 * by setting {@code throw} to {@code java_lang_AssertionErrorLit}
 * ({@code JDynAlloyXlatorVisitor}, {@code visit(JAssert)}).
 *
 * <p>The loop invariants and variant functions of annotated loops are dropped. Do-while loops are
 * not unrolled here (see {@link DoWhileBlockVisitor}), and loops inside labeled statements are
 * not reached.
 *
 * <p>{@code ASTSimplifierManager} runs it third, after {@link ForRemoverVisitor} (so there are no
 * {@code for} loops left) and {@link BreakRemoverSimplifier} (no {@code break} statements in the
 * loops), during {@code JmlStage} (docs/ARCHITECTURE.md). The loop index is per instance; the
 * manager uses one instance for every compilation unit of a run.
 *
 * <p>Unclear: whether an input that needs more iterations than the bound is meant to be a
 * counterexample (as the final {@code assert} makes it, unless the specification allows an
 * {@code AssertionError}) or to be left outside the bounds.
 */
public class WhileRemoverSimplifier extends JmlAstClonerStatementVisitor {


	private int whileIndex = 0;


	/** Increments the loop index, which numbers the loops in the new variable names. */
	public void increaseWhileIndex() {
		this.whileIndex++;
	}


	/**
	 * Returns the loop index: the number of {@code while} loops this instance has visited so far.
	 *
	 * @return the current loop index
	 */
	public int getWhileIndex() {
		return this.whileIndex;
	}


	/** Creates the pass with loop index 0. */
	public WhileRemoverSimplifier() {}


	/**
	 * Unrolls the loop of an annotated loop ({@code JmlLoopStatement}) and drops its loop
	 * invariants and variant functions: pushes only the result of {@link #visitWhileStatement}.
	 *
	 * @param self an annotated loop. Its loop must be a {@code while} loop, as
	 *     {@link ForRemoverVisitor} and {@link BreakRemoverSimplifier} leave them; for an
	 *     annotated do-while loop the cast fails ({@code ClassCastException}).
	 */
	@Override
	public void visitJmlLoopStatement(JmlLoopStatement self) {
		JWhileStatement theWhileStatement = (JWhileStatement)self.loopStmt();
		theWhileStatement.accept(this);
		JStatement unrolledWhile = (JStatement)this.getStack().pop();
		this.getStack().push(unrolledWhile);
	}



	/**
	 * Unrolls a {@code while} loop (see the class comment). Increments the loop index, collects
	 * the declarations of the body ({@link WhileBodyLocalVariableCollectorVisitor}), unrolls the
	 * loops nested in the body first, then pushes the block
	 * {@code { <unrolled loop> assert !cond; }}. Reads {@code dynalloy.toAlloy.loopUnroll} from
	 * {@link TacoConfigurator}.
	 *
	 * <p>The renaming uses the loop index as it is after the body was visited, so a loop that
	 * contains loops shares the index of the last loop nested in it.
	 *
	 * @param self the loop
	 */
	@Override
	public void visitWhileStatement(JWhileStatement self) {
		this.increaseWhileIndex();
		WhileBodyLocalVariableCollectorVisitor variableCollector = new WhileBodyLocalVariableCollectorVisitor();
		self.body().accept(variableCollector);
		variableCollector.getStack().pop();
		self.body().accept(this);
		JStatement body = (JStatement) this.getStack().pop();
		int numLoopUnrolls = TacoConfigurator.getInstance().getDynAlloyToAlloyLoopUnroll();
		JStatement unrolledWhile = unrollWhileLoop(self.cond(), body, this.getWhileIndex(), numLoopUnrolls, variableCollector.getLocalVars());
		JAssertStatement finalAssert = new JAssertStatement(body.getTokenReference(), new JUnaryExpression(body.getTokenReference(), 13, self.cond()), null, body.getComments());
		JStatement[] newBlockBody = new JStatement[2];
		newBlockBody[0] = unrolledWhile;
		newBlockBody[1] = finalAssert;
		JBlock newBlock = new JBlock(body.getTokenReference(), newBlockBody, body.getComments());
		this.getStack().push(newBlock);
	}


	/**
	 * Builds the nested {@code if} statements of an unrolled loop:
	 * {@code if (cond) { body_n; <unrollWhileLoop(n - 1)> }}, and {@code if (cond) body_1} for
	 * {@code n == 1}. Each copy of the body has the variables of {@code renameVars} renamed to
	 * {@code <name>_<whileIndex>_<copy>} by {@link VNWhileBlockVisitor}.
	 *
	 * <p>Assumes {@code numIterations >= 1}: with 0 or less the recursion does not end (a
	 * {@code StackOverflowError}).
	 *
	 * @param cond the loop condition, used as it is in every {@code if}
	 * @param body the loop body, with its nested loops already unrolled
	 * @param whileIndex the loop index used in the new names
	 * @param numIterations how many copies of the body to make
	 * @param renameVars the declarations whose variables are renamed in each copy
	 * @return the outermost {@code if}
	 */
	public JStatement unrollWhileLoop(JExpression cond, JStatement body, int whileIndex, int numIterations, ArrayList<JVariableDeclarationStatement> renameVars) {
		JStatement newIf = null;
		if (numIterations == 1) {
			JStatement iterBody = replaceVarNames(body, whileIndex, 1, renameVars);
			newIf = new JIfStatement(body.getTokenReference(), cond, iterBody, null, body.getComments());
		} else {
			JStatement iterBody = replaceVarNames(body, whileIndex, numIterations, renameVars);
			JStatement[] newBlockBody = new JStatement[2];
			newBlockBody[0] = iterBody;
			newBlockBody[1] = unrollWhileLoop(cond, body, whileIndex, numIterations-1, renameVars);
			JBlock newBlock = new JBlock(body.getTokenReference(), newBlockBody, body.getComments());
			newIf = new JIfStatement(body.getTokenReference(), cond, newBlock, null, body.getComments());
		}
		return newIf;
	}




	//	public JStatement unrollWhileLoop(JExpression cond, JStatement body, int whileIndex, int numIterations, ArrayList<JVariableDeclarationStatement> renameVars) {
	//		body.accept(this);
	//		body = (JStatement) this.getStack().pop();
	//		
	//		JStatement[] newBlockBody = new JStatement[numIterations + 1];
	//		
	//		for (int i = 0; i < numIterations; i++) {
	//			JStatement iterBody = replaceVarNames(body, whileIndex, i, renameVars);
	//			JIfStatement newIf = new JIfStatement(body.getTokenReference(), cond, iterBody, null, body.getComments());
	//			newBlockBody[i] = newIf;
	//		}
	//		JAssertStatement finalAssert = new JAssertStatement(body.getTokenReference(), new JUnaryExpression(body.getTokenReference(), 13, cond), null, body.getComments());
	//		newBlockBody[numIterations] = finalAssert;
	//		
	//		JBlock unrolledBody = new JBlock(body.getTokenReference(), newBlockBody, body.getComments());
	//				
	//		return unrolledBody;
	//	}


	private JStatement replaceVarNames(JStatement body, int whileIndex2, int i,
			ArrayList<JVariableDeclarationStatement> renameVars) {

		VNWhileBlockVisitor varSimplifier = new VNWhileBlockVisitor(renameVars, whileIndex2, i);
		body.accept(varSimplifier);
		return (JStatement) varSimplifier.getStack().pop();
	}


}


//public static void main(String[] args) {
//int x = 10;
//int y = 4;
//if (x > 0) {
//	
//	x = x - 1;
//	int z;
//	while (x > 0) {
//		int r = x - 1;
//		x = x - 1;
//		while (y > 0) {
//			int w = 3;
//			if (x>0) {
//				w = -1;
//			} else {
//				int w = 5;
//			}
//		}
//		w = 5;
//	}
//} else {
//	y = 17;
//}
//assert (!(x>0));
//
//}