package ar.edu.taco.alloy.sk;

import java.util.List;

import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.util.FormulaPrinter;
import ar.uba.dc.rfm.dynalloy.plugin.AlloyStringPlugin;

/**
 * An Alloy-text plugin of the DynAlloy-to-Alloy translation, which
 * {@code DynalloyToAlloyManager} registers with {@code useJavaArithmetic}: it states the Java
 * arithmetic operations of the method bodies as facts. Each operation got fresh variables
 * {@code SK_<predicate>_ARG_<argument>_<n>} and only a call of an empty marker predicate
 * ({@code ar.edu.taco.simplejml.builtin.AuxiliaryConstantsFactory}); this plugin finds those
 * variables among the fields of {@code QF} in the Alloy text and adds a fact that calls the
 * operation's real predicate on them, for each operation and DynAlloy index
 * ({@link SkPredFactBuilder}). The fields of {@code QF} exist only with
 * {@code dynalloy.toAlloy.removeQuantifiers}; without it, no fact is added. No state.
 */
public class SkolemizejavaArithPlugin implements AlloyStringPlugin {

	/**
	 * Appends to the Alloy text one {@code fact} with the predicate calls of
	 * {@link SkPredFactBuilder#build_axioms()}, if there are any.
	 *
	 * @param input the Alloy text of the model
	 * @return the text with the fact, or {@code input}
	 * @throws ar.edu.taco.TacoException if the skolem variables of an operation do not have the
	 *         expected arguments (see {@link SkPredFactBuilder})
	 */
	public String transform(String input) {
		SkPredFactBuilder sk_pred_fact_builder = new SkPredFactBuilder(input);
		List<AlloyFormula> sk_pred_facts = sk_pred_fact_builder.build_axioms();

		String output;
		if (!sk_pred_facts.isEmpty()) {

			StringBuffer buff = new StringBuffer();
			buff.append("\n");
			buff.append("fact {\n");
			FormulaPrinter printer = new FormulaPrinter();
			for (AlloyFormula sk_fact : sk_pred_facts) {
				String sk_fact_str = (String) sk_fact.accept(printer);
				buff.append(sk_fact_str + "\n");
			}

			buff.append("}\n");
			buff.append("\n");

			String fact_str = buff.toString();

			output = input + fact_str;

		} else {
			output = input;
		}
		return output;

	}

}
