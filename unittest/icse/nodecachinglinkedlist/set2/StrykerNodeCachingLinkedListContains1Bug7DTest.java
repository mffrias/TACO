package icse.nodecachinglinkedlist.set2;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerNodeCachingLinkedListContains1Bug7DTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "icse.nodecachinglinkedlist.set2.NodeCachingLinkedListContains1Bug7D";
	}
			
	public void test_containsTest() throws VizException {
        setConfigKeyRelevantClasses("icse.nodecachinglinkedlist.set2.NodeCachingLinkedListContains1Bug7D,icse.nodecachinglinkedlist.LinkedListNode");
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
        setConfigKeyTypeScopes("icse.nodecachinglinkedlist.set2.NodeCachingLinkedListContains1Bug7D:1,icse.nodecachinglinkedlist.LinkedListNode:4");
        check(GENERIC_PROPERTIES,"contains_0",true);
	}

}
