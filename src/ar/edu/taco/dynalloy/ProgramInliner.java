package ar.edu.taco.dynalloy;

import ar.uba.dc.rfm.alloy.util.FormulaCloner;
import ar.uba.dc.rfm.dynalloy.ast.DynalloyModule;
import ar.uba.dc.rfm.dynalloy.ast.ProgramDeclaration;
import ar.uba.dc.rfm.dynalloy.ast.programs.DynalloyProgram;
import ar.uba.dc.rfm.dynalloy.ast.programs.InvokeProgram;
import ar.uba.dc.rfm.dynalloy.util.ProgramMutator;

class ProgramInliner extends ProgramMutator {

	private DynalloyModule dynalloy_module;
	
	

	public ProgramInliner(DynalloyModule dynalloy_module) {
		super(new FormulaCloner());
		this.dynalloy_module = dynalloy_module;
	}

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
