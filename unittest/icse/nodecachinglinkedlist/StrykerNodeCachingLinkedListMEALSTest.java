package icse.nodecachinglinkedlist;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerNodeCachingLinkedListMEALSTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "icse.nodecachinglinkedlist.NodeCachingLinkedListMEALSDemo";
	}
			
	public void test_removeTest() throws VizException {
        setConfigKeyRelevantClasses("icse.nodecachinglinkedlist.NodeCachingLinkedListMEALSDemo,icse.nodecachinglinkedlist.LinkedListNode");
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
        setConfigKeyTypeScopes("icse.nodecachinglinkedlist.NodeCachingLinkedListMEALSDemo:1,icse.nodecachinglinkedlist.LinkedListNode:4");
        check(GENERIC_PROPERTIES,"remove_0",true);
	}

}
