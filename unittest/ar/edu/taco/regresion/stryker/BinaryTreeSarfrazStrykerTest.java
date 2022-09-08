package ar.edu.taco.regresion.stryker;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;



public class BinaryTreeSarfrazStrykerTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.BinTree";
	}

	public void test_add() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.BinTree,roops.core.objects.BinTreeNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyInferScope(true);
	    setConfigKeyIntBithwidth(4);
	    setConfigKeyLoopUnroll(3);
	    setConfigKeyObjectScope(0);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyTypeScopes("roops.core.objects.BinTree:1,roops.core.objects.BinTreeNode:5");
		check(GENERIC_PROPERTIES,"insert_0",false);
	}
}
