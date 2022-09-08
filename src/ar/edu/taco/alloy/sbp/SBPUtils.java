package ar.edu.taco.alloy.sbp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ar.edu.jdynalloy.ast.JField;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoCustomScope;
import ar.edu.taco.alloy.AlloyCustomScope;
import ar.edu.taco.alloy.AlloyScope;
import ar.edu.taco.infer.InferredScope;
import ar.uba.dc.rfm.alloy.ast.AlloyModule;
import ar.uba.dc.rfm.alloy.util.AlloyPrinter;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

/**
 * Utility class for dealing with SBP related operations.
 */
class SBPUtils {

	/**
	 * Returns a string of the form (elem1 + elem2 + ... + elemN).
	 */
	static String preetyPrintSet(Collection<String> set) {
		if (set.isEmpty()) {
			return "()";
		}
		StringBuilder ret = new StringBuilder("(");
		int size = set.size();
		int i = 0;
		for (String item : set) {
			if (i == size - 1 /* is the last one */) {
				ret.append(item + ")");
			} else {
				ret.append(item + " + ");
				++i;
			}
		}
		return ret.toString();
	}

	// Forwards

	// With QF
	static String buildFFieldName(JField recursiveField) {
		return buildFFieldName(recursiveField.getFieldVariable().toString());
	}

	static String buildFFieldName(String variable) {
		return "QF.f" + variable + "_0";
	}

	// No QF
	static String buildFFieldNameNoQF(JField recursiveField) {
		return buildFFieldNameNoQF(recursiveField.getFieldVariable().toString());
	}

	static String buildFFieldNameNoQF(String variable) {
		return "f" + variable;
	}

	// Backwards

	// With QF
	static String buildBFieldName(JField recursiveField) {
		return buildBFieldName(recursiveField.getFieldVariable().toString());
	}

	static String buildBFieldName(String variable) {
		return "QF.b" + variable + "_0";
	}

	// No QF
	static String buildBFieldNameNoQF(JField recursiveField) {
		return buildBFieldNameNoQF(recursiveField.getFieldVariable().toString());
	}

	static String buildBFieldNameNoQF(String variable) {
		return "b" + variable;
	}

	// 
	static String buildQFFieldName(JField field) {
		return "QF." + field.getFieldVariable().toString() + "_0";
	}

	/**
	 * Extracts the field names of the given fields as QF.fieldName_0
	 */
	static Collection<String> extractFieldNames(Collection<JField> fields) {
		return Collections2.transform(removeArrayContents(fields), new Function<JField, String>() {			@Override
			public String apply(JField field) {
				return "QF." + field.getFieldVariable().toString() + "_0";
			}
		});
	}

	private static Collection<JField> removeArrayContents(Collection<JField> fields) {
	// TODO Auto-generated method stub
		Collection<JField> c = new HashSet<JField>();
		for (JField f : fields){
			if (!f.getFieldType().equals(JType.parse("java_lang_IntArray->(Int set->lone Int)"))){
				c.add(f);
			}
		}
		return c;
	}

	
	static Collection<String> extractForwardFieldNames(Collection<JField> recursiveFields) {
		return Collections2.transform(recursiveFields, new Function<JField, String>() {
			@Override
			public String apply(JField field) {
				return SBPUtils.buildFFieldName(field);
			}
		});
	}

	/**
	 * Gets the domain of the given field if it is only one or throws
	 * a RuntimeException if otherwise.
	 */
	static String getOnlyFromOrThrowException(JField field) {
		Set<String> from = field.getFieldType().from();
		if (from.size() > 1) {
			// TODO: Add support for this.
			throw new RuntimeException("Multiple domains not yet supported: " + from);
		}
		if (field.getFieldType().equals(JType.parse("java_lang_IntArray->(Int set->lone Int)"))){
			return "java_lang_IntArray";
		}
		return from.iterator().next();
	}

	/**
	 * Gets the image of the given field if it is only one or throws
	 * a RuntimeException if otherwise.
	 */
	static String getOnlyToOrThrowException(JField field) {
		Set<String> to = Sets.newHashSet(field.getFieldType().to());
		to.remove("null");
		if (to.size() > 1) {
			// TODO: Add support for this.
			throw new RuntimeException("Multiple images not yet supported: " + to);
		} else if (to.size() == 0) {
			return "null";
		} else
			return to.iterator().next();
	}

	static void debugPrint(String fileName, AlloyModule alloyModule) {
		try {
			FileWriter fw = new FileWriter(new File(fileName));
			fw.write((String) alloyModule.accept(new AlloyPrinter()));
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static boolean isIntBuiltIn(String javaType) {
		return javaType.equals("Int");
	}

	/* javaType comes with underscores as separators */
	static int getScope(String javaType) {

		AlloyScope alloy_scope;
		if (TacoConfigurator.getInstance().getInferScope() == true) {
			String javaTypeDots = javaType.replace("_", ".");
			if (TacoConfigurator.getInstance().getTacoCustomScope().getCustomTypes().contains(javaTypeDots) == true){
				TacoCustomScope taco_custom_scope = TacoConfigurator.getInstance().getTacoCustomScope();
				AlloyCustomScope alloyScope = new AlloyCustomScope(taco_custom_scope);
				alloy_scope = new AlloyScope(alloyScope);
			} else {
				InferredScope inferred_scope = InferredScope.getInstance();
				alloy_scope = new AlloyScope(inferred_scope);
			}
		} else {
			TacoCustomScope taco_custom_scope = TacoConfigurator.getInstance().getTacoCustomScope();
			AlloyCustomScope alloyScope = new AlloyCustomScope(taco_custom_scope);
			alloy_scope = new AlloyScope(alloyScope);
		}
		
		return alloy_scope.getConcreteScopeOf(javaType);
	}

}
