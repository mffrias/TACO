package ar.edu.taco.alloy.bound;

import java.util.Comparator;

import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprProduct;

/**
 * Orders the tuples of a bound for {@link UBoundSAXHandler}, which keeps them in a sorted set
 * (so duplicates are dropped): atom constants by label, products component by component.
 * Throws {@code ClassCastException} when a constant meets a product, i.e. for tuples of
 * different arity.
 */
class TupleComparator implements Comparator<AlloyExpression> {

	@Override
	public int compare(AlloyExpression left, AlloyExpression right) {
		if ((left instanceof ExprConstant) && (right instanceof ExprConstant)) {
			ExprConstant left_constant = (ExprConstant) left;
			ExprConstant right_constant = (ExprConstant) right;
			return compareConstants(left_constant, right_constant);
		} else if ((left instanceof ExprProduct) && (right instanceof ExprProduct)) {
			ExprProduct left_product = (ExprProduct) left;
			ExprProduct right_product = (ExprProduct) right;
			return compareProducts(left_product, right_product);
		} else {
			throw new ClassCastException(left.getClass().getName() + " cannot be compared to " + right.getClass().getName());
		}
	}

	private int compareProducts(ExprProduct left, ExprProduct right) {
		int compare_first_component = compare(left.getLeft(), right.getLeft());
		if (compare_first_component == 0) {
			return compare(left.getRight(), right.getRight());
		} else
			return compare_first_component;
	}

	private int compareConstants(ExprConstant left, ExprConstant right) {
		return left.getConstantId().compareTo(right.getConstantId());
	}

}
