package ar.edu.taco.regresion.arithmetic;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestWhileMult extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.arithmetic.WhileMult";
	}
	
	public void test_m() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.WhileMult");
		setConfigKeyRelevancyAnalysis(true);
	    setConfigKeyUseJavaArithmetic(true);
	    setConfigKeyRemoveQuantifiers(true);
	    setConfigKeyLoopUnroll(3);
	    setConfigKeyObjectScope(15); 
	    setConfigKeyGenerateUnitTestCase(true);
		runAndCheck(GENERIC_PROPERTIES,"m_0", true);			
	}

}
