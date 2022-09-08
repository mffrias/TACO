package ar.edu.taco.alloy.sk;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ar.edu.jdynalloy.factory.JPredicateFactory;
import ar.edu.taco.TacoException;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.PredicateFormula;

class SkPredFactBuilder {

	public List<AlloyFormula> build_axioms() {

		List<AlloyFormula> new_axioms = new LinkedList<AlloyFormula>();

		List<AlloyFormula> mul_facts = build_sk_mul_facts();
		new_axioms.addAll(mul_facts);

		List<AlloyFormula> div_facts = build_sk_div_facts();
		new_axioms.addAll(div_facts);

		List<AlloyFormula> int_add_facts = build_sk_int_add_facts();
		new_axioms.addAll(int_add_facts);

		List<AlloyFormula> float_add_facts = build_sk_float_add_facts();
		new_axioms.addAll(float_add_facts);

		List<AlloyFormula> float_sub_facts = build_sk_float_sub_facts();
		new_axioms.addAll(float_sub_facts);

		return new_axioms;
	}

	private void fill_sk_pred_set(String alloyStr) {
		sk_pred_set = new HashSet<SkPredArgument>();
		SkPredParser sk_parser = new SkPredParser();
		sk_parser.parse(alloyStr);
		sk_pred_set.addAll(sk_parser.get_sk_pred_arguments());
	}

	private List<AlloyFormula> build_sk_mul_facts() {
		return build_generic_sk_facts(SK_MUL_PREDICATE_IDS, SK_MUL_ARGUMENT_IDS);
	}

	private List<AlloyFormula> build_sk_int_add_facts() {
		return build_generic_sk_facts(SK_INT_ADD_PREDICATE_IDS, SK_ADD_ARGUMENT_IDS);
	}

	private List<AlloyFormula> build_sk_div_facts() {
		return build_generic_sk_facts(SK_DIV_PREDICATE_IDS, SK_DIV_ARGUMENT_IDS);
	}

	private List<AlloyFormula> build_sk_float_add_facts() {
		return build_generic_sk_facts(SK_ADD_PREDICATE_IDS, SK_ADD_ARGUMENT_IDS);
	}

	private List<AlloyFormula> build_sk_float_sub_facts() {
		return build_generic_sk_facts(SK_SUB_PREDICATE_IDS, SK_SUB_ARGUMENT_IDS);
	}

	private List<AlloyFormula> build_generic_sk_facts(Set<String> sk_predicate_ids, List<String> ordered_argument_names) {
		List<AlloyFormula> mul_facts = new LinkedList<AlloyFormula>();
		for (String mul_predicate_id : sk_predicate_ids) {
			List<AlloyFormula> mul_formulas = build_sk_predicate_facts(mul_predicate_id, ordered_argument_names);
			mul_facts.addAll(mul_formulas);
		}
		return mul_facts;
	}

	private static final Set<String> SK_DIV_PREDICATE_IDS = new HashSet<String>(Arrays.asList(JPredicateFactory.PRED_JAVA_PRIMITIVE_INTEGER_VALUE_DIV_REM,
			JPredicateFactory.PRED_JAVA_PRIMITIVE_LONG_VALUE_DIV_REM, JPredicateFactory.PRED_JAVA_PRIMITIVE_FLOAT_VALUE_DIV));

	private static final Set<String> SK_MUL_PREDICATE_IDS = new HashSet<String>(Arrays.asList(JPredicateFactory.PRED_JAVA_PRIMITIVE_INTEGER_VALUE_MUL,
			JPredicateFactory.PRED_JAVA_PRIMITIVE_LONG_VALUE_MUL, JPredicateFactory.PRED_JAVA_PRIMITIVE_FLOAT_VALUE_MUL));

	private static final Set<String> SK_ADD_PREDICATE_IDS = new HashSet<String>(Arrays.asList(JPredicateFactory.PRED_JAVA_PRIMITIVE_FLOAT_VALUE_ADD));
	
	private static final Set<String> SK_INT_ADD_PREDICATE_IDS = new HashSet<String>(Arrays.asList(JPredicateFactory.PRED_JAVA_PRIMITIVE_INTEGER_VALUE_ADD));

	private static final Set<String> SK_SUB_PREDICATE_IDS = new HashSet<String>(Arrays.asList(JPredicateFactory.PRED_JAVA_PRIMITIVE_FLOAT_VALUE_SUB));

	private static final List<String> SK_MUL_ARGUMENT_IDS = Arrays.asList("left", "right", "result", "overflow");

	private static final List<String> SK_DIV_ARGUMENT_IDS = Arrays.asList("left", "right", "result", "remainder");

	private static final List<String> SK_ADD_ARGUMENT_IDS = Arrays.asList("left", "right", "result", "overflow");

	private static final List<String> SK_SUB_ARGUMENT_IDS = Arrays.asList("left", "right", "result", "overflow");

	private boolean check_sanity_sk_mul_predicates() {

		return check_sanity_sk_generic_predicate(SK_MUL_PREDICATE_IDS, SK_MUL_ARGUMENT_IDS);
	}

	private boolean check_sanity_sk_generic_predicate(Set<String> skolem_mul_predicate_ids, List<String> expected_names) {

		for (String skolem_predicate_id : skolem_mul_predicate_ids) {

			if (sk_pred_map.containsKey(skolem_predicate_id)) {
				for (String local_prefix : sk_pred_map.get(skolem_predicate_id).keySet()) {

					for (int var_instant : sk_pred_map.get(skolem_predicate_id).get(local_prefix).keySet()) {

						for (int var_index : sk_pred_map.get(skolem_predicate_id).get(local_prefix).get(var_instant).keySet()) {
							Map<String, SkPredArgument> actual_names = sk_pred_map.get(skolem_predicate_id).get(local_prefix).get(var_instant).get(var_index);

							Set<String> actual = actual_names.keySet();
							Set<String> expected = new HashSet<String>(expected_names);
							if (!actual.equals(expected)) {

								return false;
							}
						}

					}
				}
			}
		}

		return true;
	}

