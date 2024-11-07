package ar.edu.itba.forArielGodio.binarySearch;


import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class ForArielGodioBinarySeachBug01Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "forArielGodio.binarySearch.bug01.BinarySearch";
	}

	public void test_genericMethodTest() throws VizException {
		setConfigKeyRelevantClasses("forArielGodio.binarySearch.bug01.BinarySearch");
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
		setConfigKeyNumericTypeQuantificationRange(0,5);
		setConfigKeyTypeScopes("forArielGodio.binarySearch.bug01.BinarySearch:1");
		check(GENERIC_PROPERTIES,"binary(int[], int)",true);
	}

}
