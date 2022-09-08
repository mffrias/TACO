package ase2016.nodecachinglinkedlist;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerNodeCachingLinkedListAddFirst3Bugs3x2x5Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ase2016.nodecachinglinkedlist.NodeCachingLinkedListAddFirst3Bugs3x2x5";
	}
			
	public void test_addFirstTest() throws VizException {
        setConfigKeyRelevantClasses("ase2016.nodecachinglinkedlist.NodeCachingLinkedListAddFirst3Bugs3x2x5,ase2016.nodecachinglinkedlist.LinkedListNode");
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
        setConfigKeyTypeScopes("ase2016.nodecachinglinkedlist.NodeCachingLinkedListAddFirst3Bugs3x2x5:1,ase2016.nodecachinglinkedlist.LinkedListNode:4");
        check(GENERIC_PROPERTIES,"addFirst_0",true);
	}

}
