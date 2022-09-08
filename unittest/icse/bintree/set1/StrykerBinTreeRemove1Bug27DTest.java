package icse.bintree.set1;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerBinTreeRemove1Bug27DTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "icse.bintree.set1.BinTreeRemove1Bug27D";
	}

	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("icse.bintree.set1.BinTreeRemove1Bug27D,icse.bintree.BinTreeNode");
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
		setConfigKeyTypeScopes("icse.bintree.set1.BinTreeRemove1Bug27D:1,icse.bintree.BinTreeNode:3");
		check(GENERIC_PROPERTIES,"remove_0",true);
	}

}
