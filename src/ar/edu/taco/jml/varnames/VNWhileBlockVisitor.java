package ar.edu.taco.jml.varnames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmlspecs.checker.JmlAssertStatement;
import org.jmlspecs.checker.JmlAssignmentStatement;
import org.jmlspecs.checker.JmlAssumeStatement;
import org.jmlspecs.checker.JmlLoopInvariant;
import org.jmlspecs.checker.JmlLoopStatement;
import org.jmlspecs.checker.JmlPredicate;
import org.jmlspecs.checker.JmlSpecExpression;
import org.jmlspecs.checker.JmlVariableDefinition;
import org.jmlspecs.checker.JmlVariantFunction;
import org.jmlspecs.jmlrac.JavaAndJmlPrettyPrint2;
import org.multijava.mjc.JAssertStatement;
import org.multijava.mjc.JAssignmentExpression;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JExpressionStatement;
import org.multijava.mjc.JIfStatement;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JMethodDeclaration;
import org.multijava.mjc.JReturnStatement;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JThrowStatement;
import org.multijava.mjc.JVariableDeclarationStatement;
import org.multijava.mjc.JVariableDefinition;
import org.multijava.mjc.JWhileStatement;

import ar.edu.taco.jml.utils.ASTUtils;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

public class VNWhileBlockVisitor extends JmlAstClonerStatementVisitor {

	public HashMap<String, String> variableMapping = new HashMap<String, String>();

	public VNWhileBlockVisitor(ArrayList<JVariableDeclarationStatement> varsToReplace, int whileIndex, int loopUnroll) {
		for (JVariableDeclarationStatement var : varsToReplace) {
			JVariableDefinition[] vars = var.getVars();
			for (JVariableDefinition singleVar : vars) {
				String sourceName = singleVar.ident();
				String targetName = sourceName + "_" + whileIndex + "_" + loopUnroll;
				variableMapping.put(sourceName, targetName);
			}
		}	
	}


	@Override
	public void visitBlockStatement(JBlock self) {

		JStatement[] newBody = new JStatement[self.body().length];
		for (int i = 0; i < self.body().length; i++) {
			JStatement statement = self.body()[i];
			{
				statement.accept(this);
				JStatement aStatement = (JStatement)this.getStack().pop();
				newBody[i] = aStatement;
			}
		}

		JBlock newSelf = new JBlock(self.getTokenReference(), newBody, self.getComments());
		this.getStack().push(newSelf);
	}


	@Override
	public void visitIfStatement(/* @non_null */JIfStatement self) {

		self.thenClause().accept(this);
		JStatement newThen = (JStatement) this.getStack().pop();
		JStatement newElse = null;
		if (self.elseClause() != null) {
			self.elseClause().accept(this);
			newElse = (JStatement) this.getStack().pop();
		}

		VNWhileExpressionVisitor conditionSimplifierVisitor = new VNWhileExpressionVisitor(variableMapping);
		self.cond().accept(conditionSimplifierVisitor);
		JExpression condition = conditionSimplifierVisitor.getArrayStack().pop();

		JIfStatement newIfStatement = new JIfStatement(self.getTokenReference(), condition, newThen, newElse, self.getComments());

		this.getStack().push(newIfStatement);
	}


	@Override
	public void visitAssertStatement(JAssertStatement self){
		VNWhileExpressionVisitor conditionSimplifierVisitor = new VNWhileExpressionVisitor(variableMapping);
		self.predicate().accept(conditionSimplifierVisitor);
		JExpression condition = conditionSimplifierVisitor.getArrayStack().pop();

		JAssertStatement newAssertStatement = new JAssertStatement(self.getTokenReference(), condition, self.getComments());

		this.getStack().push(newAssertStatement);

	}

