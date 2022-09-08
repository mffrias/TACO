package icse.nodecachinglinkedlist;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerNodeCachingLinkedListGenericTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "pldi.nodecachinglinkedlist.NodeCachingLinkedListGeneric";
	}
			
	public void test_genericMethodTest() throws VizException {
        setConfigKeyRelevantClasses("pldi.nodecachinglinkedlist.NodeCachingLinkedListGeneric,pldi.nodecachinglinkedlist.LinkedListNode");
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
        setConfigKeyTypeScopes("pldi.nodecachinglinkedlist.NodeCachingLinkedListGeneric:,pldi.nodecachinglinkedlist.LinkedListNode:");
        check(GENERIC_PROPERTIES,"genericMethod_0",true);
	}

}
