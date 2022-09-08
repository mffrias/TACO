package examples.gcd;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerGcdTestPRVOR_REFINED86 extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "examples.stryker.gcd.prvor_refined.prvor_refined86.Gcd";
	}
	
	public void test_gcdTest() throws VizException {
		setConfigKeyRelevantClasses("examples.stryker.gcd.prvor_refined.prvor_refined86.Gcd");
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
		setConfigKeyTypeScopes("examples.stryker.gcd.prvor_refined.prvor_refined86.Gcd:1");
		check(GENERIC_PROPERTIES,"gcd_0",false);
	}



}
