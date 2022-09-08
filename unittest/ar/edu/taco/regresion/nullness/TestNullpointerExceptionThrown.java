package ar.edu.taco.regresion.nullness;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestNullpointerExceptionThrown extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.nullness.NullPointerExceptionThrown";
	}


	public void test_field_access() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.nullness.NullPointerExceptionThrown");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "nullPointerExceptionIsThrown_0", true);
	}
}
