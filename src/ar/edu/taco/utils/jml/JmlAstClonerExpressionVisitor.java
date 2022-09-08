/*
 * TACO: Translation of Annotated COde
 * Copyright (c) 2010 Universidad de Buenos Aires
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA,
 * 02110-1301, USA
 */
package ar.edu.taco.utils.jml;

import java.util.Stack;

import org.jmlspecs.checker.JmlInformalExpression;
import org.jmlspecs.checker.JmlName;
import org.jmlspecs.checker.JmlOldExpression;
import org.jmlspecs.checker.JmlPredicate;
import org.jmlspecs.checker.JmlReachExpression;
import org.jmlspecs.checker.JmlRelationalExpression;
import org.jmlspecs.checker.JmlResultExpression;
import org.jmlspecs.checker.JmlSpecExpression;
import org.jmlspecs.checker.JmlSpecQuantifiedExpression;
import org.jmlspecs.checker.JmlStoreRefExpression;
import org.jmlspecs.checker.JmlVariableDefinition;
import org.multijava.mjc.CClass;
import org.multijava.mjc.CClassType;
import org.multijava.mjc.CStdType;
import org.multijava.mjc.CTopLevel;
import org.multijava.mjc.CType;
import org.multijava.mjc.JAddExpression;
import org.multijava.mjc.JArrayAccessExpression;
import org.multijava.mjc.JArrayAccessExpressionExtension;
import org.multijava.mjc.JArrayDimsAndInits;
import org.multijava.mjc.JArrayDimsAndInitsExtension;
import org.multijava.mjc.JArrayInitializer;
import org.multijava.mjc.JArrayLengthExpression;
import org.multijava.mjc.JAssignmentExpression;
import org.multijava.mjc.JBinaryExpression;
import org.multijava.mjc.JBitwiseExpression;
import org.multijava.mjc.JBooleanLiteral;
import org.multijava.mjc.JCastExpression;
import org.multijava.mjc.JCharLiteral;
import org.multijava.mjc.JClassExpression;
import org.multijava.mjc.JClassFieldExpression;
import org.multijava.mjc.JCompoundAssignmentExpression;
import org.multijava.mjc.JConditionalAndExpression;
import org.multijava.mjc.JConditionalExpression;
import org.multijava.mjc.JConditionalOrExpression;
import org.multijava.mjc.JDivideExpression;
import org.multijava.mjc.JEqualityExpression;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JInstanceofExpression;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.mjc.JMethodCallExpressionExtension;
import org.multijava.mjc.JMinusExpression;
import org.multijava.mjc.JModuloExpression;
import org.multijava.mjc.JMultExpression;
import org.multijava.mjc.JNameExpression;
import org.multijava.mjc.JNewAnonymousClassExpression;
import org.multijava.mjc.JNewArrayExpression;
import org.multijava.mjc.JNewObjectExpression;
import org.multijava.mjc.JNullLiteral;
import org.multijava.mjc.JOrdinalLiteral;
import org.multijava.mjc.JParenthesedExpression;
import org.multijava.mjc.JPostfixExpression;
import org.multijava.mjc.JPrefixExpression;
import org.multijava.mjc.JRealLiteral;
import org.multijava.mjc.JRelationalExpression;
import org.multijava.mjc.JShiftExpression;
import org.multijava.mjc.JStringLiteral;
import org.multijava.mjc.JSuperExpression;
import org.multijava.mjc.JThisExpression;
import org.multijava.mjc.JTypeNameExpression;
import org.multijava.mjc.JUnaryExpression;
import org.multijava.mjc.JUnaryPromote;
import org.multijava.mjc.MJMathModeExpression;
import org.multijava.mjc.MJWarnExpression;
import org.multijava.util.compiler.TokenReference;

