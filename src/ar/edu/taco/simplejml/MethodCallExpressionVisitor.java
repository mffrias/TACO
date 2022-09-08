package ar.edu.taco.simplejml;

import org.multijava.mjc.JClassFieldExpression;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.mjc.JMethodCallExpressionExtension;

import ar.edu.taco.utils.jml.JmlAstClonerExpressionVisitor;

public class MethodCallExpressionVisitor extends JmlAstClonerExpressionVisitor {

	public MethodCallExpressionVisitor() {
		// TODO Auto-generated constructor stub
	}

	public void visitMethodCallExpression(/* @non_null */JMethodCallExpression self) {
		JExpression newPrefix;
		if (self.prefix() == null) {
			newPrefix = null;
		} else {
			self.prefix().accept(this);
			newPrefix = this.getArrayStack().pop();
		}

		JExpression[] newArgs = new JExpression[self.args().length];
		for (int i = 0; i < self.args().length; i++) {
			JExpression expression = self.args()[i];
//			if (expression instanceof JClassFieldExpression){
//				
//				JClassFieldParameterExpression e = new JClassFieldParameterExpression(expression.getTokenReference(), 
//						((JClassFieldExpression) expression).prefix(), ((JClassFieldExpression) expression).ident(), 
//						((JClassFieldExpression) expression).sourceName());
//					
//				e.setField(((JClassFieldExpression) expression).getField());
//				e.setType(((JClassFieldExpression) expression).getType());
//				e.setTypeBeforeViewpointAdaptation(((JClassFieldExpression) expression).getTypeBeforeViewpointAdaptation());
//				e.setPrefixWasBlank(((JClassFieldExpression) expression).prefixWasBlank());
//				
//				e.accept(this);
//			} else {
				expression.accept(this);
//			}	
			newArgs[i] = this.getArrayStack().pop();
		}

		JMethodCallExpressionExtension newSelf = new JMethodCallExpressionExtension(self, newPrefix, newArgs);
		// JMethodCallExpressionExtension newSelf = new
		// JMethodCallExpressionExtension(self.getTokenReference(),newPrefix,
		// self.ident(), newArgs, false );
		this.getArrayStack().push(newSelf);

	}
	
	
}
