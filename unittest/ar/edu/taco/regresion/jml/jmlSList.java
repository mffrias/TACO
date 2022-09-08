package ar.edu.taco.regresion.jml;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class jmlSList extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "amelia.jml.slist.SList";
	}
	
	public void testContains() throws VizException {
		setConfigKeyRelevantClasses("amelia.jml.slist.SList,amelia.jml.slist.SNode,amelia.jml.slist.Data");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyAttemptToCorrectBug(true);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES, "contains_0", true);		
	}

}


