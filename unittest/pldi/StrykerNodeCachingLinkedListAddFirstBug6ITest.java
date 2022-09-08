package pldi;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerNodeCachingLinkedListAddFirstBug6ITest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.NodeCachingLinkedList";
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
		setConfigKeyAttemptToCorrectBug(true);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("roops.core.objects.NodeCachingLinkedList:1,roops.core.objects.LinkedListNode:4");
		check(GENERIC_PROPERTIES,"addFirst_0",true);
	}
	
}
