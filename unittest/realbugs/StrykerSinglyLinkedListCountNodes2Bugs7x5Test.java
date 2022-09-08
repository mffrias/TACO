package realbugs;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;
import mujava.api.Configuration;

public class StrykerSinglyLinkedListCountNodes2Bugs7x5Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "realbugs.SinglyLinkedListCountNodes2Bugs7x5";
	}

			
	public void test_countNodesTest() throws VizException {
		setConfigKeyRelevantClasses("realbugs.SinglyLinkedListCountNodes2Bugs7x5,realbugs.SinglyLinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyCheckArithmeticException(true);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(10);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("realbugs.SinglyLinkedListCountNodes2Bugs7x5:1,realbugs.SinglyLinkedListNode:9");
		check(GENERIC_PROPERTIES,"countNodes_0",true);
	}



}
