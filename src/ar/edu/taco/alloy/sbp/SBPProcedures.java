package ar.edu.taco.alloy.sbp;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import ar.edu.jdynalloy.ast.JField;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

class SBPProcedures {

	/**
	 * Keeps track of the Alloy functions defined.
	 * It is used for verifying whether a Alloy fun was defined
	 * before and hence determine whether it could be used or not.
	 */
	private class SBPNames {

		private HashSet<String> names = Sets.newHashSet();

		public void addDefinition(String definition) {
			names.add(definition);
		}

		public boolean hasBeenDefined(String name) {
			return names.contains(name);
		}
	}

	private JDynAlloyClassHierarchy sbpInfo;

	private SBPNames sbpNames;

	SBPProcedures(JDynAlloyClassHierarchy sbpInfo) {
		this.sbpInfo = sbpInfo;
		this.sbpNames = new SBPNames();
	}

	void executeAll(StringBuilder alloyStrModified) {
		localOrderingProcedure(alloyStrModified);
		globalOrderingProcedure(alloyStrModified);
		defineMinParentProcedure(alloyStrModified);
		defineFReachProcedure(alloyStrModified);
		orderRootNodesProcedure(alloyStrModified);
		rootIsMinimumProcedure(alloyStrModified);
		orderSameMinParentProcedure(alloyStrModified);
		orderSameMinParentTypeProcedure(alloyStrModified);
		orderDiffMinParentsTypeProcedure(alloyStrModified);
		avoidHolesProcedure(alloyStrModified);
	}

	/**
	 * Implements the local_ordering() procedure.
	 */
	private void localOrderingProcedure(StringBuilder alloyStrModified) {
		alloyStrModified.append("//-----SMB: local_ordering()-----//\n");
		for (String tType : sbpInfo.javaTypes().all()) {
			int k = SBPUtils.getScope(tType);

			// next
			alloyStrModified.append(String.format("fun next_%s [] : %s -> lone %s { \n", tType, tType, tType));
			sbpNames.addDefinition(String.format("next_%s", tType));
			if (k > 1) {
				int i = 0;
				for (i = 0; i < k - 2; ++i) {
					alloyStrModified.append(String.format("  %s_%d -> %s_%d +\n", tType, i, tType, i + 1));
				}
				alloyStrModified.append(String.format("  %s_%d -> %s_%d \n}\n", tType, i, tType, i + 1));
			} else {
				alloyStrModified.append("none -> none \n}\n");
			}
			// min
			alloyStrModified.append(String.format("fun min_%s [os: set %s] : lone %s {\n", tType, tType, tType));
			sbpNames.addDefinition(String.format("min_%s", tType));
			alloyStrModified.append(String.format("  os - os.^(next_%s[]) \n}\n", tType));
			// prevs
			alloyStrModified.append(String.format("fun prevs_%s[o : %s] : set %s {\n", tType, tType, tType));
			sbpNames.addDefinition(String.format("prevs_%s", tType));
			alloyStrModified.append(String.format("  o.^(~next_%s[]) \n}\n", tType));
		}
	}

	/**
	 * Implements the global_ordering() procedure.
	 */
	private void globalOrderingProcedure(StringBuilder alloyStrModified) {
		alloyStrModified.append("//-----SMB: global_ordering()-----//\n");
		alloyStrModified.append("fun globalNext[]: java_lang_Object -> lone java_lang_Object {\n");

		List<String> javaTypes = Lists.newArrayList(sbpInfo.javaTypes().all());
		int tSize = javaTypes.size();

		StringBuffer globalNext = new StringBuffer();

		if (javaTypes.isEmpty() || (tSize == 1 && SBPUtils.getScope(javaTypes.get(0)) <= 1)) {
			globalNext.append("none -> none");
		} else {
			for (int tCount = 0; tCount < tSize; ++tCount) {
				String tType = javaTypes.get(tCount);
				int k = SBPUtils.getScope(tType);
				int i = 0;

				for (; i < k - 1; ++i) {
					if (globalNext.length()!=0) {
						globalNext.append("  +  ");
					}
					globalNext.append(String.format("%s_%d -> %s_%d", tType, i, tType, i + 1));

				}
				if (tCount < tSize - 1) {
					// T_{i + 1} exists.
					String nextTType = javaTypes.get(tCount + 1);
					if (globalNext.length()!=0) {
						globalNext.append("  +  ");
					}
					globalNext.append(String.format("%s_%d -> %s_0", tType, i, nextTType));
				}
			}
		}
		alloyStrModified.append(globalNext.toString());
		alloyStrModified.append("\n}\n");
		alloyStrModified.append("fun globalMin[s : set java_lang_Object] : lone java_lang_Object {\n");
		alloyStrModified.append("s - s.^globalNext[] \n}\n");
	}

