package ar.edu.taco.regresion.arithmetic;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestFloatArithmetic extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.arithmetic.FloatArithmetic";
	}
	
	public void test_eq_floats() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "eq_floats_0", true);
	}
	
	public void test_neq_floats() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "neq_floats_0", true);
	}

	public void test_infinity_float() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "infinity_float_0", true);
	}

	public void test_gt_floats() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "gt_floats_0", true);
	}
	public void test_gte_floats() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "gte_floats_0", true);
	}
	public void test_lt_floats() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "lt_floats_0", true);
	}
	public void test_lte_floats() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "lte_floats_0", true);
	}
	
	public void test_float_literal() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "float_literal_0", true);
	}
	
	public void test_float_max_value() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "float_max_value_0", true);
	}
	
	public void test_float_positive_infinity() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "float_positive_infinity_0", true);
	}

	public void test_many_types() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "many_types_0", true);
	}
	
	public void test_float_mul() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		check(GENERIC_PROPERTIES, "float_mul_0", true);
	}
	
	public void test_float_div() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "float_div_0", true);
	}
	
	public void test_float_add() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "float_add_0", true);
	}

	public void test_float_sub() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.FloatArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "float_sub_0", true);
	}
	
}
