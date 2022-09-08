package ar.edu.taco.infer;

import java.util.HashSet;
import java.util.Set;

import ar.uba.dc.rfm.alloy.ast.expressions.ExpressionVisitor;
import ar.uba.dc.rfm.alloy.ast.formulas.JFormulaVisitor;
import ar.uba.dc.rfm.alloy.ast.formulas.PredicateFormula;

class FunPredFormCollector extends JFormulaVisitor {

	public FunPredFormCollector(ExpressionVisitor visitor) {
		super(visitor);
	}

	private Set<String> pred_ids = new HashSet<String>();
	
	@Override
	public Object visit(PredicateFormula n) {
		pred_ids.add(n.getPredicateId());
		return super.visit(n);
	}

	public Set<String> getCollectedPredicates() {
		return pred_ids;
	}

}
