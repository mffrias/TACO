package ar.edu.itba.forArielGodio.findFirstZero;


import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class ForArielGodioAbsoluteBug02Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "forArielGodio.findFirstZero.bug02.FindFirstZero";
	}

	public void test_genericMethod1Test() throws VizException {
		setConfigKeyRelevantClasses("forArielGodio.findFirstZero.bug02.FindFirstZero");
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
		setConfigKeyTypeScopes("forArielGodio.findFirstZero.bug02.FindFirstZero:1");
		check(GENERIC_PROPERTIES,"findFirstZero(int[])",true);
	}


}
