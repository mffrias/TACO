package examples.multikeymap;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerMultiKeyMapTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "examples.stryker.multikeymap.MultiKeyMap";
	}

	

	public void test_isEqualKeyTest() throws VizException {
		setConfigKeyRelevantClasses("examples.stryker.multikeymap.MultiKeyMap,examples.stryker.multikeymap.HashEntry,examples.stryker.multikeymap.MultiKey");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(7);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(50);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("examples.stryker.multikeymap.MultiKeyMap:2,examples.stryker.multikeymap.HashEntry:2,examples.stryker.multikeymap.MultiKey:2");
		check(GENERIC_PROPERTIES,"showInstance_0",false);
	}




}
