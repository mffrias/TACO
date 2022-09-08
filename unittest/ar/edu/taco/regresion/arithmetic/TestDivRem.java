package ar.edu.taco.regresion.arithmetic;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestDivRem extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.arithmetic.DivRem";
	}

	public void test_pos_div_pos() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.DivRem");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		runAndCheck(GENERIC_PROPERTIES, "pos_div_pos_0", true);
	}

	public void test_pos_div_neg() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.DivRem");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		runAndCheck(GENERIC_PROPERTIES, "pos_div_neg_0", true);
	}
	

	public void test_neg_div_neg() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.DivRem");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		runAndCheck(GENERIC_PROPERTIES, "neg_div_neg_0", true);
	}
	
	public void test_neg_div_pos() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.DivRem");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		runAndCheck(GENERIC_PROPERTIES, "neg_div_pos_0", true);
	}
	
	public void test_pos_rem_pos() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.DivRem");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		runAndCheck(GENERIC_PROPERTIES, "pos_rem_pos_0", true);
	}
	
	public void test_neg_rem_neg() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.DivRem");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		runAndCheck(GENERIC_PROPERTIES, "neg_rem_neg_0", true);
	}
	
	public void test_pos_rem_neg() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.DivRem");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		runAndCheck(GENERIC_PROPERTIES, "pos_rem_neg_0", true);
	}
	
	public void test_neg_rem_pos() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.DivRem");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		runAndCheck(GENERIC_PROPERTIES, "neg_rem_pos_0", true);
	}
}
