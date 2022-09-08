package ar.edu.itba.forArielGodio.binarySearch;


import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class ForArielGodioBinarySeachBug03Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "forArielGodio.binarySearch.bug03.BinarySearch";
	}

	public void test_genericMethodTest() throws VizException {
		setConfigKeyRelevantClasses("forArielGodio.binarySearch.bug03.BinarySearch");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(3);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		setConfigKeyTypeScopes("forArielGodio.binarySearch.bug03.BinarySearch:1");
		check(GENERIC_PROPERTIES,"binary(int[], int)",true);
	}

}
