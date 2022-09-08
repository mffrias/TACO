package ar.edu.taco.binReachTest;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class binaryReachTest extends CollectionTestBase { 

		@Override
		protected String getClassToCheck() {
			return "ar.edu.taco.reachTest.reachTest";
		}
		
		public void test_testReachMethod_OK() throws VizException {
	        setConfigKeyRelevantClasses("ar.edu.taco.reachTest.reachTest");
			setConfigKeyRelevancyAnalysis(true);
			setConfigKeyCheckNullDereference(true);
			setConfigKeyUseJavaArithmetic(false);// fun_set_size
			setConfigKeyInferScope(false);
		    setConfigKeyIntBithwidth(5);
		    setConfigKeyLoopUnroll(7);
		    setConfigKeyObjectScope(0);
			setConfigKeySkolemizeInstanceInvariant(true);
			setConfigKeySkolemizeInstanceAbstraction(true);
			setConfigKeyGenerateUnitTestCase(true);
			setConfigKeyAttemptToCorrectBug(false);
			setConfigKeyRemoveQuantifiers(true);
			setConfigKeyUseJavaSBP(true);
			setConfigKeyUseTightUpperBounds(true);
			setConfigKeyMaxStrykerMethodsPerFile(50);
			setConfigKeyTypeScopes("ar.edu.taco.reachTest.reachTest:5");
			check(GENERIC_PROPERTIES,"testReachMethod_0",false);
		}
}