import ar.edu.taco.TacoException;
import ar.edu.taco.TacoNotImplementedYetException;
//import ar.edu.taco.simplejml.JClassFieldParameterExpression;
import ar.edu.taco.simplejml.JmlBaseVisitor;
import ar.edu.taco.utils.IdentitySet;

import java.lang.reflect.*;


public class JmlAstClonerExpressionVisitor extends JmlBaseVisitor {

	private Stack<JExpression> arrayStack = new Stack<JExpression>();

	/**
	 * Indicate if the held function should be applied to the old state of variables
	 */
	private boolean oldModifierPresent = false;

	private IdentitySet<JMethodCallExpression> methodCallExpressionsProcessed;

	public IdentitySet<JMethodCallExpression> getMethodCallExpressionsProcessed() {
		return methodCallExpressionsProcessed;
	}

	public void setMethodCallExpressionsProcessed(IdentitySet<JMethodCallExpression> methodCallExpressionsProcessed) {
		this.methodCallExpressionsProcessed = methodCallExpressionsProcessed;
	}


	public Stack<JExpression> getArrayStack() {
		return arrayStack;
	}

	/**
	 * @return the oldModifierPresent
	 */
	public boolean isOldModifierPresent() {
		return oldModifierPresent;
	}


	// ----------------------------------------------------------------------
	// EXPRESSION
	// ----------------------------------------------------------------------

	/** Visits the given unary expression. */
	public void visitUnaryExpression(/* @non_null */JUnaryExpression self) {
		self.expr().accept(this);
		JUnaryExpression newSelf = new JUnaryExpression(self.getTokenReference(), self.oper(), this.getArrayStack().pop());
		this.getArrayStack().push(newSelf);
	}

	/** Visits the given type name expression. */
	public void visitTypeNameExpression(/* @non_null */JTypeNameExpression self) {
		this.getArrayStack().push(self);
	}

	/** Visits the given this expression. */
	public void visitThisExpression(/* @non_null */JThisExpression self) {
		this.getArrayStack().push(self);
	}

	/** Visits the given super expression. */
	public void visitSuperExpression(/* @non_null */JSuperExpression self) {
		this.getArrayStack().push(self);
	}

	/** Visits the given shift expression. */
	public void visitShiftExpression(/* @non_null */JShiftExpression self) {
		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();

		JShiftExpression newSelf = new JShiftExpression(self.getTokenReference(), self.oper(), left, right);
		this.getArrayStack().push(newSelf);
	}

	/** Visits the given relational expression. */
	public void visitRelationalExpression(/* @non_null */JRelationalExpression self) {
		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();

		JRelationalExpression newSelf = new JRelationalExpression(self.getTokenReference(), self.oper(), left, right);
		this.getArrayStack().push(newSelf);
	}

	/** Visits the given prefix expression. */
	public void visitPrefixExpression(/* @non_null */JPrefixExpression self) {
		self.expr().accept(this);
		JPrefixExpression newSelf = new JPrefixExpression(self.getTokenReference(), self.oper(), this.getArrayStack().pop());
		this.getArrayStack().push(newSelf);
	}

	/** Visits the given postfix expression. */
	public void visitPostfixExpression(/* @non_null */JPostfixExpression self) {
		self.expr().accept(this);
		JPostfixExpression newSelf = new JPostfixExpression(self.getTokenReference(), self.oper(), this.getArrayStack().pop());
		this.getArrayStack().push(newSelf);

	}

	/** Visits the given parenthesed expression. */
	public void visitParenthesedExpression(/* @non_null */JParenthesedExpression self) {
		self.expr().accept(this);
		JParenthesedExpression newSelf = new JParenthesedExpression(self.getTokenReference(), this.getArrayStack().pop());
		this.getArrayStack().push(newSelf);

	}