	/**
	 * Implements the define_min_parent() procedure.
	 */
	private void defineMinParentProcedure(StringBuilder alloyStrModified) {
		alloyStrModified.append("//-----SMB: define_min_parent()-----//\n");
		for (String tType : sbpInfo.javaTypes().all()) {
			Collection<JField> nonRecursiveFieldsTargeting = sbpInfo.javaTypes().nonRecursiveFieldsTargeting(tType);
			Collection<JField> recursiveFieldsTargeting = sbpInfo.javaTypes().recursiveFieldsTargeting(tType);
			if (nonRecursiveFieldsTargeting.isEmpty() && recursiveFieldsTargeting.isEmpty()) {
				continue;
			}
			alloyStrModified.append(String.format("fun minP_%s [o : %s] : java_lang_Object {\n", tType, tType));
			sbpNames.addDefinition(String.format("minP_%s", tType));
			List<JField> rootNodesOfTypeT = sbpInfo.rootNodes().ofTypeT(tType);
			boolean existRootNodesOfTypeT = !rootNodesOfTypeT.isEmpty();
			if (existRootNodesOfTypeT) {
				alloyStrModified.append("  o !in ").append(SBPUtils.preetyPrintSet(SBPUtils.extractFieldNames(rootNodesOfTypeT))).append("\n  =>");
			}
			List<String> fieldsToPreetyPrint = Lists.newArrayList();
			fieldsToPreetyPrint.addAll(SBPUtils.extractFieldNames(nonRecursiveFieldsTargeting));
			fieldsToPreetyPrint.addAll(SBPUtils.extractForwardFieldNames(recursiveFieldsTargeting));
			alloyStrModified.append("  globalMin[").append(SBPUtils.preetyPrintSet(fieldsToPreetyPrint)).append(".o]\n");
			if (existRootNodesOfTypeT) {
				alloyStrModified.append("  else none\n");
			}
			alloyStrModified.append("}\n");
		}
	}

	/**
	 * Implements the define_freach() procedure.
	 */
	private void defineFReachProcedure(StringBuilder alloyStrModified) {
		alloyStrModified.append("//-----SMB: define_freach()-----//\n");
		Collection<String> nonRecursiveFields = SBPUtils.extractFieldNames(sbpInfo.getNonRecursiveObjectFields());
		Collection<String> recursiveFields = SBPUtils.extractForwardFieldNames(sbpInfo.getRecursiveFields());
		List<JField> rootNodes = sbpInfo.rootNodes().all();

		if (rootNodes.isEmpty() || (nonRecursiveFields.isEmpty() && recursiveFields.isEmpty())) {
			// We do nothing.
			return;
		}
		sbpNames.addDefinition("FReach");
		alloyStrModified.append("fun FReach[] : set java_lang_Object {\n");
		alloyStrModified.append(SBPUtils.preetyPrintSet(SBPUtils.extractFieldNames(rootNodes)) + ".*");
		List<String> fieldsToPreetyPrint = Lists.newArrayList();
		fieldsToPreetyPrint.addAll(nonRecursiveFields);
		fieldsToPreetyPrint.addAll(recursiveFields);
		alloyStrModified.append(SBPUtils.preetyPrintSet(fieldsToPreetyPrint));
		alloyStrModified.append(" - null\n}\n");
	}

