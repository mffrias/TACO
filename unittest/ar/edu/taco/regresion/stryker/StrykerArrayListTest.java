package ar.edu.taco.regresion.stryker;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StrykerArrayListTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.ArrayList";
	}

	public void test_indexOfTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.ArrayList");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false); 
		setConfigKeyInferScope(true);
	    setConfigKeyIntBithwidth(5);
	    setConfigKeyLoopUnroll(16);
	    setConfigKeyObjectScope(0);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		setConfigKeyMaxStrykerMethodsPerFile(70);
		setConfigKeyTypeScopes("roops.core.objects.ArrayList:1,java.lang.Object:12,java.lang.ObjectArray:1");
		check(GENERIC_PROPERTIES,"indexOf_0", false);
	}
	
	

		
	public void test_addTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.ArrayList");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyInferScope(true);
	    setConfigKeyIntBithwidth(5);
	    setConfigKeyLoopUnroll(15);
	    setConfigKeyObjectScope(0);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		setConfigKeyMaxStrykerMethodsPerFile(50);
//		setCondigKeyBuildJavaTrace(true);	
		setConfigKeyTypeScopes("roops.core.objects.ArrayList:1,java.lang.Object:15,java.lang.ObjectArray:2");
		check(GENERIC_PROPERTIES, "add_0", true);
	}

}