package ar.edu.taco.infer;

import java.util.List;
import java.util.Set;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.ast.JField;
import ar.edu.jdynalloy.ast.JProgramDeclaration;
import ar.edu.jdynalloy.ast.JVariableDeclaration;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.simplejml.builtin.JThrowable;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveCharValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveFloatValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveIntegerValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveLongValue;

abstract class ClassGraphBuilder {

	public static Graph buildExtensionTree(List<JDynAlloyModule> src_jdynalloy_modules) {
		Graph tree = new Graph();
		for (JDynAlloyModule jdynalloy_module : src_jdynalloy_modules) {

			if (isJavaPrimitiveIntegerLiteral(jdynalloy_module)) {
				continue;
			}


			if (isJavaPrimitiveCharLiteral(jdynalloy_module)) {
				continue;
			}

			if (isJavaPrimitiveLongLiteral(jdynalloy_module)) {
				continue;
			}

			if (isJavaPrimitiveFloatLiteral(jdynalloy_module)) {
				continue;
			}

			if (isThrowableOrDescendant(jdynalloy_module)) {
				continue;
			}

			String node_src;
			if (jdynalloy_module.getSignature().getExtSigId() != null) {
				node_src = jdynalloy_module.getSignature().getExtSigId();
			} else {
				node_src = "$Root$";
			}
			String extension_sig_id = jdynalloy_module.getSignature().getSignatureId();
			tree.addEge(node_src, extension_sig_id, null);
		}
		return tree;
	}

	public static Graph buildClassGraph(List<JDynAlloyModule> src_jdynalloy_modules) {
		Graph graph = new Graph();

		for (JDynAlloyModule jdynalloy_module : src_jdynalloy_modules) {
			
			if (!isSpecialType(jdynalloy_module)) {

				if (isJavaPrimitiveIntegerLiteral(jdynalloy_module)) {
					continue;
				}
	
				if (isJavaPrimitiveLongLiteral(jdynalloy_module)) {
					continue;
				}
	
				if (isJavaPrimitiveFloatLiteral(jdynalloy_module)) {
					continue;
				}
	
				if (isThrowableOrDescendant(jdynalloy_module)) {
					continue;
				}
	
				
				// Java fields
				String node_src = jdynalloy_module.getSignature().getSignatureId();
				if (node_src.equals("ClassFields")) {
					node_src = "$Root$";
				}
	
				for (JField field : jdynalloy_module.getFields()) {
	
					if (field.toString().contains("Map")){
						int i = 0;
					}
					JType field_type = field.getFieldType();
					if (field_type.isBinaryRelation()) {
						
						if (field_type.isBinRelWithSeq()) {   // the "if" body is supposed to be dead code since isBinRelWithSeq is prevented due to being considered "special".
							
							throw new IllegalStateException("seq univ inference is unsupported.");	
	
						} else {
	
							Set<String> target_signatures = field_type.to();
	
							for (String node_target : target_signatures) {
								if (node_target.equals("null")) {
									continue;
								}
								if (node_target.equals("boolean")) {
									continue;
								}
								String label_id = field.getFieldVariable().toString();
	
								graph.addEge(node_src, node_target, label_id);
							}
						}
					} else if (field_type.isTernaryRelation()) {
						// assume sequence
						String label_id = field.getFieldVariable().toString();
						JType object_array_type = JType.parse("(java_lang_ObjectArray)->((JavaPrimitiveIntegerValue) set -> lone (java_lang_Object+null))");
						if (field_type.equals(object_array_type)) {
							graph.addEge("java_lang_ObjectArray", "JavaPrimitiveIntegerValue", label_id);
							graph.addEge("JavaPrimitiveIntegerValue", "java_lang_Object", label_id);
						}
	
						JType int_array_type = JType.parse("(java_lang_IntArray)->((JavaPrimitiveIntegerValue) set -> lone (JavaPrimitiveIntegerValue))");
						if (field_type.equals(int_array_type)) {
							graph.addEge("java_lang_IntArray", "JavaPrimitiveIntegerValue", label_id);
							graph.addEge("JavaPrimitiveIntegerValue", "JavaPrimitiveIntegerValue", label_id);
						}
						
						JType char_array_type = JType.parse("(java_lang_CharArray)->((JavaPrimitiveIntegerValue) set -> lone (JavaPrimitiveCharValue))");
						if (field_type.equals(char_array_type)) {
							graph.addEge("java_lang_CharArray", "JavaPrimitiveIntegerValue", label_id);
							graph.addEge("JavaPrimitiveIntegerValue", "JavaPrimitiveCharValue", label_id);
						}
						
						JType long_array_type = JType.parse("(java_lang_LongArray)->((JavaPrimitiveIntegerValue) set -> lone (JavaPrimitiveLongValue))");
						if (field_type.equals(long_array_type)) {
							graph.addEge("java_lang_LongArray", "JavaPrimitiveIntegerValue", label_id);
							graph.addEge("JavaPrimitiveIntegerValue", "JavaPrimitiveLongValue", label_id);
						}
						
						JType map_entries_type = JType.parse("(java_util_Map)->((java_lang_Object) set -> lone (java_lang_Object+null))");
						if (field_type.equals(map_entries_type)) {
							graph.addEge("java_util_Map", "java_lang_Object", label_id);
						}
						

	
					}
				}
	
				// root types from arguments
				String class_to_check = TacoConfigurator.getInstance().getClassToCheck();
				String method_to_check = TacoConfigurator.getInstance().getMethodToCheck();
				if (jdynalloy_module.getSignature().getSignatureId().equals(class_to_check)) {
					for (JProgramDeclaration prog_declaration : jdynalloy_module.getPrograms()) {
						String program_id = prog_declaration.getProgramId();
						String qualified_program_id = class_to_check + "_" + program_id;
						if (method_to_check.startsWith(qualified_program_id)) {
	
							for (JVariableDeclaration var_decl : prog_declaration.getParameters()) {
								String parameter_id = var_decl.getVariable().toString();
								if (parameter_id.equals("throw")) {
									continue;
								}
								if (parameter_id.equals("return")) {
									continue;
								}
	
								for (String node_target : var_decl.getType().from()) {
									if (node_target.equals("null")) {
										continue;
									}
									if (node_target.equals("boolean")) {
										continue;
									}
									graph.addEge("$Root$", node_target, parameter_id);
								}
							}
	
						}
					}
				}
			} else {
				continue; //no inference for special types
			}
				
		}

		return graph;
	}

	
	//for the time being I'm only sure about considering certain special types as special.
	private static boolean isSpecialType(JDynAlloyModule jdynalloy_module) {
		String intArray = new String("java_lang_IntArray");
		String charArray = new String("java_lang_CharArray");
		String longArray = new String("java_lang_LongArray");
		String objectArray = new String("java_lang_ObjectArray");
		String sysList = new String("java_util_List");
		String JMLSeq = new String("org_jmlspecs_models_JMLObjectSequence");
		String JMLSet = new String("org_jmlspecs_models_JMLObjectSet");
		String sigId = jdynalloy_module.getSignature().getSignatureId();
		if (sigId != null && (sigId.equals(intArray) || sigId.equals(charArray) || sigId.equals(longArray) || sigId.equals(objectArray) || sigId.equals(sysList) || sigId.equals(JMLSeq) || sigId.equals(JMLSet)))
			return true;
		else
			return false;
					
	}
	
