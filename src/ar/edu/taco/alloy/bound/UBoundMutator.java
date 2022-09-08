package ar.edu.taco.alloy.bound;

import java.util.Set;

import ar.uba.dc.rfm.alloy.ast.AlloyModule;
import ar.uba.dc.rfm.alloy.util.AlloyMutator;

class UBoundMutator extends AlloyMutator {

	private String upper_bound_str;
	private Set<Integer> int_literals;

	public UBoundMutator(Set<Integer> int_literals) {
		this.int_literals = int_literals;
	}

	@Override
	public Object visit(AlloyModule n) {

		StringBuffer buff = new StringBuffer();
		buff.append(n.getAlloyStr());

		String int_literal_decl = declare_missing_int_literals(n.getAlloyStr());
		buff.append(int_literal_decl);

		buff.append(upper_bound_str);
		String new_alloy_str = buff.toString();

		return new AlloyModule(new_alloy_str, n.getGlobalSig(), n.getFacts(), n.getAssertions());

	}

	private String declare_missing_int_literals(String alloy_str) {
		StringBuffer buff = new StringBuffer();
		for (int int_literal : int_literals) {
			if (int_literal >= 0) {
				if (!alloy_str.contains(String.format("JavaPrimitiveIntegerLiteral%s extends JavaPrimitiveIntegerValue", int_literal))) {
					buff.append("\n");
					buff.append(String.format("one sig JavaPrimitiveIntegerLiteral%s extends JavaPrimitiveIntegerValue {} \n", int_literal));
					buff.append(String.format("{pred_java_primitive_integer_value_literal_%s[JavaPrimitiveIntegerLiteral%s]} \n", int_literal, int_literal));
					buff.append("\n");
				}
			} else {
				
				String neg_int_string = String.valueOf(int_literal);
				String neg_int_string_to_positive = neg_int_string.substring(1);				

				if (!alloy_str.contains(String.format("JavaPrimitiveIntegerLiteralMinus%s extends JavaPrimitiveIntegerValue", neg_int_string_to_positive))) {
					buff.append("\n");
					buff.append(String.format("one sig JavaPrimitiveIntegerLiteralMinus%s extends JavaPrimitiveIntegerValue {} \n", neg_int_string_to_positive));
					buff.append(String.format("{pred_java_primitive_integer_value_literal_minus_%s[JavaPrimitiveIntegerLiteralMinus%s]} \n", neg_int_string_to_positive,
							neg_int_string_to_positive));
					buff.append("\n");
				}

			}
		}
		String missing_literal_decl = buff.toString();
		if (!missing_literal_decl.isEmpty()) {
			return "//---- INTEGER LITERALS FROM UPPER BOUND -----//\n" + missing_literal_decl;
		} else
			return "";
	}

	public void setUpperBoundStr(String ubounds_str) {
		this.upper_bound_str = ubounds_str;
	}

}
