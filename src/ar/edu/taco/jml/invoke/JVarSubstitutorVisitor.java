package ar.edu.taco.jml.invoke;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.jmlspecs.checker.*;
import org.multijava.mjc.CClassType;
import org.multijava.mjc.CMethod;
import org.multijava.mjc.CStdType;
import org.multijava.mjc.CType;
import org.multijava.mjc.CUniverse;
import org.multijava.mjc.CVoidType;
import org.multijava.mjc.JAddExpression;
import org.multijava.mjc.JArrayAccessExpression;
import org.multijava.mjc.JArrayDimsAndInits;
import org.multijava.mjc.JArrayInitializer;
import org.multijava.mjc.JArrayLengthExpression;
import org.multijava.mjc.JAssertStatement;
import org.multijava.mjc.JAssignmentExpression;
import org.multijava.mjc.JBitwiseExpression;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JBooleanLiteral;
import org.multijava.mjc.JBreakStatement;
import org.multijava.mjc.JCastExpression;
import org.multijava.mjc.JCatchClause;
import org.multijava.mjc.JCharLiteral;
import org.multijava.mjc.JClassBlock;
import org.multijava.mjc.JClassDeclaration;
import org.multijava.mjc.JClassExpression;
import org.multijava.mjc.JClassFieldExpression;
import org.multijava.mjc.JClassOrGFImport;
import org.multijava.mjc.JCompilationUnit;
import org.multijava.mjc.JCompoundAssignmentExpression;
import org.multijava.mjc.JCompoundStatement;
import org.multijava.mjc.JConditionalAndExpression;
import org.multijava.mjc.JConditionalExpression;
import org.multijava.mjc.JConditionalOrExpression;
import org.multijava.mjc.JConstructorBlock;
import org.multijava.mjc.JConstructorDeclaration;
import org.multijava.mjc.JContinueStatement;
import org.multijava.mjc.JDivideExpression;
import org.multijava.mjc.JDoStatement;
import org.multijava.mjc.JEmptyStatement;
import org.multijava.mjc.JEqualityExpression;
import org.multijava.mjc.JExplicitConstructorInvocation;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JExpressionListStatement;
import org.multijava.mjc.JExpressionStatement;
import org.multijava.mjc.JFieldDeclaration;
import org.multijava.mjc.JForStatement;
import org.multijava.mjc.JFormalParameter;
import org.multijava.mjc.JIfStatement;
import org.multijava.mjc.JInitializerDeclaration;
import org.multijava.mjc.JInstanceofExpression;
import org.multijava.mjc.JInterfaceDeclaration;
import org.multijava.mjc.JLabeledStatement;
import org.multijava.mjc.JLocalVariable;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.mjc.JMethodCallExpressionExtension;
import org.multijava.mjc.JMethodDeclaration;
import org.multijava.mjc.JMinusExpression;
import org.multijava.mjc.JModuloExpression;
import org.multijava.mjc.JMultExpression;
import org.multijava.mjc.JNameExpression;
import org.multijava.mjc.JNewAnonymousClassExpression;
import org.multijava.mjc.JNewArrayExpression;
import org.multijava.mjc.JNewObjectExpression;
import org.multijava.mjc.JNullLiteral;
import org.multijava.mjc.JOrdinalLiteral;
import org.multijava.mjc.JPackageImport;
import org.multijava.mjc.JPackageName;
import org.multijava.mjc.JParenthesedExpression;
import org.multijava.mjc.JPostfixExpression;
import org.multijava.mjc.JPrefixExpression;
import org.multijava.mjc.JRealLiteral;
import org.multijava.mjc.JRelationalExpression;
import org.multijava.mjc.JReturnStatement;
import org.multijava.mjc.JShiftExpression;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JStringLiteral;
import org.multijava.mjc.JSuperExpression;
import org.multijava.mjc.JSwitchGroup;
import org.multijava.mjc.JSwitchLabel;
import org.multijava.mjc.JSwitchStatement;
import org.multijava.mjc.JSynchronizedStatement;
import org.multijava.mjc.JThisExpression;
import org.multijava.mjc.JThrowStatement;
import org.multijava.mjc.JTryCatchStatement;
import org.multijava.mjc.JTryFinallyStatement;
import org.multijava.mjc.JTypeDeclarationStatement;
import org.multijava.mjc.JTypeNameExpression;
import org.multijava.mjc.JUnaryExpression;
import org.multijava.mjc.JUnaryPromote;
import org.multijava.mjc.JVariableDeclarationStatement;
import org.multijava.mjc.JVariableDefinition;
import org.multijava.mjc.JWhileStatement;
import org.multijava.mjc.MJGenericFunctionDecl;
import org.multijava.mjc.MJMathModeExpression;
import org.multijava.mjc.MJTopLevelMethodDeclaration;
import org.multijava.mjc.MJWarnExpression;
import org.multijava.util.compiler.JavaStyleComment;
import org.multijava.util.compiler.TokenReference;

