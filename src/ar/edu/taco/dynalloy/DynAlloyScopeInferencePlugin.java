package ar.edu.taco.dynalloy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.xlator.ObjectCreationCounter;
import ar.edu.taco.infer.InferredScope;
import ar.edu.taco.infer.ScopeInference;
import ar.uba.dc.rfm.dynalloy.ast.DynalloyModule;
import ar.uba.dc.rfm.dynalloy.plugin.DynAlloyASTPlugin;

/**
 * The second half of TACO's scope inference (with {@code inferScope}): a DynAlloy-AST plugin
 * that runs {@code ar.edu.taco.infer.ScopeInference} on the source JDynAlloy modules and on
 * the counts of the {@link DynAlloyProgramScopeInferencePlugin} registered before it.
 * {@link DynalloyToAlloyManager} creates one per translation. It does not change the model:
 * its output is the static {@code InferredScope} singleton, which the plugins after it read
 * (the scope of the commands, through {@code TacoConfigurator.getAssertionArguments()} in
 * {@code DynAlloyAppendCommandPlugin}; symmetry breaking; tight bounds; the bitwidth of
 * {@code CardinalSizeOfPlugin}).
 */
public class DynAlloyScopeInferencePlugin implements DynAlloyASTPlugin {

	private ScopeInference scope_inference_engine = new ScopeInference ();

	/**
	 * Infers the scopes and the bitwidth ({@code ScopeInference.inferScope()}), which replaces
	 * the static {@code InferredScope} singleton (with tight bounds, {@code UBoundPlugin}
	 * replaces it again later in the same translation); nothing resets it, so in one JVM it
	 * stays from one run to the next. Then overwrites each count of the program plugin's
	 * {@code ObjectCreationCounter} with the inferred scope of that type. Unclear:
	 * nothing reads that counter afterwards, and the map {@code inferredAllocsPerType} is never
	 * used; whether the overwrite is still needed is an open question.
	 *
	 * <p>Assumes that both setters were called and that the program plugin has already run
	 * (dynalloy4 applies the plugins in the order they were registered).
	 *
	 * @param input the parsed DynAlloy module
	 * @return {@code input}, unchanged
	 * @throws IllegalStateException if a setter was not called (thrown by {@code ScopeInference})
	 */
	@Override
	public DynalloyModule transform(DynalloyModule input) {

		InferredScope theInferredScope = scope_inference_engine.inferScope();
		Map<String, Integer> inferredAllocsPerType = new HashMap<>();
		Set<String> theTypes = this.scope_inference_engine.object_alloc_counter.getAllocations().keySet();
		for (String aTypeName : theTypes){
			this.scope_inference_engine.object_alloc_counter.setAllocations(aTypeName, theInferredScope.getInferredScope(aTypeName));
		}

		return input;
	}

	/**
	 * Sets the JDynAlloy modules the model came from (those given to
	 * {@link DynalloyToAlloyManager#setSourceJDynAlloyModules(List)}), which the inference reads:
	 * the class graph for the input scope, the literals, the inputs and the contracts. Call
	 * before the translation.
	 *
	 * @param src_jdynalloy_modules the source modules
	 */
	public void setJDynAlloyModules(List<JDynAlloyModule> src_jdynalloy_modules) {
		scope_inference_engine.setJDynAlloyModules(src_jdynalloy_modules);
	}

	/**
	 * Takes the counters of the program plugin, which must be registered before this one. They
	 * are taken now, still empty, and read when {@link #transform(DynalloyModule)} runs, by which
	 * time the program plugin has filled them. Call before the translation.
	 *
	 * @param program_scope_inference_plugin the plugin whose counts the inference uses
	 */
	public void setProgramScopeInferencePlugin(DynAlloyProgramScopeInferencePlugin program_scope_inference_plugin) {
        ArithmeticOpCounter arithmetic_op_counter = program_scope_inference_plugin.getArithmeticOpCounter();
		ObjectCreationCounter object_alloc_counter = program_scope_inference_plugin.getObjectCreationCounter();
		
		scope_inference_engine.setArithmeticOpCounter(arithmetic_op_counter);
		scope_inference_engine.setObjectCreationCounter(object_alloc_counter );
	}

}
