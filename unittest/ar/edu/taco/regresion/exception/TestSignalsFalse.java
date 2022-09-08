package ar.edu.taco.regresion.exception;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestSignalsFalse extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.exception.SignalsFalse";
	}

	public void test_return_zero() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.exception.SignalsFalse");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyInferScope(false);
		setConfigKeyObjectScope(7);
        setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(7);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyRemoveQuantifiers(true);
		check(GENERIC_PROPERTIES,"return_zero_0",false);
	}
	

}
