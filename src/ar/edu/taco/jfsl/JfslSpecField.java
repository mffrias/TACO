/**
 * 
 */
package ar.edu.taco.jfsl;

import ar.edu.jdynalloy.xlator.JType;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

public class JfslSpecField {
	public JfslSpecField(String fieldId, AlloyExpression expression,
			JType expressionType, AlloyFormula formula) {
		super();
		this.fieldId = fieldId;
		this.expression = expression;
		this.expressionType = expressionType;
		this.formula = formula;
	}

	public final AlloyExpression expression;

	public final JType expressionType;

	public final AlloyFormula formula;

	public final String fieldId;

}