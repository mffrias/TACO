package ar.edu.taco.infer;

import java.util.Set;

import ar.edu.jdynalloy.ast.JDynAlloyVisitor;
import ar.edu.jdynalloy.ast.JObjectInvariant;

/**
 * Collects the ids of the functions and predicates applied in the object invariants of the
 * JDynAlloy modules it visits ({@link #getCollectedFunctions()},
 * {@link #getCollectedPredicates()}). {@code ScopeInference} runs one on every module when it
 * infers the bitwidth, and then does not use the result: the test on the {@code size_of}
 * function and predicate is commented out there, replaced by a test of
 * {@code useJavaArithmetic} (see the comment in {@code ScopeInference.infer_alloy_bitwidth}).
 * Unclear: the result is ignored; whether the collection is still needed is an open question.
 */
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
