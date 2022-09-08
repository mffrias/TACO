package ar.edu.taco.regresion.arithmetic.junit;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestJUnitArray extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.arithmetic.junit.JUnitArray";
	}
	
	public void test_array() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.junit.JUnitArray");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyLoopUnroll(1);
		setConfigKeyGenerateUnitTestCase(true);
		
		runAndCheck(GENERIC_PROPERTIES, "array_0", true);
	}

}