	/**
	 * Implements the order_root_nodes() procedure.
	 */
	private void orderRootNodesProcedure(StringBuilder alloyStrModified) {
		alloyStrModified.append("//-----SMB: order_root_nodes()-----//\n");
		for (Entry<String, List<JField>> entry : sbpInfo.rootNodes().ofType().entrySet()) {
			String tType = entry.getKey();
			List<String> fieldsOfTypeT = Lists.newArrayList(SBPUtils.extractFieldNames(entry.getValue()));
			int k = fieldsOfTypeT.size();
			if (k > 1) {
				alloyStrModified.append("fact {\n");
				for (int i = 0; i < k; ++i) {
					for (int j = i + 1; j < k; ++j) {
						alloyStrModified.append(String.format("( %s != null ", fieldsOfTypeT.get(i)));
						for (int w = i + 1; w < j - 1; ++w) {
							alloyStrModified.append(String.format(" and ( %s=null ", fieldsOfTypeT.get(w)));
							for (int v = 0; v < i; ++v) {
								alloyStrModified.append(String.format(" or %s = %s", fieldsOfTypeT.get(w), fieldsOfTypeT.get(v)));
							}
							alloyStrModified.append(") ");
						}
						alloyStrModified.append(String.format(" and %s!=null", fieldsOfTypeT.get(j)));
						for (int h = 0; h < i; ++h) {
							alloyStrModified.append(String.format(" and %s != %s", fieldsOfTypeT.get(h), fieldsOfTypeT.get(j))).append(
									String.format(" and %s != %s", fieldsOfTypeT.get(h), fieldsOfTypeT.get(i)));
						}
						alloyStrModified.append(String.format(") implies (%s -> %s) in next_%s[]", fieldsOfTypeT.get(i), fieldsOfTypeT.get(j), tType));
					}
				}
				alloyStrModified.append("}\n");
			}
		}
	}

	/**
	 * Implements the root_is_minimum() procedure.
	 */
	private void rootIsMinimumProcedure(StringBuilder alloyStrModified) {
		alloyStrModified.append("//-----SMB: root_is_minimum()-----//\n");
		for (Entry<String, List<JField>> entry : sbpInfo.rootNodes().ofType().entrySet()) {
			alloyStrModified.append("fact {\n");
			String tType = entry.getKey();
			List<String> fieldsOfTypeT = Lists.newArrayList(SBPUtils.extractFieldNames(entry.getValue()));
			int k = fieldsOfTypeT.size();
			for (int i = 0; i < k; ++i) {
				alloyStrModified.append("((");
				for (int j = 0; j < i - 1; ++j) {
					alloyStrModified.append(String.format("%s=null and ", fieldsOfTypeT.get(j)));
				}
				if (SBPUtils.isIntBuiltIn(tType)) {
					alloyStrModified.append(String.format("%s != null) implies %s = %s[0] )", fieldsOfTypeT.get(i), fieldsOfTypeT.get(i), tType));
				} else {
					alloyStrModified.append(String.format("%s != null) implies %s = %s_0 )", fieldsOfTypeT.get(i), fieldsOfTypeT.get(i), tType));
				}
				if (i < k - 1) {
					alloyStrModified.append(" and ");
				}
			}
			alloyStrModified.append("\n}\n");
		}
	}

	/**
	 * Implements the order_same_min_parent() procedure.
	 */
	private void orderSameMinParentProcedure(StringBuilder alloyStrModified) {
		alloyStrModified.append("//-----SMB: order_same_min_parent()-----//\n");
		for (String tType : sbpInfo.javaTypes().all()) {
			for (Entry<String, Collection<JField>> entry : sbpInfo.getNonRecursiveAndForwardFieldsOfImageT(tType).entrySet()) {
				List<JField> fields = Lists.newArrayList(entry.getValue());
				int k = fields.size();
				if (k <= 1) {
					continue;
				}
				if (!sbpNames.hasBeenDefined(String.format("minP_%s", tType))) {
					continue;
				}
				alloyStrModified.append(String.format("fact {\n all disj o1, o2: %s |\n", tType))
						.append(String.format("  let p1=minP_%s[o1]|\n  let p2=minP_%s[o2]|\n", tType, tType))
						.append("  (o1+o2 in FReach[] and\n  some p1 and some p2 and\n")
						.append(String.format("  p1=p2 and p1 in %s) implies \n(", entry.getKey()));
				for (int i = 0; i < k - 1; ++i) {
					for (int j = i + 1; j < k; ++j) {
						if (!(i == 0 && j == i + 1)) {
							alloyStrModified.append("and");
						}
						alloyStrModified.append(String.format("((p1.(%s)=o1 ", fields.get(i).getFieldVariable()));
						for (int l = i + 1; l < j/* - 1*/; ++l) {
							alloyStrModified.append(String.format(" and minP_%s[p1.(%s)] != p1 ", tType, fields.get(l).getFieldVariable()));
						}
						alloyStrModified.append(String.format("and p1.(%s)=o2) implies\n", fields.get(j).getFieldVariable()));
						alloyStrModified.append(String.format("  o2 = o1.next_%s[])\n", tType));
					}
				}
				alloyStrModified.append(")\n}\n");
			}
		}
	}

