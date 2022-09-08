package ar.edu.taco.regresion.stryker;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerAccountTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.stryker.LightAccount";
	}

	public void test_withdrawTest() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.stryker.LightAccount");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyLoopUnroll(5);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyTypeScopes("ar.edu.taco.stryker.LightAccount:1");
		check(GENERIC_PROPERTIES,"withdraw_0", false);
	}
}