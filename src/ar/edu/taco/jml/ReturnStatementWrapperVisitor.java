package ar.edu.taco.jml;

import org.jmlspecs.checker.JmlMethodDeclaration;
import org.jmlspecs.checker.JmlMethodSpecification;
import org.jmlspecs.checker.JmlSpecCase;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JBooleanLiteral;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JIfStatement;
import org.multijava.mjc.JNullLiteral;
import org.multijava.mjc.JOrdinalLiteral;
import org.multijava.mjc.JRealLiteral;
import org.multijava.mjc.JReturnStatement;
import org.multijava.mjc.JStatement;

import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

public class ReturnStatementWrapperVisitor extends JmlAstClonerStatementVisitor {

	@Override
	public void visitReturnStatement(/* @non_null */JReturnStatement self) {
		JIfStatement newSelf = new JIfStatement(self.getTokenReference(), new JBooleanLiteral(self.getTokenReference(), true), self, null, null);
		this.getStack().push(newSelf);
	}


	@Override
	public void visitJmlMethodDeclaration(JmlMethodDeclaration self) {
		JBlock newBody;
		if (self.body() == null) {
			newBody = null;
		} else {
			self.body().accept(this);
			newBody = (JBlock) this.getStack().pop();
		}

		JmlMethodSpecification methodSpecification;
		if (self.hasSpecification()) {
			self.methodSpecification().accept(this);
			methodSpecification = (JmlMethodSpecification) this.getStack().pop();
		} else {
			methodSpecification = null;
		}

		if (!self.returnType().isVoid()){
			JExpression defaultValueExpre = null;
			if (self.returnType().isReference())
				defaultValueExpre = new JNullLiteral(self.getTokenReference());
			if (self.returnType().isOrdinal())
				defaultValueExpre = new JOrdinalLiteral(self.getTokenReference(), "0");
			if (self.returnType().isFloatingPoint())
				defaultValueExpre = new JRealLiteral(self.getTokenReference(), "0f");
			if (self.returnType().isBoolean())
				defaultValueExpre = new JBooleanLiteral(self.getTokenReference(), false);

			JReturnStatement returnStatement = new JReturnStatement(self.getTokenReference(), defaultValueExpre, null);
			newBody = new JBlock(self.getTokenReference(), new JStatement[]{newBody, returnStatement}, null);
		}
		self.setBody(newBody);

		if (methodSpecification != null) {
			JmlSpecCase[] specCases = self.methodSpecification().specCases();

			for (int x = 0; x < methodSpecification.specCases().length; x++) {
				specCases[x] = methodSpecification.specCases()[x];
			}
		}

		this.getStack().push(self);
	}

}
