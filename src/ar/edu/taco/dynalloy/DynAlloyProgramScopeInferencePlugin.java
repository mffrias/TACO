package ar.edu.taco.dynalloy;

import java.util.*;

import ar.edu.jdynalloy.xlator.ObjectCreationCollector;
import ar.edu.jdynalloy.xlator.ObjectCreationCounter;
import ar.edu.taco.TacoConfigurator;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.FormulaVisitor;
import ar.uba.dc.rfm.da2a.prepare.ClosureRemover;
import ar.uba.dc.rfm.dynalloy.ast.DynalloyModule;
import ar.uba.dc.rfm.dynalloy.ast.ProgramDeclaration;
import ar.uba.dc.rfm.dynalloy.ast.programs.DynalloyProgram;
import ar.uba.dc.rfm.dynalloy.plugin.DynAlloyASTPlugin;
import ar.uba.dc.rfm.dynalloy.util.DfsProgramVisitor;
import ar.uba.dc.rfm.dynalloy.util.DynalloyMutator;
import ar.uba.dc.rfm.dynalloy.util.DynalloyVisitor;

/**
 * The first half of TACO's scope inference (with {@code inferScope}): a DynAlloy-AST plugin
 * that counts the object allocations and the Java-arithmetic operations of the method under
 * analysis, which {@link DynAlloyScopeInferencePlugin}, registered right after it, turns into
 * scopes. {@link DynalloyToAlloyManager} creates one per translation and registers it with
 * dynalloy4's {@code DynAlloyCompiler}, which applies it to the parsed {@code output.dals},
 * before the translation to Alloy. It does not change the model.
 *
 * <p>The counters ({@link #getObjectCreationCounter()}, of jDynAlloy's
 * {@code ObjectCreationCollector}, and {@link #getArithmeticOpCounter()}, of
 * {@code ArithmeticOpCollector}) are created with the plugin and filled by
 * {@link #transform(DynalloyModule)}; {@link DynAlloyScopeInferencePlugin} takes them when it
 * is set up and reads them after this plugin has run. They are never reset, so an instance
 * serves one translation. Holds no static state. Not to be confused with the unused
 * {@link DynAlloyProgramScopeInferecePlugin}.
 */
public class DynAlloyProgramScopeInferencePlugin implements DynAlloyASTPlugin {

	private ArithmeticOpCollector arithmetic_op_collector = new ArithmeticOpCollector();
	private ObjectCreationCollector object_creation_collector = new ObjectCreationCollector();
	private Set<AlloyFormula> programSpecs = new HashSet();

	/**
	 * Creates the plugin, with empty counters.
	 *
	 * @param theprogramSpecs formulas whose arithmetic is counted too:
	 *        {@link DynalloyToAlloyManager} passes the object invariants of the module under
	 *        analysis and the {@code requires} and {@code ensures} of the method (see
	 *        docs/FIXES-FOR-MASTER.md, N8). Only their function calls are counted (see
	 *        {@code ArithmeticOpCollector}).
	 */
	public DynAlloyProgramScopeInferencePlugin(Set<AlloyFormula> theprogramSpecs) {
		programSpecs = theprogramSpecs;
	}

	/**
	 * Counts the allocations and the arithmetic operations of the method under analysis:
	 * <ol>
	 * <li>unrolls the loops of a copy of the module as the translation will
	 *     ({@code dynalloy.toAlloy.loopUnroll} times, with {@code removeExitWhileGuard}; always
	 *     with skips, which does not change the counts);</li>
	 * <li>takes the program whose id is {@code methodToCheck} up to its {@code '('}, that is
	 *     {@code <package>_<Class>_<method>} (see docs/METHOD-NAMING.md), and inlines every call
	 *     in its body ({@code ProgramInliner});</li>
	 * <li>counts, in a module whose only program is the inlined one, the allocations (jDynAlloy's
	 *     {@code ObjectCreationCollector}: a {@code getUnusedObject} call and the
	 *     {@code instanceOf} test after it) and the arithmetic operations; then the arithmetic of
	 *     the formulas given to the constructor.</li>
	 * </ol>
	 * Every unrolled iteration, every inlined call and both branches of every choice count, so
	 * the counts bound those of any one execution. The program is the first one with that id: if
	 * the model also has an overload of the method (overloads share their program id), that one
	 * may be counted instead. Reads {@code TacoConfigurator}; adds to the counters.
	 *
	 * <p>Assumes that {@code methodToCheck} has a {@code '('} and that a program has that id
	 * (otherwise a {@link StringIndexOutOfBoundsException} or a {@link NullPointerException}),
	 * and that recursion has been unfolded (jDynAlloy's {@code RecursionUnfolderVisitor}),
	 * otherwise the inlining does not end.
	 *
	 * @param input the parsed DynAlloy module
	 * @return {@code input}, unchanged
	 */
	@Override
	public DynalloyModule transform(DynalloyModule input) {

		int unroll = TacoConfigurator.getInstance().getDynAlloyToAlloyLoopUnroll();
		DynalloyModule unrolled_dynalloy = unroll_loops(input, unroll);

		DynalloyModule inlined_unrolled_dynalloy = inline_program_calls(unrolled_dynalloy, unroll);

		collect_object_allocations(inlined_unrolled_dynalloy);

		//Here is where the actual arithmetic operator counting happens
		collect_arithmetic_operations(inlined_unrolled_dynalloy);

		collect_arithmetic_operations_from_specs(programSpecs);



		return input;
	}

	private void collect_arithmetic_operations_from_specs(Set<AlloyFormula> programSpecs) {
		for (AlloyFormula f : programSpecs){
			f.accept(arithmetic_op_collector.getDfsFormulaVisitor());
		}
	}

	private void collect_object_allocations(DynalloyModule module) {
		module.accept(new DynalloyVisitor(object_creation_collector));
	}

	private DynalloyModule inline_program_calls(DynalloyModule dynalloy_module, int unroll) {
		ProgramInliner program_inliner = new ProgramInliner(dynalloy_module);

		String method_to_check = TacoConfigurator.getInstance().getMethodToCheck().substring(0, TacoConfigurator.getInstance().getMethodToCheck().indexOf('('));

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
