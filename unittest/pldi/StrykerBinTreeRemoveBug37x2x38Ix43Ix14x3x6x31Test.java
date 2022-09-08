package pldi;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerBinTreeRemoveBug37x2x38Ix43Ix14x3x6x31Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "pldi.bintree.BinTreeRemoveBug37x2x38Ix43Ix14x3x6x31";
	}

	

	public void test_removeTest() throws VizException {
		setConfigKeyRelevantClasses("pldi.bintree.BinTreeRemoveBug37x2x38Ix43Ix14x3x6x31,pldi.bintree.BinTreeNode");
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
		setConfigKeyTypeScopes("pldi.bintree.BinTreeRemoveBug37x2x38Ix43Ix14x3x6x31:1,pldi.bintree.BinTreeNode:3");
		check(GENERIC_PROPERTIES,"remove_0",true);
	}

}
