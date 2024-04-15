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

public class WhileRemoverSimplifier extends JmlAstClonerStatementVisitor {


	private int whileIndex = 0;
	

	public void increaseWhileIndex() {
		this.whileIndex++;
	}
	
	
	public int getWhileIndex() {
		return this.whileIndex;
	}

	
	public WhileRemoverSimplifier() {}
	
	
	@Override
	public void visitJmlLoopStatement(JmlLoopStatement self) {
		JWhileStatement theWhileStatement = (JWhileStatement)self.loopStmt();
		theWhileStatement.accept(this);
		JStatement unrolledWhile = (JStatement)this.getStack().pop();
		this.getStack().push(unrolledWhile);
	}
	
	
	
	@Override
	public void visitWhileStatement(JWhileStatement self) {
		this.increaseWhileIndex();
		WhileBodyLocalVariableCollectorVisitor variableCollector = new WhileBodyLocalVariableCollectorVisitor();
		self.body().accept(variableCollector);
		variableCollector.getStack().pop();
		int numLoopUnrolls = TacoConfigurator.getInstance().getDynAlloyToAlloyLoopUnroll();
		JStatement unrolledWhile = unrollWhileLoop(self.cond(), self.body(), this.getWhileIndex(), numLoopUnrolls, variableCollector.getLocalVars());		
		this.getStack().push(unrolledWhile);
		
	}
	
	
	public JStatement unrollWhileLoop(JExpression cond, JStatement body, int whileIndex, int numIterations, ArrayList<JVariableDeclarationStatement> renameVars) {
		body.accept(this);
		body = (JStatement) this.getStack().pop();
		
		JStatement[] newBlockBody = new JStatement[numIterations + 1];
		
		for (int i = 0; i < numIterations; i++) {
			JStatement iterBody = replaceVarNames(body, whileIndex, i, renameVars);
			JIfStatement newIf = new JIfStatement(body.getTokenReference(), cond, iterBody, null, body.getComments());
			newBlockBody[i] = newIf;
		}
		JAssertStatement finalAssert = new JAssertStatement(body.getTokenReference(), new JUnaryExpression(body.getTokenReference(), 13, cond), null, body.getComments());
		newBlockBody[numIterations] = finalAssert;
		
		JBlock unrolledBody = new JBlock(body.getTokenReference(), newBlockBody, body.getComments());
				
		return unrolledBody;
	}


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