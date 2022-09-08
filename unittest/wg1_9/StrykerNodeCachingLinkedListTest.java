package wg1_9;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerNodeCachingLinkedListTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "wg1point9.NodeCachingLinkedList";
	}
			
	public void test_removeTest() throws VizException {
        setConfigKeyRelevantClasses("wg1point9.NodeCachingLinkedList,wg1point9.LinkedListNode");
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
        setConfigKeyTypeScopes("wg1point9.NodeCachingLinkedList:1,wg1point9.LinkedListNode:4");
        check(GENERIC_PROPERTIES,"remove_0",true);
	}

}
