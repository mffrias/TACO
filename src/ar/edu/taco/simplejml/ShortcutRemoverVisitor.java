package ar.edu.taco.simplejml;


import org.jmlspecs.checker.JmlAddExpression;
import org.jmlspecs.checker.JmlAssignmentStatement;
import org.jmlspecs.checker.JmlDivideExpression;
import org.jmlspecs.checker.JmlMinusExpression;
import org.jmlspecs.checker.JmlMultExpression;
import org.multijava.mjc.JAssignmentExpression;
import org.multijava.mjc.JCompoundAssignmentExpression;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JExpressionStatement;
import org.multijava.util.compiler.JavaStyleComment;

import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

public class ShortcutRemoverVisitor extends JmlAstClonerStatementVisitor {

	public void visitExpressionStatement(/* @non_null */JExpressionStatement self) {
		JExpression e = self.expr();

		if (e instanceof JCompoundAssignmentExpression){
			switch (((JCompoundAssignmentExpression)e).oper()){
			case 1: this.getStack().push(
					new JExpressionStatement(
							self.getTokenReference(),
							new JAssignmentExpression(
									self.getTokenReference(), 
									((JCompoundAssignmentExpression) e).left(), 
									new JmlAddExpression(
											self.getTokenReference(), 
											((JCompoundAssignmentExpression) e).left(), 
											((JCompoundAssignmentExpression) e).right()
											)
									),
							new JavaStyleComment[0]
							)
					); 
			break;

			case 2: this.getStack().push(
					new JExpressionStatement(
							self.getTokenReference(),
							new JAssignmentExpression(
									self.getTokenReference(), 
									((JCompoundAssignmentExpression) e).left(), 
									new JmlMinusExpression(
											self.getTokenReference(), 
											((JCompoundAssignmentExpression) e).left(), 
											((JCompoundAssignmentExpression) e).right()
											)
									),
							new JavaStyleComment[0]
							)
					); 
			break;

			case 3: this.getStack().push(
					new JExpressionStatement(
							self.getTokenReference(),
							new JAssignmentExpression(
									self.getTokenReference(), 
									((JCompoundAssignmentExpression) e).left(), 
									new JmlMultExpression(
											self.getTokenReference(), 
											((JCompoundAssignmentExpression) e).left(), 
											((JCompoundAssignmentExpression) e).right()
											)
									),
							new JavaStyleComment[0]
							)
					); 
			break;

			case 4: this.getStack().push(
					new JExpressionStatement(
							self.getTokenReference(),
							new JAssignmentExpression(
									self.getTokenReference(), 
									((JCompoundAssignmentExpression) e).left(), 
									new JmlDivideExpression(
											self.getTokenReference(), 
											((JCompoundAssignmentExpression) e).left(), 
											((JCompoundAssignmentExpression) e).right()
											)
									),
							new JavaStyleComment[0]
							)
					); 
			break;
			
			default : getStack().push(self);
			}
		} else {
			getStack().push(self);
		}
	}

	
	public void visitJmlAssignmentStatement(JmlAssignmentStatement self) {
		self.assignmentStatement().accept(this);
		JExpressionStatement theStatement = (JExpressionStatement) this.getStack().pop();
		this.getStack().push(new JmlAssignmentStatement(theStatement));
	}


}
