package realbugs;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerBinTreeIsBST1BugTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "realbugs.BinTreeIsBST1Bug";
	}

	public void test_isBSTTest() throws VizException {
		setConfigKeyRelevantClasses("realbugs.BinTreeIsBST1Bug,realbugs.BinTreeNode");
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
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("realbugs.BinTreeIsBST1Bug:1,realbugs.BinTreeNode:3");
		check(GENERIC_PROPERTIES,"isBST_0",true);
	}

}
