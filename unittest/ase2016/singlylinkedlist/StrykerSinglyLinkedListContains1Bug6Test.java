package ase2016.singlylinkedlist;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;
import mujava.api.Configuration;

public class StrykerSinglyLinkedListContains1Bug6Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ase2016.singlylinkedlist.SinglyLinkedListContains1Bug6";
	}

			
	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("ase2016.singlylinkedlist.SinglyLinkedListContains1Bug6,ase2016.singlylinkedlist.SinglyLinkedListNode");
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
		setConfigKeyTypeScopes("ase2016.singlylinkedlist.SinglyLinkedListContains1Bug6:1,ase2016.singlylinkedlist.SinglyLinkedListNode:3");
		check(GENERIC_PROPERTIES,"contains_0",true);
	}



}
