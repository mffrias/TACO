package ase2016.nodecachinglinkedlist;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerNodeCachingLinkedListRemove4Bugs23x12x43x29Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ase2016.nodecachinglinkedlist.NodeCachingLinkedListRemove4Bugs23x12x43x29";
	}
			
	public void test_removeTest() throws VizException {
        setConfigKeyRelevantClasses("ase2016.nodecachinglinkedlist.NodeCachingLinkedListRemove4Bugs23x12x43x29,ase2016.nodecachinglinkedlist.LinkedListNode");
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
        setConfigKeyTypeScopes("ase2016.nodecachinglinkedlist.NodeCachingLinkedListRemove4Bugs23x12x43x29:1,ase2016.nodecachinglinkedlist.LinkedListNode:4");
        check(GENERIC_PROPERTIES,"remove_0",true);
	}

}
