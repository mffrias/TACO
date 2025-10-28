package edu.utep.ds2;


import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class AverageTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "edu.utep.ds2.AverageMain";
	}

	public void test_genericMethodTest() throws VizException {
		setConfigKeyRelevantClasses("edu.utep.ds2.AverageMain");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(4);
		setConfigKeyLoopUnroll(3);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		setConfigKeyTypeScopes("edu.utep.ds2.AverageMain:1");
//		checkAndRunSpecIfFaulty(GENERIC_PROPERTIES,"calculateAverage(int, int)",10);
	}

}