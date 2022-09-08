package ar.edu.taco.regresion.arithmetic.simple;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestC1 extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.arithmetic.simple.C1";
	}

	public void test_m1() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.simple.C1");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		runAndCheck(GENERIC_PROPERTIES, "m1_0", true);
	}

}
