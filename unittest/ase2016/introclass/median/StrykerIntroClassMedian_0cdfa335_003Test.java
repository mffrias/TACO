package ase2016.introclass.median;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerIntroClassMedian_0cdfa335_003Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ase2016.introclass.median.introclass_0cdfa335_003";
	}

			
	public void test_medianTest() throws VizException {
		setConfigKeyRelevantClasses("ase2016.introclass.median.introclass_0cdfa335_003");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyCheckArithmeticException(true);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(4);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(true);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("ase2016.introclass.median.introclass_0cdfa335_003:1");
		check(GENERIC_PROPERTIES,"median_0",true);
	}



}
