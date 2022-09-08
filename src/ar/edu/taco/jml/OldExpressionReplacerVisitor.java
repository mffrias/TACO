package ar.edu.taco.jml;

import java.util.HashMap;
import java.util.Stack;

import org.jmlspecs.checker.JmlExpression;
import org.jmlspecs.checker.JmlFormalParameter;
import org.jmlspecs.checker.JmlOldExpression;
import org.jmlspecs.checker.JmlPredicate;
import org.jmlspecs.checker.JmlReachExpression;
import org.jmlspecs.checker.JmlRelationalExpression;
import org.jmlspecs.checker.JmlResultExpression;
import org.jmlspecs.checker.JmlSpecExpression;
import org.jmlspecs.checker.JmlSpecQuantifiedExpression;
import org.jmlspecs.checker.JmlSpecVarDecl;
import org.jmlspecs.checker.JmlStoreRefExpression;
import org.multijava.mjc.CSpecializedType;
import org.multijava.mjc.CType;
import org.multijava.mjc.JAddExpression;
import org.multijava.mjc.JArrayAccessExpression;
import org.multijava.mjc.JArrayLengthExpression;
import org.multijava.mjc.JBitwiseExpression;
import org.multijava.mjc.JBooleanLiteral;
import org.multijava.mjc.JCastExpression;
import org.multijava.mjc.JCharLiteral;
import org.multijava.mjc.JClassFieldExpression;
import org.multijava.mjc.JConditionalAndExpression;
import org.multijava.mjc.JConditionalExpression;
import org.multijava.mjc.JConditionalOrExpression;
import org.multijava.mjc.JDivideExpression;
import org.multijava.mjc.JEqualityExpression;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JLocalVariable;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.mjc.JMinusExpression;
import org.multijava.mjc.JModuloExpression;
import org.multijava.mjc.JMultExpression;
import org.multijava.mjc.JNullLiteral;
import org.multijava.mjc.JOrdinalLiteral;
import org.multijava.mjc.JParenthesedExpression;
import org.multijava.mjc.JRealLiteral;
import org.multijava.mjc.JStringLiteral;
import org.multijava.mjc.JThisExpression;
import org.multijava.mjc.JUnaryPromote;
import org.multijava.mjc.MjcVisitor;

import ar.edu.taco.TacoNotImplementedYetException;
import ar.edu.taco.simplejml.JmlBaseVisitor;



public class OldExpressionReplacerVisitor extends JmlBaseVisitor {

	public HashMap<JmlFormalParameter, JExpression> buffer = new HashMap<JmlFormalParameter, JExpression>();
	
	public static int newParameterFromOldExpressionIndex = 0;
	
	public Stack<JExpression> theStack = new Stack<JExpression>();
	
	public Stack<JExpression> getStack(){
		return theStack;
	}
	
	public String newParameterNameFromOldExpression(){
		String s = "newParameterNameFromOldExpression_" + newParameterFromOldExpressionIndex;
		newParameterFromOldExpressionIndex++;
		return s;
	}
	

	
	@Override
	public void visitLocalVariableExpression(JLocalVariableExpression self) {
		this.getStack().push(self);
	}



    public void visitJmlExpression(/*@non_null*/ JmlExpression self ){
    	throw new TacoNotImplementedYetException();
    }

    public void visitJmlOldExpression(/*@non_null*/ JmlOldExpression self ){
    	CSpecializedType theType = new CSpecializedType(self.specExpression().expression().getApparentType());
    	String theVarName = newParameterNameFromOldExpression();
       	JmlSpecExpression theExpre = self.specExpression();
    	JmlFormalParameter theVar = new JmlFormalParameter(self.getTokenReference(), 0L, 0, theType, theVarName);
    	this.buffer.put(theVar, theExpre.expression());
    	this.getStack().push(new JLocalVariableExpression(self.getTokenReference(), theVar));
    	
    	
    }
    
    
    @Override
    public void visitMethodCallExpression(/*@non_null@*/ JMethodCallExpression self){
    	self.prefix().accept(this);
    	JExpression newPrefix = this.getStack().pop();
    	JExpression[] newArgs = new JExpression[self.args().length];
    	for (int index = 0; index < self.args().length; index++){
    		self.args()[index].accept(this);
    		newArgs[index] = this.getStack().pop();
    	}
    	JMethodCallExpression newSelf = new JMethodCallExpression(self.getTokenReference(), newPrefix, self.ident(), newArgs, false);
    	this.getStack().push(newSelf);
    }

