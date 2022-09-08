package ar.edu.taco.regresion.nullness;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestNullness extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.nullness.Nullness";
	}


	public void test_field_access() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.nullness.Nullness");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "field_access_0", true);
	}
	
	public void test_method_call() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.nullness.Nullness");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "method_call_0", true);
	}
}
