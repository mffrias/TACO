package ar.edu.taco.simplejml;

import java.util.HashSet;
import java.util.Vector;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.ExpressionVisitor;
import ar.uba.dc.rfm.alloy.ast.formulas.FormulaVisitor;

/**
 * An Alloy expression visitor that collects the names of the variables whose name contains
 * {@code ARG_result}: the result variables of JML arithmetic
 * ({@code SK_jml_<predicate>_ARG_result_<n>}, see {@code builtin.JMLAuxiliaryConstantsFactory}).
 * Its only user is {@code JmlExpressionVisitor.predicateCallOccursInSpec}, a private method that
 * nothing calls. No static state.
 *
 * <p>Unclear: nothing reaches it; whether it is still needed is an open question.
 */
public class ResultVariableNamesCollectorVisitor extends ExpressionVisitor {
	
	HashSet<String> resultVariableNames = new HashSet<String>();
	
	public ResultVariableNamesCollectorVisitor(){}


	public Object visit(ExprVariable ev){
		if (ev.getVariable().getVariableId().getString().contains("ARG_result")){
			resultVariableNames.add(ev.getVariable().getVariableId().getString());
		}
		return null;
	}
	
	
	public HashSet<String> getResultVariableNames(){
		return resultVariableNames;
	}
	
	
}
