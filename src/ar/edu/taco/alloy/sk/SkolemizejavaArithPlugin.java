package ar.edu.taco.alloy.sk;

import java.util.List;

import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.util.FormulaPrinter;
import ar.uba.dc.rfm.dynalloy.plugin.AlloyStringPlugin;

public class SkolemizejavaArithPlugin implements AlloyStringPlugin {

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
