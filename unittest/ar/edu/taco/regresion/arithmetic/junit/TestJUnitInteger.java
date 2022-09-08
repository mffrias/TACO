package ar.edu.taco.regresion.arithmetic.junit;

import ar.edu.taco.arithmetic.BinSearch;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestJUnitInteger extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.arithmetic.junit.JUnitInteger";
	}
	
	public void test_positive() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.junit.JUnitInteger");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyLoopUnroll(1);
		setConfigKeyGenerateUnitTestCase(true);
		
		runAndCheck(GENERIC_PROPERTIES, "positive_0", true);
	}

	public void test_negative() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.junit.JUnitInteger");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyLoopUnroll(1);
		setConfigKeyGenerateUnitTestCase(true);
		
		runAndCheck(GENERIC_PROPERTIES, "negative_0", true);
	}
}
