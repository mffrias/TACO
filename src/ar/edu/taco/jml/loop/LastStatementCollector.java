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

/**
 * Finds what kind of statement a statement ends with: after {@code accept},
 * {@link #lastStatementClass} is the class of the last statement, found by descending into the
 * last statement of blocks, compound statements, constructor bodies, catch clauses and labeled
 * statements. {@link WhileBlockVisitor} and {@link DoWhileBlockVisitor} use it to leave out the
 * re-evaluation of the loop condition after a body that ends with {@code break}.
 *
 * <p>Its overrides push nothing on the stack. Statements it does not override (expression
 * statements other than JML assignments, empty statements, JML assertions, assumptions and
 * annotated loops, ...) go to {@link JmlAstClonerStatementVisitor}, which pushes a copy that
 * nobody reads, and leave {@link #lastStatementClass} null.
 */
public class LastStatementCollector extends JmlAstClonerStatementVisitor {

	/**
	 * The class of the last statement found, or null if the statement is empty or of a kind this
	 * visitor does not record.
	 */
	Class<?> lastStatementClass = null;
	
	/** Creates a collector with no class found yet. */
	public LastStatementCollector() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Visits the last statement of the block, if there is one. Pushes nothing.
	 *
	 * @param self the block
	 */
	@Override
	public void visitBlockStatement(JBlock self){
		if (self.body().length > 0)
			self.body()[self.body().length - 1].accept(this);		
	}


	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitJmlAssignmentStatement(org.jmlspecs.checker.JmlAssignmentStatement)
	 */
	/**
	 * Records {@code JmlAssignmentStatement}. Pushes nothing.
	 *
	 * @param self the assignment
	 */
	@Override
	public void visitJmlAssignmentStatement(JmlAssignmentStatement self) {
		this.lastStatementClass = JmlAssignmentStatement.class;
	}



	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitWhileStatement(org.multijava.mjc.JWhileStatement)
	 */
	/**
	 * Records {@code JWhileStatement}. Pushes nothing.
	 *
	 * @param self the loop
	 */
	@Override
	public void visitWhileStatement(JWhileStatement self) {
		this.lastStatementClass = JWhileStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitTryCatchStatement(org.multijava.mjc.JTryCatchStatement)
	 */
	/**
	 * Records {@code JTryCatchStatement}. Pushes nothing.
	 *
	 * @param self the statement
	 */
	@Override
	public void visitTryCatchStatement(JTryCatchStatement self) {
		this.lastStatementClass = JTryCatchStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitTryFinallyStatement(org.multijava.mjc.JTryFinallyStatement)
	 */
	/**
	 * Records {@code JTryFinallyStatement}. Pushes nothing.
	 *
	 * @param self the statement
	 */
	@Override
	public void visitTryFinallyStatement(JTryFinallyStatement self) {
		this.lastStatementClass = JTryFinallyStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitSynchronizedStatement(org.multijava.mjc.JSynchronizedStatement)
	 */
	/**
	 * Records {@code JSynchronizedStatement}. Pushes nothing.
	 *
	 * @param self the statement
	 */
	@Override
	public void visitSynchronizedStatement(JSynchronizedStatement self) {
		this.lastStatementClass = JSynchronizedStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitSwitchStatement(org.multijava.mjc.JSwitchStatement)
	 */
	/**
	 * Records {@code JSwitchStatement}. Pushes nothing.
	 *
	 * @param self the switch statement
	 */
	@Override
	public void visitSwitchStatement(JSwitchStatement self) {
		this.lastStatementClass = JSwitchStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitIfStatement(org.multijava.mjc.JIfStatement)
	 */
	/**
	 * Records {@code JIfStatement}. Pushes nothing.
	 *
	 * @param self the if statement
	 */
	@Override
	public void visitIfStatement(JIfStatement self) {
		this.lastStatementClass = JIfStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitForStatement(org.multijava.mjc.JForStatement)
	 */
	/**
	 * Records {@code JForStatement}. Pushes nothing.
	 *
	 * @param self the loop
	 */
	@Override
	public void visitForStatement(JForStatement self) {
		this.lastStatementClass = JForStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitCompoundStatement(org.multijava.mjc.JCompoundStatement)
	 */
	/**
	 * Visits the last statement, if there is one. Pushes nothing.
	 *
	 * @param self the compound statement
	 */
	@Override
	public void visitCompoundStatement(JCompoundStatement self) {
		if (self.body().length > 0)
			self.body()[self.body().length-1].accept(this);
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitCompoundStatement(org.multijava.mjc.JStatement[])
	 */
	/**
	 * Visits the last statement of the array, if there is one. Pushes nothing.
	 *
	 * @param body the statements
	 */
	@Override
	public void visitCompoundStatement(JStatement[] body) {
		if (body.length > 0)
			body[body.length-1].accept(this);
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitDoStatement(org.multijava.mjc.JDoStatement)
	 */
	/**
	 * Records {@code JDoStatement}. Pushes nothing.
	 *
	 * @param self the do-while loop
	 */
	@Override
	public void visitDoStatement(JDoStatement self) {
		this.lastStatementClass = JDoStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitConstructorBlock(org.multijava.mjc.JConstructorBlock)
	 */
	/**
	 * Visits the last statement of the constructor body, if there is one. Pushes nothing.
	 *
	 * @param self the constructor body
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
	/**
	 * Visits the body of the catch clause. Pushes nothing.
	 *
	 * @param self the catch clause
	 */
	@Override
	public void visitCatchClause(JCatchClause self) {
		self.body().accept(this);
	}



	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitVariableDeclarationStatement(org.multijava.mjc.JVariableDeclarationStatement)
	 */
	/**
	 * Records {@code JVariableDeclarationStatement}. Pushes nothing.
	 *
	 * @param self the declaration
	 */
	@Override
	public void visitVariableDeclarationStatement(JVariableDeclarationStatement self) {
		this.lastStatementClass = JVariableDeclarationStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitVariableDefinition(org.multijava.mjc.JVariableDefinition)
	 */
	/**
	 * Records {@code JVariableDefinition}. Pushes nothing.
	 *
	 * @param self the variable definition
	 */
	@Override
	public void visitVariableDefinition(JVariableDefinition self) {
		this.lastStatementClass = JVariableDefinition.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitAssertStatement(org.multijava.mjc.JAssertStatement)
	 */
	/**
	 * Records {@code JAssertStatement}. Pushes nothing.
	 *
	 * @param self the assert statement
	 */
	@Override
	public void visitAssertStatement(JAssertStatement self) {
		this.lastStatementClass = JAssertStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitReturnStatement(org.multijava.mjc.JReturnStatement)
	 */
	/**
	 * Records {@code JReturnStatement}. Pushes nothing.
	 *
	 * @param self the return statement
	 */
	@Override
	public void visitReturnStatement(JReturnStatement self) {
		this.lastStatementClass = JReturnStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitLabeledStatement(org.multijava.mjc.JLabeledStatement)
	 */
	/**
	 * Visits the statement that carries the label. Pushes nothing.
	 *
	 * @param self the labeled statement
	 */
	@Override
	public void visitLabeledStatement(JLabeledStatement self) {
		self.stmt().accept(this);
	}


	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitThrowStatement(org.multijava.mjc.JThrowStatement)
	 */
	/**
	 * Records {@code JThrowStatement}. Pushes nothing.
	 *
	 * @param self the throw statement
	 */
	@Override
	public void visitThrowStatement(JThrowStatement self) {
		this.lastStatementClass = JThrowStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitContinueStatement(org.multijava.mjc.JContinueStatement)
	 */
	/**
	 * Records {@code JContinueStatement}. Pushes nothing.
	 *
	 * @param self the continue statement
	 */
	@Override
	public void visitContinueStatement(JContinueStatement self) {
		this.lastStatementClass = JContinueStatement.class;
	}

	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitBreakStatement(org.multijava.mjc.JBreakStatement)
	 */
	/**
	 * Records {@code JBreakStatement}. Pushes nothing.
	 *
	 * @param self the break statement
	 */
	@Override
	public void visitBreakStatement(JBreakStatement self) {
		this.lastStatementClass = JBreakStatement.class;
	}

}
