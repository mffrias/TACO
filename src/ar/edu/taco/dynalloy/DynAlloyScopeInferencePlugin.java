package ar.edu.taco.dynalloy;

import java.util.List;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.xlator.ObjectCreationCounter;
import ar.edu.taco.infer.ScopeInference;
import ar.uba.dc.rfm.dynalloy.ast.DynalloyModule;
import ar.uba.dc.rfm.dynalloy.plugin.DynAlloyASTPlugin;

public class DynAlloyScopeInferencePlugin implements DynAlloyASTPlugin {

	private ScopeInference scope_inference_engine = new ScopeInference ();

	@Override
	public DynalloyModule transform(DynalloyModule input) {

		scope_inference_engine.inferScope();
		
		return input;
	}

	public void setJDynAlloyModules(List<JDynAlloyModule> src_jdynalloy_modules) {
		scope_inference_engine.setJDynAlloyModules(src_jdynalloy_modules);
	}

	public void setProgramScopeInferencePlugin(DynAlloyProgramScopeInferencePlugin program_scope_inference_plugin) {
        ArithmeticOpCounter arithmetic_op_counter = program_scope_inference_plugin.getArithmeticOpCounter();
		ObjectCreationCounter object_alloc_counter = program_scope_inference_plugin.getObjectCreationCounter();
		
		scope_inference_engine.setArithmeticOpCounter(arithmetic_op_counter);
		scope_inference_engine.setObjectCreationCounter(object_alloc_counter );
	}

}
