package ar.edu.taco.regresion.arithmetic;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestFloatAdvanced extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.arithmetic.FloatAdvanced";
	}

	public void test_Method1_goal0() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatAdvanced");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "Method1_goal0_0", true);
	}

	public void test_Method1_goal1() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatAdvanced");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "Method1_goal1_0", true);
	}

	public void test_Method2() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatAdvanced");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		check(GENERIC_PROPERTIES, "Method2_0", true);
	}

	public void test_Method3() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatAdvanced");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "Method3_0", true);
	}

	public void test_Method4() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatAdvanced");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "Method4_0", true);
	}

	public void test_Method5() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatAdvanced");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		check(GENERIC_PROPERTIES, "Method5_0", true);
	}

	public void test_Method6() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatAdvanced");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "Method6_0", true);
	}

	public void test_Method7() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatAdvanced");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		check(GENERIC_PROPERTIES, "Method7_0", true);
	}

	public void test_Method8() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatAdvanced");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		check(GENERIC_PROPERTIES, "Method8_0", true);
	}

}
