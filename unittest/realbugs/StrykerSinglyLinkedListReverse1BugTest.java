package realbugs;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;
import mujava.api.Configuration;

public class StrykerSinglyLinkedListReverse1BugTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "realbugs.SinglyLinkedListReverse1Bug";
	}

			
	public void test_reverseTest() throws VizException {
		setConfigKeyRelevantClasses("realbugs.SinglyLinkedListReverse1Bug,realbugs.SinglyLinkedListNode");
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
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("realbugs.SinglyLinkedListReverse1Bug:1,realbugs.SinglyLinkedListNode:3");
		check(GENERIC_PROPERTIES,"reverse_0",true);
	}


	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("realbugs.SinglyLinkedListReverse1Bug,realbugs.SinglyLinkedListNode");
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
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("realbugs.SinglyLinkedListReverse1Bug:1,realbugs.SinglyLinkedListNode:3");
		check(GENERIC_PROPERTIES,"contains",true);
	}

}
