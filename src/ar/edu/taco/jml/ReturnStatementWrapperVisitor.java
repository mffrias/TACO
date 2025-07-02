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

//	@Override
//	public void visitReturnStatement(/* @non_null */JReturnStatement self) {
//		JIfStatement newSelf = new JIfStatement(self.getTokenReference(), new JBooleanLiteral(self.getTokenReference(), true), self, null, null);
//		this.getStack().push(newSelf);
//	}


//	@Override
//	public void visitJmlMethodDeclaration(JmlMethodDeclaration self) {
//		JBlock newBody;
//		if (self.body() == null) {
//			newBody = null;
//		} else {
//			self.body().accept(this);
//			newBody = (JBlock) this.getStack().pop();
//		}
//
//		JmlMethodSpecification methodSpecification;
//		if (self.hasSpecification()) {
//			self.methodSpecification().accept(this);
//			methodSpecification = (JmlMethodSpecification) this.getStack().pop();
//		} else {
//			methodSpecification = null;
//		}
//
//		if (!self.returnType().isVoid()){
//			boolean alreadyHasReturn = false;
//			for (JStatement stmt : newBody.body()){
//				if (stmt instanceof JReturnStatement || (stmt instanceof  JIfStatement && ((JIfStatement) stmt).thenClause() instanceof JReturnStatement)) {
//					alreadyHasReturn = true;
//					break;
//				}
//			}
//
//			if (!alreadyHasReturn) {
//
//				JExpression defaultValueExpre = null;
//				if (self.returnType().isReference())
//					defaultValueExpre = new JNullLiteral(self.getTokenReference());
//				if (self.returnType().isOrdinal())
//					defaultValueExpre = new JOrdinalLiteral(self.getTokenReference(), "0");
//				if (self.returnType().isFloatingPoint())
//					defaultValueExpre = new JRealLiteral(self.getTokenReference(), "0f");
//				if (self.returnType().isBoolean())
//					defaultValueExpre = new JBooleanLiteral(self.getTokenReference(), false);
//
//				JReturnStatement returnStatement = new JReturnStatement(self.getTokenReference(), defaultValueExpre, null);
//
//					JStatement[] stmts = newBody.body();
//					JStatement[] extended = new JStatement[stmts.length + 1];
//
//					System.arraycopy(stmts, 0, extended, 0, stmts.length);
//					extended[stmts.length] = returnStatement;
//
//					newBody = new JBlock(self.getTokenReference(), extended, newBody.getComments());
//			}
//
//		}
//		self.setBody(newBody);
//
//		if (methodSpecification != null) {
//			JmlSpecCase[] specCases = self.methodSpecification().specCases();
//
//			for (int x = 0; x < methodSpecification.specCases().length; x++) {
//				specCases[x] = methodSpecification.specCases()[x];
//			}
//		}
//
//		this.getStack().push(self);
//	}

	@Override
	public void visitJmlMethodDeclaration(JmlMethodDeclaration self) {
		JBlock newBody;
		if (self.body() == null) {
			newBody = null;
		} else {
			self.body().accept(this);
			//Get transformed bodies from the stack
			newBody = (JBlock) this.getStack().pop();
		}

		JmlMethodSpecification methodSpecification;
		if (self.hasSpecification()) {
			self.methodSpecification().accept(this);
			methodSpecification = (JmlMethodSpecification) this.getStack().pop();
		} else {
			methodSpecification = null;
		}

		//if the method is non-void and body does not always return, append default return
		if (!self.returnType().isVoid() && newBody != null && !alwaysReturns(newBody)) {
			JExpression defaultValueExpre = null;

			//build a default return value based on return type
			if (self.returnType().isReference()) {
				defaultValueExpre = new JNullLiteral(self.getTokenReference());
			} else if (self.returnType().isOrdinal()) {
				defaultValueExpre = new JOrdinalLiteral(self.getTokenReference(), "0");
			} else if (self.returnType().isFloatingPoint()) {
				defaultValueExpre = new JRealLiteral(self.getTokenReference(), "0f");
			} else if (self.returnType().isBoolean()) {
				defaultValueExpre = new JBooleanLiteral(self.getTokenReference(), false);
			}

			JReturnStatement returnStatement = new JReturnStatement(self.getTokenReference(), defaultValueExpre, null);

			JStatement[] stmts = newBody.body();
			JStatement[] extended = new JStatement[stmts.length + 1];
			System.arraycopy(stmts, 0, extended, 0, stmts.length);
			extended[stmts.length] = returnStatement;

			newBody = new JBlock(self.getTokenReference(), extended, newBody.getComments());
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

	private boolean alwaysReturns(JStatement stmt) {
		//checks if the statement is a return or a throw
		if (stmt instanceof JReturnStatement || stmt instanceof org.multijava.mjc.JThrowStatement) {
			return true;
		}

		//check if the last statement inside returns
		if (stmt instanceof JBlock) {
			JStatement[] stmts = ((JBlock) stmt).body();
			boolean lastReturns = false;
			for (JStatement s : stmts) {
				lastReturns = alwaysReturns(s);
			}
			return lastReturns; //only return true if the last one returns
		}

		//both branches must return
		if (stmt instanceof JIfStatement) {
			JIfStatement ifStmt = (JIfStatement) stmt;
			// both branches must be non-null and always return
			return ifStmt.elseClause() != null &&
					alwaysReturns(ifStmt.thenClause()) &&
					alwaysReturns(ifStmt.elseClause());
		}

		//all other cases
		return false;
	}


}
