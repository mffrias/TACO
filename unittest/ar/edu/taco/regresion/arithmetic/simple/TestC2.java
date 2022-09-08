package ar.edu.taco.regresion.arithmetic.simple;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestC2 extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.arithmetic.simple.C2";
	}

	public void test_m2() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.simple.C2");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		runAndCheck(GENERIC_PROPERTIES, "m2_0", true);
	}

}
