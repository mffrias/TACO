package ar.edu.taco.alloy.sbp;

import java.util.List;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.uba.dc.rfm.alloy.ast.AlloyModule;
import ar.uba.dc.rfm.alloy.util.AlloyMutator;
import ar.uba.dc.rfm.dynalloy.plugin.AlloyASTPlugin;

/**
 * The Alloy-AST plugin of TACO's symmetry breaking ({@code useJavaSBP}), which
 * {@code DynalloyToAlloyManager} registers before {@code bound.UBoundPlugin}. It adds the
 * symmetry-breaking predicates of the TACO papers (README, "References / how to cite"): it
 * names the atoms of each Java type, splits each recursive field of the pre-state into a
 * forward and a backward field ({@link SBPInstrumentAlloyProcedureMutator}), and orders the
 * heap from the root nodes ({@link SBPProcedures}), all as Alloy text after the model's. The
 * root nodes are the parameters of the method under analysis ({@link JDynAlloyClassHierarchy});
 * with none, the model is returned unchanged (docs/FIXES-FOR-MASTER.md, N7). It works on the
 * fields of {@code QF}, which exist only with {@code dynalloy.toAlloy.removeQuantifiers}:
 * without it no parameter is a field of {@code QF}, so there are no root nodes and nothing is
 * added.
 *
 * <p>State: the {@code JDynAlloyClassHierarchy} built by
 * {@link #setSourceJDynAlloyModules(List)}, which {@link #transform} narrows down; one plugin
 * per translation. No static state of its own; the scopes come from the configuration and the
 * {@code InferredScope} singleton ({@code SBPUtils.getScope}).
 */
public class SymmBreakPredPlugin implements AlloyASTPlugin {

	private JDynAlloyClassHierarchy sbpInfo;

	/**
	 * Adds the symmetry-breaking predicates to the model. Narrows the class hierarchy to the
	 * fields of {@code QF} ({@code JDynAlloyClassHierarchy.matchWithQF}); if no root node is
	 * left, returns {@code input} itself. Otherwise returns a new module whose {@code QF} has
	 * each recursive field {@code r_0} replaced by {@code fr_0} and {@code br_0}, whose facts and
	 * assertion read {@code QF.br_0 + QF.fr_0} for {@code QF.r_0}, and whose text ends with the
	 * atoms, the facts of the split fields, the output of {@link SBPProcedures} and a comment
	 * with the orderings of the types, root nodes and fields.
	 *
	 * <p>Assumes that {@link #setSourceJDynAlloyModules(List)} was called (otherwise a
	 * {@code NullPointerException}); call once, since the narrowing is not undone.
	 *
	 * @param input the Alloy module translated from the DynAlloy model
	 * @return the module with the predicates, or {@code input}
	 * @throws RuntimeException if a field that symmetry breaking uses has more than one domain,
	 *         or more than one range besides {@code null} ({@code SBPUtils})
	 */
	@Override
	public AlloyModule transform(AlloyModule input) {
		// For debugging purposes:
		// SBPUtils.debugPrint("pfInitial.als", input);

		sbpInfo.matchWithQF(input.getGlobalSig().getFields());
		
		// If there are no root nodes we generate no axioms.
		if (sbpInfo.rootNodes().all().isEmpty()) {
			// For debugging purposes:
			// SBPUtils.debugPrint("pfFinal.als", input);
			return input;
		}
		final StringBuilder alloyStrModified = new StringBuilder(input.getAlloyStr());
		AlloyMutator p = new SBPInstrumentAlloyProcedureMutator(sbpInfo, alloyStrModified);
		AlloyModule output = (AlloyModule) input.accept(p);
	
		SBPProcedures sbpProcedures = new SBPProcedures(sbpInfo);
		sbpProcedures.executeAll(alloyStrModified);
		
		alloyStrModified.append("/*\n");
		alloyStrModified.append(sbpInfo.toString());
		alloyStrModified.append("\n");
		alloyStrModified.append("*/\n");
		
		AlloyModule ret = new AlloyModule(alloyStrModified.toString(), output.getGlobalSig(),
				output.getFacts(), output.getAssertions());
		
		// For debugging purposes:
		// SBPUtils.debugPrint("pfFinal.als", ret);
		return ret;
	}
	
	/**
	 * Builds the class hierarchy that {@link #transform} uses from the source JDynAlloy modules
	 * ({@code DynalloyToAlloyManager.setSourceJDynAlloyModules}): their fields and, as root
	 * nodes, the parameters of the programs of {@code classToCheck} named like
	 * {@code methodToCheck} without its parameter types, both read from the configuration now.
	 * Call before the translation.
	 *
	 * @param src_jdynalloy_modules the source modules
	 * @throws IllegalArgumentException if a {@code represents} expression is not of the form
	 *         {@code (x).(y)}
	 */
	public void setSourceJDynAlloyModules(List<JDynAlloyModule> src_jdynalloy_modules) {
		sbpInfo = JDynAlloyClassHierarchy.buildFromModules(src_jdynalloy_modules);
	}

}
