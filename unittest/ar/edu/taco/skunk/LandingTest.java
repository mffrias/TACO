package ar.edu.taco.skunk ;


import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class LandingTest extends CollectionTestBase {

	public LandingTest() {
		// TODO Auto-generated constructor stub
	}


	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.skunk.Landing";
	}
	
	public void test_LandingTest1() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.skunk.Landing");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyCheckArithmeticException(false);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyRemoveQuantifiers(true);
		// Infer-Scope
		setConfigKeyInferScope(true);
		setConfigKeyTypeScopes("ar.edu.taco.skunk.Landing:1");
		setConfigKeyLoopUnroll(1);
		// SBP+BOUND
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		// JUNIT
		setConfigKeyGenerateUnitTestCase(true);

		check(GENERIC_PROPERTIES,"initLandingOK(float)", false);
	}


	public void test_LandingTest2() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.skunk.Landing");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyCheckArithmeticException(false);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyRemoveQuantifiers(true);
		// Infer-Scope
		setConfigKeyInferScope(true);
		setConfigKeyTypeScopes("ar.edu.taco.skunk.Landing:1");
		setConfigKeyLoopUnroll(1);
		// SBP+BOUND
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		// JUNIT
		setConfigKeyGenerateUnitTestCase(true);

		check(GENERIC_PROPERTIES,"aerodynamic1(int, int)", false);
	}

	
	public void test_LandingTest3() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.skunk.Landing");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyCheckArithmeticException(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyRemoveQuantifiers(true);
		// Infer-Scope
		setConfigKeyInferScope(true);
		setConfigKeyTypeScopes("ar.edu.taco.skunk.Landing:1");
		setConfigKeyLoopUnroll(1);
		// SBP+BOUND
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		// JUNIT
		setConfigKeyGenerateUnitTestCase(true);

		check(GENERIC_PROPERTIES,"testDivZero(int, int, int)", false);
	}

	public void test_LandingTest4() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.skunk.Landing");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyCheckArithmeticException(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyRemoveQuantifiers(true);
		// Infer-Scope
		setConfigKeyInferScope(true);
		setConfigKeyTypeScopes("ar.edu.taco.skunk.Landing:7");
		setConfigKeyLoopUnroll(1);
		// SBP+BOUND
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		// JUNIT
		setConfigKeyGenerateUnitTestCase(true);

		check(GENERIC_PROPERTIES,"testArrayObject_0", false);
	}


	public void test_factorial() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.skunk.Landing");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyCheckArithmeticException(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyRemoveQuantifiers(true);
		// Infer-Scope
		setConfigKeyInferScope(true);
		setConfigKeyTypeScopes("ar.edu.taco.skunk.Landing:1");
		setConfigKeyLoopUnroll(1);
		// SBP+BOUND
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		// JUNIT
		setConfigKeyGenerateUnitTestCase(true);

		check(GENERIC_PROPERTIES,"factorial_0", false);
	}


}
