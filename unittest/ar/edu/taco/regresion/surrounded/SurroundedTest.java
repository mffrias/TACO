package ar.edu.taco.regresion.surrounded;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class SurroundedTest extends GenericTestBase {
	
	@Override
	protected String getClassToCheck() {
 		return "ar.edu.taco.surrounded.Surrounded";
	}
	
	public void testGetAPositiveNumber() throws VizException {
		runAndCheck(GENERIC_PROPERTIES, "getAPositiveNumber_0", false);
	}
	
	public void testGetANegativeNumber() throws VizException {
		runAndCheck(GENERIC_PROPERTIES, "getANegativeNumber_0", false);
	}
	
}
