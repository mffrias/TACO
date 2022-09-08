package ar.edu.taco.regresion.arithmetic;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestIntegerArithmetic extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.arithmetic.IntegerArithmetic";
	}

	public void test_add_integers() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "add_integers_0", true);
	}

	public void test_lt_integers() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "lt_integers_0", true);
	}

	public void test_lte_integers() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "lte_integers_0", true);
	}

	public void test_gt_integers() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "gt_integers_0", true);
	}

	public void test_gte_integers() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "gte_integers_0", true);
	}

	public void test_spec_cmp_integers() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "spec_cmp_integers_0", true);
	}

	public void test_eq_integers() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "eq_integers_0", true);
	}

	public void test_neq_integers() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "neq_integers_0", true);
	}

	public void test_copy_integer_literal() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "copy_integer_literal_0", true);
	}

	public void test_cmp_integer_literal() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "cmp_integer_literal_0", true);
	}

	public void test_spec_add_integers() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "spec_add_integers_0", true);
	}

	public void test_spec_jml_add_integers() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "spec_jml_add_integers_0", true);
	}

	public void test_sub_integers() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "sub_integers_0", true);
	}

	public void test_spec_sub_integers() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "spec_sub_integers_0", true);
	}

	public void test_negate_integer() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "negate_integer_0", true);
	}

	public void test_spec_negate_integer() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		runAndCheck(GENERIC_PROPERTIES, "spec_negate_integer_0", true);
	}

	public void test_operation_1() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "operation_1_0",true);
	}

	
	public void test_sub_literal() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "sub_literal_0",true);
	}

	public void test_mul_integer() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "mul_integer_0", true);
	}

	public void test_div_integer() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "div_integer_0",true);
	}

	public void test_rem_integer() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "rem_integer_0",true);
	}

	public void test_add_overflow() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "add_overflow_0",true);
	}

	public void test_sub_overflow() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "sub_overflow_0",true);
	}

	public void test_mul_overflow() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "mul_overflow_0",true);
	}

	public void test_mod_big_numbers() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "mod_big_numbers_0", true);
	}

	public void test_mod_small_numbers() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "mod_small_numbers_0", true);
	}
	
	public void test_mul_small_numbers() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "mul_small_numbers_0", true);
	}
	
	public void test_div_small_numbers() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		setConfigKeyRemoveQuantifiers(true);
		runAndCheck(GENERIC_PROPERTIES, "div_small_numbers_0", true);
	}
	
	public void test_suffix_add() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "suffix_add_0", true);
	}
	
	public void test_div_negative_and_positive() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		setConfigKeyRemoveQuantifiers(true);
		runAndCheck(GENERIC_PROPERTIES, "div_negative_and_positive_0", true);
	}
	
	public void test_div_small_negative_and_positive() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		setConfigKeyRemoveQuantifiers(true);
		runAndCheck(GENERIC_PROPERTIES, "div_small_negative_and_positive_0", true);
	}
	
	public void test_rem_small_negative_and_positive() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		setConfigKeyRemoveQuantifiers(true);
		runAndCheck(GENERIC_PROPERTIES, "rem_small_negative_and_positive_0", true);
	}
	
	public void test_rem_small_positive_and_negative() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.IntegerArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		setConfigKeyRemoveQuantifiers(true);
		runAndCheck(GENERIC_PROPERTIES, "rem_small_positive_and_negative_0", true);
	}
}
