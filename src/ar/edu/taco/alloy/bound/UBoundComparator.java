package ar.edu.taco.alloy.bound;

import java.util.Comparator;

import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
class UBoundComparator implements Comparator<UBound> {

	@Override
	public int compare(UBound a, UBound b) {
		ExprJoin a_field = a.getField();
		ExprJoin b_field = b.getField();

		AlloyExpression a_right = a_field.getRight();
		AlloyExpression b_right = b_field.getRight();

		if ((a_right instanceof ExprConstant) && (b_right instanceof ExprConstant)) {
			ExprConstant a_constant = (ExprConstant) a_right;
			ExprConstant b_constant = (ExprConstant) b_right;
			return a_constant.getConstantId().compareTo(b_constant.getConstantId());
		}

		throw new ClassCastException("cannot compare " + a_right.getClass().getName() + " and " + b_right.getClass().getName());
	}

}
