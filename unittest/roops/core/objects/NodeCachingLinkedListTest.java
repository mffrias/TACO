package roops.core.objects;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;



public class NodeCachingLinkedListTest extends CollectionTestBase {


	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.NodeCachingLinkedList";
	}
			
	public void test_removeTest() throws VizException {
        setConfigKeyRelevantClasses("roops.core.objects.NodeCachingLinkedList,roops.core.objects.LinkedListNode");
        setConfigKeyRelevancyAnalysis(true);
        setConfigKeyCheckNullDereference(true);
        setConfigKeyUseJavaArithmetic(false);
        setConfigKeyInferScope(true);
        setConfigKeyObjectScope(0);
        setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(4);
        setConfigKeySkolemizeInstanceInvariant(true);
        setConfigKeySkolemizeInstanceAbstraction(false);
        setConfigKeyGenerateUnitTestCase(true);
        setConfigKeyAttemptToCorrectBug(true);
        setConfigKeyMaxStrykerMethodsPerFile(1);
        setConfigKeyRemoveQuantifiers(true);
        setConfigKeyUseJavaSBP(true);
        setConfigKeyUseTightUpperBounds(true);
        setConfigKeyTypeScopes("roops.core.objects.NodeCachingLinkedList:1,roops.core.objects.LinkedListNode:7");
        check(GENERIC_PROPERTIES,"remove(int)",true);
	}
	
	public void test_addFirstTest() throws VizException {
        setConfigKeyRelevantClasses("roops.core.objects.NodeCachingLinkedList,roops.core.objects.LinkedListNode");
        setConfigKeyRelevancyAnalysis(true);
        setConfigKeyCheckNullDereference(true);
        setConfigKeyUseJavaArithmetic(false);
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
        setConfigKeyUseJavaSBP(true);
        setConfigKeyUseTightUpperBounds(true);
        setConfigKeyTypeScopes("roops.core.objects.NodeCachingLinkedList:1,roops.core.objects.LinkedListNode:7");
        check(GENERIC_PROPERTIES,"addFirst(java_lang_Object)",true);
	}


}
