package ar.edu.itba.forArielGodio;


import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class ForArielGodioInverseTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "forArielGodio.Inverse";
	}

	//Con aritmética java tarda infinito en traducir. Con 4 bits, encuentra el bug al toque.
	
	public void test_genericMethod1Test() throws VizException {
		setConfigKeyRelevantClasses("forArielGodio.Inverse");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
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
		setConfigKeyTypeScopes("forArielGodio.Inverse:1");
		check(GENERIC_PROPERTIES,"inverse(int[], int[])",true);
	}

}
