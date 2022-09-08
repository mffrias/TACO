package icse.nodecachinglinkedlist.set3;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerNodeCachingLinkedListContains6Bug1Dx2Dx3Dx4Dx5Dx10DTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "icse.nodecachinglinkedlist.set3.NodeCachingLinkedListContains6Bug1Dx2Dx3Dx4Dx5Dx10D";
	}
			
	public void test_containsTest() throws VizException {
        setConfigKeyRelevantClasses("icse.nodecachinglinkedlist.set3.NodeCachingLinkedListContains6Bug1Dx2Dx3Dx4Dx5Dx10D,icse.nodecachinglinkedlist.LinkedListNode");
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
        setConfigKeyTypeScopes("icse.nodecachinglinkedlist.set3.NodeCachingLinkedListContains6Bug1Dx2Dx3Dx4Dx5Dx10D:1,icse.nodecachinglinkedlist.LinkedListNode:4");
        check(GENERIC_PROPERTIES,"contains_0",true);
	}

}
