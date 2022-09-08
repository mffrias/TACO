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

public class LastStatementCollector extends JmlAstClonerStatementVisitor {

	Class<?> lastStatementClass = null;
	
	public LastStatementCollector() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void visitBlockStatement(JBlock self){
		if (self.body().length > 0)
			self.body()[self.body().length - 1].accept(this);		
	}


	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitJmlAssignmentStatement(org.jmlspecs.checker.JmlAssignmentStatement)
	 */
	@Override
	public void visitJmlAssignmentStatement(JmlAssignmentStatement self) {
		this.lastStatementClass = JmlAssignmentStatement.class;
	}



	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitWhileStatement(org.multijava.mjc.JWhileStatement)
	 */
	@Override
	public void visitWhileStatement(JWhileStatement self) {
		this.lastStatementClass = JWhileStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitTryCatchStatement(org.multijava.mjc.JTryCatchStatement)
	 */
	@Override
	public void visitTryCatchStatement(JTryCatchStatement self) {
		this.lastStatementClass = JTryCatchStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitTryFinallyStatement(org.multijava.mjc.JTryFinallyStatement)
	 */
	@Override
	public void visitTryFinallyStatement(JTryFinallyStatement self) {
		this.lastStatementClass = JTryFinallyStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitSynchronizedStatement(org.multijava.mjc.JSynchronizedStatement)
	 */
	@Override
	public void visitSynchronizedStatement(JSynchronizedStatement self) {
		this.lastStatementClass = JSynchronizedStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitSwitchStatement(org.multijava.mjc.JSwitchStatement)
	 */
	@Override
	public void visitSwitchStatement(JSwitchStatement self) {
		this.lastStatementClass = JSwitchStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitIfStatement(org.multijava.mjc.JIfStatement)
	 */
	@Override
	public void visitIfStatement(JIfStatement self) {
		this.lastStatementClass = JIfStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitForStatement(org.multijava.mjc.JForStatement)
	 */
	@Override
	public void visitForStatement(JForStatement self) {
		this.lastStatementClass = JForStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitCompoundStatement(org.multijava.mjc.JCompoundStatement)
	 */
	@Override
	public void visitCompoundStatement(JCompoundStatement self) {
		if (self.body().length > 0)
			self.body()[self.body().length-1].accept(this);
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitCompoundStatement(org.multijava.mjc.JStatement[])
	 */
	@Override
	public void visitCompoundStatement(JStatement[] body) {
		if (body.length > 0)
			body[body.length-1].accept(this);
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitDoStatement(org.multijava.mjc.JDoStatement)
	 */
	@Override
	public void visitDoStatement(JDoStatement self) {
		this.lastStatementClass = JDoStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitConstructorBlock(org.multijava.mjc.JConstructorBlock)
	 */
	@Override
	public void visitConstructorBlock(JConstructorBlock self) {
		if (self.body().length > 0){
			self.body()[self.body().length - 1].accept(this);
		}
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitCatchClause(org.multijava.mjc.JCatchClause)
	 */
	@Override
	public void visitCatchClause(JCatchClause self) {
		self.body().accept(this);
	}



	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitVariableDeclarationStatement(org.multijava.mjc.JVariableDeclarationStatement)
	 */
	@Override
	public void visitVariableDeclarationStatement(JVariableDeclarationStatement self) {
		this.lastStatementClass = JVariableDeclarationStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitVariableDefinition(org.multijava.mjc.JVariableDefinition)
	 */
	@Override
	public void visitVariableDefinition(JVariableDefinition self) {
		this.lastStatementClass = JVariableDefinition.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitAssertStatement(org.multijava.mjc.JAssertStatement)
	 */
	@Override
	public void visitAssertStatement(JAssertStatement self) {
		this.lastStatementClass = JAssertStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitReturnStatement(org.multijava.mjc.JReturnStatement)
	 */
	@Override
	public void visitReturnStatement(JReturnStatement self) {
		this.lastStatementClass = JReturnStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitLabeledStatement(org.multijava.mjc.JLabeledStatement)
	 */
	@Override
	public void visitLabeledStatement(JLabeledStatement self) {
		self.stmt().accept(this);
	}


	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitThrowStatement(org.multijava.mjc.JThrowStatement)
	 */
	@Override
	public void visitThrowStatement(JThrowStatement self) {
		this.lastStatementClass = JThrowStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitContinueStatement(org.multijava.mjc.JContinueStatement)
	 */
	@Override
	public void visitContinueStatement(JContinueStatement self) {
		this.lastStatementClass = JContinueStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitBreakStatement(org.multijava.mjc.JBreakStatement)
	 */
	@Override
	public void visitBreakStatement(JBreakStatement self) {
		this.lastStatementClass = JBreakStatement.class;
	}

}
