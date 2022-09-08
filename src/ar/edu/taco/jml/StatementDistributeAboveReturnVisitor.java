package ar.edu.taco.jml;

import org.jmlspecs.checker.JmlAssumeStatement;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JReturnStatement;
import org.multijava.mjc.JStatement;

import ar.edu.jdynalloy.ast.JAssume;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

public class StatementDistributeAboveReturnVisitor extends JmlAstClonerStatementVisitor {

	JmlAssumeStatement theAssume;
	
	public StatementDistributeAboveReturnVisitor(JmlAssumeStatement newAssume) {
		theAssume = newAssume;
	}
	
	
	
	public void visitReturnStatement(/* @non_null */JReturnStatement self) {
		JBlock newSelf = new JBlock(self.getTokenReference(), new JStatement[]{this.theAssume, self}, self.getComments());
		this.getStack().push(newSelf);
	}


}
