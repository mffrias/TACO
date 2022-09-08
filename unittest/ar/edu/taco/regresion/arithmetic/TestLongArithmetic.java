package ar.edu.taco.regresion.arithmetic;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestLongArithmetic extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.arithmetic.LongArithmetic";
	}
	
	public void test_add_longs() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.LongArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		runAndCheck(GENERIC_PROPERTIES, "add_longs_0", true);
	}	

	public void test_sub_longs() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.LongArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		runAndCheck(GENERIC_PROPERTIES, "sub_longs_0", true);
	}	

	public void test_eq_longs() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.LongArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		runAndCheck(GENERIC_PROPERTIES, "eq_longs_0", true);
	}	

	public void test_gt_longs() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.LongArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		runAndCheck(GENERIC_PROPERTIES, "gt_longs_0", true);
	}	

	public void test_gte_longs() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.LongArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		runAndCheck(GENERIC_PROPERTIES, "gte_longs_0", true);
	}	

	public void test_lt_longs() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.LongArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		runAndCheck(GENERIC_PROPERTIES, "lt_longs_0", true);
	}	

	public void test_lte_longs() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.LongArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		runAndCheck(GENERIC_PROPERTIES, "lte_longs_0", true);
	}	

	public void test_neq_longs() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.LongArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		runAndCheck(GENERIC_PROPERTIES, "neq_longs_0", true);
	}	

	public void test_add_long_and_literal() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.LongArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		runAndCheck(GENERIC_PROPERTIES, "add_long_and_literal_0", true);
	}	

	public void test_mul_longs() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.LongArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		runAndCheck(GENERIC_PROPERTIES, "mul_longs_0", true);
	}
	
	public void test_div_longs() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.LongArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		runAndCheck(GENERIC_PROPERTIES, "div_longs_0", true);
	}	

	public void test_div_longs_stmt() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.LongArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		runAndCheck(GENERIC_PROPERTIES, "div_longs_stmt_0", true);
	}	
	
	public void test_rem_longs() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.LongArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		runAndCheck(GENERIC_PROPERTIES, "rem_longs_0", true);
	}	

	public void test_puzzle_1() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.LongArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		runAndCheck(GENERIC_PROPERTIES, "puzzle_1_0", true);
	}

	public void test_puzzle_2() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.LongArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		runAndCheck(GENERIC_PROPERTIES, "puzzle_2_0", true);
	}
	
	public void test_add_overflow() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.LongArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		runAndCheck(GENERIC_PROPERTIES, "add_overflow_0", true);
	}

	public void test_sub_overflow() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.LongArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		runAndCheck(GENERIC_PROPERTIES, "sub_overflow_0", true);
	}

}
