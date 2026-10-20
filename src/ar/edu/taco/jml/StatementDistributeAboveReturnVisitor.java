package ar.edu.taco.jml;

import org.jmlspecs.checker.JmlAssumeStatement;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JReturnStatement;
import org.multijava.mjc.JStatement;

import ar.edu.jdynalloy.ast.JAssume;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

/**
 * Copies a statement and puts a given {@code assume} in front of every {@code return} in it:
 * {@code return e;} becomes {@code { //@ assume P; return e; }}, with the same assume object in
 * every place. {@link SpecMethodCallRemoverVisitor} uses it, on each statement of the body of the
 * method under analysis, for the assumptions about the final state; the assumption comes before
 * the returned expression is evaluated. No static state.
 */
public class StatementDistributeAboveReturnVisitor extends JmlAstClonerStatementVisitor {

	JmlAssumeStatement theAssume;
	
	/**
	 * Creates the visitor.
	 *
	 * @param newAssume the assumption to put in front of every {@code return}
	 */
	public StatementDistributeAboveReturnVisitor(JmlAssumeStatement newAssume) {
		theAssume = newAssume;
	}
	
	
	
	public void visitReturnStatement(/* @non_null */JReturnStatement self) {
		JBlock newSelf = new JBlock(self.getTokenReference(), new JStatement[]{this.theAssume, self}, self.getComments());
		this.getStack().push(newSelf);
	}


}
