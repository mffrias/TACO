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

import org.jmlspecs.checker.JmlAbstractVisitor;
import org.multijava.mjc.JAddExpression;
import org.multijava.mjc.JArrayAccessExpression;
import org.multijava.mjc.JArrayDimsAndInits;
import org.multijava.mjc.JArrayInitializer;
import org.multijava.mjc.JArrayLengthExpression;
import org.multijava.mjc.JAssignmentExpression;
import org.multijava.mjc.JBinaryExpression;
import org.multijava.mjc.JBitwiseExpression;
import org.multijava.mjc.JCastExpression;
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
import org.multijava.mjc.JMinusExpression;
import org.multijava.mjc.JModuloExpression;
import org.multijava.mjc.JMultExpression;
import org.multijava.mjc.JNameExpression;
import org.multijava.mjc.JNewAnonymousClassExpression;
import org.multijava.mjc.JNewArrayExpression;
import org.multijava.mjc.JNewObjectExpression;
import org.multijava.mjc.JParenthesedExpression;
import org.multijava.mjc.JPostfixExpression;
import org.multijava.mjc.JPrefixExpression;
import org.multijava.mjc.JRelationalExpression;
import org.multijava.mjc.JShiftExpression;
import org.multijava.mjc.JSuperExpression;
import org.multijava.mjc.JThisExpression;
import org.multijava.mjc.JTypeNameExpression;
import org.multijava.mjc.JUnaryExpression;
import org.multijava.mjc.JUnaryPromote;
import org.multijava.mjc.MJMathModeExpression;
import org.multijava.mjc.MJWarnExpression;

import ar.edu.taco.TacoNotImplementedYetException;

public class JmlAstTransverseExpressionVisitor extends JmlAbstractVisitor {


	private Stack<JExpression> arrayStack = new Stack<JExpression>();

	public Stack<JExpression> getArrayStack() {
		return arrayStack;
	}

	
	// ----------------------------------------------------------------------
	// EXPRESSION
	// ----------------------------------------------------------------------

	/** Visits the given unary expression. */
	public void visitUnaryExpression(/* @non_null */JUnaryExpression self) {
		self.expr().accept(this);
		
	}

	/** Visits the given type name expression. */
	public void visitTypeNameExpression(/* @non_null */JTypeNameExpression self) {
	}

	/** Visits the given this expression. */
	public void visitThisExpression(/* @non_null */JThisExpression self) {
	}

	/** Visits the given super expression. */
	public void visitSuperExpression(/* @non_null */JSuperExpression self) {
	}

	/** Visits the given shift expression. */
	public void visitShiftExpression(/* @non_null */JShiftExpression self) {
		self.left().accept(this);
		self.right().accept(this);
	}

	/** Visits the given relational expression. */
	public void visitRelationalExpression(/*@non_null*/ JRelationalExpression self ) {
    	self.left().accept(this);
    	self.right().accept(this);
    }

	/** Visits the given prefix expression. */
	public void visitPrefixExpression(/* @non_null */JPrefixExpression self) {
		self.expr().accept(this);
	}

	/** Visits the given postfix expression. */
	public void visitPostfixExpression(/* @non_null */JPostfixExpression self) {
		self.expr().accept(this);
	}

	/** Visits the given parenthesed expression. */
	public void visitParenthesedExpression(/* @non_null */JParenthesedExpression self) {
		self.expr().accept(this);
	}

	/** Visits the given object allocator expression. */
	public void visitNewObjectExpression(/* @non_null */JNewObjectExpression self) {
		self.thisExpr().accept(this);
		for (JExpression expression : self.params()) {
			expression.accept(this);
		}
	}

	/** Visits the given object allocator expression for an anonymous class. */
	public void visitNewAnonymousClassExpression(/* @non_null */JNewAnonymousClassExpression self) {
		throw new TacoNotImplementedYetException();
	}

	/** Visits the given array allocator expression. */
	public void visitNewArrayExpression(/* @non_null */JNewArrayExpression self) {
		self.dims().accept(this);
	}
	
	@Override
	public void visitArrayInitializer(JArrayInitializer self) {
		for (JExpression expression : self.elems()) {
			expression.accept(this);
		}		
	}
	
	@Override
	public void visitArrayDimsAndInit(JArrayDimsAndInits self) {
		for (JExpression expression : self.dims()) {
			expression.accept(this);
		}		
		self.init().accept(this);
	}

	/** Visits the given name expression. */
	public void visitNameExpression(/* @non_null */JNameExpression self) {
	}