	/** Visits the given object allocator expression. */
	public void visitNewObjectExpression(/* @non_null */JNewObjectExpression self) {
		JExpression newThisExpr;
		if (self.thisExpr() == null) {
			newThisExpr = null;
		} else {
			self.thisExpr().accept(this);
			newThisExpr = this.getArrayStack().pop();
		}
		JExpression[] newParams = new JExpression[self.params().length];
		for (int i = 0; i < self.params().length; i++) {
			JExpression expression = self.params()[i];
			expression.accept(this);
			newParams[i] = this.getArrayStack().pop();
		}
		
		CType newType1 = self.getType();
//		CClassType newType = CTopLevel.getTypeRep(self.getType().getCClass().getJavaName(), true);
		JNewObjectExpression newSelf = new JNewObjectExpression(self.getTokenReference(), (CClassType) newType1, newThisExpr, newParams);
		this.getArrayStack().push(newSelf);
	}

	/** Visits the given object allocator expression for an anonymous class. */
	public void visitNewAnonymousClassExpression(/* @non_null */JNewAnonymousClassExpression self) {
		throw new TacoNotImplementedYetException();
	}

	/** Visits the given array allocator expression. */
	public void visitNewArrayExpression(/* @non_null */JNewArrayExpression self) {

		JExpression[] newDims = new JExpression[self.dims().dims().length];
		for (int i = 0; i < self.dims().dims().length; i++) {
			JExpression expression = self.dims().dims()[i];
			if (expression == null) {
				newDims[i] = null;
			} else {
				expression.accept(this);
				newDims[i] = this.getArrayStack().pop();
			}
		}

		JArrayInitializer newInit;
		if (self.dims().init() == null) {
			newInit = null;
		} else {
			self.dims().init().accept(this);
			newInit = (JArrayInitializer) this.getArrayStack().pop();
		}

		JArrayDimsAndInitsExtension newDimsAndInits = new JArrayDimsAndInitsExtension(self.dims(), newDims, newInit);

		JNewArrayExpression newSelf = new JNewArrayExpression(self.getTokenReference(), self.getType(), newDimsAndInits);
		this.getArrayStack().push(newSelf);
	}

	@Override
	public void visitArrayInitializer(JArrayInitializer self) {
		JExpression[] newElems = new JExpression[self.elems().length];
		for (int i = 0; i < self.elems().length; i++) {
			JExpression expression = self.elems()[i];
			expression.accept(this);
			newElems[i] = this.getArrayStack().pop();
		}
		JArrayInitializer newSelf = new JArrayInitializer(self.getTokenReference(), newElems);
		this.getArrayStack().push(newSelf);
	}

	@Override
	public void visitArrayDimsAndInit(JArrayDimsAndInits self) {

		// for (JExpression expression : self.dims()) {
		// expression.accept(this);
		// }
		// self.init().accept(this);
		throw new TacoNotImplementedYetException("This code never should be invoked. See visitNewArrayExpression.");
	}

	/** Visits the given name expression. */
	public void visitNameExpression(/* @non_null */JNameExpression self) {
		this.getArrayStack().push(self);
	}

	/** Visits the given binary expression with the given operator. */
	protected void visitBinaryExpression(/* @non_null */JBinaryExpression self, String oper) {
		throw new TacoException("This code never should be invoked. JBinaryExpression is an abstract class!");
		/*
		 * self.left().accept(this); self.right().accept(this);
		 * 
		 * JBinaryExpression newSelf = new
		 * JBinaryExpression(self.getTokenReference(), self.(),
		 * this.getArrayStack().pop(), this.getArrayStack().pop());
		 * this.getArrayStack().push(newSelf);
		 */
	}

	/** Visits the given add expression. */
	public void visitAddExpression(/* @non_null */JAddExpression self) {
		JAddExpression newSelf = (JAddExpression) self.clone();
		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();
		newSelf.setLeft(left);

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();
		newSelf.setRight(right);

		this.getArrayStack().push(newSelf);
	}

