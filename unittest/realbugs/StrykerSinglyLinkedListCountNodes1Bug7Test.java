package realbugs;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;
import mujava.api.Configuration;

public class StrykerSinglyLinkedListCountNodes1Bug7Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "realbugs.SinglyLinkedListCountNodes1Bug7";
	}

			
	public void test_countNodesTest() throws VizException {
		setConfigKeyRelevantClasses("realbugs.SinglyLinkedListCountNodes1Bug7,realbugs.SinglyLinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyCheckArithmeticException(true);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(4);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("realbugs.SinglyLinkedListCountNodes1Bug7:1,realbugs.SinglyLinkedListNode:3");
		check(GENERIC_PROPERTIES,"countNodes_0",true);
	}



}