import ar.edu.taco.TacoNotImplementedYetException;
import ar.edu.taco.jml.expression.ESExpressionVisitor;
import ar.edu.taco.jml.utils.ASTUtils;
import ar.edu.taco.simplejml.methodinfo.MethodInformation;
import ar.edu.taco.simplejml.methodinfo.MethodInformationBuilder;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

public class JVarSubstitutorVisitor extends JmlAstClonerStatementVisitor {

	HashMap<String, String> binding;
	public JVarSubstitutorVisitor(HashMap<String, String> binding) {
		this.binding = binding;
	}


	public void visitJmlSpecExpression(JmlSpecExpression self) {
		self.expression().accept(this);
		this.getStack().push(this.getStack().pop());

	}

	public void visitVariableDeclarationStatement(/* @non_null */JVariableDeclarationStatement self) {
		JVariableDefinition[] varsAndDefs = self.getVars();
		JVariableDefinition[] newVarsAndDefs = new JVariableDefinition[varsAndDefs.length];

		for (int idx = 0; idx < varsAndDefs.length; idx++){
			JExpression expre = null;
			if (varsAndDefs[idx].expr() != null){
				varsAndDefs[idx].expr().accept(this);
				expre = (JExpression) this.getStack().pop();
			} 
			String ident = varsAndDefs[idx].ident();
			String newIdent = ident;
			if (binding.containsKey(ident)){
				newIdent = binding.get(ident);
			}
			newVarsAndDefs[idx] = new JVariableDefinition(varsAndDefs[idx].getTokenReference(), varsAndDefs[idx].modifiers(), varsAndDefs[idx].getType(), newIdent, expre);
		}
		JVariableDeclarationStatement newSelf = new JVariableDeclarationStatement(self.getTokenReference(), newVarsAndDefs, self.getComments());
		this.getStack().push(newSelf);

	}


	@Override
	public void visitLocalVariableExpression(JLocalVariableExpression self){
		JLocalVariableExpression newSelf = self;
		String oldName = self.ident();
		if (binding.containsKey(oldName)){
			String newName = binding.get(oldName);
			JVariableDefinition newSelfDefi = new JVariableDefinition(self.getTokenReference(), 0L, self.getType(), newName, null);
			newSelf = new JLocalVariableExpression(self.getTokenReference(), newSelfDefi);
		}
		this.getStack().push(newSelf);
	}

	//	@Override
	//	public void visitJmlAssignmentStatement(JmlAssignmentStatement self){
	//		JExpression lhs = ((JAssignmentExpression)self.assignmentStatement().expr()).left();
	//		String oldName = ((JLocalVariableExpression)lhs).ident();
	//		if (lhs instanceof JLocalVariableExpression){
	//		if (binding.containsKey(oldName)){
	//			String newName = binding.get(oldName);
	//			JVariableDefinition new_var_def = new JVariableDefinition(lhs.getTokenReference(),0L,lhs.getApparentType(),newName, null);
	//			JLocalVariableExpression newVarExp = new JLocalVariableExpression(lhs.getTokenReference(), new_var_def);
	//			
	//		}
	////		this.getStack().push(newSelf);
	//	}
	//		


	@Override
	public void visitExpressionStatement(/* @non_null */JExpressionStatement self) {
		self.expr().accept(this);
		JExpression newExpression = (JExpression)this.getStack().pop();
		JExpressionStatement newExpressionStatement = new JExpressionStatement(self.getTokenReference(), newExpression, self.getComments());
		getStack().push(newExpressionStatement);
	}



	@Override
	public void visitConditionalExpression(JConditionalExpression self) {

		JExpression cond = self.cond();
		cond.accept(this);
		JExpression newCond = (JExpression) this.getStack().pop();
		JExpression left = self.left();
		left.accept(this);
		JExpression newLeft = (JExpression) this.getStack().pop(); 
		JExpression right = self.right();
		right.accept(this);
		JExpression newRight = (JExpression) this.getStack().pop();

		JConditionalExpression newSelf = new JConditionalExpression(self.getTokenReference(), newCond, newLeft, newRight);
		this.getStack().push(newSelf);
	}

	@Override
	public void visitNewObjectExpression(JNewObjectExpression self) {

		CMethod theMethod = self.constructor();
		JExpression[] params = self.params();
		JExpression[] newParams = new JExpression[params.length];
		for (int idx = 0; idx < params.length; idx++){
			params[idx].accept(this);
			JExpression newParam = (JExpression) this.getStack().pop();
			newParams[idx] = newParam;
		}

		JNewObjectExpression newSelf = new JNewObjectExpression(self.getTokenReference(), (CClassType) self.getType(), self.thisExpr(), newParams);
		this.getStack().push(newSelf);
	}

