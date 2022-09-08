package ar.edu.taco.skunk;


import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class SListTest extends CollectionTestBase {

	public SListTest() {
		// TODO Auto-generated constructor stub
	}


	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.skunk.SList";
	}
	
	public void test_SListTest1() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.skunk.SList, ar.edu.taco.skunk.Node");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyCheckArithmeticException(false);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyRemoveQuantifiers(true);
		// Infer-Scope
		setConfigKeyInferScope(true);
		setConfigKeyTypeScopes("ar.edu.taco.skunk.SList:1, ar.edu.taco.skunk.Node:7");
		setConfigKeyLoopUnroll(5);
		// SBP+BOUND
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		// JUNIT
		setConfigKeyGenerateUnitTestCase(true);

		check(GENERIC_PROPERTIES,"hasElement_0", false);
	}

	
}