	/** Visits the given boolean AND expression. */
	public void visitConditionalAndExpression(/* @non_null */JConditionalAndExpression self) {
		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();

		JConditionalAndExpression newSelf = new JConditionalAndExpression(self.getTokenReference(), left, right);
		newSelf.setType(CStdType.Boolean);
		this.getArrayStack().push(newSelf);
	}

	/** Visits the given boolean OR expression. */
	public void visitConditionalOrExpression(/* @non_null */JConditionalOrExpression self) {
		JConditionalOrExpression newSelf = (JConditionalOrExpression)self.clone();
		
		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();
		
		newSelf.setLeft(left);
		newSelf.setRight(right);
		
		this.getArrayStack().push(newSelf);
	}

	/** Visits the given divide expression. */
	public void visitDivideExpression(/* @non_null */JDivideExpression self) {
		JDivideExpression newSelf = (JDivideExpression)self.clone();
		
		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();

		newSelf.setLeft(left);
		newSelf.setRight(right);
		this.getArrayStack().push(newSelf);
	}

	/** Visits the given minus expression. */
	public void visitMinusExpression(/* @non_null */JMinusExpression self) {
		JMinusExpression newSelf = (JMinusExpression)self.clone();

		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();
		newSelf.setLeft(left);

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();
		newSelf.setRight(right);
		this.getArrayStack().push(newSelf);
	}

	/** Visits the given modulo division expression. */
	public void visitModuloExpression(/* @non_null */JModuloExpression self) {
		JModuloExpression newSelf = (JModuloExpression)self.clone();
		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();

		newSelf.setLeft(left);
		newSelf.setRight(right);
		this.getArrayStack().push(newSelf);
	}

	/** Visits the given multiplication expression. */
	public void visitMultExpression(/* @non_null */JMultExpression self) {
		JMultExpression newSelf = (JMultExpression)self.clone();
		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();
		
		newSelf.setLeft(left);
		newSelf.setRight(right);

		this.getArrayStack().push(newSelf);
	}

	/** Visits the given method call expression. */
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

		JMethodCallExpression newSelf = new JMethodCallExpressionExtension(self, newPrefix, newArgs);


