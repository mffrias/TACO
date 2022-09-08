package ar.edu.taco.infer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Scope {
	private Map<String, IntegerOrInfinity> scope = new HashMap<String, IntegerOrInfinity>();

	public void setInputScopeInteger(String type_str, int input_scope) {
		this.scope.put(type_str, new IntegerOrInfinity(input_scope));
	}

	public void setInputScopeInfinity(String type_str) {
		this.scope.put(type_str, IntegerOrInfinity.INFINITY);
	}

	public void incInputScopeInteger(String type_str, int i) {
		IntegerOrInfinity value = this.scope.get(type_str);
		if ((value == null) || (value.equals(IntegerOrInfinity.INFINITY)))
			throw new IllegalStateException();
	}

	public IntegerOrInfinity getInferredScopeOf(String signature_id) {
		if (scope.get(signature_id)!=null) {
			return scope.get(signature_id);
		} else {
			return new IntegerOrInfinity(0);
		}
	}

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