	@Override
	public void visitUnaryPromoteExpression(JUnaryPromote self) {
		self.expr().accept(this);
		JExpression newExpre = this.getStack().pop();
		JUnaryPromote newSelf = new JUnaryPromote(newExpre, newExpre.getApparentType());
		this.getStack().push(newSelf);

	}

    public void visitJmlReachExpression(/*@non_null*/ JmlReachExpression self ){
    	//This method assumes that in \old does not occur INSIDE a Reach Expression.
    	this.getStack().push(self);
    }
    
    public void visitJmlRelationalExpression(/*@non_null*/ JmlRelationalExpression self ){
    	self.left().accept(this);
    	JExpression left = this.getStack().pop();
    	self.right().accept(this);
    	JExpression right = this.getStack().pop();
    	JmlRelationalExpression newSelf = new JmlRelationalExpression(self.getTokenReference(), self.oper(), left, right);
    	this.getStack().push(newSelf);
    }
    
    public void visitJmlResultExpression(/*@non_null*/ JmlResultExpression self ){
    	this.getStack().push(self);
    }

//    public void visitJmlSpecVarDecl(/*@non_null*/ JmlSpecVarDecl self ){
//    	this.getStack().push(self);
//    }

    public void visitAddExpression(/*@non_null@*/ JAddExpression self ){
    	self.left().accept(this);
    	JExpression left = this.getStack().pop();
    	self.right().accept(this);
    	JExpression right = this.getStack().pop();
    	JAddExpression newSelf = new JAddExpression(self.getTokenReference(), left, right);
    	this.getStack().push(newSelf);
    }
    
    public void visitConditionalAndExpression(/*@non_null@*/ JConditionalAndExpression self ){
       	self.left().accept(this);
    	JExpression left = this.getStack().pop();
    	self.right().accept(this);
    	JExpression right = this.getStack().pop();
    	JConditionalAndExpression newSelf = new JConditionalAndExpression(self.getTokenReference(), left, right);
    	this.getStack().push(newSelf);
    }

    public void visitConditionalOrExpression(/*@non_null@*/ JConditionalOrExpression self ){
       	self.left().accept(this);
    	JExpression left = this.getStack().pop();
    	self.right().accept(this);
    	JExpression right = this.getStack().pop();
    	JConditionalOrExpression newSelf = new JConditionalOrExpression(self.getTokenReference(), left, right);
    	this.getStack().push(newSelf);
    }
    
    public void visitDivideExpression(/*@non_null@*/ JDivideExpression self ){
       	self.left().accept(this);
    	JExpression left = this.getStack().pop();
    	self.right().accept(this);
    	JExpression right = this.getStack().pop();
    	JDivideExpression newSelf = new JDivideExpression(self.getTokenReference(), left, right);
    	this.getStack().push(newSelf);
    }
    
    public void visitMinusExpression(/*@non_null@*/ JMinusExpression self ){
      	self.left().accept(this);
    	JExpression left = this.getStack().pop();
    	self.right().accept(this);
    	JExpression right = this.getStack().pop();
    	JMinusExpression newSelf = new JMinusExpression(self.getTokenReference(), left, right);
    	this.getStack().push(newSelf);
    }

    public void visitModuloExpression(/*@non_null@*/ JModuloExpression self ){
      	self.left().accept(this);
    	JExpression left = this.getStack().pop();
    	self.right().accept(this);
    	JExpression right = this.getStack().pop();
    	JModuloExpression newSelf = new JModuloExpression(self.getTokenReference(), left, right);
    	this.getStack().push(newSelf);
    }
    
    public void visitMultExpression(/*@non_null@*/ JMultExpression self ){
      	self.left().accept(this);
    	JExpression left = this.getStack().pop();
    	self.right().accept(this);
    	JExpression right = this.getStack().pop();
    	JMultExpression newSelf = new JMultExpression(self.getTokenReference(), left, right);
    	this.getStack().push(newSelf);
    }
    
//    public void visitMethodCallExpression(/*@non_null@*/ JMethodCallExpression self ){
//    	String theVarName = this.generateNewReturnParameterName();
//    	CSpecializedType theReturnType = new CSpecializedType(self.method().returnType());
//    	JmlFormalParameter theExpre = new JmlFormalParameter(self.getTokenReference(), 0L, 0, theReturnType, theVarName);
//    	this.entriesBuffer.put(theExpre, self);
//    }
    

