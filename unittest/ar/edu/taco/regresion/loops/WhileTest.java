package ar.edu.taco.regresion.loops;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class WhileTest extends GenericTestBase {
	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.loops.WhileLoop";
	}
	
	/**
	 * This test passed the 
	 * @throws VizException
	 */
	public void testLoopInvariantDoesNotKeepInvariant() throws VizException {
		setConfigKeyObjectScope(4);
		setConfigKeyIntBithwidth(4);
		setConfigKeyInferScope(false);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyTypeScopes("ar.edu.taco.loops.WhileLoop:1,java.lang.Object:5");
		
		runAndCheck(GENERIC_PROPERTIES,"LoopInvariantDoesNotKeepInvariant_0", true);
	}
	
	public void testLoopInvariantKeepInvariant_max() throws VizException {
		setConfigKeyObjectScope(4);
		setConfigKeyIntBithwidth(4);
		setConfigKeyInferScope(false);
		setConfigKeyTypeScopes("ar.edu.taco.loops.WhileLoop:1,java.lang.Object:5");

		runAndCheck(GENERIC_PROPERTIES,"LoopInvariantKeepInvariant_max_0", false);
	}
	
	public void testLoopInvariantInvalidInvariant() throws VizException {
		setConfigKeyObjectScope(2);
		setConfigKeyIntBithwidth(4);
		setConfigKeyInferScope(false);
		setConfigKeyTypeScopes("ar.edu.taco.loops.WhileLoop:1,java.lang.Object:5");

		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		runAndCheck(GENERIC_PROPERTIES,"LoopInvariantInvalidInvariant_0", true);
	}
		
}
