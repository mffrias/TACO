package pldi;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerBinTreeContainsBug3Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "pldi.bintree.BinTreeContainsBug3";
	}

	

	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("pldi.bintree.BinTreeContainsBug3,pldi.bintree.BinTreeNode");
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
		setConfigKeyTypeScopes("pldi.bintree.BinTreeContainsBug3:1,pldi.bintree.BinTreeNode:3");
		check(GENERIC_PROPERTIES,"contains_0",false);
	}

}
