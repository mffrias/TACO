package pldi;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerNodeCachingLinkedListContainsBug3x2x6Dx1x4x8Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.NodeCachingLinkedList";
	}

	public void test_containsTest() throws VizException {
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
		check(GENERIC_PROPERTIES,"contains_0",true);
	}
	
}
