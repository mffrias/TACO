package ar.edu.itba.forArielGodio.fibonacci;


import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class ForArielGodioFibonacciBug02Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "forArielGodio.fibonacci.bug02.Fibonacci";
	}

	public void test_genericMethod1Test() throws VizException {
		setConfigKeyRelevantClasses("forArielGodio.fibonacci.bug02.Fibonacci");
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
		this.setConfigKeyNumericTypeQuantificationRange(0, 5);
		setConfigKeyTypeScopes("forArielGodio.fibonacci.bug02.Fibonacci:1");
		check(GENERIC_PROPERTIES,"Fibonacci(int)",true);
	}
}
