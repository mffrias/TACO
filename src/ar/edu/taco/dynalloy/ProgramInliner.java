package ar.edu.taco.dynalloy;

import ar.uba.dc.rfm.alloy.util.FormulaCloner;
import ar.uba.dc.rfm.dynalloy.ast.DynalloyModule;
import ar.uba.dc.rfm.dynalloy.ast.ProgramDeclaration;
import ar.uba.dc.rfm.dynalloy.ast.programs.DynalloyProgram;
import ar.uba.dc.rfm.dynalloy.ast.programs.InvokeProgram;
import ar.uba.dc.rfm.dynalloy.util.ProgramMutator;

/**
 * Inlines the program calls of a DynAlloy program, for counting only: each call is replaced
 * by the body of the program it invokes, itself inlined. The body is not instantiated: its
 * formal parameters are not replaced by the actual ones and its local variables are not
 * renamed, so the result serves to count operations and allocations
 * ({@link DynAlloyProgramScopeInferencePlugin}), not to be translated. Calls are resolved
 * among the programs of the module given to the constructor (see
 * {@link #visit(InvokeProgram)}). Holds no static state.
 */
class ProgramInliner extends ProgramMutator {

	private DynalloyModule dynalloy_module;
	
	

	public ProgramInliner(DynalloyModule dynalloy_module) {
		super(new FormulaCloner());
		this.dynalloy_module = dynalloy_module;
	}

	/**
	 * Returns the body of the program the call invokes, with its own calls inlined. The program
	 * is the one with the call's id and number of arguments, which is how DynAlloy resolves
	 * calls, or else the first one with the id; overloads share their id, so two with the same
	 * number of parameters cannot be told apart. See docs/FIXES-FOR-MASTER.md, N5. Assumes the
	 * calls are not recursive (jDynAlloy unfolds recursion before): a recursive call is inlined
	 * until the stack overflows. If no program has the call's id, throws a
	 * {@link NullPointerException}.
	 *
	 * @param u the call
	 * @return the inlined body, in place of the call
	 */
	@Override
	public Object visit(InvokeProgram u) {
		ProgramDeclaration called_program = calledProgram(u);
		DynalloyProgram program_to_inline = (DynalloyProgram) called_program.getBody().accept(this);

		return program_to_inline;
	}

	// The program a call invokes: the one with its name and number of arguments, which is how
	// DynAlloy resolves calls; overloaded methods and constructors share their name.
	private ProgramDeclaration calledProgram(InvokeProgram u) {
		for (ProgramDeclaration program : dynalloy_module.getPrograms()) {
			if (program.getProgramId().equals(u.getProgramId())
					&& program.getParameters().size() == u.getActualParameters().size()) {
				return program;
			}
		}
		return dynalloy_module.getProgram(u.getProgramId());
	}

	
	

}
