package ar.edu.taco.regresion.loops;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class LoopNoExitTest extends GenericTestBase {
	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.loops.LoopNoExit";
	}
	
	/**
	 * This test passed the 
	 * @throws VizException
	 */
	public void test_loop_no_exit_valid() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.loops.LoopNoExit");
		setConfigKeyObjectScope(4);
		setConfigKeyIntBithwidth(4);
		setConfigKeyInferScope(false);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyRemoveExitWhileGuard(false);
		
		setConfigKeyTypeScopes("ar.edu.taco.loops.LoopNoExit:1,java.lang.Object:1");
		
		runAndCheck(GENERIC_PROPERTIES,"loopNoExit_0", false);
	}

	public void test_loop_no_exit_counterexample() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.loops.LoopNoExit");
		setConfigKeyObjectScope(4);
		setConfigKeyIntBithwidth(4);
		setConfigKeyInferScope(false);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyRemoveExitWhileGuard(true);
		
		setConfigKeyTypeScopes("ar.edu.taco.loops.LoopNoExit:1,java.lang.Object:1");
		
		runAndCheck(GENERIC_PROPERTIES,"loopNoExit_0", true);
	}
}
