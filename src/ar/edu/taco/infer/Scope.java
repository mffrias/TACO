package ar.edu.taco.infer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A scope as the scope inference builds it: a number of atoms per Alloy signature id, each an
 * {@link IntegerOrInfinity}. A signature without an entry reads as 0. {@link ScopeInference}
 * builds the four scopes of {@link InferredScope} with it; {@code UBoundPlugin} also changes
 * the final one.
 */
public class Scope {
	private Map<String, IntegerOrInfinity> scope = new HashMap<String, IntegerOrInfinity>();

	public void setInputScopeInteger(String type_str, int input_scope) {
		this.scope.put(type_str, new IntegerOrInfinity(input_scope));
	}

	public void setInputScopeInfinity(String type_str) {
		this.scope.put(type_str, IntegerOrInfinity.INFINITY);
	}

	/**
	 * Does not change the scope, despite its name: it only checks that the signature has a finite
	 * entry, and ignores {@code i}. Nothing in TACO calls it.
	 *
	 * @param type_str a signature id
	 * @param i ignored
	 * @throws IllegalStateException if the signature has no entry or an infinite one
	 */
	public void incInputScopeInteger(String type_str, int i) {
		IntegerOrInfinity value = this.scope.get(type_str);
		if ((value == null) || (value.equals(IntegerOrInfinity.INFINITY)))
			throw new IllegalStateException();
	}

	/**
	 * Returns the entry of a signature.
	 *
	 * @param signature_id a signature id
	 * @return the entry, or a new 0 if the signature has none; never {@code null}
	 */
	public IntegerOrInfinity getInferredScopeOf(String signature_id) {
		if (scope.get(signature_id)!=null) {
			return scope.get(signature_id);
		} else {
			return new IntegerOrInfinity(0);
		}
	}

	/**
	 * Returns a new scope with the same entries, every infinite one replaced by
	 * {@code object_limit}. This scope is not changed.
	 *
	 * @param object_limit the number of atoms for an infinite entry ({@code objectScope})
	 * @return the bounded scope, with finite entries only
	 */
	public Scope bound(int object_limit) {
		Scope bounded_scope = new Scope();

		for (String signature_id : scope.keySet()) {
			if (scope.get(signature_id).equals(IntegerOrInfinity.INFINITY)) {
				bounded_scope.setInputScopeInteger(signature_id, object_limit);
			} else {
				int signature_scope = scope.get(signature_id).int_value;
				bounded_scope.setInputScopeInteger(signature_id, signature_scope);
			}
		}

		return bounded_scope;
	}
	
	public Set<String> signatureSet() {
		return this.scope.keySet();
	}
	
	public String toString() {
		return this.scope.toString();
	}
}