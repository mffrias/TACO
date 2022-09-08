package ar.edu.taco.simplejml;


import java.util.HashSet;

import org.jmlspecs.checker.JmlAssignmentStatement;
import org.jmlspecs.checker.JmlAssumeStatement;
import org.jmlspecs.checker.JmlConstructorDeclaration;
import org.jmlspecs.checker.JmlEqualityExpression;
import org.jmlspecs.checker.JmlFormalParameter;
import org.jmlspecs.checker.JmlMethodSpecification;
import org.jmlspecs.checker.JmlPredicate;
import org.jmlspecs.checker.JmlSpecExpression;
import org.multijava.mjc.CSpecializedType;
import org.multijava.mjc.CType;
import org.multijava.mjc.JAssignmentExpression;
import org.multijava.mjc.JClassFieldExpression;
import org.multijava.mjc.JConstructorBlock;
import org.multijava.mjc.JEqualityExpression;
import org.multijava.mjc.JExpressionStatement;
import org.multijava.mjc.JLocalVariable;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JVariableDeclarationStatement;
import org.multijava.mjc.JVariableDefinition;
import org.multijava.util.compiler.JavaStyleComment;

import ar.edu.jdynalloy.ast.JBlock;
import ar.edu.jdynalloy.ast.JVariableDeclaration;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

public class AssumeSimplifierVisitor extends JmlAstClonerStatementVisitor{

	public static int assumeSimplifierVarIndex = 0;
	public HashSet<JmlFormalParameter> newParameters = new HashSet<JmlFormalParameter>(); 
	
	public String getNewVarName(){
		assumeSimplifierVarIndex++;
		return "rightHandSideFormerAssume_" + assumeSimplifierVarIndex;
	}
	
	@Override
	public void visitJmlAssumeStatement(JmlAssumeStatement self){
		boolean isEquality = self.predicate().specExpression().expression() instanceof JEqualityExpression;
		boolean leftVarOrField = isEquality && 
				(((JEqualityExpression)self.predicate().specExpression().expression()).left() instanceof JLocalVariableExpression ||
						(((JEqualityExpression)self.predicate().specExpression().expression()).left() instanceof JClassFieldExpression));
		JmlAssignmentStatement theNewAssignment = null;
		if (isEquality && leftVarOrField){
			//Will create new variable and assign the RHS expression to it, so that the standard translation copes with it.
			//Will modify the assume so that now the LHS is equated to the just added variable.
			CType typeRHS = ((JEqualityExpression)self.predicate().specExpression().expression()).right().getApparentType();
			String newVarName = getNewVarName();
			JVariableDefinition theNewVar = new JVariableDefinition(self.getTokenReference(), 0,
					typeRHS, newVarName, ((JEqualityExpression)self.predicate().specExpression().expression()).right());
			
			JVariableDeclarationStatement theNewVarDeclSt = new JVariableDeclarationStatement(self.getTokenReference(), theNewVar, new JavaStyleComment[0]);
			
			JLocalVariableExpression theVarExpre = new JLocalVariableExpression(self.getTokenReference(), theNewVar);
			JEqualityExpression newEquExpre = new JEqualityExpression(self.getTokenReference(), JEqualityExpression.OPE_EQ, ((JEqualityExpression)(self.predicate().specExpression().expression())).left(), 
					theVarExpre);
			JmlSpecExpression theEquSpecExpre = new JmlSpecExpression(newEquExpre);
			JmlPredicate theEquPred = new JmlPredicate(theEquSpecExpre);
			JmlAssumeStatement simplfiedAssume = new JmlAssumeStatement(self.getTokenReference(), false, false, theEquPred, null, new JavaStyleComment[0]);
			JStatement[] theStatements = new JStatement[]{theNewVarDeclSt, simplfiedAssume};
			org.multijava.mjc.JBlock newBlockWithSimplifiedAssume = new org.multijava.mjc.JBlock(self.getTokenReference(), theStatements, self.getComments());
			super.getStack().push(newBlockWithSimplifiedAssume);
		} else {
			super.getStack().push(self);
		}
		
	}
	
	
//	@Override
//	public void visitJmlConstructorDeclaration(JmlConstructorDeclaration self) {
//		self.body().accept(this);
//		JConstructorBlock newConstructorBlock = (JConstructorBlock) this.getStack().pop();
//
//		JmlMethodSpecification methodSpecification;
//		if (self.methodSpecification() == null) {
//			methodSpecification = null;
//		} else {
//			self.methodSpecification().accept(this);
//			methodSpecification = (JmlMethodSpecification) this.getStack().pop();
//		}
//
//		JmlConstructorDeclaration jmlConstructorDeclaration = JmlConstructorDeclaration.makeInstance(self.getTokenReference(), self.modifiers(), self.ident(),
//				self.parameters(), self.getExceptions(), newConstructorBlock, self.javadocComment(), null, methodSpecification);
//		for (JmlFormalParameter param : this.newParameters){
//			jmlConstructorDeclaration.addParameter(param);
//		}
//
//		this.getStack().push(jmlConstructorDeclaration);
//	}
	

}
