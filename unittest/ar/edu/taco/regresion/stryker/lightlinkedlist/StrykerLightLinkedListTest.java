package ar.edu.taco.regresion.stryker.lightlinkedlist;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerLightLinkedListTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.stryker.LightLinkedList";
	}

	public void test_contains() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.stryker.lightlinkedlist.LinkedListNode, ar.edu.taco.stryker.lightlinkedlist.LightLinkedList");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyLoopUnroll(5);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyJMLObjectSequenceToAlloySequence(true);
		setConfigKeyJMLObjectSetToAlloySet(true);
		runAndCheck(GENERIC_PROPERTIES,"contains_0", true);
	}

}
