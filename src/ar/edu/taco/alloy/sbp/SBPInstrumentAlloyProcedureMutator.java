package ar.edu.taco.alloy.sbp;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ar.edu.jdynalloy.ast.JField;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.AlloySig;
import ar.uba.dc.rfm.alloy.util.AlloyMutator;
import ar.uba.dc.rfm.alloy.util.FormulaMutator;

class SBPInstrumentAlloyProcedureMutator extends AlloyMutator {

	private JDynAlloyClassHierarchy sbpInfo;
	private StringBuilder alloyStrModified;

	SBPInstrumentAlloyProcedureMutator(JDynAlloyClassHierarchy sbpInfo,
			StringBuilder alloyStrModified) {
		super(new FormulaMutator(new SBPExpressionMutator(
				sbpInfo.getRecursiveFields())));
		this.sbpInfo = sbpInfo;
		this.alloyStrModified = alloyStrModified;
	}
	
	@Override
	public Object visit(AlloySig n) {
		/*
		 * Following the instrument_Alloy() procedure defined in fig 6.2 we do the part of:
		 * for each type T
		 * 		do k <- scope(T) 
		 * 		"one sig T1,. . . ,Tk extends T {}"
		 * end for 
		 */
		StringBuffer sbpSigs = new StringBuffer("//-------------SMB sigs-------------// \n");
		for (String tType : sbpInfo.javaTypes().all()) {
			StringBuffer sigToAddBuffer = new StringBuffer("one sig ");
			int k = SBPUtils.getScope(tType);
			StringBuffer instances = new StringBuffer();
			int i;
			for (i = 0; i < k - 1; ++i) {
				instances.append(tType + "_" + i + ", ");
			}
			instances.append(tType + "_" + i);
			sigToAddBuffer.append(instances).append(" extends " + tType + " {} \n");
			sbpSigs.append(sigToAddBuffer).append("\n");
		}
		alloyStrModified.append(sbpSigs.toString());
		
		/*
		 * Following the instrument_Alloy() procedure defined in fig 6.2 we do the part of
		 * adding the fact.
		 */
		StringBuilder sbpFacts = new StringBuilder();
		for (JField recursiveField : sbpInfo.getRecursiveFields()) {
			String fieldVariable = recursiveField.getFieldVariable().toString();
			String t = SBPUtils.getOnlyFromOrThrowException(recursiveField);
			sbpFacts.append("fact {\n  no ( " + SBPUtils.buildFFieldName(fieldVariable) + ".univ & " +
					SBPUtils.buildBFieldName(fieldVariable) + ".univ ) and \n  " + t + " = " +
					SBPUtils.buildFFieldName(fieldVariable) +  ".univ + " +
					SBPUtils.buildBFieldName(fieldVariable) + ".univ \n}\n");
		}
		alloyStrModified.append(sbpFacts);
		
		/*
		 * Following the instrument_Alloy() procedure we do the part of
		 * for each recursive r : T -> (one T + null) do
		 * 		Add new field fr : T -> lone(T + null)
		 * 		Add new field br : T -> lone(T)
		 * 		...
		 * 		Remove field r
		 * end for
		 */
		Map<AlloyVariable, String> updatedMap = Maps.newHashMap();
		Set<String> variablesToRemove = Sets.newHashSet();
		AlloyTyping currentFields = n.getFields();
		for (JField recursiveField : sbpInfo.getRecursiveFields()) {
			Set<String> originalFrom = recursiveField.getFieldType().from();
			Set<String> toWithNull = recursiveField.getFieldType().to();
			toWithNull.add("null");
			Set<String> toWithoutNull = recursiveField.getFieldType().to();
			toWithoutNull.remove("null");
			updatedMap.put(new AlloyVariable(SBPUtils.buildFFieldNameNoQF(recursiveField) + "_0"),
					SBPUtils.preetyPrintSet(originalFrom) + " -> lone(" +
					SBPUtils.preetyPrintSet(toWithNull) + ")");
			updatedMap.put(new AlloyVariable(SBPUtils.buildBFieldNameNoQF(recursiveField) + "_0"),
					SBPUtils.preetyPrintSet(originalFrom) + " -> lone(" +
					SBPUtils.preetyPrintSet(toWithoutNull) + ")");
			variablesToRemove.add(recursiveField.getFieldVariable().toString() + "_0");
		}
		for (AlloyVariable alloyVar : currentFields.varSet()) {
			if (!variablesToRemove.contains(alloyVar.toString())) {
				updatedMap.put(alloyVar, currentFields.get(alloyVar));
			}
		}
		return new AlloySig(n.isAbstract(), n.isOne(), n.getSignatureId(),
				new AlloyTyping(updatedMap), n.getExtSigId());
	}
}
