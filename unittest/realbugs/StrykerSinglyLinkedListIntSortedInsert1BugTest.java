package realbugs;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;
import mujava.api.Configuration;

public class StrykerSinglyLinkedListIntSortedInsert1BugTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "realbugs.SinglyLinkedListIntSortedInsert1Bug";
	}

			
	public void test_sortedInsertTest() throws VizException {
		setConfigKeyRelevantClasses("realbugs.SinglyLinkedListIntSortedInsert1Bug,realbugs.SinglyLinkedListNodeInt");
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
		setConfigKeyTypeScopes("realbugs.SinglyLinkedListIntSortedInsert1Bug:1,realbugs.SinglyLinkedListNodeInt:4");
		check(GENERIC_PROPERTIES,"sortedInsert_0",true);
	}



}
