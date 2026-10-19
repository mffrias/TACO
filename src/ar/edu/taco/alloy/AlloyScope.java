package ar.edu.taco.alloy;

import ar.edu.taco.infer.InferredScope;

/**
 * The scope of each signature as symmetry breaking ({@code sbp.SBPUtils.getScope}) and the
 * tight upper bounds ({@code bound.UBoundPlugin}, {@code bound.UBoundRepository}) read it:
 * either the configuration's ({@link AlloyCustomScope}) or the scope inference's
 * ({@link InferredScope}), whichever it was created with. It reads the instance given to its
 * constructor, not the {@code InferredScope} singleton. No static state.
 */
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

	/**
	 * Returns the scope of a signature. With the configuration's scope: its {@code type_scopes}
	 * value, or {@code objectScope} if it has none. With an inferred scope: the final scope
	 * ({@link InferredScope#getInferredScope(String)}), 0 for a signature without one.
	 *
	 * @param signature_id an Alloy signature id, e.g. {@code pkg_Node}
	 * @return the scope
	 */
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

	/**
	 * Returns the number of atoms of a signature that symmetry breaking names
	 * ({@code SBPUtils.getScope}). With the configuration's scope, the same as
	 * {@link #getScopeOf(String)}. With an inferred scope, the bounded input scope plus the
	 * program scope ({@link InferredScope#getBoundedInputScope(String)},
	 * {@link InferredScope#getInferredProgramScope(String)}): the scope before the inference
	 * takes the larger of it and the {@code type_scopes} value and adds the subsignatures' atoms,
	 * so it can be less than the final scope.
	 *
	 * @param signature_id an Alloy signature id
	 * @return the number of atoms
	 */
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
