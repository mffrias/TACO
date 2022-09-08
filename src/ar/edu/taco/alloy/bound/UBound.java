package ar.edu.taco.alloy.bound;

import java.io.Serializable;

import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;

class UBound implements Serializable {

	private static final long serialVersionUID = 6650789898106798894L;

	private ExprJoin field;

	private AlloyExpression upper_bound;

	public UBound(ExprJoin field, AlloyExpression upper_bound) {
		this.field = field;
		this.upper_bound = upper_bound;
	}
	
	public ExprJoin getField() {
		return field;
	}

	public AlloyExpression getUpperBoundExpression() {
		return upper_bound;
	}

}
