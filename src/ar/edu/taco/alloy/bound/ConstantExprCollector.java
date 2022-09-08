package ar.edu.taco.alloy.bound;

import java.util.HashSet;
import java.util.Set;

import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.alloy.ast.expressions.ExpressionVisitor;

class ConstantExprCollector extends ExpressionVisitor {

	private Set<String> constant_ids = new HashSet<String>();
	
	@Override
	public Object visit(ExprConstant n) {
		constant_ids.add(n.getConstantId());
		return super.visit(n);
	}
	
	public Set<String> getCollectedConstants() {
		return constant_ids;
	}

}
