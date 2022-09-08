package icse.singlylinkedlist.set3;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerSinglyLinkedListContains6Bug7Dx10Dx11Dx12Dx20Dx21DTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "icse.singlylinkedlist.set3.SinglyLinkedListContains6Bug7Dx10Dx11Dx12Dx20Dx21D";
	}

			
	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("icse.singlylinkedlist.set3.SinglyLinkedListContains6Bug7Dx10Dx11Dx12Dx20Dx21D,icse.singlylinkedlist.SinglyLinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyCheckArithmeticException(true);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(4);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(true);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("icse.singlylinkedlist.set3.SinglyLinkedListContains6Bug7Dx10Dx11Dx12Dx20Dx21D:1,icse.singlylinkedlist.SinglyLinkedListNode:3");
		check(GENERIC_PROPERTIES,"contains_0",true);
	}



}
