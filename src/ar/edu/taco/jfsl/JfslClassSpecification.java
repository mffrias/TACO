/**
 * 
 */
package ar.edu.taco.jfsl;

import java.util.Vector;

import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

public class JfslClassSpecification {
	public final Vector<AlloyFormula> invariant = new Vector<AlloyFormula>();
	public final Vector<JfslSpecField> spec_fields = new Vector<JfslSpecField>();
}