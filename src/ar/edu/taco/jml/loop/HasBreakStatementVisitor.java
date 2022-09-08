package ar.edu.taco.jml.loop;

import java.util.Stack;

import org.jmlspecs.checker.JmlAssertStatement;
import org.jmlspecs.checker.JmlAssignableClause;
import org.jmlspecs.checker.JmlAssignmentStatement;
import org.jmlspecs.checker.JmlAssumeStatement;
import org.jmlspecs.checker.JmlClassDeclaration;
import org.jmlspecs.checker.JmlCompilationUnit;
import org.jmlspecs.checker.JmlConstraint;
import org.jmlspecs.checker.JmlConstructorDeclaration;
import org.jmlspecs.checker.JmlEnsuresClause;
import org.jmlspecs.checker.JmlExceptionalBehaviorSpec;
import org.jmlspecs.checker.JmlExceptionalSpecBody;
import org.jmlspecs.checker.JmlExceptionalSpecCase;
import org.jmlspecs.checker.JmlFieldDeclaration;
import org.jmlspecs.checker.JmlGenericSpecBody;
import org.jmlspecs.checker.JmlGenericSpecCase;
import org.jmlspecs.checker.JmlInformalExpression;
import org.jmlspecs.checker.JmlInterfaceDeclaration;
import org.jmlspecs.checker.JmlInvariant;
import org.jmlspecs.checker.JmlLoopStatement;
import org.jmlspecs.checker.JmlMethodDeclaration;
import org.jmlspecs.checker.JmlNormalBehaviorSpec;
import org.jmlspecs.checker.JmlNormalSpecBody;
import org.jmlspecs.checker.JmlNormalSpecCase;
import org.jmlspecs.checker.JmlRepresentsDecl;
import org.jmlspecs.checker.JmlRequiresClause;
import org.jmlspecs.checker.JmlSetStatement;
import org.jmlspecs.checker.JmlSignalsClause;
import org.jmlspecs.checker.JmlSignalsOnlyClause;
import org.jmlspecs.checker.JmlSpecification;
import org.multijava.mjc.JAssertStatement;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JBreakStatement;
import org.multijava.mjc.JCatchClause;
import org.multijava.mjc.JClassBlock;
import org.multijava.mjc.JCompoundStatement;
import org.multijava.mjc.JConstructorBlock;
import org.multijava.mjc.JContinueStatement;
import org.multijava.mjc.JDoStatement;
import org.multijava.mjc.JEmptyStatement;
import org.multijava.mjc.JExpressionListStatement;
import org.multijava.mjc.JExpressionStatement;
import org.multijava.mjc.JForStatement;
import org.multijava.mjc.JIfStatement;
import org.multijava.mjc.JLabeledStatement;
import org.multijava.mjc.JReturnStatement;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JSwitchStatement;
import org.multijava.mjc.JSynchronizedStatement;
import org.multijava.mjc.JThrowStatement;
import org.multijava.mjc.JTryCatchStatement;
import org.multijava.mjc.JTryFinallyStatement;
import org.multijava.mjc.JTypeDeclarationStatement;
import org.multijava.mjc.JVariableDeclarationStatement;
import org.multijava.mjc.JVariableDefinition;
import org.multijava.mjc.JWhileStatement;

import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

public class HasBreakStatementVisitor extends JmlAstClonerStatementVisitor {

	public boolean hasBreak = false;

	public HasBreakStatementVisitor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void visitBlockStatement(JBlock self){
		for (int idx = 0; idx < self.body().length; idx++){
			if (self.body()[idx] != null)
				self.body()[idx].accept(this);		
		}
	}




	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitTryCatchStatement(org.multijava.mjc.JTryCatchStatement)
	 */
	@Override
	public void visitTryCatchStatement(JTryCatchStatement self) {
		if (self.tryClause() != null){
			self.tryClause().accept(this);
		}
		for (int idx = 0; idx < self.catchClauses().length; idx++){
			if (self.catchClauses()[idx] != null){
				self.catchClauses()[idx].body().accept(this);
			}
		}
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitTryFinallyStatement(org.multijava.mjc.JTryFinallyStatement)
	 */
	@Override
	public void visitTryFinallyStatement(JTryFinallyStatement self) {
		if (self.tryClause() != null){
			self.tryClause().accept(this);
		}
		if (self.finallyClause() != null){
			self.finallyClause().accept(this);
		}
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitSwitchStatement(org.multijava.mjc.JSwitchStatement)
	 */
	@Override
	public void visitSwitchStatement(JSwitchStatement self) {
		for (int idx = 0; idx < self.groups().length; idx++)
			for (int idx2 = 0; idx2 < self.groups()[idx].getStatements().length; idx2++)
				if (self.groups()[idx].getStatements()[idx2] != null){
					self.groups()[idx].getStatements()[idx2].accept(this);
				}
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitIfStatement(org.multijava.mjc.JIfStatement)
	 */
	@Override
	public void visitIfStatement(JIfStatement self) {
		if (self.thenClause() != null){
			self.thenClause().accept(this);
		}
		if (self.elseClause() != null){
			self.elseClause().accept(this);
		}
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitForStatement(org.multijava.mjc.JForStatement)
	 */
	@Override
	public void visitForStatement(JForStatement self) {
		if (self.body() != null){
			self.body().accept(this);
		}
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitCompoundStatement(org.multijava.mjc.JCompoundStatement)
	 */
	@Override
	public void visitCompoundStatement(JCompoundStatement self) {
		for (int idx = 0; idx < self.body().length; idx++){
			if (self.body()[idx] != null){
				self.body()[idx].accept(this);
			}
		}
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitCompoundStatement(org.multijava.mjc.JStatement[])
	 */
	@Override
	public void visitCompoundStatement(JStatement[] body) {
		for (int idx = 0; idx < body.length; idx++){
			if (body[idx] != null){
				body[idx].accept(this);
			}
		}
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitDoStatement(org.multijava.mjc.JDoStatement)
	 */
	@Override
	public void visitDoStatement(JDoStatement self) {
		if (self.body() != null){
			self.body().accept(this);
		}
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitConstructorBlock(org.multijava.mjc.JConstructorBlock)
	 */
	@Override
	public void visitConstructorBlock(JConstructorBlock self) {
		for (int idx = 0; idx < self.body().length; idx++){
			if (self.body()[idx] != null){
				self.body()[idx].accept(this);
			}
		}
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitCatchClause(org.multijava.mjc.JCatchClause)
	 */
	@Override
	public void visitCatchClause(JCatchClause self) {
		if (self.body() != null){
			self.body().accept(this);
		}
	}



	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitLabeledStatement(org.multijava.mjc.JLabeledStatement)
	 */
	@Override
	public void visitLabeledStatement(JLabeledStatement self) {
		if (self.stmt() != null){
			self.stmt().accept(this);
		}
	}


	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitBreakStatement(org.multijava.mjc.JBreakStatement)
	 */
	@Override
	public void visitBreakStatement(JBreakStatement self) {
		this.hasBreak = true;
	}

}
