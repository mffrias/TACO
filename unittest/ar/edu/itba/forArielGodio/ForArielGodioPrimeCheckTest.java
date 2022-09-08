package ar.edu.itba.forArielGodio;


import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class ForArielGodioPrimeCheckTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "forArielGodio.PrimeCheck";
	}

	/// Tarda infinito en traducir a cnf en mi notebook
	
	public void test_genericMethod1Test() throws VizException {
		setConfigKeyRelevantClasses("forArielGodio.PrimeCheck");
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
		setConfigKeyTypeScopes("forArielGodio.PrimeCheck:1");
		check(GENERIC_PROPERTIES,"isPrime(int)",true);
	}
}
