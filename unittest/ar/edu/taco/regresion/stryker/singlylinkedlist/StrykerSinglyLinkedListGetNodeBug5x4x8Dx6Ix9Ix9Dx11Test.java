package ar.edu.taco.regresion.stryker.singlylinkedlist;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerSinglyLinkedListGetNodeBug5x4x8Dx6Ix9Ix9Dx11Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.SinglyLinkedListGetNodeBug5x4x8Dx6Ix9Ix9Dx11";
	}

			
	public void test_getNodeTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.SinglyLinkedListGetNodeBug5x4x8Dx6Ix9Ix9Dx11,roops.core.objects.SinglyLinkedListNode");
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
		setConfigKeyTypeScopes("roops.core.objects.SinglyLinkedListGetNodeBug5x4x8Dx6Ix9Ix9Dx11:1,roops.core.objects.SinglyLinkedListNode:3");
		check(GENERIC_PROPERTIES,"getNode_0",true);
	}



}
