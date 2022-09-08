package ar.edu.itba.forArielGodio.addLoop;


import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class ForArielGodioAddLoopBug03Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "forArielGodio.addLoop.bug03.AddLoop";
	}

	public void test_genericMethod1Test() throws VizException {
		setConfigKeyRelevantClasses("forArielGodio.addLoop.bug03.AddLoop");
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
		setConfigKeyTypeScopes("forArielGodio.addLoop.bug03.AddLoop:1");
		check(GENERIC_PROPERTIES,"addLoop(int, int)",true);
	}
}
