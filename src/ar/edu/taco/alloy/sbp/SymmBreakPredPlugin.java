package ar.edu.taco.alloy.sbp;

import java.util.List;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.uba.dc.rfm.alloy.ast.AlloyModule;
import ar.uba.dc.rfm.alloy.util.AlloyMutator;
import ar.uba.dc.rfm.dynalloy.plugin.AlloyASTPlugin;

public class SymmBreakPredPlugin implements AlloyASTPlugin {

	private JDynAlloyClassHierarchy sbpInfo;

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
	
	public void setSourceJDynAlloyModules(List<JDynAlloyModule> src_jdynalloy_modules) {
		sbpInfo = JDynAlloyClassHierarchy.buildFromModules(src_jdynalloy_modules);
	}

}
