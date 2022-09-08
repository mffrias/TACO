package ar.edu.taco.regresion.arithmetic.simple;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestDeepASTrandom extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.bv32.DeepASTrandom";
	}

	public void test_Method1() throws VizException {
		setConfigKeyRelevantClasses("roops.core.bv32.DeepASTrandom");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "Method1_0", true);
	}


	
}
