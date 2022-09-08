package ar.edu.taco.alloy.sbp;

import java.util.List;
import java.util.Map;

import ar.edu.jdynalloy.ast.JField;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprUnion;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.util.ExpressionMutator;

import com.google.common.collect.Maps;

class SBPExpressionMutator extends ExpressionMutator {

	private Map<String, JField> recursiveFields = Maps.newHashMap();

	SBPExpressionMutator(List<JField> recursiveFields) {
		for (JField field : recursiveFields) {
			this.recursiveFields.put(field.getFieldVariable().toString() + "_0", field);
		}
	}

	@Override
	public Object visit(ExprJoin n) {
		if (n.getLeft().toString().equals("QF") &&
				recursiveFields.containsKey(n.getRight().toString())) {
			ExprJoin bJoin = new ExprJoin(n.getLeft(), 
								new ExprVariable(new AlloyVariable(SBPUtils.buildBFieldNameNoQF(n.getRight().toString()))));
			ExprJoin fJoin = new ExprJoin(n.getLeft(), new ExprVariable(new AlloyVariable(SBPUtils.buildFFieldNameNoQF(n.getRight().toString()))));
			return new ExprUnion(bJoin, fJoin);
		}
		return super.visit(n);
	}

	@Override
	public Object visit(ExprVariable n) {
		String variable = n.getVariable().toString();
		if (recursiveFields.containsKey(variable)) {
			return new ExprVariable(new AlloyVariable("(" +
					SBPUtils.buildBFieldNameNoQF(variable) +
					SBPUtils.buildFFieldNameNoQF(variable) + ")"));
		}
		return super.visit(n);
	}

}