	@Override
	public void visitNewArrayExpression(JNewArrayExpression self) {
		JArrayDimsAndInits dimsAndInits = self.dims();
		dimsAndInits.accept(this);
		JArrayDimsAndInits newDimsAndInits = (JArrayDimsAndInits) this.getStack().pop();
		JNewArrayExpression newSelf = new JNewArrayExpression(self.getTokenReference(), self.getType(), newDimsAndInits);
		this.getStack().push(newSelf);
	}

	@Override
	public void visitMethodCallExpression(JMethodCallExpression self) {

		JMethodCallExpression newSelf = (JMethodCallExpression) self.clone();

		JExpression[] args = self.args();
		JExpression[] newArgs = newSelf.args();
		for (int idx = 0; idx < args.length; idx++){
			args[idx].accept(this);
			newArgs[idx] = (JExpression) this.getStack().pop();
		}

		JExpression newPrefix;
		if (self.prefix() == null) {
			newPrefix = null;
		} else {
			self.prefix().accept(this);
			newPrefix = (JExpression) this.getStack().pop();
		}

		newSelf.setPrefix(newPrefix);
		this.getStack().push(newSelf);
	}



	@Override
	public void visitCastExpression(JCastExpression self) {

		self.expr().accept(this);
		JExpression newExpr = (JExpression) this.getStack().pop();
		JCastExpression newSelf = (JCastExpression) self.clone();
		newSelf.setExpr(newExpr);
		this.getStack().push(newSelf);
	}


	@Override
	public void visitArrayAccessExpression(JArrayAccessExpression self) {
		JExpression prefix = self.prefix();
		prefix.accept(this);
		JExpression newPrefix = (JExpression) this.getStack().pop();

		JExpression accessor = self.accessor();
		accessor.accept(this);
		JExpression newAccessor = (JExpression) this.getStack().pop();
		JArrayAccessExpression newSelf = new JArrayAccessExpression(self.getTokenReference(), newPrefix, newAccessor);
		this.getStack().push(newSelf);
	}



	/** Visits the given assignment expression. */
	public void visitAssignmentExpression(/* @non_null */JAssignmentExpression self) {

		JExpression left = self.left();
		left.accept(this);
		JExpression newLeft = (JExpression) this.getStack().pop();

		JExpression right = self.right();
		right.accept(this);
		JExpression newRight = (JExpression) this.getStack().pop();

		JAssignmentExpression newSelf = new JAssignmentExpression(self.getTokenReference(), newLeft, newRight);
		this.getStack().push(newSelf);
	}


	/** Visits the given postfix expression. */
	public void visitPostfixExpression(/* @non_null */JPostfixExpression self) {

		JExpression expr = self.expr();
		expr.accept(this);
		JExpression newExpr = (JExpression) this.getStack().pop();

		JPostfixExpression newSelf = new JPostfixExpression(self.getTokenReference(), self.oper(), newExpr);
		this.getStack().push(newSelf);
	}

	public void visitPrefixExpression(/* @non_null */JPrefixExpression self) {

		JExpression expr = self.expr();
		expr.accept(this);
		JExpression newExpr = (JExpression) this.getStack().pop();

		JPrefixExpression newSelf = new JPrefixExpression(self.getTokenReference(), self.oper(), newExpr);
		this.getStack().push(newSelf);
	}



	/** Visits the given unary expression. */
	public void visitUnaryExpression(/* @non_null */JUnaryExpression self) {

		self.expr().accept(this);
		JExpression expr = (JExpression) this.getStack().pop();

		JUnaryExpression newSelf = new JUnaryExpression(self.getTokenReference(), self.oper(), expr);
		this.getStack().push(newSelf);
	}


	public void visitShiftExpression(/* @non_null */JShiftExpression self) {

		self.left().accept(this);
		JExpression left = (JExpression) this.getStack().pop();

		self.right().accept(this);
		JExpression right = (JExpression) this.getStack().pop();

		JShiftExpression newSelf = new JShiftExpression(self.getTokenReference(), self.oper(), left, right);
		this.getStack().push(newSelf);
	}



	/** Visits the given relational expression. */
	public void visitRelationalExpression(/* @non_null */JRelationalExpression self) {
		self.left().accept(this);
		JExpression left = (JExpression) this.getStack().pop();

		self.right().accept(this);
		JExpression right = (JExpression) this.getStack().pop();

		JRelationalExpression newSelf = new JRelationalExpression(self.getTokenReference(), self.oper(), left, right);
		this.getStack().push(newSelf);
	}


	@Override
	public void visitJmlRelationalExpression(JmlRelationalExpression self) {

		self.left().accept(this);
		JExpression left = (JExpression) this.getStack().pop();

		self.right().accept(this);
		JExpression right = (JExpression) this.getStack().pop();

		JmlRelationalExpression newSelf = new JmlRelationalExpression(self.getTokenReference(), self.oper(), left, right);
		this.getStack().push(newSelf);
	}	


