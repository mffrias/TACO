package ar.edu.taco.regresion.stryker.singlylinkedlist;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerSinglyLinkedListInsertBackBug9x12Ix8Ix10ITest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.SinglyLinkedListInsertBackBug9x12Ix8Ix10I";
	}

			
	public void test_insertBackTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.SinglyLinkedListInsertBackBug9x12Ix8Ix10I,roops.core.objects.SinglyLinkedListNode");
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
		setConfigKeyTypeScopes("roops.core.objects.SinglyLinkedListInsertBackBug9x12Ix8Ix10I:1,roops.core.objects.SinglyLinkedListNode:4");
		check(GENERIC_PROPERTIES,"insertBack_0",true);
	}



}
