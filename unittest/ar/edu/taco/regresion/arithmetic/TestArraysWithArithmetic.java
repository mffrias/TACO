package ar.edu.taco.regresion.arithmetic;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestArraysWithArithmetic extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.arithmetic.ArraysWithArithmetic";
	}


	public void test_array_length() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.ArraysWithArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "array_length_0", true);
	}
	
	public void test_array_constructor() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.ArraysWithArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyCheckNullDereference(true);
		runAndCheck(GENERIC_PROPERTIES, "array_constructor_0", true);
	}
	
	public void test_array_read() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.ArraysWithArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "array_read_0", true);
	}
	
	public void test_array_write() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.ArraysWithArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "array_write_0", true);
	}

	public void test_arrays_read_write() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.ArraysWithArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "arrays_read_write_0", true);
	}
	
	public void test_array_null_length() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.ArraysWithArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "array_null_length_0", true);
	}
	
	public void test_array_null_read_variable() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.ArraysWithArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "array_null_read_variable_0", true);
	}
	
	public void test_array_null_read_literal() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.ArraysWithArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "array_null_read_literal_0", true);
	}
	
	public void test_arrays_read_out_of_bounds() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.ArraysWithArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "arrays_read_out_of_bounds_0", true);
	}
	
	public void test_arrays_write_out_of_bounds() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.ArraysWithArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "arrays_write_out_of_bounds_0", true);
	}
	
	public void test_array_initialization() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.ArraysWithArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "array_initialization_0", false);
	}
	
	public void test_big_array() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.ArraysWithArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "big_array_0", true);
	}
}
