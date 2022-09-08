package ar.edu.taco.regresion.sbp;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class SBPDataLeftRightParentTreeTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.sbp.DataLeftRightParentTree";
	}
	
	public void test_sbp_findMethod() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.sbp.DataLeftRightParentTree");
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
		setConfigKeyTypeScopes("ar.edu.taco.sbp.DataLeftRightParentTree:1, " +
				"ar.edu.taco.sbp.DataLeftRightParentTree$Node:5, " +
				"ar.edu.taco.sbp.DataLeftRightParentTree$Data:5");
		runAndCheck(GENERIC_PROPERTIES,"showInstance_0", true);
	}
}
