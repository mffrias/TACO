package ar.edu.taco.alloy;

import ar.edu.taco.infer.InferredScope;

public class AlloyScope {

	private final InferredScope inferred_scope;
	private final AlloyCustomScope custom_scope;

	public AlloyScope(InferredScope inferred_scope) {
		this.inferred_scope = inferred_scope;
		this.custom_scope = null;
	}

	public AlloyScope(AlloyCustomScope alloy_custom_scope) {
		this.inferred_scope = null;
		this.custom_scope = alloy_custom_scope;
	}

	public int getScopeOf(String signature_id) {
		if (this.custom_scope != null) {
			if (this.custom_scope.getCustomAlloyTypes().contains(signature_id)) {
				return this.custom_scope.getScopeForAlloySig(signature_id);
			} else
				return this.custom_scope.getTopLevelScope();

		} else if (this.inferred_scope != null) {
			return this.inferred_scope.getInferredScope(signature_id);
		} else
			throw new IllegalStateException();

	}

	public int getConcreteScopeOf(String signature_id) {
		if (this.custom_scope != null) {
			return getScopeOf(signature_id);
		} else if (this.inferred_scope != null) {
			int bounded_input_scope = this.inferred_scope.getBoundedInputScope(signature_id);
			int inferred_program_scope = this.inferred_scope.getInferredProgramScope(signature_id);

			int concrete_scope = bounded_input_scope + inferred_program_scope;
			return concrete_scope;
		} else
			throw new IllegalStateException();

	}

}
