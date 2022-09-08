package ar.edu.taco.regresion.sbp;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class SBPDataHeadTailListTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.sbp.DataHeadTailList";
	}
	
	public void test_sbp_showInstance() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.sbp.DataHeadTailList");
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

		setConfigKeyTypeScopes("ar.edu.taco.sbp.DataHeadTailList:1, " +
				"ar.edu.taco.sbp.DataHeadTailList$Node:5, " +
				"ar.edu.taco.sbp.DataHeadTailList$Data:5");
		
		runAndCheck(GENERIC_PROPERTIES,"showInstance_0", true);
	}
	
	public void test_sbp_showStaticInstance() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.sbp.DataHeadTailList");
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
		setConfigKeyTypeScopes("ar.edu.taco.sbp.DataHeadTailList:1, " +
				"ar.edu.taco.sbp.DataHeadTailList$Node:5, " +
				"ar.edu.taco.sbp.DataHeadTailList$Data:5");
		runAndCheck(GENERIC_PROPERTIES,"showStaticInstance_0", true);
	}
}
