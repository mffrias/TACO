package ar.edu.taco.regresion.jml;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestQuantifiers extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.jml.Quantifiers";
	}
	
	public void testReturnZero() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.jml.Quantifiers");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "returnZero_0", true);		
	}

}
