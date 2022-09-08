package ar.edu.taco.regresion.stryker.singlylinkedlist;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerSinglyLinkedListContainsBug7x5x11x20x10x3Ix23Ix8Dx12Dx14DTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.SinglyLinkedListContainsBug7x5x11x20x10x3Ix23Ix8Dx12Dx14D";
	}

			
	public void test_insertBackTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.SinglyLinkedListContainsBug7x5x11x20x10x3Ix23Ix8Dx12Dx14D,roops.core.objects.SinglyLinkedListNode");
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
		setConfigKeyTypeScopes("roops.core.objects.SinglyLinkedListContainsBug7x5x11x20x10x3Ix23Ix8Dx12Dx14D:1,roops.core.objects.SinglyLinkedListNode:3");
		check(GENERIC_PROPERTIES,"contains_0",true);
	}



}
