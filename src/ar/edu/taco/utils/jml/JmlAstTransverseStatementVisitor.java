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
package ar.edu.taco.utils.jml;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.jmlspecs.checker.JmlAssumeStatement;
import org.jmlspecs.checker.JmlLoopInvariant;
import org.jmlspecs.checker.JmlSpecCase;
import org.jmlspecs.checker.JmlSpecification;
import org.multijava.mjc.JAssertStatement;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JCatchClause;
import org.multijava.mjc.JCompoundStatement;
import org.multijava.mjc.JConstructorBlock;
import org.multijava.mjc.JDoStatement;
import org.multijava.mjc.JEmptyStatement;
import org.multijava.mjc.JExpressionStatement;
import org.multijava.mjc.JForStatement;
import org.multijava.mjc.JIfStatement;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.mjc.JParenthesedExpression;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JSwitchGroup;
import org.multijava.mjc.JSwitchStatement;
import org.multijava.mjc.JSynchronizedStatement;
import org.multijava.mjc.JTryCatchStatement;
import org.multijava.mjc.JTryFinallyStatement;
import org.multijava.mjc.JVariableDeclarationStatement;
import org.multijava.mjc.JWhileStatement;

import ar.edu.jdynalloy.ast.JAlloyProgramBuffer;
import ar.edu.taco.TacoNotImplementedYetException;
import ar.edu.taco.simplejml.JmlBaseVisitor;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

public class JmlAstTransverseStatementVisitor extends JmlBaseVisitor {

	private static Logger log = Logger.getLogger(JmlAstTransverseStatementVisitor.class);

	protected final JAlloyProgramBuffer programBuffer = new JAlloyProgramBuffer();
	protected Stack<AlloyFormula> loopInvariants = new Stack<AlloyFormula>();
	protected boolean isTryCatchBlock = true;
	protected boolean methodReturnValue = false;
	protected boolean leaveCurrentSubroutine = false;
	protected List<AlloyExpression> instanceModifiedVariables = new ArrayList<AlloyExpression>();
	//	protected Stack<Boolean> isReturnPresent = new Stack<Boolean>();

	@Override
	public void visitBlockStatement(/* @non_null */JBlock self) {
		log.debug("Visiting: " + self.getClass().getName());

		for (JStatement statement : self.body()) {
			statement.accept(this);
		}
	}

	@Override
	public void visitCompoundStatement(/* @non_null */JCompoundStatement self) {
		log.debug("Visiting: " + self.getClass().getName());

		for (JStatement statement : self.body()) {
			statement.accept(this);
		}
	}

	@Override
	public void visitCompoundStatement(/* @non_null */JStatement[] self) {
		log.debug("Visiting: " + self.getClass().getName());

		for (JStatement statement : self) {
			statement.accept(this);
		}
	}

	@Override
	public void visitConstructorBlock(/* @non_null */JConstructorBlock self) {
		log.debug("Visiting: " + self.getClass().getName());

		for (JStatement statement : self.body()) {
			statement.accept(this);
		}
	}

	
	
	@Override
	public void visitDoStatement(/* @non_null */JDoStatement self) {
		log.debug("Visiting: " + self.getClass().getName());

		self.body().accept(this);
	}

	@Override
	public void visitEmptyStatement(JEmptyStatement self) {
		log.debug("Visiting: " + self.getClass().getName());

		// do nothing
	}

	@Override
	public void visitExpressionStatement(/* @non_null */JExpressionStatement self) {
		log.debug("Visiting: " + self.getClass().getName());

		self.expr().accept(this);
	}

	@Override
	public void visitForStatement(/* @non_null */JForStatement self) {
		log.debug("Visiting: " + self.getClass().getName());

		self.init().accept(this);
		self.incr().accept(this);
		self.body().accept(this);
	}

	@Override
	public void visitIfStatement(/* @non_null */JIfStatement self) {
		log.debug("Visiting: " + self.getClass().getName());

		self.thenClause().accept(this);
		self.elseClause().accept(this);
	}

	//	@Override
	//	public void visitJmlGenericSpecBody(JmlGenericSpecBody self) {
	//		log.debug("Visiting: " + self.getClass().getName());
	//
	//		if (self.specClauses() != null) {
	//			for (JmlSpecBodyClause jmlSpecBodyClause : self.specClauses()) {
	//				jmlSpecBodyClause.accept(this);
	//			}
	//		}
	//	}

