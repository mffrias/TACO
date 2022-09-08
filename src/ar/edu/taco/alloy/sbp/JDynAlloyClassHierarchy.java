package ar.edu.taco.alloy.sbp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.ast.JField;
import ar.edu.jdynalloy.ast.JProgramDeclaration;
import ar.edu.jdynalloy.ast.JRepresents;
import ar.edu.jdynalloy.ast.JVariableDeclaration;
import ar.edu.jdynalloy.factory.JSignatureFactory;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveCharValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveFloatValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveIntegerValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveLongValue;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class JDynAlloyClassHierarchy {

	private List<JField> recursiveFields = Lists.newArrayList();
	private List<JField> nonRecursiveFields = Lists.newArrayList();

	private RootNodesHelper rootNodesHelper = new RootNodesHelper();
	private JavaTypesHelper javaTypesHelper = new JavaTypesHelper();

	private Set<String> specFields = Sets.newHashSet();

	private boolean matchedWithQF = false;

	private JDynAlloyClassHierarchy(List<JDynAlloyModule> src_jdynalloy_modules) {
		for (JDynAlloyModule jDynAlloyModule : src_jdynalloy_modules) {
			for (JRepresents represents : jDynAlloyModule.getRepresents()) {
				String rep[] = represents.getExpression().toString().split("\\.");
				if (rep.length < 2) {
					throw new IllegalArgumentException(
							"The spec expression is expected to be of the form (x).(y)");
				}
				specFields.add(rep[1].substring(1, rep[1].length() - 1));
			}
			rootNodesHelper.processModule(jDynAlloyModule);
			javaTypesHelper.processModule(jDynAlloyModule);
			for (JField field : jDynAlloyModule.getFields()) {
				if (!isJMLField(field) && !isArrayField(field) &&  
						!isSpecField(field) && !isStaticField(field)) {
					if (isRecursiveField(field)) {
						recursiveFields.add(field);
					} else {
						nonRecursiveFields.add(field);
					}
				}
			}
		}
	}

	RootNodesHelper rootNodes() {
		Preconditions.checkArgument(matchedWithQF,
				"Must call mathWithQF before accesing this object");
		return rootNodesHelper;
	}

	JavaTypesHelper javaTypes() {
		Preconditions.checkArgument(matchedWithQF,
				"Must call mathWithQF before accesing this object");
		return javaTypesHelper;
	}

	List<JField> getRecursiveFields() {
		Preconditions.checkArgument(matchedWithQF,
				"Must call mathWithQF before accesing this object");
		return recursiveFields;
	}

	List<JField> getNonRecursiveObjectFields() {
		Preconditions.checkArgument(matchedWithQF,
				"Must call mathWithQF before accesing this object");
		List<JField> nonRecursiveObjectFields = new ArrayList<JField>();
		for (JField f : nonRecursiveFields){
			if (
					(!f.getFieldType().equals(JType.parse("JavaPrimitiveIntegerValue"))) &&
					(!f.getFieldType().equals(JType.parse("JavaPrimitiveLongValue"))) &&
					(!f.getFieldType().equals(JType.parse("JavaPrimitiveCharValue"))) &&
					(!f.getFieldType().equals(JType.parse("JavaPrimitiveFloatValue"))) &&
					(!f.getFieldType().equals(JType.parse("boolean")))
					) {
				nonRecursiveObjectFields.add(f);
			}
		}
		return nonRecursiveObjectFields;
	}

	/**
	 * Used for matching the jDynAlloyModule fields with the actual fields defined in QF so as
	 * to consider only those fields in the intersection of both sets. Should be called
	 * before accessing any information from this object.
	 */
	void matchWithQF(AlloyTyping qf) {
		Preconditions.checkNotNull(qf, "Qf can't be null.");
		Set<String> qfFieldNames = Sets.newHashSet();
		for (AlloyVariable var : qf) {
			qfFieldNames.add(var.getVariableId().getString());
		}
		filterOutNonQFFields(qfFieldNames, recursiveFields);
		filterOutNonQFFields(qfFieldNames, nonRecursiveFields);
		javaTypesHelper.matchWithQF(qfFieldNames);
		rootNodesHelper.matchWithJavaTypes(javaTypesHelper.javaTypes);
		rootNodesHelper.matchWithQF(qfFieldNames);
		matchedWithQF = true;
	}

	private void filterOutNonQFFields(Set<String> qfFieldNames, List<JField> fieldsToFilter) {
		Iterator<JField> it = fieldsToFilter.iterator();
		while (it.hasNext()) {
			JField field = it.next();
			if (!qfFieldNames.contains(
					field.getFieldVariable().getVariableId().getString())) {
				// We have to filter it out.
				it.remove();
			}
		}
	}

	/**
	 * Gets all non-recursive and forward fields of type T' -> T where T
	 * is the given type classified by T'. The map will have entries of
	 * kind <T', {f1, ..., fn}> where fi: T' -> T.
	 */
	Map<String, Collection<JField>> getNonRecursiveAndForwardFieldsOfImageT(final String t) {
		Collection<JField> nonRecursiveFieldsOfImageT = Collections2.filter(
				nonRecursiveFields, new Predicate<JField>() {
					@Override
					public boolean apply(JField field) {
						return !field.getFieldVariable().getVariableId().getString().startsWith("SK_jml_pred_java_primitive") &&
								field.getFieldType().to().contains(t);
					}
				});
		Collection<JField> nonRecursiveQFFieldsOfImageT = Collections2.transform(
				nonRecursiveFieldsOfImageT, new Function<JField, JField>() {
					@Override
					public JField apply(JField arg0) {
						return new JField(new AlloyVariable(SBPUtils.buildQFFieldName(arg0)),
								arg0.getFieldType());
					}
				});
		Collection<JField> recursiveFieldsOfImageT = Collections2.filter(
				recursiveFields, new Predicate<JField>() {
					@Override
					public boolean apply(JField field) {
						return !field.getFieldVariable().getVariableId().getString().startsWith("SK_jml_pred_java_primitive") &&
								field.getFieldType().to().contains(t);
					}
				});
		Collection<JField> fRecursiveFieldsOfImageT = Collections2.transform(
				recursiveFieldsOfImageT, new Function<JField, JField>() {
					@Override
					public JField apply(JField field) {
						return new JField(new AlloyVariable("(" + SBPUtils.buildFFieldName(field) + ")"),
								field.getFieldType());
					}
				});
		List<JField> ret = Lists.newArrayList();
		ret.addAll(nonRecursiveQFFieldsOfImageT);
		ret.addAll(fRecursiveFieldsOfImageT);
		return classifyByFrom(ret);
	}

	private Map<String, Collection<JField>> classifyByFrom(Collection<JField> fields) {
		Map<String, Collection<JField>> ret = Maps.newHashMap();
		for (JField field : fields) {
			Collection<JField> newCollection = Lists.newArrayList();
			String from = SBPUtils.getOnlyFromOrThrowException(field);
			if (ret.containsKey(from)) {
				newCollection.addAll(ret.get(from));
			}
			newCollection.add(field);
			ret.put(from, newCollection);
		}
		return ret;
	}

	static JDynAlloyClassHierarchy buildFromModules(List<JDynAlloyModule> src_jdynalloy_modules) {
		return new JDynAlloyClassHierarchy(src_jdynalloy_modules);
	}

	private boolean isSpecField(JField field) {
		return specFields.contains(field.getFieldVariable().toString());
	}

	/**
	 * Checks whether the given field is recursive or not.
	 * A field is considered recursive if domain and codomain (minus the null
	 * value) match. For example, field next: LNode -> LNode+null is considered
	 * a recursive field.
	 */
	private boolean isRecursiveField(JField field) {
		JType fieldType = field.getFieldType();
		if (!fieldType.isBinaryRelation()) {
			return false;
		} else {
			if (fieldType.equals(JType.parse("java_lang_IntArray->(Int set->lone Int)"))){
				return false;
			}
			if (fieldType.from().equals(fieldType.to())) {
				return true;
			}
		}
		Set<String> fromWithNull = Sets.newHashSet(fieldType.from());
		fromWithNull.add("null");
		if (!fieldType.isBinaryRelation()){
			return false;
		} else {
			return fromWithNull.equals(fieldType.to());
		} 
	}

	/**
	 * Checks whether the given field is a static field or not.
	 */
	private boolean isStaticField(JField field) {
		return SBPUtils.getOnlyFromOrThrowException(field).equals("ClassFields");
	}



	/**
	 * Checks whether the given field comes from a JML type.
	 */
	private boolean isJMLField(JField field) {
		return field.getFieldType().isJML();
	}



	/**
	 * Checks whether the given field is the java_util_List field or not.
	 */
	private boolean isJavaUtilListField(JField field) {
		return field.getFieldType().isSpecialType() && field.getFieldType().getSpecialType().equals(JType.SpecialType.ALLOY_LIST_CONTAINS);
	}


	/**
	 * Checks whether the given field is an array field or not.
	 */
	private boolean isArrayField(JField field) {
		return field.getFieldType().isTernaryRelation() || field.getFieldType().isBinRelWithSeq() || 
				field.getFieldType().from().contains("java_lang_IntArray") ||
				field.getFieldType().from().contains("java_lang_CharArray") ||
				field.getFieldType().from().contains("java_lang_ObjectArray") ||
				field.getFieldType().from().contains("java_lang_LongArray");
	}

	/**
	 * Helper class that provides method for dealing with java types (T type).
	 */
	class JavaTypesHelper {

		private List<String> javaTypes = Lists.newArrayList();
		private List<JField> fields = Lists.newArrayList();

		private void processModule(JDynAlloyModule module) {
			for (JField field : module.getFields()) {
				if (!isArrayField(field) && !isSpecField(field)) {
					fields.add(field);
				}
			}
		}

		/**
		 * Used for matching the jDynAlloyModule fields with the actual fields defined in QF so as
		 * to consider only those fields in the intersection of both sets. Should be called
		 * before accessing any information from this object.
		 */
		private void matchWithQF(Set<String> qfFieldNames) {
			filterOutNonQFFields(qfFieldNames, fields);
			for (JField field : fields) {

				if (!field.getFieldVariable().getVariableId().getString().startsWith("SK_jml_pred_java_primitive")){
					for (String type : field.getFieldType().from()) {
						if (!javaTypes.contains(type))
							javaTypes.add(type);
					}

					for (String type : field.getFieldType().to()) {
						if (!javaTypes.contains(type))
							javaTypes.add(type);
					}
				}

			}
			// Filter out not applicable types.
			javaTypes.remove("null");
			javaTypes.remove("ClassFields");
			// The Int built-in should not be considered.
			javaTypes.remove(JSignatureFactory.INT.getSignatureId());
			javaTypes.remove(JavaPrimitiveIntegerValue
					.getInstance().getModule().getSignature().getSignatureId());
			javaTypes.remove(JavaPrimitiveFloatValue
					.getInstance().getModule().getSignature().getSignatureId());
			javaTypes.remove(JavaPrimitiveLongValue
					.getInstance().getModule().getSignature().getSignatureId());
			javaTypes.remove(JavaPrimitiveCharValue
					.getInstance().getModule().getSignature().getSignatureId());
			javaTypes.remove(JSignatureFactory.BOOLEAN.getSignatureId());
		}

		/**
		 * @return All Java Types (a.k.a all T)
		 */
		List<String> all() {
			return javaTypes;
		}

		/**
		 * @return The list of non-recursive fields / f: T -> javaType given
		 */
		Collection<JField> nonRecursiveFieldsTargeting(final String javaType) {
			return Collections2.filter(nonRecursiveFields, new Predicate<JField>() {
				@Override
				public boolean apply(JField field) {
					return !field.getFieldVariable().getVariableId().getString().startsWith("SK_jml_pred_java_primitive") &&
							SBPUtils.getOnlyToOrThrowException(field).equals(javaType);
				}
			});
		}

		/**
		 * @return The list of recursive fields / f: T -> javaType given
		 */
		Collection<JField> recursiveFieldsTargeting(final String javaType) {
			return Collections2.filter(recursiveFields, new Predicate<JField>() {
				@Override
				public boolean apply(JField field) {
					return SBPUtils.getOnlyToOrThrowException(field).equals(javaType);
				}
			});
		}

		/**
		 * @return The list of java types / exists f: T -> javaType given. Ordered by T.
		 */
		Set<String> javaTypesWithFieldsTargeting(final String javaType) {
			final List<String> javaTypesAsList = Lists.newArrayList(javaTypes);
			SortedSet<String> ret = Sets.newTreeSet(new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return javaTypesAsList.indexOf(o1) - javaTypesAsList.indexOf(o2);
				}
			});
			Collection<JField> fieldsTargetingJavaType = 
					Collections2.filter(fields, new Predicate<JField>() {
						@Override
						public boolean apply(JField field) {
							if (field.getFieldVariable().getVariableId().getString().startsWith("SK_jml_pred_java_primitive"))
								return false;
							else 
								return SBPUtils.getOnlyToOrThrowException(field).equals(javaType) &&
									javaTypes.contains(SBPUtils.getOnlyFromOrThrowException(field));
						}
					});
			for (JField jField : fieldsTargetingJavaType) {
				ret.add(SBPUtils.getOnlyFromOrThrowException(jField));
			}
			return ret;
		}

		/**
		 * @return True if there exists a field / f: fromJavaType -> toJavaType
		 */
		boolean existsField(final String fromJavaType, final String toJavaType) {
			for (JField field : fields) {
				if (!field.getFieldVariable().getVariableId().getString().startsWith("SK_jml_pred_java_primitive") && 
						SBPUtils.getOnlyFromOrThrowException(field).equals(fromJavaType) &&
						SBPUtils.getOnlyToOrThrowException(field).equals(toJavaType)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Helper class that provides methods for dealing with root nodes.
	 */
	class RootNodesHelper {

		private Map<String, List<JField>> rootNodesOfJavaType = Maps.newHashMap();

		private List<JField> rootNodes = Lists.newArrayList();

		private void processModule(JDynAlloyModule module) {
			for (JProgramDeclaration program : module.getPrograms()) {
				String sigId = program.getSignatureId();
				if (!(sigId.equals(TacoConfigurator.getInstance().getClassToCheck()) && 
						(sigId + "_" + program.getProgramId() + "_0").equals(
								TacoConfigurator.getInstance().getMethodToCheck()))) {
					continue;
				}
				for (JVariableDeclaration var : program.getParameters()) {
					String variable = var.getVariable().toString();
					// The return and throws fields are not root nodes.
					if (variable.equals("return") || variable.equals("throw")) {
						continue;
					}
					String rootNodeType = var.getType().toString();
					List<JField> fields = Lists.newArrayList();
					if (rootNodesOfJavaType.containsKey(rootNodeType)) {
						fields.addAll(rootNodesOfJavaType.get(rootNodeType));
					}
					JField rootField = new JField(var.getVariable(), var.getType()); 
					fields.add(rootField);
					rootNodesOfJavaType.put(rootNodeType, fields);
					rootNodes.add(rootField);
				}
			}
		}

		/**
		 * Used for matching the root nodes so as to only keep those of
		 * type javaType where applicable (the all() method will still
		 * return all). Should be called before accessing
		 * any method from this object.
		 */
		private void matchWithJavaTypes(List<String> javaTypes) {
			Set<String> toRemoveTypes = Sets.newTreeSet();
			for (String typeOfRootNode : rootNodesOfJavaType.keySet()) {
				if (!javaTypes.contains(typeOfRootNode)) {
					toRemoveTypes.add(typeOfRootNode);
				}
			}
			for (String toRemoveType : toRemoveTypes) {
				rootNodesOfJavaType.remove(toRemoveType);
			}
		}

		/**
		 * Used for matching the root nodes with the QF so as to consider 
		 * only those fields in the intersection of both sets. Should be called
		 * before accessing any information from this object.
		 */
		private void matchWithQF(Set<String> qfFieldNames) {
			filterOutNonQFFields(qfFieldNames, rootNodes);
		}

		/**
		 * @return All root nodes of type t. T must be a java type.
		 */
		List<JField> ofTypeT(String t) {
			List<JField> ret = rootNodesOfJavaType.get(t);
			if (ret == null) {
				ret = Lists.newArrayList();
			}
			return ret;
		}

		/**
		 * @return A map (t, root nodes of type t). T is a java type.
		 */
		Map<String, List<JField>> ofType() {
			return rootNodesOfJavaType;
		}

		/**
		 * @return All root nodes. Including those of type
		 * different than a java type.
		 */
		List<JField> all() {
			return rootNodes;
		}

	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("type ordering:\n");
		buffer.append("==============");
		for (int i = 0; i< javaTypes().all().size(); i++) {
			buffer.append("\n");
			buffer.append((i+1) + ") " + javaTypes().all().get(i));
		}

		buffer.append("\n\n");
		buffer.append("root nodes ordering:\n");
		buffer.append("====================");
		for (int i = 0; i< rootNodes().all().size(); i++) {
			buffer.append("\n");
			buffer.append((i+1) + ") " + rootNodes().all().get(i));
		}

		buffer.append("\n\n");
		buffer.append("recursive field ordering:\n");
		buffer.append("=========================");
		for (int i = 0; i< this.recursiveFields.size(); i++) {
			buffer.append("\n");
			buffer.append((i+1) + ") " + this.recursiveFields.get(i));
		}


		buffer.append("\n\n");
		buffer.append("non-recursive field ordering:\n");
		buffer.append("=============================");
		for (int i = 0; i< this.nonRecursiveFields.size(); i++) {
			buffer.append("\n");
			buffer.append((i+1) + ") " + this.nonRecursiveFields.get(i));
		}

		return buffer.toString();

	}
}
