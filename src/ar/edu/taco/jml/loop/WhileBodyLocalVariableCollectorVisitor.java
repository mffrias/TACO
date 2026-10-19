package ar.edu.taco.jml.loop;

import java.util.ArrayList;

import org.multijava.mjc.JVariableDeclarationStatement;

import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

/**
 * Collects the local variable declarations of a loop body, for {@link WhileRemoverSimplifier},
 * which renames those variables in each unrolled copy of the body ({@code VNWhileBlockVisitor}).
 * It copies the statements like {@link JmlAstClonerStatementVisitor} and records every
 * {@code JVariableDeclarationStatement} it reaches, those of nested blocks and loops included;
 * the copy it pushes is discarded by its caller.
 */
public class WhileBodyLocalVariableCollectorVisitor extends JmlAstClonerStatementVisitor {

	
	private ArrayList<JVariableDeclarationStatement> localVars = new ArrayList<JVariableDeclarationStatement>();
	
	/**
	 * Returns the declarations recorded so far, in the order visited.
	 *
	 * @return the list itself, not a copy
	 */
	public ArrayList<JVariableDeclarationStatement> getLocalVars(){
		return this.localVars;
	}
	
	/**
	 * Records the declaration and pushes it unchanged.
	 *
	 * @param self a local variable declaration
	 */
	public void visitVariableDeclarationStatement(/* @non_null */JVariableDeclarationStatement self) {
		this.localVars.add(self);
		this.getStack().push(self);

	}	
}
