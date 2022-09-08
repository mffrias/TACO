package icse.nodecachinglinkedlist.set5;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerNodeCachingLinkedListAddFirst2Bug7Ix8DTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "icse.nodecachinglinkedlist.set5.NodeCachingLinkedListAddFirst2Bug7Ix8D";
	}
			
	public void test_addFirstTest() throws VizException {
        setConfigKeyRelevantClasses("icse.nodecachinglinkedlist.set5.NodeCachingLinkedListAddFirst2Bug7Ix8D,icse.nodecachinglinkedlist.LinkedListNode");
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
        setConfigKeyTypeScopes("icse.nodecachinglinkedlist.set5.NodeCachingLinkedListAddFirst2Bug7Ix8D:1,icse.nodecachinglinkedlist.LinkedListNode:4");
        check(GENERIC_PROPERTIES,"addFirst_0",true);
	}

}
