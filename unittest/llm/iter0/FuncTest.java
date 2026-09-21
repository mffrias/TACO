package llm.iter0;


import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class FuncTest extends CollectionTestBase {

	public FuncTest(){}

	@Override
	protected String getClassToCheck() { return "llm.iter0.GenericFunc"; }

//	public String toString(){
//		return "Hi!!!!!!";
//	}

	public void test_genericMethodTest() throws VizException {
		setConfigKeyRelevantClasses("llm.iter0.GenericFunc");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(4);
		setConfigKeyLoopUnroll(3);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		setConfigKeyTypeScopes("llm.iter0.GenericFunc:1");
		check(GENERIC_PROPERTIES,"func(float, float)",true);
//		checkAndRunSpecIfFaulty(GENERIC_PROPERTIES,"func(float, float)");
//		iterativeCheckWithLLM(GENERIC_PROPERTIES,"func(float, float)");
	}

	public void test_checkMethodTest() throws VizException {
		setConfigKeyRelevantClasses("llm.iter0.GenericFunc");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(4);
		setConfigKeyLoopUnroll(3);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		setConfigKeyTypeScopes("llm.iter0.GenericFunc:3");
//		check(GENERIC_PROPERTIES,"check(int)",true);
		checkAndRunSpecIfFaulty(GENERIC_PROPERTIES,"func(float, float)");
//		iterativeCheckWithLLM(GENERIC_PROPERTIES,"func(float, float)");
	}
}