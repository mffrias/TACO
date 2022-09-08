package ar.edu.taco.simplejml;

import java.util.Vector;

import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.ExpressionVisitor;

public class VariableNameCollectorVisitor extends ExpressionVisitor {
	
	Vector<String> result = new Vector<String>();

	public VariableNameCollectorVisitor() {
		// TODO Auto-generated constructor stub
	}


	public Object visit(ExprVariable n) {
		result.add(n.getVariable().toString());
		return null;
	}

	

}