    public void visitEqualityExpression(/*@non_null@*/ JEqualityExpression self ){
      	self.left().accept(this);
    	JExpression left = this.getStack().pop();
    	self.right().accept(this);
    	JExpression right = this.getStack().pop();
    	JEqualityExpression newSelf = new JEqualityExpression(self.getTokenReference(), self.oper(), left, right);
    	this.getStack().push(newSelf);
    }
    
    public void visitConditionalExpression(/*@non_null@*/ JConditionalExpression self ){
    	self.cond().accept(this);
    	JExpression cond = this.getStack().pop();
     	self.left().accept(this);
    	JExpression left = this.getStack().pop();
    	self.right().accept(this);
    	JExpression right = this.getStack().pop();
    	JConditionalExpression newSelf = new JConditionalExpression(self.getTokenReference(), cond, left, right);
    	this.getStack().push(newSelf);
    }
    
    public void visitFieldExpression(/*@non_null@*/ JClassFieldExpression self ){
    	self.prefix().accept(this);
    	JExpression prefix = this.getStack().pop();
    	JClassFieldExpression newSelf = new JClassFieldExpression(self.getTokenReference(), prefix, self.ident());
    	this.getStack().push(newSelf);
    }
    
	
	public void visitThisExpression(JThisExpression self) {
		this.getStack().push(self);
	}

    public void visitCastExpression(/*@non_null@*/ JCastExpression self ){
    	self.expr().accept(this);
    	JExpression theExpre = this.getStack().pop();
    	JCastExpression newSelf = new JCastExpression(self.getTokenReference(), theExpre, self.getType());
    	this.getStack().push(newSelf);
    }
    
    
	public void visitParenthesedExpression(JParenthesedExpression self) {
		self.expr().accept(this);
		JExpression theExpre = this.getStack().pop();
		JParenthesedExpression newSelf = new JParenthesedExpression(self.getTokenReference(), theExpre);
		this.getStack().push(newSelf);
	}

    public void visitBitwiseExpression(/*@non_null@*/ JBitwiseExpression self ){
      	self.left().accept(this);
    	JExpression left = this.getStack().pop();
    	self.right().accept(this);
    	JExpression right = this.getStack().pop();
    	JBitwiseExpression newSelf = new JBitwiseExpression(self.getTokenReference(), self.oper(), left, right);
    	this.getStack().push(newSelf);
    }

    public void visitArrayLengthExpression(/*@non_null@*/JArrayLengthExpression self ){
    	self.prefix().accept(this);
    	JExpression thePrefix = this.getStack().pop();
    	JArrayLengthExpression newSelf = new JArrayLengthExpression(self.getTokenReference(), thePrefix);
    	this.getStack().push(newSelf);
    }

    public void visitArrayAccessExpression(/*@non_null@*/ JArrayAccessExpression self ){
    	self.accessor().accept(this);
    	JExpression accessor = this.getStack().pop();
    	self.prefix().accept(this);
    	JExpression prefix = this.getStack().pop();
    	JArrayAccessExpression newSelf = new JArrayAccessExpression(self.getTokenReference(), prefix, accessor);
    	this.getStack().push(newSelf);
    }
    
    public void visitBooleanLiteral(/*@non_null@*/ JBooleanLiteral self ){
    	this.getStack().push(self);
    }
    
    public void visitCharLiteral(/*@non_null@*/ JCharLiteral self ){
    	this.getStack().push(self);
    }
    
    public void visitOrdinalLiteral(/*@non_null@*/ JOrdinalLiteral self ){
    	this.getStack().push(self);
    }
    
    public void visitRealLiteral(/*@non_null@*/ JRealLiteral self ){
    	this.getStack().push(self);
    }
    
    public void visitStringLiteral(/*@non_null@*/ JStringLiteral self ){
    	this.getStack().push(self);
    }
    
    public void visitNullLiteral(/*@non_null@*/ JNullLiteral self ){
    	this.getStack().push(self);
    }
        
 

	
}
