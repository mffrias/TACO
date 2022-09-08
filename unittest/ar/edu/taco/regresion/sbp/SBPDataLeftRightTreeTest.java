package ar.edu.taco.regresion.sbp;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class SBPDataLeftRightTreeTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.sbp.DataLeftRightTree";
	}
	
	public void test_sbp_findMethod() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.sbp.DataLeftRightTree");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyLoopUnroll(1);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("ar.edu.taco.sbp.DataLeftRightTree:1, " +
				"ar.edu.taco.sbp.DataLeftRightTree$Node:5, " +
				"ar.edu.taco.sbp.DataLeftRightTree$Data:5");
		runAndCheck(GENERIC_PROPERTIES,"showInstance_0", true);
	}
}
