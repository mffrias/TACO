package ar.edu.taco.alloy.bound;

import ar.uba.dc.rfm.alloy.ast.expressions.ExprProduct;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprUnion;
import ar.uba.dc.rfm.alloy.util.ExpressionPrinter;

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
