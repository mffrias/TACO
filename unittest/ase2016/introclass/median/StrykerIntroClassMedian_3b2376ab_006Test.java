package ase2016.introclass.median;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerIntroClassMedian_3b2376ab_006Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ase2016.introclass.median.introclass_3b2376ab_006";
	}

			
	public void test_medianTest() throws VizException { //string bug lost in translation
		setConfigKeyRelevantClasses("ase2016.introclass.median.introclass_3b2376ab_006");
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
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("ase2016.introclass.median.introclass_3b2376ab_006:1");
		check(GENERIC_PROPERTIES,"median_0",true);
	}



}
