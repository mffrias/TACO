package icse.singlylinkedlist.set4;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerSinglyLinkedListInsertBack3Bug2Dx3Ix10ITest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "icse.singlylinkedlist.set4.SinglyLinkedListInsertBack3Bug2Dx3Ix10I";
	}

			
	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("icse.singlylinkedlist.set4.SinglyLinkedListInsertBack3Bug2Dx3Ix10I,icse.singlylinkedlist.SinglyLinkedListNode");
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
		setConfigKeyAttemptToCorrectBug(true);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("icse.singlylinkedlist.set4.SinglyLinkedListInsertBack3Bug2Dx3Ix10I:1,icse.singlylinkedlist.SinglyLinkedListNode:3");
		check(GENERIC_PROPERTIES,"insertBack_0",true);
	}



}
