package ar.edu.taco.regresion;

import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class PruneSetTest extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.PruneSet";
	}
	

	public void testGetSetSize() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.PruneSet");
		
		setConfigKeyObjectScope(2);
		setConfigKeyIntBithwidth(4);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyRelevancyAnalysis(true);
		runAndCheck(GENERIC_PROPERTIES,"getSetSize_0", true);
	}

}
