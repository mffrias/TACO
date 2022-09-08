package pldi;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerNodeCachingLinkedList extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "pldi.nodecachinglinkedlist.NodeCachingLinkedList";
	}

	
	public void test() throws VizException {
		setConfigKeyRelevantClasses("pldi.nodecachinglinkedlist.NodeCachingLinkedList,pldi.nodecachinglinkedlist.LinkedListNode");
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
		setConfigKeyTypeScopes("pldi.nodecachinglinkedlist.NodeCachingLinkedList:1,pldi.nodecachinglinkedlist.LinkedListNode:4");
		check(GENERIC_PROPERTIES,"remove_0",true);
	}
	

	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("pldi.nodecachinglinkedlist.NodeCachingLinkedList,pldi.nodecachinglinkedlist.LinkedListNode");
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
		setConfigKeyTypeScopes("pldi.nodecachinglinkedlist.NodeCachingLinkedList:1,pldi.nodecachinglinkedlist.LinkedListNode:4");
		check(GENERIC_PROPERTIES,"contains_0",false);
	}

	   public void test_addFirstTest() throws VizException {
	        setConfigKeyRelevantClasses("pldi.nodecachinglinkedlist.NodeCachingLinkedList,pldi.nodecachinglinkedlist.LinkedListNode");
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
	        setConfigKeyTypeScopes("pldi.nodecachinglinkedlist.NodeCachingLinkedList:1,pldi.nodecachinglinkedlist.LinkedListNode:4");
	        check(GENERIC_PROPERTIES,"addFirst_0",false);
	    }


	
	public void test_removeTest() throws VizException {
		setConfigKeyRelevantClasses("pldi.nodecachinglinkedlist.NodeCachingLinkedList,pldi.nodecachinglinkedlist.LinkedListNode");
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
		setConfigKeyTypeScopes("pldi.nodecachinglinkedlist.NodeCachingLinkedList:1,pldi.nodecachinglinkedlist.LinkedListNode:4");
		check(GENERIC_PROPERTIES,"remove_0",false);
	}
	
	
}
