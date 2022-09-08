package ar.edu.itba.forArielGodio.findFirstInSorted;


import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class ForArielGodioAbsoluteBug02Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "forArielGodio.findFirstInSorted.bug02.FindFirstInSorted";
	}

	public void test_genericMethod1Test() throws VizException {
		setConfigKeyRelevantClasses("forArielGodio.findFirstInSorted.bug02.FindFirstInSorted");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(4);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		setConfigKeyTypeScopes("forArielGodio.findFirstInSorted.bug02.FindFirstInSorted:1");
		check(GENERIC_PROPERTIES,"findfirstinsorted(int[], int)",true);
	}


}
