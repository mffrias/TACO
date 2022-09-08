package ase2016.bintree;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerBinTreeInsert4Bugs6x4x21x18Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ase2016.bintree.BinTreeInsert4Bugs6x4x21x18";
	}

	public void test_insertTest() throws VizException {
		setConfigKeyRelevantClasses("ase2016.bintree.BinTreeInsert4Bugs6x4x21x18,ase2016.bintree.BinTreeNode");
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
		setConfigKeyTypeScopes("ase2016.bintree.BinTreeInsert4Bugs6x4x21x18:1,ase2016.bintree.BinTreeNode:3");
		check(GENERIC_PROPERTIES,"insert_0",true);
	}

}
