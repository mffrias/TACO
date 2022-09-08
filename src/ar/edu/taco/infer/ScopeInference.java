package ar.edu.taco.infer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.xlator.ObjectCreationCounter;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoCustomScope;
import ar.edu.taco.dynalloy.ArithmeticOpCounter;
import ar.edu.taco.dynalloy.CharOpCounter;
import ar.edu.taco.dynalloy.FloatOpCounter;
import ar.edu.taco.dynalloy.IntegerOpCounter;
import ar.edu.taco.dynalloy.LongOpCounter;
import ar.edu.taco.infer.Graph.LabeledNode;
import ar.edu.taco.simplejml.builtin.JObject;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveCharValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveFloatValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveIntegerValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveLongValue;

public class ScopeInference {

	private static final int DEFAULT_ALLOY_BITWDITH = 1;
	private List<JDynAlloyModule> src_jdynalloy_modules = null;
	private ArithmeticOpCounter arithmetic_op_counter = null;
	private ObjectCreationCounter object_alloc_counter = null;

	/**<p>Infer all the scopes and the bitwidth to be used in the analysis.</p>
	 * <p>If the analysis must be performed using Java arithmetics, the inferred
	 * bitwidth will be the sum of the scopes of the signatures being analyzed.</p>
	 * @return
	 */
	public InferredScope inferScope() {
		check_state();

		// infer input-scope
		Graph class_graph = ClassGraphBuilder.buildClassGraph(this.src_jdynalloy_modules);
		Scope inferred_concrete_input_scope = infer_input_scope(class_graph);

		// bound input-scope
		int object_limit = TacoConfigurator.getInstance().getObjectScope();
		Scope bounded_concrete_input_scope = inferred_concrete_input_scope.bound(object_limit);

		// infer program-scope
		Scope inferred_concrete_program_scope = infer_program_scope();

		// consolidate scopes using extends relation and Custom Scope
		Graph extension_tree = ClassGraphBuilder.buildExtensionTree(this.src_jdynalloy_modules);
		Scope inferred_scope = consolidate_scope(extension_tree, bounded_concrete_input_scope, inferred_concrete_program_scope, TacoConfigurator.getInstance().getTacoCustomScope());//mfrias: added the custom scope.

		// infer bitwidth
		int inferred_bitwidth = infer_alloy_bitwidth(inferred_scope);
		
		

		// notify new inferred scope
		InferredScope.initialize_inferred_scope(inferred_scope, inferred_bitwidth, inferred_concrete_input_scope, bounded_concrete_input_scope,
				inferred_concrete_program_scope);

		return InferredScope.getInstance();
	}

	private Scope consolidate_scope(final Graph tree, final Scope bounded_input_scope, final Scope inferred_program_scope, final TacoCustomScope customScope) {//mfrias: added the custom scope in order to consolidate using this scope as well.
		Scope inferred_scope = new Scope();
		Set<String> signature_set = new HashSet<String>();
		signature_set.addAll(bounded_input_scope.signatureSet());
		signature_set.addAll(inferred_program_scope.signatureSet());

		for (String signature_id : signature_set) {
			int bounded_input_scope_of = bounded_input_scope.getInferredScopeOf(signature_id).int_value;
			int inferred_program_scope_of = inferred_program_scope.getInferredScopeOf(signature_id).int_value;
			int custom_scope_of = 0;
			if (customScope.getCustomTypes().contains(signature_id.replace("_", "."))) {
				custom_scope_of = customScope.getScopeForType(signature_id.replace("_", ".")); //mfrias: compute the custom scope for type signature_id.
			}	
			int concrete_scope_of = Math.max(custom_scope_of, bounded_input_scope_of + inferred_program_scope_of); //mfrias: consider the maximum with the custom scope.
			inferred_scope.setInputScopeInteger(signature_id, concrete_scope_of);
		}

		Scope expanded_scope = expand_signature_hierarchy(tree, inferred_scope, "$Root$");

		return expanded_scope;
	}

