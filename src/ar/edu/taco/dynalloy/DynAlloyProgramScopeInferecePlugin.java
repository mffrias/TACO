package ar.edu.taco.dynalloy;

import java.util.ArrayList;
import java.util.Collections;

import ar.edu.taco.TacoConfigurator;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.da2a.prepare.ClosureRemover;
import ar.uba.dc.rfm.dynalloy.ast.DynalloyModule;
import ar.uba.dc.rfm.dynalloy.ast.ProgramDeclaration;
import ar.uba.dc.rfm.dynalloy.ast.programs.DynalloyProgram;
import ar.uba.dc.rfm.dynalloy.plugin.DynAlloyASTPlugin;
import ar.uba.dc.rfm.dynalloy.util.DynalloyMutator;
import ar.uba.dc.rfm.dynalloy.util.DynalloyVisitor;

public class DynAlloyProgramScopeInferecePlugin implements DynAlloyASTPlugin {

	private ArithmeticOpCollector arithmetic_op_collector = new ArithmeticOpCollector();
	private ObjectCreationCollector object_creation_collector = new ObjectCreationCollector();

	@Override
	public DynalloyModule transform(DynalloyModule input) {

		int unroll = TacoConfigurator.getInstance().getDynAlloyToAlloyLoopUnroll();
		DynalloyModule unrolled_dynalloy = unroll_loops(input, unroll);

		DynalloyModule inlined_unrolled_dynalloy = inline_program_calls(unrolled_dynalloy, unroll);

		collect_object_allocations(inlined_unrolled_dynalloy);

		collect_arithmetic_operations(inlined_unrolled_dynalloy);

		return input;
	}

	private void collect_object_allocations(DynalloyModule module) {
		module.accept(new DynalloyVisitor(object_creation_collector));
	}

	private DynalloyModule inline_program_calls(DynalloyModule dynalloy_module, int unroll) {
		ProgramInliner program_inliner = new ProgramInliner(dynalloy_module);

		String method_to_check = TacoConfigurator.getInstance().getMethodToCheck();

		ProgramDeclaration program_decl = dynalloy_module.getProgram(method_to_check);
		DynalloyProgram inlined_program = (DynalloyProgram) program_decl.getBody().accept(program_inliner);

		ProgramDeclaration inlined_program_decl = new ProgramDeclaration(program_decl.getProgramId(), program_decl.getParameters(),
				program_decl.getLocalVariables(), inlined_program, program_decl.getParameterTyping(), program_decl.getPredsFromArithInContracts(), program_decl.getVarsFromArithInContracts());

		DynalloyModule result = new DynalloyModule(dynalloy_module.getModuleId(), dynalloy_module.getImports(), dynalloy_module.getAlloyStr(),
				dynalloy_module.getActions(), Collections.singleton(inlined_program_decl), 
				dynalloy_module.getAssertions(), new AlloyTyping(), new ArrayList<AlloyFormula>());

		return result;
	}

	private DynalloyModule unroll_loops(DynalloyModule dynalloy_module, int unroll) {
		boolean remove_exit_while_guard = TacoConfigurator.getInstance().getRemoveExitWhileGuard();
		ClosureRemover program_unroller = new ClosureRemover(unroll, true, remove_exit_while_guard);
		DynalloyModule unrolled = (DynalloyModule) dynalloy_module.accept(new DynalloyMutator(program_unroller));
		return unrolled;
	}

	private void collect_arithmetic_operations(DynalloyModule module) {
		module.accept(new DynalloyVisitor(arithmetic_op_collector));
	}

	public ObjectCreationCounter getObjectCreationCounter() {
		return object_creation_collector.getObjectCreationCounter();
	}

	public ArithmeticOpCounter getArithmeticOpCounter() {
		return arithmetic_op_collector.getArithmeticOpCounter();
	}
}
