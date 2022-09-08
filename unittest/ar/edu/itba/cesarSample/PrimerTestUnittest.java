package ar.edu.itba.cesarSample;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class PrimerTestUnittest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.itba.cesarSample.PrimerTest1";
	}
	
	public void test_1() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.itba.cesarSample.PrimerTest1");
		setConfigKeyRelevancyAnalysis(true);
	    setConfigKeyUseJavaArithmetic(true);
	    setConfigKeyRemoveQuantifiers(true);
	    setConfigKeyLoopUnroll(3);
	    setConfigKeyObjectScope(15); 
	    setConfigKeyGenerateUnitTestCase(true);
		check(GENERIC_PROPERTIES,"sample_0", true);			
	}

}