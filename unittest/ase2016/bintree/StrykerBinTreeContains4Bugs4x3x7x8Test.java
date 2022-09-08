package ase2016.bintree;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerBinTreeContains4Bugs4x3x7x8Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ase2016.bintree.BinTreeContains4Bugs4x3x7x8";
	}

	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("ase2016.bintree.BinTreeContains4Bugs4x3x7x8,ase2016.bintree.BinTreeNode");
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
		setConfigKeyTypeScopes("ase2016.bintree.BinTreeContains4Bugs4x3x7x8:1,ase2016.bintree.BinTreeNode:3");
		check(GENERIC_PROPERTIES,"contains_0",true);
	}

}