	@Override
	public void visitJmlLoopStatement(JmlLoopStatement self) {
		JmlLoopInvariant[] newJmlLoopInvariants = new JmlLoopInvariant[self.loopInvariants().length];
		for (int x = 0; x < self.loopInvariants().length; x++) {
			JmlLoopInvariant aJmlLoopInvariant = self.loopInvariants()[x];
			VNWhileExpressionVisitor exprSimplifierVisitor = new VNWhileExpressionVisitor(variableMapping);

			aJmlLoopInvariant.predicate().specExpression().expression().accept(exprSimplifierVisitor);
			JExpression expr = exprSimplifierVisitor.getArrayStack().pop();
			JmlPredicate newJmlPredicate = new JmlPredicate(new JmlSpecExpression(expr));

			JmlLoopInvariant newJmlLoopInvariant = new JmlLoopInvariant(aJmlLoopInvariant.getTokenReference(), aJmlLoopInvariant.isRedundantly(), newJmlPredicate);
			newJmlLoopInvariants[x] = newJmlLoopInvariant;
		}


		JmlVariantFunction[] newJmlLoopVariants = new JmlVariantFunction[self.variantFunctions().length];
		for (int i = 0; i < self.variantFunctions().length; i++) {
			JmlVariantFunction aJmlLoopVariant = self.variantFunctions()[i];
			VNWhileExpressionVisitor exprSimplifierVisitor = new VNWhileExpressionVisitor(variableMapping);

			aJmlLoopVariant.specExpression().expression().accept(exprSimplifierVisitor);
			JExpression expr = exprSimplifierVisitor.getArrayStack().pop();
			JmlSpecExpression newJmlExpression = new JmlSpecExpression(expr);

			JmlVariantFunction newJmlLoopVariant = new JmlVariantFunction(self.getTokenReference(), self.acceptsBreak(), newJmlExpression);
			newJmlLoopVariants[i] = newJmlLoopVariant;
		}

		self.stmt().accept(this);

		JStatement newStatement = (JStatement) this.getStack().pop();

		JmlLoopStatement newJmlLoopStatement = new JmlLoopStatement(self.getTokenReference(), newJmlLoopInvariants, newJmlLoopVariants, newStatement, self.getComments());

		this.getStack().push(newJmlLoopStatement);
	}

	
	@Override
	public void visitWhileStatement(JWhileStatement self) {
		self.body().accept(this);
		JStatement newBody = (JStatement) this.getStack().pop();

		VNWhileExpressionVisitor conditionSimplifierVisitor = new VNWhileExpressionVisitor(variableMapping);
		self.cond().accept(conditionSimplifierVisitor);
		JExpression condition = conditionSimplifierVisitor.getArrayStack().pop();

		JWhileStatement newJWhileStatement = new JWhileStatement(self.getTokenReference(), condition, newBody, self.getComments());

		this.getStack().push(newJWhileStatement);
	}
	

	@Override
	public void visitVariableDeclarationStatement(JVariableDeclarationStatement self) {

		JVariableDefinition[] newVars = new JVariableDefinition[self.getVars().length];
		for (int i = 0; i < self.getVars().length; i++) {
			JVariableDefinition variableDefinition = self.getVars()[i];
			variableDefinition.accept(this);
			newVars[i] = (JVariableDefinition) getStack().pop();
		}

		JVariableDeclarationStatement newSelf = new JVariableDeclarationStatement(self.getTokenReference(), newVars, self.getComments());
		this.getStack().push(newSelf);
	}

	
	@Override
	public void visitJmlVariableDefinition(JmlVariableDefinition self) {
		VNWhileExpressionVisitor conditionSimplifierVisitor = new VNWhileExpressionVisitor(variableMapping);
		self.expr().accept(conditionSimplifierVisitor);

		String newIdent = self.ident();
		if (this.variableMapping.containsKey(self.ident())) {
			newIdent = this.variableMapping.get(self.ident());
		}
		
		JmlVariableDefinition newSelf = new JmlVariableDefinition(self.getTokenReference(), self.modifiers(), self.getType(), newIdent,
				conditionSimplifierVisitor.getArrayStack().pop());

		this.getStack().push(newSelf);
	}

	@Override
	public void visitVariableDefinition(JVariableDefinition self) {

		
		String newIdent = self.ident();
		if (this.variableMapping.get(self.ident()) != null) {
			newIdent = this.variableMapping.get(self.ident());
		}
		
		VNWhileExpressionVisitor expreSimplifierVisitor = new VNWhileExpressionVisitor(variableMapping);
		JExpression newExpr = null;
		if (self.expr() != null) {
			self.expr().accept(expreSimplifierVisitor);
			newExpr = expreSimplifierVisitor.getArrayStack().pop();
		}
		JVariableDefinition newSelf = new JVariableDefinition(self.getTokenReference(), self.modifiers(), self.getType(), newIdent, newExpr);
		getStack().push(newSelf);

	}

