package ar.edu.taco.jml;

import org.jmlspecs.checker.JmlAssignmentStatement;
import org.jmlspecs.checker.JmlAssumeStatement;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JReturnStatement;
import org.multijava.mjc.JStatement;

import ar.edu.jdynalloy.ast.JAssume;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

public class StatementDistributeAboveReturnVisitor extends JmlAstClonerStatementVisitor {

	JmlAssignmentStatement theAssignment;
	
	public StatementDistributeAboveReturnVisitor(JmlAssignmentStatement newAssignment) {
		theAssignment = newAssignment;
	}
	
	
	
	public void visitReturnStatement(/* @non_null */JReturnStatement self) {
		JBlock newSelf = new JBlock(self.getTokenReference(), new JStatement[]{this.theAssignment, self}, self.getComments());
		this.getStack().push(newSelf);
	}


}
