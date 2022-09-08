package ar.edu.taco.regresion.arithmetic;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestDivision extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.arithmetic.Division";
	}

	public void test_neg2_div_pos2() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.Division");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		//setCondigKeyBuildJavaTrace(true);

		runAndCheck(GENERIC_PROPERTIES, "neg2_div_pos2_0", true);
	}

	public void test_pos2_div_pos2() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.Division");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		runAndCheck(GENERIC_PROPERTIES, "pos2_div_pos2_0", true);
	}
	
	public void test_pos2_div_neg2() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.Division");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		runAndCheck(GENERIC_PROPERTIES, "pos2_div_neg2_0", true);
	}
	public void test_neg2_div_neg2() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.Division");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		runAndCheck(GENERIC_PROPERTIES, "neg2_div_neg2_0", true);
	}
	

	public void test_pos2_mul_neg1_plus_zero() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.Division");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		runAndCheck(GENERIC_PROPERTIES, "pos2_mul_neg1_plus_zero_0", true);
	}
	
}
