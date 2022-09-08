package ar.edu.taco.infer;

import java.util.Set;

public class InferredScope {

	private final Scope inferred_scope;
	private final Scope inferred_concrete_input_scope;
	private final Scope bounded_concrete_input_scope;
	private final Scope inferred_concrete_program_scope;
	private final int inferred_alloy_bitwidth;

	private InferredScope(Scope inferred_scope, int inferred_alloy_bitwidth, Scope inferred_concrete_input_scope, Scope bounded_concrete_input_scope,
			Scope inferred_concrete_program_scope) {
		this.inferred_scope = inferred_scope;
		this.inferred_alloy_bitwidth = inferred_alloy_bitwidth;
		this.inferred_concrete_input_scope = inferred_concrete_input_scope;
		this.bounded_concrete_input_scope = bounded_concrete_input_scope;
		this.inferred_concrete_program_scope = inferred_concrete_program_scope;
	}

	private static InferredScope instance = null;

	public static InferredScope getInstance() {
		if (instance == null)
			throw new IllegalStateException();

		return instance;
	}

	public static void initialize_inferred_scope(Scope inferred_scope, int inferred_bitwidth, Scope inferred_concrete_input_scope,
			Scope bounded_concrete_input_scope, Scope inferred_concrete_program_scope) {
		instance = new InferredScope(inferred_scope, inferred_bitwidth, inferred_concrete_input_scope, bounded_concrete_input_scope,
				inferred_concrete_program_scope);
	}

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

	public IntegerOrInfinity getInferredInputScope(String signature_id) {
		IntegerOrInfinity inferred_scope = inferred_concrete_input_scope.getInferredScopeOf(signature_id);
		if (inferred_scope == null)
			return new IntegerOrInfinity(0);
		else
			return inferred_scope;

	}

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

	public int getInferredAlloyBitwidth() {
		return this.inferred_alloy_bitwidth;
	}

	public Set<String> inferred_signature_set() {
		return this.inferred_scope.signatureSet();
	}

	public int getInferredScope(String signature_id) {
		if (this.inferred_scope.getInferredScopeOf(signature_id) == null)
			throw new IllegalStateException();
		else
			return this.inferred_scope.getInferredScopeOf(signature_id).int_value;
	}
	
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
