package ar.edu.taco.dynalloy;

import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.formulas.FormulaVisitor;
import ar.uba.dc.rfm.alloy.util.ExpressionPrinter;
import ar.uba.dc.rfm.dynalloy.ast.programs.InvokeAction;
import ar.uba.dc.rfm.dynalloy.ast.programs.TestPredicate;
import ar.uba.dc.rfm.dynalloy.util.DfsProgramVisitor;

class ObjectCreationCollector extends DfsProgramVisitor {

	private static final String GET_UNUSED_OBJECT = "getUnusedObject";

	private static final String INSTANCE_OF = "instanceOf";

	public ObjectCreationCollector() {
		super(new FormulaVisitor());
	}

	private boolean getUnusedObject_was_found = false;

	private ObjectCreationCounter object_creation_counter = new ObjectCreationCounter();

	@Override
	public Object visit(InvokeAction u) {
		if (u.getActionId().equals(GET_UNUSED_OBJECT)) {
			getUnusedObject_was_found = true;
		}
		return super.visit(u);
	}

	@Override
	public Object visit(TestPredicate t) {
		if (getUnusedObject_was_found) {
			if (t.getPredicateFormula().getPredicateId().equals(INSTANCE_OF)) {
				AlloyExpression type_set = t.getPredicateFormula().getParameters().get(1);
				allocate(type_set);
				getUnusedObject_was_found = false;
			}
		}
		return super.visit(t);
	}

	private void allocate(AlloyExpression type_set) {
		String type_str = (String) type_set.accept(new ExpressionPrinter());
		object_creation_counter.inc_alloc_type(type_str);
	}

	public ObjectCreationCounter getObjectCreationCounter() {
		return object_creation_counter;
	}

}
