package ar.edu.taco.jml.varnames;

import java.util.HashMap;
import java.util.Map;

import org.multijava.mjc.JExpression;
import org.multijava.mjc.JFormalParameter;
import org.multijava.mjc.JGeneratedLocalVariable;
import org.multijava.mjc.JLocalVariable;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JVariableDefinition;

import ar.edu.taco.TacoException;
import ar.edu.taco.utils.jml.JmlAstClonerExpressionVisitor;

public class VNWhileExpressionVisitor extends JmlAstClonerExpressionVisitor {

	private Map<String, String> variableMapping;

	public VNWhileExpressionVisitor(HashMap<String, String> varMapping) {
		this.variableMapping = varMapping;
	}


	/** Visits the given local variable expression. */
	public void visitLocalVariableExpression(/* @non_null */JLocalVariableExpression self) {
		JLocalVariable variable = self.variable();
		String newIden = renameVariable(self.variable());

		JExpression newExpre = null;
		if (variable.expr() != null) {
			VNWhileExpressionVisitor visitor = new VNWhileExpressionVisitor((HashMap<String, String>) this.variableMapping);
			variable.expr().accept(visitor);
			newExpre = visitor.getArrayStack().pop();
		}

		JLocalVariable localVariable = null;

		if (variable instanceof JFormalParameter) {
			//no need to rename a formal parameter. No chance it is a local variable in a while loop
//			JFormalParameter jFormalParameter = (JFormalParameter) variable;
//			localVariable = new JFormalParameter(jFormalParameter.getTokenReference(), jFormalParameter.modifiers(),jFormalParameter.getDescription(), jFormalParameter.specializedType(), newIden);
			localVariable = variable;
		} else if (variable instanceof JGeneratedLocalVariable) {
			JGeneratedLocalVariable jGeneratedLocalVariable = (JGeneratedLocalVariable) variable;
			localVariable = new JGeneratedLocalVariable(jGeneratedLocalVariable.getTokenReference(), jGeneratedLocalVariable.modifiers(), jGeneratedLocalVariable.getType() , newIden, newExpre);
		} else if (variable instanceof JVariableDefinition) {
			JVariableDefinition jVariableDefinition = (JVariableDefinition) variable;
			localVariable = new JVariableDefinition(jVariableDefinition.getTokenReference(), jVariableDefinition.modifiers(),jVariableDefinition.getType(),newIden, newExpre);

			//			long modifiers, 
			//			CType type,
			//			String ident,
			//	JExpression initializer ) {	

		} else {
			throw new TacoException("invalid JLocalVariable type not yet supported");
		}

		JLocalVariableExpression newVariableExpre = 
							new JLocalVariableExpression(self.getTokenReference(), localVariable);
		this.getArrayStack().push(newVariableExpre);
	}



	private String renameVariable(JLocalVariable variable) {
		String newIden;
		if (variableMapping.containsKey(variable.ident())) {
			newIden = variableMapping.get(variable.ident());
		} else {
			newIden = variable.ident();
		}
		//ISSUE mfrias: Not cloning the variable, may fail!!

		//		variable.setIdent(newIden);

		//		JLocalVariable localVariable;
		//		if (variable instanceof JFormalParameter) {
		//			JFormalParameter jFormalParameter = (JFormalParameter) variable; 
		//			localVariable = new JFormalParameter(jFormalParameter.getTokenReference(), jFormalParameter.modifiers(),jFormalParameter.getDescription(), jFormalParameter.specializedType(), newIden);
		//		} else if (variable instanceof JGeneratedLocalVariable) {
		//			JGeneratedLocalVariable jGeneratedLocalVariable = (JGeneratedLocalVariable) variable;
		//			localVariable = new JGeneratedLocalVariable(jGeneratedLocalVariable.getTokenReference(), jGeneratedLocalVariable.modifiers(), jGeneratedLocalVariable.getType() , newIden, jGeneratedLocalVariable.getValue());
		//		} else if (variable instanceof JVariableDefinition) {
		//			//			public JVariableDefinition( TokenReference where,
		//			//					long modifiers, 
		//			//					CType type,
		//			//					String ident,
		//			//					JExpression initializer )
		//			JVariableDefinition jVariableDefinition = (JVariableDefinition) variable;
		//			localVariable = new JVariableDefinition(jVariableDefinition.getTokenReference(), jVariableDefinition.modifiers(),jVariableDefinition.getType(),newIden,jVariableDefinition.expr());
		//
		//			//			long modifiers, 
		//			//			CType type,
		//			//			String ident,
		//			//			JExpression initializer ) {			
		//		} else {
		//			throw new TacoException("invalid JLocalVariable type not supported");
		//		}
		//		return localVariable;
		return newIden;
	}


}
