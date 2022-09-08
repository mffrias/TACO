package ase2016.nodecachinglinkedlist;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerNodeCachingLinkedListAddFirst2Bugs3x2Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ase2016.nodecachinglinkedlist.NodeCachingLinkedListAddFirst2Bugs3x2";
	}
			
	public void test_addFirstTest() throws VizException {
        setConfigKeyRelevantClasses("ase2016.nodecachinglinkedlist.NodeCachingLinkedListAddFirst2Bugs3x2,ase2016.nodecachinglinkedlist.LinkedListNode");
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
        setConfigKeyTypeScopes("ase2016.nodecachinglinkedlist.NodeCachingLinkedListAddFirst2Bugs3x2:1,ase2016.nodecachinglinkedlist.LinkedListNode:4");
        check(GENERIC_PROPERTIES,"addFirst_0",true);
	}

}
