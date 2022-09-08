package ar.edu.itba.ayvmyc2021;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestPrimeraClase extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.itba.ayvmyc2021.Samples";
	}
	
	public void test_1() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.itba.ayvmyc2021.Samples");
		setConfigKeyRelevancyAnalysis(true);
	    setConfigKeyUseJavaArithmetic(true);
	    setConfigKeyRemoveQuantifiers(true);
	    setConfigKeyLoopUnroll(3);
	    setConfigKeyObjectScope(15); 
	    setConfigKeyGenerateUnitTestCase(true);
		check(GENERIC_PROPERTIES,"sample_0", true);			
	}

}