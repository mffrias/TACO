package ase2016.introclass.smallest;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerIntroClassSmallest_3b2376ab_008Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ase2016.introclass.smallest.introclass_3b2376ab_008";
	}

			
	public void test_smallestTest() throws VizException {
		setConfigKeyRelevantClasses("ase2016.introclass.smallest.introclass_3b2376ab_008");
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
		setConfigKeyTypeScopes("ase2016.introclass.smallest.introclass_3b2376ab_008:1");
		check(GENERIC_PROPERTIES,"smallest_0",true);
	}



}
