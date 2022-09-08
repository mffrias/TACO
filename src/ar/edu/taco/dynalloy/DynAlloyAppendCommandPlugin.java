package ar.edu.taco.dynalloy;

import java.util.ArrayList;

import ar.edu.taco.TacoConfigurator;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.dynalloy.ast.AssertionDeclaration;
import ar.uba.dc.rfm.dynalloy.ast.DynalloyModule;
import ar.uba.dc.rfm.dynalloy.ast.ProgramDeclaration;
import ar.uba.dc.rfm.dynalloy.plugin.DynAlloyASTPlugin;

class DynAlloyAppendCommandPlugin implements DynAlloyASTPlugin {

	@Override
	public DynalloyModule transform(DynalloyModule input) {
		StringBuffer sb = new StringBuffer();
		sb.append(input.getAlloyStr());

		TacoConfigurator taco_configurator = TacoConfigurator.getInstance();
		String scope_of_analysis = taco_configurator.getAssertionArguments();
		
		if (taco_configurator.getGenerateCheck() == true) {
			for (AssertionDeclaration assertion : input.getAssertions())
				sb.append("check " + assertion.getAssertionId() + " " + scope_of_analysis + "\n");
		}

		if (taco_configurator.getGenerateRun() == true) {
			for (ProgramDeclaration programDeclaration : input.getPrograms()) {
				if (programDeclaration.getProgramId().equals(taco_configurator.getMethodToCheck())) {
					sb.append("run " + programDeclaration.getProgramId() + " " + scope_of_analysis + "\n");
				}
			}
		}
		
		if (taco_configurator.getIncludeSimulationProgramDeclaration()) {
			for (ProgramDeclaration programDeclaration : input.getPrograms()) {
				if (programDeclaration.getProgramId().startsWith("simulate_")) {
					sb.append("run " + programDeclaration.getProgramId() + " " + scope_of_analysis + "\n");
				}
			}
		}

		return new DynalloyModule(input.getModuleId(), input.getImports(), sb.toString(), 
				input.getActions(), input.getPrograms(), input.getAssertions(), new AlloyTyping(), new ArrayList<AlloyFormula>());
	}

}
