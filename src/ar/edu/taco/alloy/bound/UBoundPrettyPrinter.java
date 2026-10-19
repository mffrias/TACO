package ar.edu.taco.alloy.bound;

import ar.uba.dc.rfm.alloy.ast.expressions.ExprProduct;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprUnion;
import ar.uba.dc.rfm.alloy.util.ExpressionPrinter;

/**
 * Prints a bound expression for {@link UBoundPlugin}: as {@code ExpressionPrinter} does, except
 * that a union puts each operand after the first on a new line that starts with {@code +}, and
 * a product is printed {@code a->b}.
 */
class UBoundPrettyPrinter extends ExpressionPrinter {

	@Override
	public Object visit(ExprUnion n) {
		String left_str = (String) n.getLeft().accept(this);
		String right_str = (String) n.getRight().accept(this);
		return left_str + "\n" + "+" + right_str;
	}

	@Override
	public Object visit(ExprProduct n) {
		String left_str = (String) n.getLeft().accept(this);
		String right_str = (String) n.getRight().accept(this);
		return left_str + "->" + right_str;
	}

}
