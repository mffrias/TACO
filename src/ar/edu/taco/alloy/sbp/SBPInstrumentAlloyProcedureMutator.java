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

/**
 * The first part of {@link SymmBreakPredPlugin}'s work, the {@code instrument_Alloy()}
 * procedure (its comments cite a "fig 6.2"), as a mutator of the Alloy module. It splits each
 * recursive field {@code r: T -> T + null} of the pre-state ({@code QF.r_0}) into a forward
 * field {@code fr_0: T -> lone (T + null)} and a backward field {@code br_0: T -> lone T},
 * replaces {@code QF.r_0} with {@code QF.br_0 + QF.fr_0} in the facts and the assertion
 * ({@link SBPExpressionMutator}), and appends to the given text the atoms
 * {@code one sig T_0, ..., T_<k-1> extends T} of each Java type T (k from
 * {@code SBPUtils.getScope}) and, for each recursive field, a fact that puts every atom of its
 * domain in the domain of exactly one of {@code fr_0} and {@code br_0}. The module's own text
 * ({@code getAlloyStr()}) is not rewritten. Returns the new module.
 */
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
	
	/**
	 * Replaces the recursive fields of {@code QF} with their forward and backward fields,
	 * keeping the other fields, and appends to the text the atoms {@code T_i} and the facts of
	 * the split fields (see the class comment). For a type whose k is 0 it still declares one
	 * atom, {@code T_0}.
	 *
	 * @param n the global signature {@code QF}
	 * @return the new {@code QF}
	 * @throws RuntimeException if a recursive field has more than one domain
	 */
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