	@Override
	public void visitJmlAssignmentStatement(JmlAssignmentStatement self) {
		self.assignmentStatement().accept(this);
		JExpressionStatement newExpressionStatement = (JExpressionStatement) this.getStack().pop();
		JmlAssignmentStatement newAssignamentStatement = new JmlAssignmentStatement(newExpressionStatement);

		getStack().push(newAssignamentStatement);
	}

	
	
	@Override
	public void visitExpressionStatement(JExpressionStatement self) {
		VNWhileExpressionVisitor visitor = new VNWhileExpressionVisitor(variableMapping);
		self.expr().accept(visitor);
		JExpression newExpression = visitor.getArrayStack().pop();
		JExpressionStatement newExpressionStatement = new JExpressionStatement(self.getTokenReference(), newExpression, self.getComments());

		getStack().push(newExpressionStatement);
	}

	public void visitJAssignmentExpression(JAssignmentExpression self){
		VNWhileExpressionVisitor visitor = new VNWhileExpressionVisitor(variableMapping);

		JExpression left = self.left();
		left.accept(visitor);
		JExpression newLeft = visitor.getArrayStack().pop();
		
		JExpression right = self.right();
		right.accept(visitor);
		JExpression newRight = visitor.getArrayStack().pop();
		
		JAssignmentExpression newSelf = new JAssignmentExpression(self.getTokenReference(), newLeft, newRight);
		
		this.getStack().push(newSelf);
	}
	
	
	@Override
	public void visitReturnStatement(JReturnStatement self) {
		VNWhileExpressionVisitor exprSimplifierVisitor = new VNWhileExpressionVisitor(variableMapping);
		JExpression expr = null;

		if (self.expr() != null) {
			self.expr().accept(exprSimplifierVisitor);
			expr = exprSimplifierVisitor.getArrayStack().pop();
		}

		JReturnStatement newSelf = new JReturnStatement(self.getTokenReference(), expr, self.getComments());

		this.getStack().push(newSelf);
	}

	
	@Override
	public void visitThrowStatement(JThrowStatement self) {
		VNWhileExpressionVisitor exprSimplifierVisitor = new VNWhileExpressionVisitor(variableMapping);

		self.expr().accept(exprSimplifierVisitor);
		JExpression newExpression = exprSimplifierVisitor.getArrayStack().pop();
		JThrowStatement newExpressionStatement = new JThrowStatement(self.getTokenReference(), newExpression, self.getComments());

		this.getStack().push(newExpressionStatement);
	}


	@Override
	public void visitJmlAssertStatement(JmlAssertStatement self) {
		VNWhileExpressionVisitor exprSimplifierVisitor = new VNWhileExpressionVisitor(variableMapping);
		JExpression expr = null;

		self.predicate().specExpression().expression().accept(exprSimplifierVisitor);
		expr = exprSimplifierVisitor.getArrayStack().pop();
		JmlPredicate jmlPredicate = new JmlPredicate(new JmlSpecExpression(expr));
		JmlAssertStatement newSelf = new JmlAssertStatement(self.getTokenReference(), self.isRedundantly(), jmlPredicate, self.throwMessage(),
				self.getComments());

		this.getStack().push(newSelf);
	}

	
	@Override
	public void visitJmlAssumeStatement(JmlAssumeStatement self) {
		VNWhileExpressionVisitor exprSimplifierVisitor = new VNWhileExpressionVisitor(variableMapping);
		JExpression expr = null;

		self.predicate().specExpression().expression().accept(exprSimplifierVisitor);
		expr = exprSimplifierVisitor.getArrayStack().pop();
		JmlPredicate jmlPredicate = new JmlPredicate(new JmlSpecExpression(expr));
		JmlAssumeStatement newSelf = new JmlAssumeStatement(self.getTokenReference(), self.isRedundantly(), jmlPredicate, self.throwMessage(),
				self.getComments());

		this.getStack().push(newSelf);
	}

	
	@Override
	public void visitJmlLoopInvariant(JmlLoopInvariant self) {
		VNWhileExpressionVisitor exprSimplifierVisitor = new VNWhileExpressionVisitor(variableMapping);
		JExpression expr = null;

		self.predicate().specExpression().expression().accept(exprSimplifierVisitor);

		expr = exprSimplifierVisitor.getArrayStack().pop();

		JmlPredicate jmlPredicate = new JmlPredicate(new JmlSpecExpression(expr));

		JmlLoopInvariant newSelf = new JmlLoopInvariant(self.getTokenReference(), self.isRedundantly(), jmlPredicate);

		this.getStack().push(newSelf);
	}

}
