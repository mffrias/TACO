package pldi;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerNodeCachingLinkedListAddFirstBug6Ix4Ix5Ix7Ix2Dx5Dx3x6DTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "pldi.nodecachinglinkedlist.NodeCachingLinkedListAddFirstBug6Ix4Ix5Ix7Ix2Dx5Dx3x6D";
	}

	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("pldi.nodecachinglinkedlist.NodeCachingLinkedListAddFirstBug6Ix4Ix5Ix7Ix2Dx5Dx3x6D,pldi.nodecachinglinkedlist.LinkedListNode");
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
		setConfigKeyTypeScopes("pldi.nodecachinglinkedlist.NodeCachingLinkedListAddFirstBug6Ix4Ix5Ix7Ix2Dx5Dx3x6D:1,pldi.nodecachinglinkedlist.LinkedListNode:4");
		check(GENERIC_PROPERTIES,"addFirst_0",false);
	}
	
}
