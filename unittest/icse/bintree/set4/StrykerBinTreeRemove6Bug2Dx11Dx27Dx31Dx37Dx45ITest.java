package icse.bintree.set4;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerBinTreeRemove6Bug2Dx11Dx27Dx31Dx37Dx45ITest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "icse.bintree.set4.BinTreeRemove6Bug2Dx11Dx27Dx31Dx37Dx45I";
	}

	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("icse.bintree.set4.BinTreeRemove6Bug2Dx11Dx27Dx31Dx37Dx45I,icse.bintree.BinTreeNode");
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
		setConfigKeyTypeScopes("icse.bintree.set4.BinTreeRemove6Bug2Dx11Dx27Dx31Dx37Dx45I:1,icse.bintree.BinTreeNode:3");
		check(GENERIC_PROPERTIES,"remove_0",true);
	}

}