	/** Visits the given add expression. */
	public void visitAddExpression(/* @non_null */JAddExpression self) {
		self.left().accept(this);
		JExpression left = (JExpression) this.getStack().pop();

		self.right().accept(this);
		JExpression right = (JExpression) this.getStack().pop();

		JAddExpression newSelf = new JAddExpression(self.getTokenReference(), left, right);
		this.getStack().push(newSelf);
	}


	/** Visits the given divide expression. */
	public void visitDivideExpression(/* @non_null */JDivideExpression self) {
		self.left().accept(this);
		JExpression left = (JExpression) this.getStack().pop();

		self.right().accept(this);
		JExpression right = (JExpression) this.getStack().pop();

		JDivideExpression newSelf = new JDivideExpression(self.getTokenReference(), left, right);
		this.getStack().push(newSelf);
	}


	/** Visits the given minus expression. */
	public void visitMinusExpression(/* @non_null */JMinusExpression self) {

		self.left().accept(this);
		JExpression left = (JExpression) this.getStack().pop();

		self.right().accept(this);
		JExpression right = (JExpression) this.getStack().pop();

		JMinusExpression newSelf = new JMinusExpression(self.getTokenReference(), left, right);
		this.getStack().push(newSelf);

	}



	/** Visits the given modulo division expression. */
	public void visitModuloExpression(/* @non_null */JModuloExpression self) {

		self.left().accept(this);
		JExpression left = (JExpression) this.getStack().pop();

		self.right().accept(this);
		JExpression right = (JExpression) this.getStack().pop();

		JModuloExpression newSelf = new JModuloExpression(self.getTokenReference(), left, right);
		this.getStack().push(newSelf);
	}



	/** Visits the given multiplication expression. */
	public void visitMultExpression(/* @non_null */JMultExpression self) {

		self.left().accept(this);
		JExpression left = (JExpression) this.getStack().pop();

		self.right().accept(this);
		JExpression right = (JExpression) this.getStack().pop();

		JMultExpression newSelf = new JMultExpression(self.getTokenReference(), left, right);
		this.getStack().push(newSelf);
	}




	/** Visits the given equality expression. */
	public void visitEqualityExpression(/* @non_null */JEqualityExpression self) {
		self.left().accept(this);
		JExpression left = (JExpression) this.getStack().pop();

		self.right().accept(this);
		JExpression right = (JExpression) this.getStack().pop();

		JEqualityExpression newSelf = new JEqualityExpression(self.getTokenReference(), self.oper(), left, right);
		this.getStack().push(newSelf);
	}



	/** Visits the given bitwise expression. */
	public void visitBitwiseExpression(/* @non_null */JBitwiseExpression self) {
		self.left().accept(this);
		JExpression left = (JExpression) this.getStack().pop();

		self.right().accept(this);
		JExpression right = (JExpression) this.getStack().pop();

		JBitwiseExpression newSelf = new JBitwiseExpression(self.getTokenReference(), self.oper(), left, right);
		this.getStack().push(newSelf);
	}