	private Scope expand_signature_hierarchy(final Graph tree, final Scope inferred_scope, final String node_id) {
		Scope scope = new Scope();
		int scope_of = inferred_scope.getInferredScopeOf(node_id).int_value;
		if (tree.getLabelledEgdes(node_id) != null) {
			for (LabeledNode labeled_node : tree.getLabelledEgdes(node_id)) {
				String child_node_id = labeled_node.node_id;
				Scope child_scope = expand_signature_hierarchy(tree, inferred_scope, child_node_id);
				scope_of += child_scope.getInferredScopeOf(child_node_id).int_value;

				for (String sig_id : child_scope.signatureSet()) {
					int expanded_scope = child_scope.getInferredScopeOf(sig_id).int_value;
					scope.setInputScopeInteger(sig_id, expanded_scope);
				}

			}
		}
		scope.setInputScopeInteger(node_id, scope_of);
		return scope;
	}

	/**<p>If the analysis must be performed using Java arithmetics, the inferred
	 * bitwidth will be the sum of the scopes of the signatures being analyzed.</p>
	 * @param consolidated_scope
	 * @return the inferred alloy bitwidth
	 */
	private int infer_alloy_bitwidth(Scope consolidated_scope) {
		// look for cardinality functions

		Set<String> predicate_ids = new HashSet<String>();
		Set<String> function_ids = new HashSet<String>();
		for (JDynAlloyModule jdyn_module : src_jdynalloy_modules) {
			JDynAlloyFunPredCollector visitor = new JDynAlloyFunPredCollector(TacoConfigurator.getInstance().getUseJavaArithmetic());
			jdyn_module.accept(visitor);
			function_ids.addAll(visitor.getCollectedFunctions());
			predicate_ids.addAll(visitor.getCollectedPredicates());
		}

//mfrias: the condition below seems wrong. The scope is required in class CardinalSizeOfPlugin in order to build the correct (considers the scope) predicate. 
// Therefore, in this point, neither the function nor the predicate were included. Since the "sizeOf" funcs/preds are included iff we useJavaArithmetic, I will
// instead check this condition.		
//		if (function_ids.contains("fun_java_primitive_integer_value_size_of") || predicate_ids.contains("pred_java_primitive_integer_value_size_of")) {
		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {

			IntegerOrInfinity object_scope = consolidated_scope.getInferredScopeOf(JObject.getInstance().getModule().getSignature().getSignatureId());
			int object_scope_int = object_scope.int_value;
			int inferred_alloy_bitwidth = log2(object_scope_int) + 1;

			return inferred_alloy_bitwidth;
		}

		return DEFAULT_ALLOY_BITWDITH;
	}

	private int log2(int val) {
		double v = (Math.log(val) / Math.log(2));
		return (int) Math.ceil(v);
	}

	private Scope infer_program_scope() {

		Scope inferred_program_scope = new Scope();

		add_object_creation_count(inferred_program_scope);

		LiteralCount literal_count = countAllLiterals();

		IntegerOpCounter integer_op_counter = arithmetic_op_counter.integerOpCounter;
		int inferred_integer_program_scope = infer_integer_program_scope(literal_count.integer_literal_count, integer_op_counter);
		String java_primitive_integer_value_sig_id = JavaPrimitiveIntegerValue.getInstance().getModule().getSignature().getSignatureId();
		inferred_program_scope.setInputScopeInteger(java_primitive_integer_value_sig_id, inferred_integer_program_scope);

		LongOpCounter long_op_counter = arithmetic_op_counter.longOpCounter;
		int inferred_long_program_scope = infer_long_scope(literal_count.long_literal_count, long_op_counter);
		String java_primitive_long_value_sig_id = JavaPrimitiveLongValue.getInstance().getModule().getSignature().getSignatureId();
		inferred_program_scope.setInputScopeInteger(java_primitive_long_value_sig_id, inferred_long_program_scope);

		FloatOpCounter float_op_counter = arithmetic_op_counter.floatOpCounter;
		int inferred_float_program_scope = infer_float_scope(literal_count.float_literal_count, float_op_counter);
		String java_primitive_float_value_sig_id = JavaPrimitiveFloatValue.getInstance().getModule().getSignature().getSignatureId();
		inferred_program_scope.setInputScopeInteger(java_primitive_float_value_sig_id, inferred_float_program_scope);

		CharOpCounter char_op_counter = arithmetic_op_counter.charOpCounter;
		int inferred_char_program_scope = infer_char_scope(literal_count.char_literal_count, char_op_counter);
		String java_primitive_char_value_sig_id = JavaPrimitiveCharValue.getInstance().getModule().getSignature().getSignatureId();
		inferred_program_scope.setInputScopeInteger(java_primitive_char_value_sig_id, inferred_char_program_scope);

		
		return inferred_program_scope;
	}

	
	private static final int JAVA_PRIMITIVE_CHAR_VALUE_ADD_SCOPE = 1;
	private static final int JAVA_PRIMITIVE_CHAR_VALUE_SUB_SCOPE = 1;
	private static final int JAVA_PRIMITIVE_CHAR_VALUE_NARROWING_CAST_SCOPE = 1;

