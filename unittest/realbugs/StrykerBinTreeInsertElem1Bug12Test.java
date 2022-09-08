package realbugs;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerBinTreeInsertElem1Bug12Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "realbugs.BinTreeInsertElem1Bug12";
	}

	public void test_insertElemTest() throws VizException {
		setConfigKeyRelevantClasses("realbugs.BinTreeInsertElem1Bug12,realbugs.BinTreeNode");
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
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("realbugs.BinTreeInsertElem1Bug12:1,realbugs.BinTreeNode:3");
		check(GENERIC_PROPERTIES,"insertElem_0",true);
	}

}
