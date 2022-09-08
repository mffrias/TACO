package ar.edu.taco.regresion.sbp;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class SBPListWithArrayTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.sbp.ListWithArray";
	}
	
	public void test_sbp_showInstance() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.sbp.ListWithArray");
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
		setConfigKeyTypeScopes("ar.edu.taco.sbp.ListWithArray:1, " +
				"ar.edu.taco.sbp.ListWithArray$Node:5, java.lang.Object:6, int:3");
		runAndCheck(GENERIC_PROPERTIES,"showInstance_0", true);
	}
}
