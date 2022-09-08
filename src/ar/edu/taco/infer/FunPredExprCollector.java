package ar.edu.taco.infer;

import java.util.HashSet;
import java.util.Set;

import ar.uba.dc.rfm.alloy.ast.expressions.ExprFunction;
import ar.uba.dc.rfm.alloy.ast.expressions.ExpressionVisitor;

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
