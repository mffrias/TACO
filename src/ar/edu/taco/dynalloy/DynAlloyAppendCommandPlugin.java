package ar.edu.taco.dynalloy;

import java.util.ArrayList;

import ar.edu.taco.TacoConfigurator;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.dynalloy.ast.AssertionDeclaration;
import ar.uba.dc.rfm.dynalloy.ast.DynalloyModule;
import ar.uba.dc.rfm.dynalloy.ast.ProgramDeclaration;
import ar.uba.dc.rfm.dynalloy.plugin.DynAlloyASTPlugin;

/**
 * The DynAlloy-AST plugin that adds the Alloy commands to the model, since dynalloy4 generates
 * none. {@link DynalloyToAlloyManager} registers it on every translation, after the scope
 * inference plugins; dynalloy4's {@code DynAlloyCompiler} applies it to the parsed
 * {@code output.dals}, before the translation to Alloy. {@code AlloyStage} runs the first
 * command of the result. See dynalloy4's docs/ARCHITECTURE.md ("Translation"). Holds no state.
 */
class DynAlloyAppendCommandPlugin implements DynAlloyASTPlugin {

	/**
	 * Returns a copy of the module whose Alloy text ({@code getAlloyStr()}) ends with the
	 * commands, all with the scope of {@code TacoConfigurator.getAssertionArguments()} (with
	 * {@code inferScope}, that of the {@code InferredScope} singleton, which
	 * {@link DynAlloyScopeInferencePlugin} has just set):
	 * <ul>
	 * <li>with {@code generateCheck} (the default), {@code check <assertion>} for each assertion
	 *     of the module;</li>
	 * <li>with {@code generateRun}, {@code run <program>} for the program whose id equals
	 *     {@code methodToCheck}; that name has the parameter types and program ids do not, so
	 *     under the current naming none is added (see docs/METHOD-NAMING.md);</li>
	 * <li>with {@code include_simulation_program_declaration}, {@code run <program>} for each
	 *     {@code simulate_} program.</li>
	 * </ul>
	 * The copy keeps the input's imports, actions, programs and assertions. It does not keep the
	 * input's {@code dynalloyFields} (left {@code null}; nothing reads them from the
	 * {@code SpecContext}), and has an empty typing and no predicates from arithmetic in object
	 * invariants, as the parser leaves them.
	 *
	 * @param input the parsed DynAlloy module
	 * @return the module with the commands
	 */
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
