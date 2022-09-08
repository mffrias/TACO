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
		String called_program_id = u.getProgramId();
		
		ProgramDeclaration called_program = dynalloy_module.getProgram(called_program_id);
		DynalloyProgram program_to_inline = (DynalloyProgram) called_program.getBody().accept(this);

		return program_to_inline;
	}

	
	

}
