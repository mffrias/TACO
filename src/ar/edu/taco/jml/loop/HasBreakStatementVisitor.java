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
 * Tells whether a statement contains a {@code break}: after {@code accept}, {@link #hasBreak} is
 * true if one was found. {@code simplejml.BlockStatementsVisitor} uses it on the body of each
 * {@code while} and do-while loop it translates, to decide whether the loop needs a
 * {@code break_reached} variable; by then {@link BreakRemoverSimplifier} has removed the breaks
 * of the {@code while} loops it reaches.
 *
 * <p>It searches nested statements (blocks, {@code if}, {@code try}, {@code switch}, labeled
 * statements, {@code for} and do-while loops) without copying them: its overrides push nothing on
 * the stack, unlike those of {@link JmlAstClonerStatementVisitor}. A {@code break} of a nested
 * loop or {@code switch} counts too, and labels are not looked at. Statements it does not
 * override go to the cloner: an annotated loop ({@code JmlLoopStatement}) is not entered, and a
 * nested {@code while} loop goes to the cloner's {@code visitWhileStatement}, which pops a copy
 * of the body that this visitor does not push for a block.
 */
public class HasBreakStatementVisitor extends JmlAstClonerStatementVisitor {

	/** Whether a {@code break} was found. Set by {@link #visitBreakStatement}, never reset. */
	public boolean hasBreak = false;

	/** Creates a visitor with {@link #hasBreak} false. */
	public HasBreakStatementVisitor() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Visits each statement of the block. Pushes nothing.
	 *
	 * @param self the block
	 */
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
	/**
	 * Visits the try block and the body of each catch clause. Pushes nothing.
	 *
	 * @param self the statement
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
	/**
	 * Visits the try block and the finally block. Pushes nothing.
	 *
	 * @param self the statement
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
	/**
	 * Visits the statements of every switch group. Pushes nothing.
	 *
	 * @param self the switch statement
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
	/**
	 * Visits both branches. Pushes nothing.
	 *
	 * @param self the if statement
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
	/**
	 * Visits the loop body, not its initialization or increment. Pushes nothing.
	 *
	 * @param self the loop
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
	/**
	 * Visits each statement. Pushes nothing.
	 *
	 * @param self the compound statement
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
	/**
	 * Visits each statement of the array. Pushes nothing.
	 *
	 * @param body the statements
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
	/**
	 * Visits the loop body. Pushes nothing.
	 *
	 * @param self the do-while loop
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
	/**
	 * Visits each statement of the constructor body. Pushes nothing.
	 *
	 * @param self the constructor body
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
	/**
	 * Visits the body of the catch clause. Pushes nothing.
	 *
	 * @param self the catch clause
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
	/**
	 * Visits the statement that carries the label. Pushes nothing.
	 *
	 * @param self the labeled statement
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
	/**
	 * Sets {@link #hasBreak}. Pushes nothing.
	 *
	 * @param self the break statement
	 */
	@Override
	public void visitBreakStatement(JBreakStatement self) {
		this.hasBreak = true;
	}

}