		// JMethodCallExpressionExtension newSelf = new
		// JMethodCallExpressionExtension(self.getTokenReference(),newPrefix,
		// self.ident(), newArgs, false );
		this.getArrayStack().push(newSelf);

	}

	/** Visits the given local variable expression. */
	public void visitLocalVariableExpression(/* @non_null */JLocalVariableExpression self) {

		//JLocalVariableExpression newSelf = new JLocalVariableExpression(self.getTokenReference(), self.variable());
		this.getArrayStack().push(self);
	}

	/** Visits the given instanceof expression. */
	public void visitInstanceofExpression(/* @non_null */JInstanceofExpression self) {
		self.expr().accept(this);
		JInstanceofExpression newSelf = new JInstanceofExpression(self.getTokenReference(), this.getArrayStack().pop(), self.dest());
		this.getArrayStack().push(newSelf);
	}

	/** Visits the given equality expression. */
	public void visitEqualityExpression(/* @non_null */JEqualityExpression self) {
		JEqualityExpression newSelf = (JEqualityExpression)self.clone();
		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();

		newSelf.setLeft(left);
		newSelf.setRight(right);
		this.getArrayStack().push(newSelf);
	}

	/** Visits the given conditional expression. */
	public void visitConditionalExpression(/* @non_null */JConditionalExpression self) {
		
		self.cond().accept(this);
		JExpression cond = this.getArrayStack().pop();

		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();

		JConditionalExpression newSelf = new JConditionalExpression(self.getTokenReference(), cond, left, right);
		newSelf.setType(self.left().getType());
		this.getArrayStack().push(newSelf);
	}

	/** Visits the given compound expression. */
	public void visitCompoundAssignmentExpression(/* @non_null */JCompoundAssignmentExpression self) {
		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();

		JCompoundAssignmentExpression newSelf = new JCompoundAssignmentExpression(self.getTokenReference(), self.oper(), left, 
				right);
		this.getArrayStack().push(newSelf);

	}

	/** Visits the given field expression. */
	public void visitFieldExpression(/* @non_null */JClassFieldExpression self) {
		JExpression prefix;
		if (self.prefix() == null && !self.getField().isStatic()) {
			prefix = new JThisExpression(self.getTokenReference());
		} else if (self.prefix() == null && self.getField().isStatic()) {
			prefix = new JTypeNameExpression(self.getTokenReference(),self.getField().owner().getType(), new JNameExpression(self.getTokenReference(),self.getField().owner().ident()));
		} else {
			self.prefix().accept(this);
			prefix = this.getArrayStack().pop();
		}

		JClassFieldExpression newSelf = new JClassFieldExpression(self.getTokenReference(), prefix, self.ident());
		newSelf.setField(self.getField());
		newSelf.setType(self.getType());

		this.getArrayStack().push(newSelf);
	}

	/** Visits the given class expression. */
	public void visitClassExpression(/* @non_null */JClassExpression self) {
		this.getArrayStack().push(self);

	}

	/** Visits the given cast expression. */
	public void visitCastExpression(/* @non_null */JCastExpression self) {
		self.expr().accept(this);
		JCastExpression newSelf = new JCastExpression(self.getTokenReference(), this.getArrayStack().pop(), self.getType());
		this.getArrayStack().push(newSelf);

	}

	/** Visits the given unary promotion expression. */
	public void visitUnaryPromoteExpression(/* @non_null */JUnaryPromote self) {
		self.expr().accept(this);
		JExpression theNewExpre = this.getArrayStack().pop();
		JUnaryPromote newSelf = new JUnaryPromote(theNewExpre, self.getType());
		this.getArrayStack().push(newSelf);
	}

	/** Visits the given bitwise expression. */
	public void visitBitwiseExpression(/* @non_null */JBitwiseExpression self) {
		JBitwiseExpression newSelf = (JBitwiseExpression)self.clone();
		
		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();
		
		newSelf.setLeft(left);
		newSelf.setRight(right);
		
		this.getArrayStack().push(newSelf);
	}

	/** Visits the given assignment expression. */
	public void visitAssignmentExpression(/* @non_null */JAssignmentExpression self) {
		JAssignmentExpression newSelf = (JAssignmentExpression)self.clone();
		
		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();
		
		newSelf.setLeft(left);
		newSelf.setRight(right);
		
		this.getArrayStack().push(newSelf);
	}

	/** Visits the given array length expression. */
	public void visitArrayLengthExpression(/* @non_null */JArrayLengthExpression self) {
		self.prefix().accept(this);
		JArrayLengthExpression newSelf = new JArrayLengthExpression(self.getTokenReference(), this.getArrayStack().pop());
		this.getArrayStack().push(newSelf);
	}

	/** Visits the given array access expression. */
	public void visitArrayAccessExpression(/* @non_null */JArrayAccessExpression self) {
		self.accessor().accept(this);
		self.prefix().accept(this);
		JArrayAccessExpression newSelf = new JArrayAccessExpressionExtension(self,this.getArrayStack().pop(), this.getArrayStack().pop());
		this.getArrayStack().push(newSelf);
	}

	@Override
	public void visitJmlRelationalExpression(JmlRelationalExpression self) {
		JmlRelationalExpression newSelf = (JmlRelationalExpression) self.clone();
		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();
		newSelf.setLeft(left);
		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();
		newSelf.setRight(right);
		this.getArrayStack().push(newSelf);
	}

	@Override
	public void visitJmlPredicate(JmlPredicate self) {
		self.specExpression().accept(this);
		JmlPredicate newSelf = new JmlPredicate((JmlSpecExpression) this.getArrayStack().pop());
		this.getArrayStack().push(newSelf);
	}

	@Override
	public void visitJmlSpecExpression(JmlSpecExpression self) {
		self.expression().accept(this);
		JmlSpecExpression newSelf = new JmlSpecExpression(this.getArrayStack().pop());
		this.getArrayStack().push(newSelf);		
	}

	@Override
	public void visitJmlSpecQuantifiedExpression(JmlSpecQuantifiedExpression self) {
		JmlPredicate newPredicate = null;
		if (self.predicate() != null) {
			self.predicate().accept(this);
			newPredicate = (JmlPredicate) this.getArrayStack().pop();			
		}

		self.specExpression().accept(this);
		JmlSpecExpression newSpecExpression = (JmlSpecExpression) this.getArrayStack().pop();

		JmlSpecQuantifiedExpression newSelf = new JmlSpecQuantifiedExpression(self.getTokenReference(),self.oper(),self.quantifiedVarDecls(), newPredicate, newSpecExpression);
		this.getArrayStack().push(newSelf);
	}

	@Override
	public void visitJmlReachExpression(JmlReachExpression self) {
		self.specExpression().accept(this);
		JmlSpecExpression newSpecExpression = (JmlSpecExpression)this.getArrayStack().pop();

		JmlReachExpression newSelf = new  JmlReachExpression( self.getTokenReference(), newSpecExpression, self.referenceType(), self.storeRefExpressions());
		this.getArrayStack().push(newSelf);
	}

	@Override
	public void visitJmlResultExpression(JmlResultExpression self) {
		this.getArrayStack().push(self);	    
	}

	@Override
	public void visitJmlInformalExpression(JmlInformalExpression self) {
		this.getArrayStack().push(self);
	}

	@Override
	public void visitJmlOldExpression(JmlOldExpression self) {
		boolean containsMethodCall = ((self.specExpression().expression() instanceof JMethodCallExpression) ||
				(self.specExpression().expression() instanceof JEqualityExpression && 
						(
								(
										(((JEqualityExpression) self.specExpression().expression()).left() instanceof JMethodCallExpression) ||
										(((JEqualityExpression) self.specExpression().expression()).right() instanceof JMethodCallExpression)
										)
								)
						));

		boolean extractOldExpression = (containsMethodCall) && 
				this.methodCallExpressionsProcessed != null &&
				!this.methodCallExpressionsProcessed.contains(self.specExpression().expression());

		self.specExpression().accept(this);
		JExpression newSelf = null;
		if (extractOldExpression) {
			newSelf = this.getArrayStack().pop();
			this.oldModifierPresent = true;
		} else {
			newSelf = new JmlOldExpression(self.getTokenReference(), (JmlSpecExpression)this.getArrayStack().pop(), self.label());			
			this.oldModifierPresent = false;
		}
		this.getArrayStack().push(newSelf);
	}

	/** Visits the given warn expression. */
	public void visitWarnExpression(/* @non_null */MJWarnExpression self) {
		throw new TacoNotImplementedYetException();

	}

	/** Visits the given math mode expression. */
	public void visitMathModeExpression(/* @non_null */MJMathModeExpression self) {
		throw new TacoNotImplementedYetException();
	}

	@Override
	public void visitOrdinalLiteral(JOrdinalLiteral self) {
		this.getArrayStack().push(self);
	}

	@Override
	public void visitBooleanLiteral(JBooleanLiteral self) {
		this.getArrayStack().push(self);
	}

	@Override
	public void visitStringLiteral(JStringLiteral self) {
		this.getArrayStack().push(self);
	}

	@Override
	public void visitCharLiteral(JCharLiteral self) {
		this.getArrayStack().push(self);
	}

	@Override
	public void visitNullLiteral(JNullLiteral self) {
		this.getArrayStack().push(self);
	}

	@Override
	public void visitRealLiteral(JRealLiteral self) {
		this.getArrayStack().push(self);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
}