	private void fill_sk_pred_map() {

		sk_pred_map = new HashMap<String, Map<String, Map<Integer, Map<Integer, Map<String, SkPredArgument>>>>>();

		for (SkPredArgument sk_pred_argument : sk_pred_set) {

			String predicate_id = sk_pred_argument.get_skolemized_predicate_id();
			String skolem_prefix = sk_pred_argument.get_skolemized_local_variable_prefix();
			int var_instant = sk_pred_argument.get_var_instant();
			int var_index = sk_pred_argument.get_var_index();
			String argument_name = sk_pred_argument.get_argument_name();

			// group by instant
			if (!sk_pred_map.containsKey(predicate_id)) {
				sk_pred_map.put(predicate_id, new HashMap<String, Map<Integer, Map<Integer, Map<String, SkPredArgument>>>>());
			}
			Map<String, Map<Integer, Map<Integer, Map<String, SkPredArgument>>>> skolem_prefixes = sk_pred_map.get(predicate_id);

			// group by prefix
			if (!skolem_prefixes.containsKey(skolem_prefix)) {
				skolem_prefixes.put(skolem_prefix, new HashMap<Integer, Map<Integer, Map<String, SkPredArgument>>>());
			}
			Map<Integer, Map<Integer, Map<String, SkPredArgument>>> var_instants = skolem_prefixes.get(skolem_prefix);

			// group by instant
			if (!var_instants.containsKey(var_instant)) {
				var_instants.put(var_instant, new HashMap<Integer, Map<String, SkPredArgument>>());
			}
			Map<Integer, Map<String, SkPredArgument>> var_indexes = var_instants.get(var_instant);

			// group by index
			if (!var_indexes.containsKey(var_index)) {
				var_indexes.put(var_index, new HashMap<String, SkPredArgument>());
			}

			Map<String, SkPredArgument> arg_names = var_indexes.get(var_index);
			arg_names.put(argument_name, sk_pred_argument);

		}

	}

	private boolean check_sanity_sk_div_predicates() {

		return check_sanity_sk_generic_predicate(SK_DIV_PREDICATE_IDS, SK_DIV_ARGUMENT_IDS);
	}

	private boolean check_sanity_sk_add_predicates() {

		return check_sanity_sk_generic_predicate(SK_ADD_PREDICATE_IDS, SK_ADD_ARGUMENT_IDS);
	}

	private boolean check_sanity_sk_sub_predicates() {

		return check_sanity_sk_generic_predicate(SK_SUB_PREDICATE_IDS, SK_SUB_ARGUMENT_IDS);
	}

	private List<AlloyFormula> build_sk_predicate_facts(String predicate_id, List<String> ordered_arguments) {

		List<AlloyFormula> formulas = new LinkedList<AlloyFormula>();

		if (sk_pred_map.containsKey(predicate_id)) {
			Map<String, Map<Integer, Map<Integer, Map<String, SkPredArgument>>>> sk_prefixes = sk_pred_map.get(predicate_id);
			for (String local_prefix : sk_prefixes.keySet()) {

				Map<Integer, Map<Integer, Map<String, SkPredArgument>>> var_instants = sk_prefixes.get(local_prefix);
				for (int var_instant : var_instants.keySet()) {

					Map<Integer, Map<String, SkPredArgument>> sk_pred_arguments = var_instants.get(var_instant);

					for (int var_index : sk_pred_arguments.keySet()) {

						List<AlloyExpression> arguments = new LinkedList<AlloyExpression>();
						for (String arg_name : ordered_arguments) {

							SkPredArgument sk_pred_argument = sk_pred_arguments.get(var_index).get(arg_name);
							String sk_pred_argument_str = sk_pred_argument.toString();

							AlloyExpression expr_argument = ExprJoin.join(ExprConstant.buildExprConstant("QF"), ExprVariable
									.buildNonMutableExprVariable(sk_pred_argument_str));
							arguments.add(expr_argument);

						}

						AlloyFormula fact_formula = new PredicateFormula(null, predicate_id, arguments);
						formulas.add(fact_formula);
					}
				}
			}
		}

		return formulas;
	}

	private Set<SkPredArgument> sk_pred_set = new HashSet<SkPredArgument>();

	private Map<String, Map<String, Map<Integer, Map<Integer, Map<String, SkPredArgument>>>>> sk_pred_map = new HashMap<String, Map<String, Map<Integer, Map<Integer, Map<String, SkPredArgument>>>>>();

	public SkPredFactBuilder(String alloyStr) {
		fill_sk_pred_set(alloyStr);
		fill_sk_pred_map();
		boolean check_sanity = check_sanity();
		if (!check_sanity) {
			throw new TacoException("SkPredFactBuilder: " + "Sanity check FAILED while constructing object of class " + this.getClass().getName());
		}
	}

	private boolean check_sanity() {

		boolean mul_sanity_check = check_sanity_sk_mul_predicates();
		if (!mul_sanity_check) {
			return false;
		}

		boolean div_sanity_check = check_sanity_sk_div_predicates();
		if (!div_sanity_check) {
			return false;
		}

		boolean add_sanity_check = check_sanity_sk_add_predicates();
		if (!add_sanity_check) {
			return false;
		}

		boolean sub_sanity_check = check_sanity_sk_sub_predicates();
		if (!sub_sanity_check) {
			return false;
		}

		return true;
	}
}
