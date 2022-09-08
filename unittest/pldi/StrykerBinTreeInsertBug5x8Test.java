package pldi;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerBinTreeInsertBug5x8Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "pldi.bintree.BinTreeInsertBug5x8";
	}

	

	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("pldi.bintree.BinTreeInsertBug5x8,pldi.bintree.BinTreeNode");
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
		setConfigKeyTypeScopes("pldi.bintree.BinTreeInsertBug5x8:1,pldi.bintree.BinTreeNode:4");
		check(GENERIC_PROPERTIES,"insert_0",false);
	}

}
