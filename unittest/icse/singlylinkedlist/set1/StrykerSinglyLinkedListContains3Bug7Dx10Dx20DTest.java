package icse.singlylinkedlist.set1;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerSinglyLinkedListContains3Bug7Dx10Dx20DTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "icse.singlylinkedlist.set1.SinglyLinkedListContains3Bug7Dx10Dx20D";
	}

			
	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("icse.singlylinkedlist.set1.SinglyLinkedListContains3Bug7Dx10Dx20D,icse.singlylinkedlist.SinglyLinkedListNode");
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
		setConfigKeyTypeScopes("icse.singlylinkedlist.set1.SinglyLinkedListContains3Bug7Dx10Dx20D:1,icse.singlylinkedlist.SinglyLinkedListNode:3");
		check(GENERIC_PROPERTIES,"contains_0",true);
	}



}
