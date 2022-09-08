package ar.edu.taco;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class AssertExampleTest extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.AssertExample";
	}

	public void test_assert_method() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.AssertExample");
		setConfigKeyRelevancyAnalysis(true);
		runAndCheck(GENERIC_PROPERTIES,"assertion_method_0", true);			
	}
}
