package ar.edu.taco.infer;

import java.util.HashSet;
import java.util.Set;

import ar.uba.dc.rfm.alloy.ast.expressions.ExprFunction;
import ar.uba.dc.rfm.alloy.ast.expressions.ExpressionVisitor;

/**
 * Collects the ids of the Alloy functions applied in the expressions it visits
 * ({@link #getCollectedFunctions()}). {@link JDynAlloyFunPredCollector} pairs it with a
 * {@link FunPredFormCollector}, so that the formulas inside expressions are visited too.
 */
class FunPredExprCollector extends ExpressionVisitor {

	private Set<String> fun_ids = new HashSet<String>();

	@Override
	public Object visit(ExprFunction n) {
		fun_ids.add(n.getFunctionId());
		return super.visit(n);
	}
	
	public Set<String> getCollectedFunctions() {
		return fun_ids;
	}

}
