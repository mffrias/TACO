/**
 * 
 */
package ar.edu.taco.jfsl;

import java.util.Vector;

import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

public class JfslBehaviorCase {
	public final Vector<AlloyFormula> requires = new Vector<AlloyFormula>();
	public final Vector<AlloyFormula> ensures = new Vector<AlloyFormula>();
	public final Vector<AlloyExpression> modifies = new Vector<AlloyExpression>();
	public boolean modifies_everything = false;

	public boolean isEmpty() {
		return requires.isEmpty() && ensures.isEmpty()
				&& modifies.isEmpty();
	}
}