	private int infer_char_scope(int char_literal_count, CharOpCounter char_op_counter) {

		int inferred_add_char_scope = char_op_counter.add_count() * JAVA_PRIMITIVE_CHAR_VALUE_ADD_SCOPE;
		int inferred_sub_char_scope = char_op_counter.sub_count() * JAVA_PRIMITIVE_CHAR_VALUE_SUB_SCOPE;
		int inferred_narrowing_cast_char_scope = char_op_counter.narrowing_cast_count() * JAVA_PRIMITIVE_CHAR_VALUE_NARROWING_CAST_SCOPE;

		int inferred_char_program_scope = char_literal_count;
		inferred_char_program_scope += inferred_add_char_scope;
		inferred_char_program_scope += inferred_sub_char_scope;
		inferred_char_program_scope += inferred_narrowing_cast_char_scope;

		return inferred_char_program_scope;
	}

	
	private static final int JAVA_PRIMITIVE_INTEGER_VALUE_ADD_SCOPE = 1;
	private static final int JAVA_PRIMITIVE_INTEGER_VALUE_SUB_SCOPE = 1;
	private static final int JAVA_PRIMITIVE_INTEGER_VALUE_SSHR_SCOPE = 1;
	private static final int JAVA_PRIMITIVE_INTEGER_VALUE_MUL_SCOPE = 1;
	private static final int JAVA_PRIMITIVE_INTEGER_VALUE_DIV_REM_SCOPE = 2;
	private static final int JAVA_PRIMITIVE_INTEGER_VALUE_CHAR_CAST_SCOPE = 1;
	private static final int JAVA_PRIMITIVE_INTEGER_VALUE_NARROWING_CAST_SCOPE = 1;

	private int infer_integer_program_scope(int integer_literal_count, IntegerOpCounter integer_op_counter) {

		int inferred_add_int_scope = integer_op_counter.add_count() * JAVA_PRIMITIVE_INTEGER_VALUE_ADD_SCOPE;
		int inferred_sub_int_scope = integer_op_counter.sub_count() * JAVA_PRIMITIVE_INTEGER_VALUE_SUB_SCOPE;
		int inferred_sshr_int_scope = integer_op_counter.sshr_count() * JAVA_PRIMITIVE_INTEGER_VALUE_SSHR_SCOPE;
		int inferred_mul_int_scope = integer_op_counter.mul_count() * JAVA_PRIMITIVE_INTEGER_VALUE_MUL_SCOPE;
		int inferred_div_rem_int_scope = integer_op_counter.div_rem_count() * JAVA_PRIMITIVE_INTEGER_VALUE_DIV_REM_SCOPE;
		int inferred_char_cast_to_int_scope = integer_op_counter.cast_char_count() * JAVA_PRIMITIVE_INTEGER_VALUE_CHAR_CAST_SCOPE;
		int inferred_narrowing_cast_to_int_scope = integer_op_counter.narrowing_cast_count() * JAVA_PRIMITIVE_INTEGER_VALUE_NARROWING_CAST_SCOPE;

		int inferred_integer_program_scope = integer_literal_count;
		inferred_integer_program_scope += inferred_add_int_scope;
		inferred_integer_program_scope += inferred_sub_int_scope;
		inferred_integer_program_scope += inferred_sshr_int_scope;
		inferred_integer_program_scope += inferred_mul_int_scope;
		inferred_integer_program_scope += inferred_div_rem_int_scope;
		inferred_integer_program_scope += inferred_char_cast_to_int_scope;
		inferred_integer_program_scope += inferred_narrowing_cast_to_int_scope;

		return inferred_integer_program_scope;
	}

