package ayvmyc1o2017;
import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class SLLTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ayvmyc1o2017.SLL";
	}

			
	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("ayvmyc1o2017.SLL,ayvmyc1o2017.Node");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyCheckArithmeticException(true);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(3);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		setConfigKeyTypeScopes("ayvmyc1o2017.SLL:1,ayvmyc1o2017.Node:3");
		check(GENERIC_PROPERTIES,"contains_0",true);
	}



}