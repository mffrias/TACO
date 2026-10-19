package ar.edu.taco.simplejml;

import java.util.Vector;

import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.ExpressionVisitor;

/**
 * An Alloy expression visitor that collects the name ({@code toString()}) of every variable it
 * meets, in order and with repetitions, in its package-private field {@code result}. Its only user
 * is {@code JmlExpressionVisitor.predicateCallContainsAnyOfTheVars}, a private method that only
 * commented-out code calls. No static state.
 *
 * <p>Unclear: nothing reaches it; whether it is still needed is an open question.
 */
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
