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

/**
 * Rewrites the compound assignments {@code x += e}, {@code x -= e}, {@code x *= e} and
 * {@code x /= e} that stand alone as statements into {@code x = x + e}, {@code x = x - e},
 * {@code x = x * e} and {@code x = x / e}, with JML's operator nodes ({@code JmlAddExpression} and
 * the like); the left side is reused, not copied, so it appears twice. A
 * {@code JmlAssignmentStatement} is unwrapped, rewritten the same way and wrapped again. Other
 * compound assignment statements ({@code %=}, shifts, bitwise operators) are left for
 * {@code BlockStatementsVisitor}, and compound assignments inside other expressions are left as
 * they are. The rest of the AST is copied ({@link JmlAstClonerStatementVisitor}).
 *
 * <p>Although it sits in this package, it is a JML simplification pass:
 * {@code jml.ASTSimplifierManager} runs it after {@code BlockSimplifier} and before
 * {@code GhostFieldsSimplifier}, during {@code engine.JmlStage} (docs/ARCHITECTURE.md). No state.
 */
public class ShortcutRemoverVisitor extends JmlAstClonerStatementVisitor {

	/**
	 * Pushes {@code x = x op e} for a statement {@code x op= e} with {@code op} one of {@code +},
	 * {@code -}, {@code *} and {@code /}, and the statement itself otherwise. The new statement
	 * keeps the old one's position, not its comments.
	 *
	 * @param self the expression statement
	 */
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
