package ar.edu.taco.regresion.stryker.linkedlist;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerLinkedListTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.linkedlist.base.LinkedList";
	}

	
	
	

	
	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.linkedlist.base.LinkedList,roops.core.objects.linkedlist.base.LinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(3);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(7);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(true);
		setConfigKeyMaxStrykerMethodsPerFile(50);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("roops.core.objects.linkedlist.base.LinkedList:1,roops.core.objects.linkedlist.base.LinkedListNode:10");
		check(GENERIC_PROPERTIES,"contains_0",false);
	}


	public void test_removeNodeTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.linkedlist.base.LinkedList,roops.core.objects.linkedlist.base.LinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyInferScope(false);
		setConfigKeyObjectScope(3);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(7);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(true);
		setConfigKeyMaxStrykerMethodsPerFile(50);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("roops.core.objects.linkedlist.base.LinkedList:1,roops.core.objects.linkedlist.base.LinkedListNode:7");
		check(GENERIC_PROPERTIES,"removeNode_0",false);
	}

	
	public void test_getNodeTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.linkedlist.base.LinkedList,roops.core.objects.linkedlist.base.LinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyInferScope(false);
		setConfigKeyObjectScope(3);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(7);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(true);
		setConfigKeyMaxStrykerMethodsPerFile(50);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("roops.core.objects.linkedlist.base.LinkedList:1,roops.core.objects.linkedlist.base.LinkedListNode:7");
		check(GENERIC_PROPERTIES,"getNode_0",false);
	}

	
	public void test_insertBackTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.linkedlist.base.LinkedList,roops.core.objects.linkedlist.base.LinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyInferScope(false);
		setConfigKeyObjectScope(3);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(7);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(true);
		setConfigKeyMaxStrykerMethodsPerFile(50);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("roops.core.objects.linkedlist.base.LinkedList:1,roops.core.objects.linkedlist.base.LinkedListNode:7");
		check(GENERIC_PROPERTIES,"insertBack_0",false);
	}

	public void test_removeIndexTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.linkedlist.base.LinkedList,roops.core.objects.linkedlist.base.LinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyInferScope(false);
		setConfigKeyObjectScope(3);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(7);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(true);
		setConfigKeyMaxStrykerMethodsPerFile(50);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("roops.core.objects.linkedlist.base.LinkedList:1,roops.core.objects.linkedlist.base.LinkedListNode:7");
		check(GENERIC_PROPERTIES,"removeIndex_0",false);
	}

	

}
