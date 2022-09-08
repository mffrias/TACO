package ar.edu.taco.alloy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


import ar.edu.taco.TacoCustomScope;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveCharValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveFloatValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveIntegerValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveLongValue;
import ar.edu.taco.simplejml.helpers.JavaClassNameNormalizer;

public class AlloyCustomScope implements Serializable {


	private static final long serialVersionUID = -6306362330957130877L;

	private int custom_alloy_bitwidth;
	private int custom_alloy_max_sequence_length;
	private int custom_top_level_scope;
	private Map<String, Integer> custom_scopes = new HashMap<String, Integer>();

	public AlloyCustomScope(TacoCustomScope tacoScope) {

		// Alloy Bitwidth
		custom_alloy_bitwidth = tacoScope.getAlloyBitwidth();
		custom_alloy_max_sequence_length = tacoScope.getAlloyMaxSequenceLength();
		custom_top_level_scope = tacoScope.getTopLevelScope();
		for (String typename : tacoScope.getCustomTypes()) {

			String alloy_signature;

			if (typename.equals("int")) {
				alloy_signature = JavaPrimitiveIntegerValue.getInstance().getModule().getSignature().getSignatureId();	
			} else if (typename.equals("long")) {
				alloy_signature = JavaPrimitiveLongValue.getInstance().getModule().getSignature().getSignatureId();
			} else if (typename.equals("char")) {
				alloy_signature = JavaPrimitiveFloatValue.getInstance().getModule().getSignature().getSignatureId();
			} else if (typename.equals("float")) {
				alloy_signature = JavaPrimitiveCharValue.getInstance().getModule().getSignature().getSignatureId();
			} else {
				JavaClassNameNormalizer classNameNormalizer = new JavaClassNameNormalizer(typename);
				alloy_signature = classNameNormalizer.getQualifiedClassName();
			}
			int type_scope = tacoScope.getScopeForType(typename);
			custom_scopes.put(alloy_signature, type_scope);
		}
	}

	public int getTopLevelScope() {
		return custom_top_level_scope;
	}

	public int getAlloyBitwidth() {
		return custom_alloy_bitwidth;
	}

	public int getAlloyMaxSequenceLength() {
		return custom_alloy_max_sequence_length;
	}

	public Set<String> getCustomAlloyTypes() {
		return this.custom_scopes.keySet();
	}

	public int getScopeForAlloySig(String alloy_signature) {
		return this.custom_scopes.get(alloy_signature);
	}
}
