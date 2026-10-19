package ar.edu.taco.infer;

import java.util.Set;

/**
 * The result of the scope inference ({@link ScopeInference#inferScope()}): four {@link Scope}s
 * and the Alloy bitwidth, kept in a static singleton that the later steps of the DynAlloy-to-Alloy
 * translation read. What each scope holds is described in {@link ScopeInference}.
 * <ul>
 * <li>the final scope ({@link #getInferredScope(String)}): what the Alloy command gives each
 *     signature ({@code TacoConfigurator.getAssertionArguments()});</li>
 * <li>the input scope ({@link #getInferredInputScope(String)}), which may be infinite;</li>
 * <li>the bounded input scope and the program scope ({@link #getBoundedInputScope(String)},
 *     {@link #getInferredProgramScope(String)}); {@code AlloyScope}, which symmetry breaking
 *     ({@code SBPUtils}) and the tight upper bounds ({@code UBoundPlugin}) use, reads their sum
 *     as well as the final scope;</li>
 * <li>the bitwidth ({@link #getInferredAlloyBitwidth()}), used when {@code int.bitwidth} is 0
 *     or less ({@code TacoConfigurator}, {@code CardinalSizeOfPlugin}).</li>
 * </ul>
 *
 * <p><b>Static state.</b> The singleton is set by {@link #initialize_inferred_scope}, which
 * {@code ScopeInference.inferScope()} calls and {@code UBoundPlugin} calls again, and is never
 * cleared: it survives between runs in one JVM, so after a run without {@code inferScope} it
 * still holds the scopes of the last run that had it. Its readers in TACO test {@code inferScope}
 * first; drivers read it after a run to check the inferred scopes. The scopes are shared, not
 * copied: {@code UBoundPlugin} changes the final scope in place.
 */
public class InferredScope {

	private Scope inferred_scope;
	private Scope inferred_concrete_input_scope;
	private Scope bounded_concrete_input_scope;
	private Scope inferred_concrete_program_scope;
	private int inferred_alloy_bitwidth;

	private InferredScope(Scope inferred_scope, int inferred_alloy_bitwidth, Scope inferred_concrete_input_scope, Scope bounded_concrete_input_scope,
			Scope inferred_concrete_program_scope) {
		this.inferred_scope = inferred_scope;
		this.inferred_alloy_bitwidth = inferred_alloy_bitwidth;
		this.inferred_concrete_input_scope = inferred_concrete_input_scope;
		this.bounded_concrete_input_scope = bounded_concrete_input_scope;
		this.inferred_concrete_program_scope = inferred_concrete_program_scope;
	}

	private static InferredScope instance = null;

	/**
	 * Creates an instance with no scopes, whose scope getters throw {@code NullPointerException}.
	 * Nothing in TACO calls it; the instances in use come from {@link #initialize_inferred_scope}.
	 */
	public InferredScope() {

	}

	/**
	 * Returns the scopes set last in this JVM by {@link #initialize_inferred_scope}, by this run or
	 * by an earlier one.
	 *
	 * @return the singleton
	 * @throws IllegalStateException if no scope has been set yet in this JVM
	 */
	public static InferredScope getInstance() {
		if (instance == null)
			throw new IllegalStateException();

		return instance;
	}

	/**
	 * Replaces the singleton with a new instance holding the given scopes (not copies).
	 *
	 * @param inferred_scope the final scope
	 * @param inferred_bitwidth the inferred Alloy bitwidth
	 * @param inferred_concrete_input_scope the input scope, possibly with infinite entries
	 * @param bounded_concrete_input_scope the input scope with infinity replaced by
	 *        {@code objectScope}
	 * @param inferred_concrete_program_scope the program scope
	 */
	public static void initialize_inferred_scope(Scope inferred_scope, int inferred_bitwidth, Scope inferred_concrete_input_scope,
			Scope bounded_concrete_input_scope, Scope inferred_concrete_program_scope) {
		instance = new InferredScope(inferred_scope, inferred_bitwidth, inferred_concrete_input_scope, bounded_concrete_input_scope,
				inferred_concrete_program_scope);
	}