	//	@Override
	//	public void visitJmlGenericSpecCase(JmlGenericSpecCase self) {
	//		log.debug("Visiting: " + self.getClass().getName());
	//
	//		if (self.hasSpecHeader()) {
	//			for (JmlRequiresClause jmlRequiresClause : self.specHeader()) {
	//				jmlRequiresClause.accept(this);
	//			};
	//		}
	//		
	//		if (self.hasSpecBody()) {
	//			self.specBody().accept(this);
	//		}
	//		
	//		if(self.hasSpecVarDecls()) {
	//			for (JmlSpecVarDecl jmlSpecVarDecl : self.specVarDecls()) {
	//				jmlSpecVarDecl.accept(this);	
	//			}
	//		}
	//	}

	@Override
	public void visitJmlLoopInvariant(JmlLoopInvariant self) {
		log.debug("Visiting: " + self.getClass().getName());

		self.predicate().accept(this);
	}

	@Override
	public void visitJmlSpecification(JmlSpecification self) {
		log.debug("Visiting: " + self.getClass().getName());

		if (self.hasSpecCases()) {
			for (JmlSpecCase jmlSpecCase : self.specCases()) {
				jmlSpecCase.accept(this);
			}
		}
	}

	//	@Override
	//	public void visitJmlExceptionalSpecCase(JmlExceptionalSpecCase self) {
	//		if (self.specHeader() != null) {
	//			for (JmlRequiresClause jmlRequiresClause : self.specHeader()) {
	//				jmlRequiresClause.accept(this);
	//			}
	//		}
	//		
	//		if (self.specBody() != null && self.specBody().specClauses() != null) {
	//			for (JmlSpecBodyClause jmlSpecBodyClause : self.specBody().specClauses()) {
	//				jmlSpecBodyClause.accept(this);
	//			}
	//		}
	//	}

	//	@Override
	//	public void visitJmlNormalSpecCase(JmlNormalSpecCase self) {
	//		if (self.specHeader() != null) {
	//			for (JmlRequiresClause jmlRequiresClause : self.specHeader()) {
	//				jmlRequiresClause.accept(this);
	//			}
	//		}
	//		
	//		if (self.specBody() != null && self.specBody().specClauses() != null) {
	//			for (JmlSpecBodyClause jmlSpecBodyClause : self.specBody().specClauses()) {
	//				jmlSpecBodyClause.accept(this);
	//			}
	//		}
	//	}

	@Override
	public void visitParenthesedExpression(JParenthesedExpression self) {
		log.debug("Visiting: " + self.getClass().getName());

		self.expr().accept(this);
	}

	@Override
	public void visitSwitchStatement(/* @non_null */JSwitchStatement self) {
		log.debug("Visiting: " + self.getClass().getName());

		for (JSwitchGroup switchGroup : self.groups()) {
			switchGroup.accept(this);
		}
	}

	@Override
	public void visitSwitchGroup(/* @non_null */ JSwitchGroup self) {
		log.debug("Visiting: " + self.getClass().getName());

		for (JStatement js : self.getStatements()) {
			js.accept(this);
		}
	}

	
	@Override
	public void visitSynchronizedStatement(/* @non_null */JSynchronizedStatement self) {
		log.debug("Visiting: " + self.getClass().getName());

		self.body().accept(this);
	}

	@Override
	public void visitTryCatchStatement(/* @non_null */JTryCatchStatement self) {
		log.debug("Visiting: " + self.getClass().getName());
		this.isTryCatchBlock = true;
		self.tryClause().accept(this);
		for (JCatchClause catchClausule : self.catchClauses()) {
			catchClausule.accept(this);
		}
	}

	@Override
	public void visitTryFinallyStatement(/* @non_null */JTryFinallyStatement self) {
		log.debug("Visiting: " + self.getClass().getName());

		self.tryClause().accept(this);
		self.finallyClause().accept(this);
	}

	@Override
	public void visitVariableDeclarationStatement(/* @non_null */JVariableDeclarationStatement self) {
		log.debug("Visiting: " + self.getClass().getName());

		for (int x = 0; x < self.getVars().length; x++) {
			self.getVars()[x].accept(this);
		}
	}

	@Override
	public void visitWhileStatement(/* @non_null */JWhileStatement self) {
		log.debug("Visiting: " + self.getClass().getName());

		self.body().accept(this);
	}


	@Override 
	public void visitAssertStatement(/* @non_null */ JAssertStatement self){
		log.debug("Visiting: " + self.getClass().getName());

		self.predicate().accept(this);
	}
	

}