	private static boolean isJavaPrimitiveIntegerLiteral(JDynAlloyModule jdynalloy_module) {
		String java_primitive_integer_value_sig = JavaPrimitiveIntegerValue.getInstance().getModule().getSignature().getSignatureId();
		String extSigId = jdynalloy_module.getSignature().getExtSigId();
		if (extSigId != null && extSigId.equals(java_primitive_integer_value_sig))
			return true;
		else
			return false;
	}
	
	
	private static boolean isJavaPrimitiveCharLiteral(JDynAlloyModule jdynalloy_module) {
		String java_primitive_char_value_sig = "JavaPrimitiveCharValue";
		String extSigId = jdynalloy_module.getSignature().getExtSigId();
		if (extSigId != null && extSigId.equals(java_primitive_char_value_sig))
			return true;
		else
			return false;
	}


	private static boolean isJavaPrimitiveLongLiteral(JDynAlloyModule jdynalloy_module) {
		String java_primitive_long_value_sig = "JavaPrimitiveLongValue";
		String extSigId = jdynalloy_module.getSignature().getExtSigId();
		if (extSigId != null && extSigId.equals(java_primitive_long_value_sig))
			return true;
		else
			return false;
	}

	private static boolean isJavaPrimitiveFloatLiteral(JDynAlloyModule jdynalloy_module) {
		String java_primitive_float_value_sig = "JavaPrimitiveFloatValue";
		String extSigId = jdynalloy_module.getSignature().getExtSigId();
		if (extSigId != null && extSigId.equals(java_primitive_float_value_sig))
			return true;
		else
			return false;
	}

	private static boolean isThrowableOrDescendant(JDynAlloyModule jdynalloy_module) {
		if (jdynalloy_module.getLiteralSingleton() != null) {
			return true;
		}
		String throwable_sig_id = JThrowable.getInstance().getModule().getSignature().getSignatureId();
		if (jdynalloy_module.getSignature().getSignatureId().equals(throwable_sig_id)) {
			return true;
		}

		return false;
	}

}
