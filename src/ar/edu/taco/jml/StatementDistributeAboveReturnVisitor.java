package ar.edu.taco.jml;

import org.jmlspecs.checker.JmlAssignmentStatement;
import org.jmlspecs.checker.JmlAssumeStatement;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JReturnStatement;
import org.multijava.mjc.JStatement;

import ar.edu.jdynalloy.ast.JAssume;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

/**
 * Copies a statement and puts a given assignment in front of every {@code return} in it:
 * {@code return e;} becomes {@code { f = c; return e; }}, with the same statement object in every
 * place. {@link SpecMethodCallRemoverVisitor} uses it, on each statement of the body of the
 * method under analysis, to give the fields that stand for pure-method calls of the
 * specification their final-state values (an assignment since master 5f62e261; an
 * {@code assume} before); the assignment comes before the returned expression is evaluated. No
 * static state.
 */
public class StatementDistributeAboveReturnVisitor extends JmlAstClonerStatementVisitor {

	JmlAssignmentStatement theAssignment;
	
	/**
	 * Creates the visitor.
	 *
	 * @param newAssignment the assignment to put in front of every {@code return}
	 */
	public StatementDistributeAboveReturnVisitor(JmlAssignmentStatement newAssignment) {
		theAssignment = newAssignment;
	}
	
	
	
	public void visitReturnStatement(/* @non_null */JReturnStatement self) {
		JBlock newSelf = new JBlock(self.getTokenReference(), new JStatement[]{this.theAssignment, self}, self.getComments());
		this.getStack().push(newSelf);
	}


}