	/**
	 * Implements the order_same_min_parent_type() procedure.
	 */
	private void orderSameMinParentTypeProcedure(StringBuilder alloyStrModified) {
		alloyStrModified.append("//-----SMB: order_same_min_parent_type()-----//\n");
		for (String tType : sbpInfo.javaTypes().all()) {
			for (String tPrimaType : sbpInfo.javaTypes().all()) {
				if (sbpInfo.javaTypes().existsField(tType, tPrimaType)) {
					if (!sbpNames.hasBeenDefined(String.format("minP_%s", tType))) {
						continue;
					}
					alloyStrModified.append(String.format("fact {\n all disj o1, o2:%s |\n", tType)).append(String.format("  let p1=minP_%s[o1]|\n", tType))
							.append(String.format("  let p2=minP_%s[o2]|\n", tType)).append("  (o1 + o2 in FReach[] and\n")
							.append("  some p1 and some p2 and\n")
							.append(String.format("  p1!=p2 and p1+p2 in %s and p1 in" + " prevs_%s[p2] )\n", tPrimaType, tPrimaType))
							.append(String.format("  implies o1 in prevs_%s[o2]\n}\n", tType));
				}
			}
		}
	}

	/**
	 * Implements the order_diff_min_parent_types() procedure.
	 */
	private void orderDiffMinParentsTypeProcedure(StringBuilder alloyStrModified) {
		alloyStrModified.append("//-----SMB: order_diff_min_parent_type()-----//\n");
		for (String tType : sbpInfo.javaTypes().all()) {
			List<String> tPrimas = Lists.newArrayList(sbpInfo.javaTypes().javaTypesWithFieldsTargeting(tType));
			int tPrimasSize = tPrimas.size();
			if (tPrimasSize > 1) {
				for (int i = 0; i < tPrimasSize; ++i) {
					for (int j = i + 1; j < tPrimasSize; ++j) {
						if (!sbpNames.hasBeenDefined(String.format("minP_%s", tType))) {
							continue;
						}
						alloyStrModified.append(String.format("fact {\n all disj o1, o2:%s |\n", tType))
								.append(String.format("  let p1=minP_%s[o1]|\n", tType)).append(String.format("  let p2=minP_%s[o2]|\n", tType))
								.append("  ( o1+o2 in FReach[] and\n some p1 and some p2 and\n")
								.append(String.format("p1 in %s and p2 in %s )\n", tPrimas.get(i), tPrimas.get(j)))
								.append(String.format("implies o1 in prevs_%s[o2]\n}\n", tType));
					}
				}
			}
		}
	}

	/**
	 * Implements the avoid_holes() procedure.
	 */
	private void avoidHolesProcedure(StringBuilder alloyStrModified) {
		alloyStrModified.append("//-----SMB: avoid_holes()-----//\n");
		if (!sbpNames.hasBeenDefined("FReach")) {
			return;
		}
		for (String tType : sbpInfo.javaTypes().all()) {
			alloyStrModified.append("fact {\n all o : " + tType + " | \n").append("  o in FReach[] implies\n   prevs_" + tType + "[o] in FReach[]\n}\n");
		}
	}

}