	/** Visits the given binary expression with the given operator. */
	protected void visitBinaryExpression(/* @non_null */JBinaryExpression self, String oper) {
    	self.left().accept(this);
    	self.right().accept(this);
	}

	/** Visits the given add expression. */
	public void visitAddExpression(/* @non_null */JAddExpression self) {
    	self.left().accept(this);
    	self.right().accept(this);		
	}

	/** Visits the given boolean AND expression. */
	public void visitConditionalAndExpression(/* @non_null */JConditionalAndExpression self) {
    	self.left().accept(this);
    	self.right().accept(this);	
	}

	/** Visits the given boolean OR expression. */
	public void visitConditionalOrExpression(/* @non_null */JConditionalOrExpression self) {
    	self.left().accept(this);
    	self.right().accept(this);			
	}

	/** Visits the given divide expression. */
	public void visitDivideExpression(/* @non_null */JDivideExpression self) {
    	self.left().accept(this);
    	self.right().accept(this);	
	}

	/** Visits the given minus expression. */
	public void visitMinusExpression(/* @non_null */JMinusExpression self) {
    	self.left().accept(this);
    	self.right().accept(this);	
	}

	/** Visits the given modulo division expression. */
	public void visitModuloExpression(/* @non_null */JModuloExpression self) {
    	self.left().accept(this);
    	self.right().accept(this);	
	}

	/** Visits the given multiplication expression. */
	public void visitMultExpression(/* @non_null */JMultExpression self) {
    	self.left().accept(this);
    	self.right().accept(this);	
	}

	/** Visits the given method call expression. */
	public void visitMethodCallExpression(/* @non_null */JMethodCallExpression self) {
		self.prefix().accept(this);
		for (JExpression expression : self.args()) {
			expression.accept(this);
		}					
	}

	/** Visits the given local variable expression. */
	public void visitLocalVariableExpression(/* @non_null */JLocalVariableExpression self) {
		self.variable().accept(this);
	}

	/** Visits the given instanceof expression. */
	public void visitInstanceofExpression(/* @non_null */JInstanceofExpression self) {
		self.expr().accept(this);
	}
	
	/** Visits the given equality expression. */
	public void visitEqualityExpression(/* @non_null */JEqualityExpression self) {
    	self.left().accept(this);
    	self.right().accept(this);	
	}

	/** Visits the given conditional expression. */
	public void visitConditionalExpression(/* @non_null */JConditionalExpression self) {
    	self.cond().accept(this);
		self.left().accept(this);
    	self.right().accept(this);	
	}

	/** Visits the given compound expression. */
	public void visitCompoundAssignmentExpression(/* @non_null */JCompoundAssignmentExpression self) {
    	self.left().accept(this);
    	self.right().accept(this);	
	}

	/** Visits the given field expression. */
	public void visitFieldExpression(/* @non_null */JClassFieldExpression self) {
		self.prefix().accept(this);
	}

	/** Visits the given class expression. */
	public void visitClassExpression(/* @non_null */JClassExpression self) {
	}

	/** Visits the given cast expression. */
	public void visitCastExpression(/* @non_null */JCastExpression self) {
		self.expr().accept(this);	
	}

	/** Visits the given unary promotion expression. */
	public void visitUnaryPromoteExpression(/* @non_null */JUnaryPromote self) {
		self.expr().accept(this);	
	}

	/** Visits the given bitwise expression. */
	public void visitBitwiseExpression(/* @non_null */JBitwiseExpression self) {
    	self.left().accept(this);
    	self.right().accept(this);
	}

	/** Visits the given assignment expression. */
	public void visitAssignmentExpression(/* @non_null */JAssignmentExpression self) {
    	self.left().accept(this);
    	self.right().accept(this);
	}

	/** Visits the given array length expression. */
	public void visitArrayLengthExpression(/* @non_null */JArrayLengthExpression self) {
		self.prefix().accept(this);
	}

	/** Visits the given array access expression. */
	public void visitArrayAccessExpression(/* @non_null */JArrayAccessExpression self) {
		self.prefix().accept(this);
		self.accessor().accept(this);
	}

	/** Visits the given warn expression. */
	public void visitWarnExpression(/* @non_null */MJWarnExpression self) {
		throw new TacoNotImplementedYetException();
		
	}

	/** Visits the given math mode expression. */
	public void visitMathModeExpression(/* @non_null */MJMathModeExpression self) {
		throw new TacoNotImplementedYetException();	
	}

}
