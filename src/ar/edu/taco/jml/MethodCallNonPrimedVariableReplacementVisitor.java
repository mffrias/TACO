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

import ar.edu.taco.TacoNotImplementedYetException;
import ar.edu.taco.simplejml.JmlBaseVisitor;


public class MethodCallNonPrimedVariableReplacementVisitor extends JmlBaseVisitor {
	
	private static int newVarIndex = 0;
	
	private HashMap<JmlFormalParameter, JExpression> buffer = new HashMap<JmlFormalParameter, JExpression>();
	
	private Stack<Object> theStack = new Stack<Object>();
	
	private static String newVarName(){
		String theVarName = "newOldExpressionVarName_" + newVarIndex;
		newVarIndex++;
		return theVarName;
	}
	
	public Stack<Object> getStack(){
		return theStack;
	}
	
	public HashMap<JmlFormalParameter, JExpression> getBuffer(){
		return buffer;
	}
	
	public void setBuffer(HashMap<JmlFormalParameter, JExpression> buffer){
		this.buffer = buffer;
	}
	

	@Override
	public void visitJmlPredicate(JmlPredicate self) {
		self.specExpression().accept(this);
		JmlSpecExpression newExpre = (JmlSpecExpression)this.getStack().pop();
		JmlPredicate newSelf = new JmlPredicate(newExpre);
		this.theStack.push(newSelf);
	}

	@Override
	public void visitJmlSpecExpression(JmlSpecExpression self) {
		self.expression().accept(this);
		JExpression theExpre = (JExpression)this.getStack().pop();
		JmlSpecExpression newSelf = new JmlSpecExpression(theExpre);
		this.getStack().push(newSelf);
	}


    public void visitJmlExpression(/*@non_null*/ JmlExpression self ){
    	throw new TacoNotImplementedYetException();
    }

    public void visitJmlOldExpression(/*@non_null*/ JmlOldExpression self ){
    	String varName = newVarName();
    	CType theType = self.specExpression().expression().getType();
    	JmlFormalParameter theNewVar = new JmlFormalParameter(self.getTokenReference(), 0L, 0, new CSpecializedType(theType), varName);
    	this.buffer.put(theNewVar, self.specExpression().expression());
    }

    public void visitJmlReachExpression(/*@non_null*/ JmlReachExpression self ){
    	JmlReachExpression newSelf = null;
    	CType theType = self.getType();
    	JmlSpecExpression theExpression = self.specExpression();
    	theExpression.accept(this);
    	JmlSpecExpression theExpre = (JmlSpecExpression)this.getStack().pop();
    	JmlStoreRefExpression theNewStoreRef = null;
    	if (self.storeRefExpression() != null){
    		self.storeRefExpression().accept(this);
    		theNewStoreRef = (JmlStoreRefExpression)this.getStack().pop();
    		newSelf = new JmlReachExpression(self.getTokenReference(), theExpre, theType, theNewStoreRef);
    	}
    	JmlStoreRefExpression[] theNewStoreRefs = null;
    	if (self.storeRefExpressions() != null){
    		for (int index = 0; index < self.storeRefExpressions().length; index++){
    			self.storeRefExpressions()[index].accept(this);
    			theNewStoreRefs[index] = (JmlStoreRefExpression)this.getStack().pop();
    			newSelf = new JmlReachExpression(self.getTokenReference(), theExpre, theType, theNewStoreRefs);
    		}
    	}
    	this.getStack().push(newSelf);
    }
    
    public void visitJmlRelationalExpression(/*@non_null*/ JmlRelationalExpression self ){
    	self.left().accept(this);
    	JExpression l = (JExpression)this.getStack().pop();
    	self.right().accept(this);
    	JExpression r = (JExpression)this.getStack().pop();
    	JmlRelationalExpression newSelf = new JmlRelationalExpression(self.getTokenReference(), self.oper(), l, r);
    }
    
    public void visitJmlResultExpression(/*@non_null*/ JmlResultExpression self ){
    	
    }

    public void visitJmlSpecVarDecl(/*@non_null*/ JmlSpecVarDecl self ){

    }

    public void visitAddExpression(/*@non_null@*/ JAddExpression self ){
    	self.left().accept(this);
    	JExpression l = (JExpression)this.getStack().pop();
    	self.right().accept(this);
    	JExpression r = (JExpression)this.getStack().pop();
    	JAddExpression newSelf = new JAddExpression(self.getTokenReference(),l,r);
    	this.getStack().push(newSelf);
    }
    
    public void visitConditionalAndExpression(/*@non_null@*/ JConditionalAndExpression self ){
    	self.left().accept(this);
    	JExpression l = (JExpression)this.getStack().pop();
    	self.right().accept(this);
    	JExpression r = (JExpression)this.getStack().pop();
    	JConditionalAndExpression newSelf = new JConditionalAndExpression(self.getTokenReference(), l, r);
    	this.getStack().push(newSelf);
    }

