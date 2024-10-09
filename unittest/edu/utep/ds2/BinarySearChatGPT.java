package edu.utep.ds2;


import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class BinarySearChatGPT extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "edu.utep.ds2.BinarySearch";
	}

	public void test_genericMethodTest() throws VizException {
		setConfigKeyRelevantClasses("edu.utep.ds2.BinarySearch");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(2);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		setConfigKeyTypeScopes("edu.utep.ds2.BinarySearch:1");
		check(GENERIC_PROPERTIES,"search(int[], int, int, int)",true);
	}

}
