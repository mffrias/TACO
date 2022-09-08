package ar.edu.taco.regresion.stryker;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;


public class StrykerArithmFunny extends CollectionTestBase {

	

		@Override
		protected String getClassToCheck() {
			return "roops.core.objects.arithmFunny";
		}


		
		public void test_arith() throws VizException {
			setConfigKeyRelevantClasses("roops.core.objects.arithmFunny");
			setConfigKeyRelevancyAnalysis(true);
			setConfigKeyCheckNullDereference(true);
			setConfigKeyUseJavaArithmetic(true); 
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
			setConfigKeyTypeScopes("java.lang.Object:1");
			check(GENERIC_PROPERTIES,"arith_0", false);
		}


}
