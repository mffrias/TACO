package ar.edu.taco.jfsl;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class JfslPersonTest extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.jfsl.Person";
	}
	
	public void test_get_married() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.jfsl.Person");
		
		
		runAndCheck(GENERIC_PROPERTIES,"get_married_0", true);		
	}
	
}

