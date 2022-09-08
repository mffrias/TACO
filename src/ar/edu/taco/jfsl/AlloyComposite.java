package ar.edu.taco.jfsl;

import java.util.List;

import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

class AlloyComposite {

	public enum DECL {
		SET, SEQ, NONE
	}

	public final AlloyExpression expression;
	public final AlloyFormula formula;
	public final List<AlloyExpression> frame;
	public final String specField;
	public final DECL specFieldMult;

	public AlloyComposite(AlloyExpression expression) {
		this.expression = expression;
		this.formula = null;
		this.frame = null;
		this.specField = null;
		this.specFieldMult = null;
	}

	public AlloyComposite(AlloyFormula formula) {
		this.formula = formula;
		this.expression = null;
		this.frame = null;
		this.specField = null;
		this.specFieldMult = null;
	}

	public AlloyComposite(List<AlloyExpression> frame) {
		this.expression = null;
		this.formula = null;
		this.frame = frame;
		this.specField = null;
		this.specFieldMult = null;
	}

	public AlloyComposite(String specFieldId, DECL specFieldMult, AlloyExpression type,
			List<AlloyExpression> frame, AlloyFormula constraint) {
		this.specField = specFieldId;
		this.expression = type;
		this.frame = frame;
		this.formula = constraint;
		this.specFieldMult = specFieldMult;
	}

}
