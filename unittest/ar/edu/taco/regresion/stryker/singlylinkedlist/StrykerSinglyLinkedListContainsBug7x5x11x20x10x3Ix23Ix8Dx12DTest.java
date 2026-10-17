package ar.edu.taco.regresion.stryker.singlylinkedlist;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerSinglyLinkedListContainsBug7x5x11x20x10x3Ix23Ix8Dx12DTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.SinglyLinkedListContainsBug7x5x11x20x10x3Ix23Ix8Dx12D";
	}

			
	public void test_insertBackTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.SinglyLinkedListContainsBug7x5x11x20x10x3Ix23Ix8Dx12D,roops.core.objects.SinglyLinkedListNode");
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
		setConfigKeyTypeScopes("roops.core.objects.SinglyLinkedListContainsBug7x5x11x20x10x3Ix23Ix8Dx12D:1,roops.core.objects.SinglyLinkedListNode:3");
		check(GENERIC_PROPERTIES,"roops_core_objects_SinglyLinkedListContainsBug7x5x11x20x10x3Ix23Ix8Dx12D_contains(java_lang_Object, java_lang_Object, java_lang_Object, java_lang_Object, java_lang_Object, roops_core_objects_SinglyLinkedListNode, roops_core_objects_SinglyLinkedListNode, roops_core_objects_SinglyLinkedListNode, roops_core_objects_SinglyLinkedListNode, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean, boolean)",true);
	}



}
