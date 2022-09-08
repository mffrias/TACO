package examples.gcd;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerGcdTestROR53 extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "examples.stryker.gcd.ror.ror53.Gcd";
	}

	
	public void test_gcdTest() throws VizException {
		setConfigKeyRelevantClasses("examples.stryker.gcd.ror.ror53.Gcd");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyCheckArithmeticException(false);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(3);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(true);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		setConfigKeyTypeScopes("examples.stryker.gcd.ror.ror53.Gcd:1");
		check(GENERIC_PROPERTIES,"gcd_0",false);
	}



}
