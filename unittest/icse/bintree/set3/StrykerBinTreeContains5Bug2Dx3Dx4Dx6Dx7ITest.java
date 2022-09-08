package icse.bintree.set3;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerBinTreeContains5Bug2Dx3Dx4Dx6Dx7ITest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "icse.bintree.set3.BinTreeContains5Bug2Dx3Dx4Dx6Dx7I";
	}

	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("icse.bintree.set3.BinTreeContains5Bug2Dx3Dx4Dx6Dx7I,icse.bintree.BinTreeNode");
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
		setConfigKeyTypeScopes("icse.bintree.set3.BinTreeContains5Bug2Dx3Dx4Dx6Dx7I:1,icse.bintree.BinTreeNode:3");
		check(GENERIC_PROPERTIES,"contains_0",true);
	}

}