    public void visitConditionalOrExpression(/*@non_null@*/ JConditionalOrExpression self ){
    	self.left().accept(this);
    	JExpression l = (JExpression)this.getStack().pop();
    	self.right().accept(this);
    	JExpression r = (JExpression)this.getStack().pop();
    	JConditionalOrExpression newSelf = new JConditionalOrExpression(self.getTokenReference(), l, r);
    	this.getStack().push(newSelf);
    	
    }
    
    public void visitDivideExpression(/*@non_null@*/ JDivideExpression self ){
    	self.left().accept(this);
    	JExpression l = (JExpression)this.getStack().pop();
    	self.right().accept(this);
    	JExpression r = (JExpression)this.getStack().pop();
    	JDivideExpression newSelf = new JDivideExpression(self.getTokenReference(), l, r);
    	this.getStack().push(newSelf);
    }
    
    public void visitMinusExpression(/*@non_null@*/ JMinusExpression self ){
    	self.left().accept(this);
    	JExpression l = (JExpression)this.getStack().pop();
    	self.right().accept(this);
    	JExpression r = (JExpression)this.getStack().pop();
    	JMinusExpression newSelf = new JMinusExpression(self.getTokenReference(), l, r);
    	this.getStack().push(newSelf);
    }

    public void visitModuloExpression(/*@non_null@*/ JModuloExpression self ){
    	self.left().accept(this);
    	JExpression l = (JExpression)this.getStack().pop();
    	self.right().accept(this);
    	JExpression r = (JExpression)this.getStack().pop();
    	JModuloExpression newSelf = new JModuloExpression(self.getTokenReference(), l, r);
    	this.getStack().push(newSelf);
    }
    
    public void visitMultExpression(/*@non_null@*/ JMultExpression self ){
    	self.left().accept(this);
    	JExpression l = (JExpression)this.getStack().pop();
    	self.right().accept(this);
    	JExpression r = (JExpression)this.getStack().pop();
    	JMultExpression newSelf = new JMultExpression(self.getTokenReference(), l, r);
    	this.getStack().push(newSelf);
    }
    
    public void visitMethodCallExpression(/*@non_null@*/ JMethodCallExpression self ){
    	JExpression prefix = self.prefix();
    	prefix.accept(this);
    	JExpression newPrefix = (JExpression)this.getStack().pop();
    	JExpression[] args = self.args();
    	JExpression[] newArgs = new JExpression[args.length];
    	for (int index = 0; index < args.length; index++){
    		args[index].accept(this);
    		JExpression arg = (JExpression)this.getStack().pop();
    		newArgs[index] = arg;
    	}
    	JMethodCallExpression newSelf = new JMethodCallExpression(self.getTokenReference(), newPrefix, newArgs);
    	this.getStack().push(newSelf);
    }
    

    public void visitEqualityExpression(/*@non_null@*/ JEqualityExpression self ){
    	self.left().accept(this);
    	JExpression l = (JExpression)this.getStack().pop();
    	self.right().accept(this);
    	JExpression r = (JExpression)this.getStack().pop();
    	JEqualityExpression newSelf = new JEqualityExpression(self.getTokenReference(), self.oper(), l, r);
    	this.getStack().push(newSelf);
    }
    
    public void visitConditionalExpression(/*@non_null@*/ JConditionalExpression self ){
    	self.cond().accept(this);
    	JExpression theCond = (JExpression)this.getStack().pop();
    	self.left().accept(this);
    	JExpression l = (JExpression)this.getStack().pop();
    	self.right().accept(this);
    	JExpression r = (JExpression)this.getStack().pop();
    	JConditionalExpression newSelf = new JConditionalExpression(self.getTokenReference(), theCond, l, r);
    	this.getStack().push(newSelf);
    }
    
    public void visitFieldExpression(/*@non_null@*/ JClassFieldExpression self ){
    	self.prefix().accept(this);
    }
    
	
	public void visitThisExpression(JThisExpression self) {

	}

    public void visitCastExpression(/*@non_null@*/ JCastExpression self ){
    	self.expr().accept(this);
    }
    
    
	public void visitParenthesedExpression(JParenthesedExpression self) {
		self.expr().accept(this);
	}

    public void visitBitwiseExpression(/*@non_null@*/ JBitwiseExpression self ){
    	self.left().accept(this);
    	self.right().accept(this);
    }

    public void visitArrayLengthExpression(/*@non_null@*/JArrayLengthExpression self ){
    	self.prefix().accept(this);
    }

    public void visitArrayAccessExpression(/*@non_null@*/ JArrayAccessExpression self ){
    	self.accessor().accept(this);
    	self.prefix().accept(this);
    }
    
    public void visitBooleanLiteral(/*@non_null@*/ JBooleanLiteral self ){
    
    }
    
    public void visitCharLiteral(/*@non_null@*/ JCharLiteral self ){
    	
    }
    
    public void visitOrdinalLiteral(/*@non_null@*/ JOrdinalLiteral self ){
    	
    }
    
    public void visitRealLiteral(/*@non_null@*/ JRealLiteral self ){
    	
    }
    
    public void visitStringLiteral(/*@non_null@*/ JStringLiteral self ){
    	
    }
    
    public void visitNullLiteral(/*@non_null@*/ JNullLiteral self ){
    	
    }
}
