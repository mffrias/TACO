package ar.edu.taco.jml;

import ar.edu.jdynalloy.ast.JSkip;
import ar.edu.jdynalloy.factory.JExpressionFactory;
import org.jmlspecs.checker.JmlAssignmentStatement;
import org.jmlspecs.checker.JmlAssumeStatement;
import org.multijava.mjc.*;

import ar.edu.jdynalloy.ast.JAssume;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;
import org.multijava.util.compiler.JavaStyleComment;

public class StatementDistributeAboveReturnVisitor extends JmlAstClonerStatementVisitor {

	JmlAssignmentStatement theAssignment;
	
	public StatementDistributeAboveReturnVisitor(JmlAssignmentStatement newAssignment) {
		theAssignment = newAssignment;
	}
	
	
	
	public void visitReturnStatement(/* @non_null */JReturnStatement self) {
//		JLocalVariable exit_stmt_reached = new JVariableDefinition(self.getTokenReference(), 0L, CStdType.Boolean, "exit_stmt_reached", new JBooleanLiteral(self.getTokenReference(), false));
//		JLocalVariableExpression exitLocalVarExpre = new JLocalVariableExpression(self.getTokenReference(), exit_stmt_reached);
//		JAssignmentExpression exitAssExpre = new JAssignmentExpression(self.getTokenReference(), exitLocalVarExpre, new JBooleanLiteral(self.getTokenReference(), false));
//		JExpressionStatement exitExprStat = new JExpressionStatement(self.getTokenReference(), exitAssExpre, null);
//		JmlAssignmentStatement exitStmtFalse = new JmlAssignmentStatement(exitExprStat);
		JBlock newSelf = new JBlock(self.getTokenReference(), new JStatement[]{this.theAssignment, self}, self.getComments());
		this.getStack().push(newSelf);
	}


	//When the method is void, a copy of the distributed statement is added to the end of the program.
	//This fails if the program had a throw as the last statetement because the added statement becomes dead code.
	//Adding "if (true) throw" makes the added code no longer dead.
	public void visitThrowStatement(JThrowStatement self) {
		this.getStack().push(new JIfStatement(self.getTokenReference(), new JBooleanLiteral(self.getTokenReference(), true), self, null, new JavaStyleComment[0]));

	}


}
