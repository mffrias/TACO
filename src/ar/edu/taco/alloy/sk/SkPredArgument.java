package ar.edu.taco.alloy.sk;

/**
 * One skolem variable of a Java arithmetic operation, as a field of {@code QF} names it:
 * {@code <prefix>_SK_<predicate>_ARG_<argument>_<n>_<index>}. Here
 * {@code SK_<predicate>_ARG_<argument>_<n>} is the variable that
 * {@code ar.edu.taco.simplejml.builtin.AuxiliaryConstantsFactory} declared for its operation
 * number {@code n} ({@code <argument>} is {@code left}, {@code right}, {@code result},
 * {@code overflow} or {@code remainder}), {@code <prefix>} the prefix DynAlloy gives the local
 * variables of a call ({@code l0}, {@code l1}, ...) and {@code <index>} the DynAlloy index, e.g.
 * {@code l2_SK_pred_java_primitive_integer_value_add_ARG_left_0_1}. The code calls {@code n}
 * the index and the DynAlloy index the instant. {@link #toString()} rebuilds the name.
 * Immutable; equality is identity.
 */
class SkPredArgument {

	private SkPredArgument(String localVariablePrefix, String skolemizedPredicateId, String argumentName, int varIndex, int varInstant) {
		super();
		skolemized_local_variable_prefix = localVariablePrefix;
		skolemized_predicate_id = skolemizedPredicateId;
		argument_name = argumentName;
		var_index = varIndex;
		var_instant = varInstant;
	}

	private final String skolemized_local_variable_prefix;
	private final String skolemized_predicate_id;
	private final String argument_name;
	private final int var_index;
	private final int var_instant;

	public String toString() {
		return skolemized_local_variable_prefix + "_SK_" + skolemized_predicate_id + "_ARG_" + argument_name + "_" + var_index + "_" + var_instant;
	}

	/**
	 * Returns true iff the format is expected Format:
	 * <<STRING>>_SK_<<STRING>>_ARG_<<STRING>>_<<INTEGER>>_<<INTEGER>>
	 */
	public static boolean is_skolemized_predicate_argument_str(String str) {
		String[] first_split = str.split("_SK_");
		if (first_split.length != 2)
			return false;

		String[] second_split = first_split[1].split("_ARG_");
		if (second_split.length != 2)
			return false;

		String[] third_split = second_split[1].split("_");
		if (third_split.length != 3)
			return false;

		String index_str = third_split[1];
		try {
			Integer.valueOf(index_str);
		} catch (NumberFormatException ex) {
			return false;
		}

		String instant_str = third_split[2];
		try {
			Integer.valueOf(instant_str);
		} catch (NumberFormatException ex) {
			return false;
		}

		return true;
	}

	/**
	 * Parses a field name of the format of the class comment. A prefix that contains
	 * {@code _var_} is cut before it, so that {@link #toString()} then gives another name than
	 * the one parsed, and the fact built from it names no field of {@code QF}. Unclear: why the
	 * prefix is cut, and whether such prefixes occur (those seen are {@code l0}, {@code l2}, ...).
	 *
	 * @param skolemized_predicate_argument_str a field name of {@code QF}
	 * @return the parsed variable
	 * @throws IllegalArgumentException if the name does not have the format
	 *         ({@link #is_skolemized_predicate_argument_str(String)})
	 */
	public static SkPredArgument parse_skolemized_predicate_argument_str(String skolemized_predicate_argument_str) {
		/**
		 * expected Format: <<STRING>>_SK_<<STRING>>_ARG_<<STRING>>_<<INTEGER>>
		 */
		if (!is_skolemized_predicate_argument_str(skolemized_predicate_argument_str)) {
			throw new IllegalArgumentException("The string " + skolemized_predicate_argument_str
					+ " is not a valid parseable SkolemizedPredicateArgument string");
		}

		String local_variable_prefix = skolemized_predicate_argument_str.split("_SK_")[0];
		if (local_variable_prefix.contains("_var_")) {
			local_variable_prefix = local_variable_prefix.split("_var_")[0];
		}

		String skolemized_predicate_id = skolemized_predicate_argument_str.split("_SK_")[1].split("_ARG_")[0];
		String argument_name = skolemized_predicate_argument_str.split("_SK_")[1].split("_ARG_")[1].split("_")[0];

		String var_index_str = skolemized_predicate_argument_str.split("_SK_")[1].split("_ARG_")[1].split("_")[1];
		int var_index = Integer.valueOf(var_index_str);

		String var_instant_str = skolemized_predicate_argument_str.split("_SK_")[1].split("_ARG_")[1].split("_")[2];
		int var_instant = Integer.valueOf(var_instant_str);

		return new SkPredArgument(local_variable_prefix, skolemized_predicate_id, argument_name, var_index, var_instant);
	}

	public String get_skolemized_predicate_id() {
		return skolemized_predicate_id;
	}

	public String get_skolemized_local_variable_prefix() {
		return skolemized_local_variable_prefix;
	}

	public String get_argument_name() {
		return argument_name;
	}

	public int get_var_instant() {
		return var_instant;
	}

	public int get_var_index() {
		return var_index;
	}

}
