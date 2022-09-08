package ase2016.singlylinkedlist;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerSinglyLinkedListInsertBack1Bug11Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ase2016.singlylinkedlist.SinglyLinkedListInsertBack1Bug11";
	}

			
	public void test_insertBackTest() throws VizException {
		setConfigKeyRelevantClasses("ase2016.singlylinkedlist.SinglyLinkedListInsertBack1Bug11,ase2016.singlylinkedlist.SinglyLinkedListNode");
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
		setConfigKeyTypeScopes("ase2016.singlylinkedlist.SinglyLinkedListInsertBack1Bug11:1,ase2016.singlylinkedlist.SinglyLinkedListNode:3");
		check(GENERIC_PROPERTIES,"insertBack_0",true);
	}



}
