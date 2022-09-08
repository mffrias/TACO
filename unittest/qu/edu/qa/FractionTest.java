package qu.edu.qa;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class FractionTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "qu.edu.qa.Fraction";
	}
	
	public void test_getNum() throws VizException {
		setConfigKeyRelevantClasses("qu.edu.qa.Fraction");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyRemoveQuantifiers(true);
		// Infer-Scope
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(1);
		setConfigKeyLoopUnroll(1);
		// SBP+BOUND
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		// JUNIT
		setConfigKeyGenerateUnitTestCase(false);

		check(GENERIC_PROPERTIES,"getNum_0", false);
	}
	
	
	public void test_getDenum() throws VizException {
		setConfigKeyRelevantClasses("qu.edu.qa.Fraction");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyRemoveQuantifiers(true);
		// Infer-Scope
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(1);
		setConfigKeyLoopUnroll(1);
		// SBP+BOUND
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		// JUNIT
		setConfigKeyGenerateUnitTestCase(false);

		check(GENERIC_PROPERTIES,"getDenum_0", false);
	}
	
	
	
	public void test_mul() throws VizException {
		setConfigKeyRelevantClasses("qu.edu.qa.Fraction");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyRemoveQuantifiers(true);
	    setConfigKeyIntBithwidth(5);

		// Infer-Scope
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(1);
		setConfigKeyLoopUnroll(1);
		
		// SBP+BOUND
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		// JUNIT
		setConfigKeyGenerateUnitTestCase(true);

		check(GENERIC_PROPERTIES,"mul_0", false);
	}
	
	
	
	public void test_test() throws VizException {
		setConfigKeyRelevantClasses("qu.edu.qa.Fraction");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyRemoveQuantifiers(false);
	    setConfigKeyIntBithwidth(2);

		// Infer-Scope
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(1);
		setConfigKeyLoopUnroll(1);
		
		// SBP+BOUND
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		
		// JUNIT
		setConfigKeyGenerateUnitTestCase(true);

		check(GENERIC_PROPERTIES,"test_0", false);
	}
	
	
	public void test_div() throws VizException {
		setConfigKeyRelevantClasses("qu.edu.qa.Fraction");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyRemoveQuantifiers(true);
	    setConfigKeyIntBithwidth(2);

		// Infer-Scope
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(1);
		setConfigKeyLoopUnroll(1);
		
		// SBP+BOUND
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		
		// JUNIT
		setConfigKeyGenerateUnitTestCase(true);

		check(GENERIC_PROPERTIES,"div_0", false);
	}
	
	
	

	
	public void test_toDouble() throws VizException {
		setConfigKeyRelevantClasses("qu.edu.qa.Fraction");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyRemoveQuantifiers(true);
		// Infer-Scope
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(1);
		setConfigKeyLoopUnroll(1);
		// SBP+BOUND
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		// JUNIT
		setConfigKeyGenerateUnitTestCase(true);

		check(GENERIC_PROPERTIES,"toDouble_0", false);
	}
	
	
	
}
	