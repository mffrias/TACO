package icse.bintree.set5;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerBinTreeRemove6Bug2Dx4Dx21Dx34Ix37Dx44DTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "icse.bintree.set5.BinTreeRemove6Bug2Dx4Dx21Dx34Ix37Dx44D";
	}

	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("icse.bintree.set5.BinTreeRemove6Bug2Dx4Dx21Dx34Ix37Dx44D,icse.bintree.BinTreeNode");
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
		setConfigKeyTypeScopes("icse.bintree.set5.BinTreeRemove6Bug2Dx4Dx21Dx34Ix37Dx44D:1,icse.bintree.BinTreeNode:3");
		check(GENERIC_PROPERTIES,"remove_0",true);
	}

}
