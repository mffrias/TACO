package icse.nodecachinglinkedlist.set3;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerNodeCachingLinkedListRemove3Bug16Dx31Dx39DTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "icse.nodecachinglinkedlist.set3.NodeCachingLinkedListRemove3Bug16Dx31Dx39D";
	}
			
	public void test_removeTest() throws VizException {
        setConfigKeyRelevantClasses("icse.nodecachinglinkedlist.set3.NodeCachingLinkedListRemove3Bug16Dx31Dx39D,icse.nodecachinglinkedlist.LinkedListNode");
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
        setConfigKeyTypeScopes("icse.nodecachinglinkedlist.set3.NodeCachingLinkedListRemove3Bug16Dx31Dx39D:1,icse.nodecachinglinkedlist.LinkedListNode:4");
        check(GENERIC_PROPERTIES,"remove_0",true);
	}

}
