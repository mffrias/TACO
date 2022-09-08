package ar.edu.taco.jfsl;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class JfslIntTreeTest extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.jfsl.IntTree";
	}
	
	public void testSearch() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.jfsl.IntTree,ar.edu.taco.jfsl.Node");
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(1);
		runAndCheck(GENERIC_PROPERTIES,"search_0", false);		
	}
	
	public void testClear() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.jfsl.IntTree,ar.edu.taco.jfsl.Node");
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES,"clear_0", true);		
	}
	
	
}

