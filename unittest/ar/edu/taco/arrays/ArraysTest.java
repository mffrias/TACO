package ar.edu.taco.arrays;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class ArraysTest extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.arrays.Arrays";
	}
	
	public void testIntegerArrayInitialization() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arrays.Arrays");
	    
		runAndCheck(GENERIC_PROPERTIES,"int_array_initialization_0", false);		
	}


}