	/** Visits the given instanceof expression. */
	public void visitInstanceofExpression(/* @non_null */JInstanceofExpression self) {
		self.expr().accept(this);
		JInstanceofExpression newSelf = new JInstanceofExpression(self.getTokenReference(), (JExpression) this.getStack().pop(), self.dest());
		this.getStack().push(newSelf);
	}


	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlAssignmentStatement(org.jmlspecs.checker.JmlAssignmentStatement)
	 */
	@Override
	public void visitJmlAssignmentStatement(JmlAssignmentStatement self) {
		JExpressionStatement expre = self.assignmentStatement();
		expre.accept(this);
		JExpressionStatement newExpre = (JExpressionStatement) this.getStack().pop();
		JmlAssignmentStatement newSelf = new JmlAssignmentStatement(newExpre);
		this.getStack().push(newSelf);
	}




	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitArrayDimsAndInit(org.multijava.mjc.JArrayDimsAndInits)
	 */
	@Override
	public void visitArrayDimsAndInit(JArrayDimsAndInits self) {
		JArrayInitializer init = self.init();
		JArrayInitializer newInit = init;
		if (init != null){
			init.accept(this);
			newInit = (JArrayInitializer) this.getStack().pop();
		}
		JExpression[] dims = self.dims();
		JExpression[] newDims = new JExpression[dims.length];

		for (int idx = 0; idx < dims.length; idx++){
			if (dims[idx] != null) {
				dims[idx].accept(this);
				newDims[idx] = (JExpression) this.getStack().pop();
			} else {
				newDims[idx] = null;
			}
		}
		JArrayDimsAndInits newSelf = new JArrayDimsAndInits(self.getTokenReference(), null, newDims, newInit);
		this.getStack().push(newSelf);

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitArrayInitializer(org.multijava.mjc.JArrayInitializer)
	 */
	@Override
	public void visitArrayInitializer(JArrayInitializer self) {
		JExpression[] newElems = new JExpression[self.elems().length];
		for (int i = 0; i < self.elems().length; i++) {
			JExpression expression = self.elems()[i];
			expression.accept(this);
			newElems[i] = (JExpression) this.getStack().pop();
		}
		JArrayInitializer newSelf = new JArrayInitializer(self.getTokenReference(), newElems);
		this.getStack().push(newSelf);
	}
	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitArrayLengthExpression(org.multijava.mjc.JArrayLengthExpression)
	 */
	@Override
	public void visitArrayLengthExpression(JArrayLengthExpression arg0) {
		this.getStack().push(arg0);

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitAssertStatement(org.multijava.mjc.JAssertStatement)
	 */
	@Override
	public void visitAssertStatement(JAssertStatement self) {
		JExpression expr = self.predicate();
		expr.accept(this);
		JExpression newExpr = (JExpression)this.getStack().pop();
		JAssertStatement newSelf = new JAssertStatement(self.getTokenReference(), newExpr, self.getComments());
		this.getStack().push(newSelf);
	}


	@Override
	public void visitNameExpression(JNameExpression self) {
		String ident = self.getName();
		JNameExpression newSelf = new JNameExpression(self.getTokenReference(), ident);
		this.getStack().push(newSelf);

	}

	
	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitBlockStatement(org.multijava.mjc.JBlock)
	 */
	@Override
	public void visitBlockStatement(JBlock self) {

		JStatement[] body = self.body();
		JStatement[] newBody = new JStatement[body.length];
		for (int idx = 0; idx < body.length; idx++){
			body[idx].accept(this);
			newBody[idx] = (JStatement) this.getStack().pop();
		}

		JBlock newSelf = new JBlock(self.getTokenReference(), newBody, self.getComments());
		this.getStack().push(newSelf);
	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitBooleanLiteral(org.multijava.mjc.JBooleanLiteral)
	 */
	@Override
	public void visitBooleanLiteral(JBooleanLiteral self) {
		this.getStack().push(self);
	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitBreakStatement(org.multijava.mjc.JBreakStatement)
	 */
	@Override
	public void visitBreakStatement(JBreakStatement self) {
		this.getStack().push(self);

	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitCatchClause(org.multijava.mjc.JCatchClause)
	 */
	@Override
	public void visitCatchClause(JCatchClause self) {

		JBlock body = self.body();
		body.accept(this);
		JBlock newBody = (JBlock) this.getStack().pop();

		JCatchClause newSelf = new JCatchClause(self.getTokenReference(), self.exception(), newBody);
		this.getStack().push(newSelf);
	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitCharLiteral(org.multijava.mjc.JCharLiteral)
	 */
	@Override
	public void visitCharLiteral(JCharLiteral self) {
		this.getStack().push(self);

	}




	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitCompoundAssignmentExpression(org.multijava.mjc.JCompoundAssignmentExpression)
	 */
	@Override
	public void visitCompoundAssignmentExpression(JCompoundAssignmentExpression self) {

		JExpression left = self.left();
		left.accept(this);
		JExpression newLeft = (JExpression) this.getStack().pop();

		JExpression right = self.right();
		right.accept(this);
		JExpression newRight = (JExpression) this.getStack().pop();

		JCompoundAssignmentExpression newSelf = new JCompoundAssignmentExpression(self.getTokenReference(), self.oper(), newLeft, newRight);
		this.getStack().push(newSelf);
	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitCompoundStatement(org.multijava.mjc.JCompoundStatement)
	 */
	@Override
	public void visitCompoundStatement(JCompoundStatement self) {

		JStatement[] body = self.body();
		JStatement[] newBody = new JStatement[body.length];
		for (int idx = 0; idx < body.length; idx++){
			body[idx].accept(this);
			newBody[idx] = (JStatement) this.getStack().pop();
		}
		JCompoundStatement newSelf = new JCompoundStatement(self.getTokenReference(), newBody);
		this.getStack().push(newSelf);

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitConditionalAndExpression(org.multijava.mjc.JConditionalAndExpression)
	 */
	@Override
	public void visitConditionalAndExpression(JConditionalAndExpression self) {

		JExpression left = self.left();
		left.accept(this);
		JExpression newLeft = (JExpression) this.getStack().pop();

		JExpression right = self.right();
		right.accept(this);
		JExpression newRight = (JExpression) this.getStack().pop();

		JConditionalAndExpression newSelf = new JConditionalAndExpression(self.getTokenReference(), newLeft, newRight);
		this.getStack().push(newSelf);
	}



	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitConditionalOrExpression(org.multijava.mjc.JConditionalOrExpression)
	 */
	@Override
	public void visitConditionalOrExpression(JConditionalOrExpression self) {
		JExpression left = self.left();
		left.accept(this);
		JExpression newLeft = (JExpression) this.getStack().pop();

		JExpression right = self.right();
		right.accept(this);
		JExpression newRight = (JExpression) this.getStack().pop();

		JConditionalOrExpression newSelf = new JConditionalOrExpression(self.getTokenReference(), newLeft, newRight);
		this.getStack().push(newSelf);

	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitContinueStatement(org.multijava.mjc.JContinueStatement)
	 */
	@Override
	public void visitContinueStatement(JContinueStatement self) {
		this.getStack().push(self);
	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitDoStatement(org.multijava.mjc.JDoStatement)
	 */
	@Override
	public void visitDoStatement(JDoStatement self) {
		this.getStack().push(self);
	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitEmptyStatement(org.multijava.mjc.JEmptyStatement)
	 */
	@Override
	public void visitEmptyStatement(JEmptyStatement self) {
		throw new TacoNotImplementedYetException();
	}



	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitExplicitConstructorInvocation(org.multijava.mjc.JExplicitConstructorInvocation)
	 */
	@Override
	public void visitExplicitConstructorInvocation(JExplicitConstructorInvocation self) {
		JExpression[] params = self.params();
		JExpression[] newParams = new JExpression[params.length];
		for (int idx = 0; idx < newParams.length; idx++){
			params[idx].accept(this);
			newParams[idx] = (JExpression) this.getStack().pop();
		}
		JExplicitConstructorInvocation newSelf = new JExplicitConstructorInvocation(self.getTokenReference(), self.ident(), newParams);
		this.getStack().push(newSelf);

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitExpressionListStatement(org.multijava.mjc.JExpressionListStatement)
	 */
	@Override
	public void visitExpressionListStatement(JExpressionListStatement arg0) {
		throw new TacoNotImplementedYetException();

	}



	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitFieldExpression(org.multijava.mjc.JClassFieldExpression)
	 */
	@Override
	public void visitFieldExpression(JClassFieldExpression self) {
		JExpression thePrefix = self.prefix();
		thePrefix.accept(this);
		JExpression theNewPrefix = (JExpression) this.getStack().pop();
		
		JClassFieldExpression newSelf = 
				new JClassFieldExpression(self.getTokenReference(), theNewPrefix, self.ident(), self.sourceName());
		newSelf.setField(self.getField());
		newSelf.setType(self.getApparentType());
		this.getStack().push(newSelf);

	}




	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitForStatement(org.multijava.mjc.JForStatement)
	 */
	@Override
	public void visitForStatement(JForStatement self) {

		JStatement init = self.init();
		init.accept(this);
		JStatement newInit = (JStatement) this.getStack().pop();

		JExpression cond = self.cond();
		cond.accept(this);
		JExpression newCond = (JExpression) this.getStack().pop();

		JStatement incr = self.incr();
		incr.accept(this);
		JStatement newIncr = (JStatement) this.getStack().pop();

		JStatement body = self.body();
		body.accept(this);
		JStatement newBody = (JStatement) this.getStack().pop();

		JForStatement newSelf = new JForStatement(self.getTokenReference(), newInit , newCond, newIncr, newBody, self.getComments());
		this.getStack().push(newSelf);
	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitGenericFunctionDecl(org.multijava.mjc.MJGenericFunctionDecl)
	 */
	@Override
	public void visitGenericFunctionDecl(MJGenericFunctionDecl self) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitIfStatement(org.multijava.mjc.JIfStatement)
	 */
	@Override
	public void visitIfStatement(JIfStatement self) {

		JExpression cond = self.cond();
		cond.accept(this);
		JExpression newCond = (JExpression) this.getStack().pop();

		JStatement thenClause = self.thenClause();
		JStatement newThenClause = null;
		if (thenClause != null) {
			thenClause.accept(this);
			newThenClause = (JStatement) this.getStack().pop();
		}

		JStatement elseClause = self.elseClause();
		JStatement newElseClause = null;
		if (elseClause != null) {
			elseClause.accept(this);
			newElseClause = (JStatement) this.getStack().pop();
		}

		JIfStatement newSelf = new JIfStatement(self.getTokenReference(), newCond, newThenClause, newElseClause, self.getComments());
		this.getStack().push(newSelf);
	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitInitializerDeclaration(org.multijava.mjc.JInitializerDeclaration)
	 */
	@Override
	public void visitInitializerDeclaration(JInitializerDeclaration self) {
		throw new TacoNotImplementedYetException();

	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitInterfaceDeclaration(org.multijava.mjc.JInterfaceDeclaration)
	 */
	@Override
	public void visitInterfaceDeclaration(JInterfaceDeclaration arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitLabeledStatement(org.multijava.mjc.JLabeledStatement)
	 */
	@Override
	public void visitLabeledStatement(JLabeledStatement arg0) {
		throw new TacoNotImplementedYetException();

	}








	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitNullLiteral(org.multijava.mjc.JNullLiteral)
	 */
	@Override
	public void visitNullLiteral(JNullLiteral self) {
		this.getStack().push(self);
	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitOrdinalLiteral(org.multijava.mjc.JOrdinalLiteral)
	 */
	@Override
	public void visitOrdinalLiteral(JOrdinalLiteral self) {
		this.getStack().push(self);

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitPackageImport(org.multijava.mjc.JPackageImport)
	 */
	@Override
	public void visitPackageImport(JPackageImport arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitPackageName(org.multijava.mjc.JPackageName)
	 */
	@Override
	public void visitPackageName(JPackageName arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitParenthesedExpression(org.multijava.mjc.JParenthesedExpression)
	 */
	@Override
	public void visitParenthesedExpression(JParenthesedExpression self) {

		JExpression expr = self.unParenthesize();
		expr.accept(this);
	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitRealLiteral(org.multijava.mjc.JRealLiteral)
	 */
	@Override
	public void visitRealLiteral(JRealLiteral self) {
		this.getStack().push(self);
	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitReturnStatement(org.multijava.mjc.JReturnStatement)
	 */
	@Override
	public void visitReturnStatement(JReturnStatement self) {
		JExpression retExpression = self.expr();
		if (retExpression != null){
			retExpression.accept(this);
			JStatement newSelf = new JReturnStatement(self.getTokenReference(), (JExpression)this.getStack().pop(), self.getComments());
			this.getStack().push(newSelf);
		} else {
			this.getStack().push(self);
		}

	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitStringLiteral(org.multijava.mjc.JStringLiteral)
	 */
	@Override
	public void visitStringLiteral(JStringLiteral self) {
		this.getStack().push(self);
	}



	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitSuperExpression(org.multijava.mjc.JSuperExpression)
	 */
	@Override
	public void visitSuperExpression(JSuperExpression self) {
		JExpression prefix = self.prefix();
		JExpression newPrefix = null;
		assert prefix != null;
		if (prefix != null){
			prefix.accept(this);
			newPrefix = (JExpression) this.getStack().pop();
		} 
		JSuperExpression newSelf = new JSuperExpression(self.getTokenReference(), newPrefix);
		this.getStack().push(newSelf);
	}



	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitSwitchGroup(org.multijava.mjc.JSwitchGroup)
	 */
	@Override
	public void visitSwitchGroup(JSwitchGroup self) {

		JStatement[] sts = self.getStatements();
		JStatement[] newSts = new JStatement[sts.length];
		for (int idx = 0; idx < sts.length; idx++){
			sts[idx].accept(this);
			newSts[idx] = (JStatement) this.getStack().pop(); 
		}
		JSwitchGroup newSelf = new JSwitchGroup(self.getTokenReference(), self.labels(), newSts );
		this.getStack().push(newSelf);
	}



	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitSwitchLabel(org.multijava.mjc.JSwitchLabel)
	 */
	@Override
	public void visitSwitchLabel(JSwitchLabel self) {
		this.getStack().push(self);
	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitSwitchStatement(org.multijava.mjc.JSwitchStatement)
	 */
	@Override
	public void visitSwitchStatement(JSwitchStatement self) {

		JExpression expr = self.expr();
		expr.accept(this);
		JExpression newExpr = (JExpression) this.getStack().pop();

		JSwitchGroup[] groups = self.groups();
		JSwitchGroup[] newGroups = new JSwitchGroup[groups.length];
		for (int idx = 0; idx < groups.length; idx++){
			groups[idx].accept(this);
			newGroups[idx] = (JSwitchGroup) this.getStack().pop();
		}
		JSwitchStatement newSelf = new JSwitchStatement(self.getTokenReference(), newExpr, newGroups, self.getComments());
		this.getStack().push(newSelf);
	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitSynchronizedStatement(org.multijava.mjc.JSynchronizedStatement)
	 */
	@Override
	public void visitSynchronizedStatement(JSynchronizedStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitThisExpression(org.multijava.mjc.JThisExpression)
	 */
	@Override
	public void visitThisExpression(JThisExpression self) {
		this.getStack().push(self);

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitThrowStatement(org.multijava.mjc.JThrowStatement)
	 */
	@Override
	public void visitThrowStatement(JThrowStatement self) {

		JExpression expr = self.expr();
		expr.accept(this);
		JExpression newExpr = (JExpression) this.getStack().pop();
		JThrowStatement newSelf = new JThrowStatement(self.getTokenReference(), newExpr, self.getComments());
		this.getStack().push(newSelf);
	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitTopLevelMethodDeclaration(org.multijava.mjc.MJTopLevelMethodDeclaration)
	 */
	@Override
	public void visitTopLevelMethodDeclaration(MJTopLevelMethodDeclaration arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitTryCatchStatement(org.multijava.mjc.JTryCatchStatement)
	 */
	@Override
	public void visitTryCatchStatement(JTryCatchStatement self) {

		JBlock tryExpr = self.tryClause();
		tryExpr.accept(this);
		JBlock newTry = (JBlock) this.getStack().pop();

		JCatchClause[] catchs = self.catchClauses();
		JCatchClause[] newCatchs = new JCatchClause[catchs.length];
		for (int idx = 0; idx < catchs.length; idx++){
			catchs[idx].accept(this);
			newCatchs[idx] = (JCatchClause) this.getStack().pop();
		}
		JTryCatchStatement newSelf = new JTryCatchStatement(self.getTokenReference(), newTry, newCatchs, self.getComments());
		this.getStack().push(newSelf);
	}



	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitTryFinallyStatement(org.multijava.mjc.JTryFinallyStatement)
	 */
	@Override
	public void visitTryFinallyStatement(JTryFinallyStatement self) {

		JBlock trySt = self.tryClause();
		trySt.accept(this);
		JBlock newTry = (JBlock) this.getStack().pop();

		JBlock finallySt = self.finallyClause();
		finallySt.accept(this);
		JBlock newFinally = (JBlock) this.getStack().pop();

		JTryFinallyStatement newSelf = new JTryFinallyStatement(self.getTokenReference(), newTry, newFinally, self.getComments());
		this.getStack().push(newSelf);
	}



	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitTypeDeclarationStatement(org.multijava.mjc.JTypeDeclarationStatement)
	 */
	@Override
	public void visitTypeDeclarationStatement(JTypeDeclarationStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitTypeNameExpression(org.multijava.mjc.JTypeNameExpression)
	 */
	@Override
	public void visitTypeNameExpression(JTypeNameExpression arg0) {
		this.getStack().push(arg0);

	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitUnaryPromoteExpression(org.multijava.mjc.JUnaryPromote)
	 */
	@Override
	public void visitUnaryPromoteExpression(JUnaryPromote self) {
		this.getStack().push(self);

	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitVariableDefinition(org.multijava.mjc.JVariableDefinition)
	 */
	@Override
	public void visitVariableDefinition(JVariableDefinition self) {

		String ident = self.ident();
		String newIdent = ident;
		if (binding.containsKey(ident)){
			newIdent = binding.get(ident);
		}
		JExpression init = self.expr();
		init.accept(this);
		JExpression newInit = (JExpression) this.getStack().pop();

		JVariableDefinition newSelf = new JVariableDefinition(self.getTokenReference(), self.modifiers(), self.getType(), newIdent, newInit);
		this.getStack().push(newSelf);
	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitWarnExpression(org.multijava.mjc.MJWarnExpression)
	 */
	@Override
	public void visitWarnExpression(MJWarnExpression arg0) {
		throw new TacoNotImplementedYetException();

	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitWhileStatement(org.multijava.mjc.JWhileStatement)
	 */
	@Override
	public void visitWhileStatement(JWhileStatement self) {

		JExpression cond = self.cond();
		cond.accept(this);
		JExpression newCond = (JExpression) this.getStack().pop();

		JStatement body = self.body();
		body.accept(this);
		JStatement newBody = (JStatement) this.getStack().pop();

		JWhileStatement newSelf = new JWhileStatement(self.getTokenReference(), newCond, newBody, self.getComments());
		this.getStack().push(newSelf);
	}






	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitCompoundStatement(org.multijava.mjc.JStatement[])
	 */
	@Override
	public void visitCompoundStatement(JStatement[] body) {
		JStatement[] newBody = new JStatement[body.length];
		for (int idx = 0; idx < body.length; idx++){
			body[idx].accept(this);
			newBody[idx] = (JStatement) this.getStack().pop();
		}
		this.getStack().push(newBody);
	}



	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitConstructorBlock(org.multijava.mjc.JConstructorBlock)
	 */
	@Override
	public void visitConstructorBlock(JConstructorBlock self) {

		JStatement[] body = self.body();
		JStatement[] newBody = new JStatement[body.length];
		for (int idx = 0; idx < body.length; idx++){
			body[idx].accept(this);
			newBody[idx] = (JStatement) this.getStack().pop();
		}

		JConstructorBlock newSelf = new JConstructorBlock(self.getTokenReference(), newBody);
		this.getStack().push(newSelf);
	}




	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitJmlFieldDeclaration(org.jmlspecs.checker.JmlFieldDeclaration)
	 */
	@Override
	public void visitJmlFieldDeclaration(JmlFieldDeclaration self) {
		this.getStack().push(self);
	}



	/* (non-Javadoc)
	 * @see ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor#visitClassBlock(org.multijava.mjc.JClassBlock)
	 */
	@Override
	public void visitClassBlock(JClassBlock self) {

		JStatement[] body = self.body();
		JStatement[] newBody = new JStatement[body.length];
		for (int idx = 0; idx < body.length; idx++){
			body[idx].accept(this);
			newBody[idx] = (JStatement) this.getStack().pop();
		}

		JClassBlock newSelf = new JClassBlock(self.getTokenReference(), self.isStaticInitializer(), newBody);
		this.getStack().push(newSelf);
	}

}
