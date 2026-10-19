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

/**
 * Rewrites the expressions of the model's formulas for
 * {@link SBPInstrumentAlloyProcedureMutator}: each reference to the pre-state copy of a
 * recursive field, recognised by its name {@code <field>_0}, becomes the union of its backward
 * and forward fields. Returns the rewritten expression.
 */
class SBPExpressionMutator extends ExpressionMutator {

	private Map<String, JField> recursiveFields = Maps.newHashMap();

	SBPExpressionMutator(List<JField> recursiveFields) {
		for (JField field : recursiveFields) {
			this.recursiveFields.put(field.getFieldVariable().toString() + "_0", field);
		}
	}

	/**
	 * Replaces {@code QF.r_0}, for a recursive field r, with {@code QF.br_0 + QF.fr_0}; other
	 * joins are traversed as usual.
	 *
	 * @param n the join
	 * @return the rewritten expression
	 */
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

	/**
	 * Replaces a bare {@code r_0}, for a recursive field r, with a variable named
	 * {@code (br_0fr_0)}: the two names are joined without a {@code +}, so the result names no
	 * declared field. The references of the form {@code QF.r_0} are replaced by
	 * {@link #visit(ExprJoin)} before they reach this method.
	 *
	 * @param n the variable
	 * @return the rewritten expression
	 */
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