	/**
	 * Returns the program scope of a signature: the atoms the method under analysis needs besides
	 * its input (allocations, literals, results of arithmetic operations).
	 *
	 * @param signature_id an Alloy signature id, e.g. {@code JavaPrimitiveIntegerValue}
	 * @return the scope, 0 for a signature without one
	 * @throws IllegalStateException if the scope is infinite (the inference never makes it so)
	 */
	public int getInferredProgramScope(String signature_id) {
		IntegerOrInfinity inferred_scope = inferred_concrete_program_scope.getInferredScopeOf(signature_id);
		if (inferred_scope == null)
			return 0;
		else if (inferred_scope.equals(IntegerOrInfinity.INFINITY))
			throw new IllegalStateException();
		else {
			return inferred_scope.int_value;
		}
	}

	/**
	 * Returns the input scope of a signature, before infinity is bounded.
	 *
	 * @param signature_id an Alloy signature id
	 * @return the scope, possibly {@link IntegerOrInfinity#INFINITY}; 0 for a signature without one
	 */
	public IntegerOrInfinity getInferredInputScope(String signature_id) {
		IntegerOrInfinity inferred_scope = inferred_concrete_input_scope.getInferredScopeOf(signature_id);
		if (inferred_scope == null)
			return new IntegerOrInfinity(0);
		else
			return inferred_scope;

	}

	/**
	 * Returns the bounded input scope of a signature: the input scope with infinity replaced by
	 * {@code objectScope}, and at least 1 for {@code classToCheck} if {@link ScopeInference}
	 * finds that the method is an instance method.
	 *
	 * @param signature_id an Alloy signature id
	 * @return the scope, 0 for a signature without one
	 * @throws IllegalStateException if the scope is infinite (bounding prevents it)
	 */
	public int getBoundedInputScope(String signature_id) {
		IntegerOrInfinity bounded_scope = bounded_concrete_input_scope.getInferredScopeOf(signature_id);
		if (bounded_scope == null)
			return 0;
		else if (bounded_scope.equals(IntegerOrInfinity.INFINITY))
			throw new IllegalStateException();
		else {
			return bounded_scope.int_value;
		}
	}

	/**
	 * Returns the inferred Alloy bitwidth: {@link ScopeInference#DEFAULT_ALLOY_BITWDITH} without
	 * {@code useJavaArithmetic}, otherwise computed from the scope of {@code java_lang_Object}
	 * (see {@link ScopeInference}). Its readers use it only when {@code int.bitwidth} is 0 or less.
	 *
	 * @return the bitwidth
	 */
	public int getInferredAlloyBitwidth() {
		return this.inferred_alloy_bitwidth;
	}

	/**
	 * Returns the signatures of the final scope: the live key set, {@code "$Root$"} included
	 * ({@code TacoConfigurator} skips it, as well as {@code java_lang_Throwable} and
	 * {@code ClassFields}).
	 *
	 * @return the signature ids
	 */
	public Set<String> inferred_signature_set() {
		return this.inferred_scope.signatureSet();
	}

	/**
	 * Returns the final scope of a signature, the one the Alloy command gives it.
	 *
	 * @param signature_id an Alloy signature id
	 * @return the scope, 0 for a signature without one ({@link Scope#getInferredScopeOf} never
	 *         returns {@code null}, so the {@code IllegalStateException} here is never thrown)
	 */
	public int getInferredScope(String signature_id) {
		if (this.inferred_scope.getInferredScopeOf(signature_id) == null)
			throw new IllegalStateException();
		else
			return this.inferred_scope.getInferredScopeOf(signature_id).int_value;
	}
	
	/**
	 * Returns the final scope itself, not a copy; {@code UBoundPlugin} changes it in place.
	 *
	 * @return the final scope
	 */
	public Scope getInferredScope(){
		return inferred_scope;
	}
	
	public Scope getInferredConcreteInputScope(){
		return inferred_concrete_input_scope;
	}
	
	public Scope getBoundedConcreteInputScope(){
		return bounded_concrete_input_scope;
	}
	
	public Scope getInferredConcreteProgramScope(){
		return inferred_concrete_program_scope;
	}
}