	private static final int JAVA_PRIMITIVE_LONG_VALUE_ADD_SCOPE = 1;
	private static final int JAVA_PRIMITIVE_LONG_VALUE_SUB_SCOPE = 1;
	private static final int JAVA_PRIMITIVE_LONG_VALUE_MUL_SCOPE = 1;
	private static final int JAVA_PRIMITIVE_LONG_VALUE_DIV_REM_SCOPE = 5;
	private static final int JAVA_PRIMITIVE_LONG_VALUE_CASTS = 1;

	private int infer_long_scope(int long_literal_count, LongOpCounter long_op_counter) {

		int inferred_add_long_scope = long_op_counter.add_count() * JAVA_PRIMITIVE_LONG_VALUE_ADD_SCOPE;
		int inferred_sub_long_scope = long_op_counter.sub_count() * JAVA_PRIMITIVE_LONG_VALUE_SUB_SCOPE;
		int inferred_mul_long_scope = long_op_counter.mul_count() * JAVA_PRIMITIVE_LONG_VALUE_MUL_SCOPE;
		int inferred_div_rem_long_scope = long_op_counter.div_rem_count() * JAVA_PRIMITIVE_LONG_VALUE_DIV_REM_SCOPE;
		int inferred_casts_long_scope = long_op_counter.casts_count() * JAVA_PRIMITIVE_LONG_VALUE_CASTS;

		int inferred_long_program_scope = long_literal_count;
		inferred_long_program_scope += inferred_add_long_scope;
		inferred_long_program_scope += inferred_sub_long_scope;
		inferred_long_program_scope += inferred_mul_long_scope;
		inferred_long_program_scope += inferred_div_rem_long_scope;
		inferred_long_program_scope += inferred_casts_long_scope;

		return inferred_long_program_scope;

	}

	private static final int JAVA_PRIMITIVE_FLOAT_VALUE_ADD_SCOPE = 1;
	private static final int JAVA_PRIMITIVE_FLOAT_VALUE_SUB_SCOPE = 1;
	private static final int JAVA_PRIMITIVE_FLOAT_VALUE_MUL_SCOPE = 1;
	private static final int JAVA_PRIMITIVE_FLOAT_VALUE_DIV_SCOPE = 1;

	private int infer_float_scope(int float_literal_count, FloatOpCounter float_op_counter) {

		int inferred_add_float_scope = float_op_counter.add_count() * JAVA_PRIMITIVE_FLOAT_VALUE_ADD_SCOPE;
		int inferred_sub_float_scope = float_op_counter.sub_count() * JAVA_PRIMITIVE_FLOAT_VALUE_SUB_SCOPE;
		int inferred_mul_float_scope = float_op_counter.mul_count() * JAVA_PRIMITIVE_FLOAT_VALUE_MUL_SCOPE;
		int inferred_div_float_scope = float_op_counter.div_count() * JAVA_PRIMITIVE_FLOAT_VALUE_DIV_SCOPE;

		int inferred_float_program_scope = float_literal_count;
		inferred_float_program_scope += inferred_add_float_scope;
		inferred_float_program_scope += inferred_sub_float_scope;
		inferred_float_program_scope += inferred_mul_float_scope;
		inferred_float_program_scope += inferred_div_float_scope;

		return inferred_float_program_scope;
	}

