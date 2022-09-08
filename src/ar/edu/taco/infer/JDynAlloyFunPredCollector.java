package ar.edu.taco.infer;

import java.util.Set;

import ar.edu.jdynalloy.ast.JDynAlloyVisitor;
import ar.edu.jdynalloy.ast.JObjectInvariant;

class JDynAlloyFunPredCollector extends JDynAlloyVisitor {

	private FunPredFormCollector formula_collector;
	private FunPredExprCollector expression_collector;

	public JDynAlloyFunPredCollector(boolean isJavaArithmetic) {
		super(isJavaArithmetic);
		expression_collector = new FunPredExprCollector();
		formula_collector = new FunPredFormCollector(expression_collector);
		expression_collector.setFormulaVisitor(formula_collector);
	}

	@Override
	public Object visit(JObjectInvariant node) {
		node.getFormula().accept(formula_collector);
		return super.visit(node);
	}

	public Set<String> getCollectedFunctions() {
		return this.expression_collector.getCollectedFunctions();
	}

	public Set<String> getCollectedPredicates() {
		return this.formula_collector.getCollectedPredicates();
	}

}
