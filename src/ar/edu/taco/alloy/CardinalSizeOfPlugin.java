package ar.edu.taco.alloy;

import java.util.LinkedList;
import java.util.List;

import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.infer.InferredScope;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveIntegerValue;
import ar.uba.dc.rfm.dynalloy.plugin.AlloyStringPlugin;

public class CardinalSizeOfPlugin implements AlloyStringPlugin {

	/* (non-Javadoc)
	 * @see ar.uba.dc.rfm.dynalloy.plugin.AlloyStringPlugin#transform(java.lang.String)
	 */
	@Override
	public String transform(String input) {

		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {

			String java_primitive_integer_value_sig_id = JavaPrimitiveIntegerValue.getInstance().getModule().getSignature().getSignatureId();
			if (input.contains(java_primitive_integer_value_sig_id)) {

				List<String> alloy_predicates = new LinkedList<String>();
				List<String> alloy_functions = new LinkedList<String>();

				int bitwidth = getBitwidth();
/*mfrias-1->0*/	if (bitwidth > 0) {
					int number_of_non_negatives = (int) Math.pow(2, bitwidth-1);

					// positive literals
					for (int i = 0; i < number_of_non_negatives; i++) {

						if (!JavaPrimitiveIntegerValue.getInstance().is_int_literal_already_defined(i)) {
							alloy_predicates.add(JavaPrimitiveIntegerValue.getInstance().pred_java_primitive_integer_value_literal(i));
							alloy_functions.add(JavaPrimitiveIntegerValue.getInstance().fun_java_primitive_integer_value_literal(i));
						}

					}

					String pred_java_primitive_integer_value_size_of = JavaPrimitiveIntegerValue.getInstance().pred_java_primitive_integer_value_size_of(number_of_non_negatives);
					String fun_java_primitive_integer_value_size_of = JavaPrimitiveIntegerValue.getInstance().fun_java_primitive_integer_value_size_of();
					alloy_predicates.add(pred_java_primitive_integer_value_size_of);
					alloy_functions.add(fun_java_primitive_integer_value_size_of);
				}

				StringBuffer buff = new StringBuffer();
				buff.append(input);
				for (String alloy_function : alloy_functions) {
					buff.append(alloy_function);
				}
				for (String alloy_predicate : alloy_predicates) {
					buff.append(alloy_predicate);
				}
				return buff.toString();
			}
		}
		return input;

	}

	/**
	 * <p>If the custom bitwidth is setted to a non positive integer 
	 * <b>and</b> the scope inferring feature is activated, the returned value is the 
	 * inferred bitwidth. Otherwise, the custom bitwidth is returned.</p>
	 * @return
	 */
	private int getBitwidth() {
		if (TacoConfigurator.getInstance().getInferScope() == true && TacoConfigurator.getInstance().getBitwidth() <= 0) {
			return Math.max(1, InferredScope.getInstance().getInferredAlloyBitwidth());
		} else {
			return TacoConfigurator.getInstance().getBitwidth();
		}
	}

}