	private void add_object_creation_count(Scope scope) {
		for (String signature_id : this.object_alloc_counter.signatureSet()) {
			int allocation_count = this.object_alloc_counter.getAllocationCount(signature_id);
			scope.setInputScopeInteger(signature_id, allocation_count);
		}
	}

	private static class LiteralCount {
		int float_literal_count = 0;
		int integer_literal_count = 0;
		int long_literal_count = 0;
		int char_literal_count = 0;
	}

	private LiteralCount countAllLiterals() {

		LiteralCount literal_count = new LiteralCount();

		for (JDynAlloyModule module : this.src_jdynalloy_modules) {
			if (module.getSignature().isOne()) {
				String extended_sig_id = module.getSignature().getExtSigId();

				if (extended_sig_id != null) {

					String java_primitive_integer_value_sig_id = "JavaPrimitiveIntegerValue";
					if (extended_sig_id.equals(java_primitive_integer_value_sig_id)) {
						literal_count.integer_literal_count++;
					}

					String java_primitive_long_value_sig_id = "JavaPrimitiveLongValue";
					if (extended_sig_id.equals(java_primitive_long_value_sig_id)) {
						literal_count.long_literal_count++;
					}

					String java_primitive_float_value_sig_id = "JavaPrimitiveFloatValue";
					if (extended_sig_id.equals(java_primitive_float_value_sig_id)) {
						literal_count.float_literal_count++;
					}

					String java_primitive_char_value_sig_id = "JavaPrimitiveCharValue";
					if (extended_sig_id.equals(java_primitive_char_value_sig_id)) {
						literal_count.char_literal_count++;
					}
				}

			}
		}
		return literal_count;
	}

	private Scope infer_input_scope(Graph class_graph) {

		GraphPathCounter count_all_paths = new GraphPathCounter();
		Map<String, IntegerOrInfinity> number_of_paths = count_all_paths.count_all_paths(class_graph);

		Scope inferred_input_scope = new Scope();
		for (String node_id : class_graph.nodeSet()) {
			if (!node_id.equals("$Root$")) {
				IntegerOrInfinity paths = number_of_paths.get(node_id);
				if (paths.equals(IntegerOrInfinity.INFINITY)) {
					inferred_input_scope.setInputScopeInfinity(node_id);
				} else {
					IntegerOrInfinity scope = inferred_input_scope.getInferredScopeOf(node_id);
					int nonInfiniteScope = scope.int_value;
					nonInfiniteScope += paths.int_value;
					inferred_input_scope.setInputScopeInteger(node_id, nonInfiniteScope);
					if (TacoConfigurator.getInstance().getUseJavaArithmetic() && (node_id.equals("java_lang_IntArray") || node_id.equals("java_lang_ObjectArray"))){
						String java_primitive_integer_value_sig_id = JavaPrimitiveIntegerValue.getInstance().getModule().getSignature().getSignatureId();
						IntegerOrInfinity intsNumber = inferred_input_scope.getInferredScopeOf(java_primitive_integer_value_sig_id);
						if (!intsNumber.equals(IntegerOrInfinity.INFINITY)){
							int intNumber = intsNumber.int_value;
							intNumber = intNumber + 2;
							inferred_input_scope.setInputScopeInteger(java_primitive_integer_value_sig_id, intNumber);
						}
					}
					
				}
			}
		}
		return inferred_input_scope;
	}

	private void check_state() {
		if (src_jdynalloy_modules == null)
			throw new IllegalStateException();

		if (arithmetic_op_counter == null)
			throw new IllegalStateException();

		if (object_alloc_counter == null)
			throw new IllegalStateException();
	}

	public void setJDynAlloyModules(List<JDynAlloyModule> src_jdynalloy_modules) {
		this.src_jdynalloy_modules = src_jdynalloy_modules;

	}

	public void setArithmeticOpCounter(ArithmeticOpCounter arithmetic_op_counter) {
		this.arithmetic_op_counter = arithmetic_op_counter;
	}

	public void setObjectCreationCounter(ObjectCreationCounter object_alloc_counter) {
		this.object_alloc_counter = object_alloc_counter;
	}
}
