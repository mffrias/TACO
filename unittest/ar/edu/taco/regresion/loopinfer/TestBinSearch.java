package ar.edu.taco.regresion.loopinfer;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestBinSearch extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.loopinfer.BinSearch";
	}

	public void test_bin_search() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.loopinfer.BinSearch");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyInferScope(false);
		setConfigKeyObjectScope(1);
        setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(3);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyRemoveQuantifiers(true);
		check(GENERIC_PROPERTIES,"bin_search_0",false);
	}
	

}
