package qu.edu.qa;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;



public class EuclidTest extends CollectionTestBase {

		@Override
		protected String getClassToCheck() {
			return "qu.edu.qa.Euclid";
		}
		
		public void test_Euclides() throws VizException {
			setConfigKeyRelevantClasses("qu.edu.qa.Euclid");
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

			check(GENERIC_PROPERTIES,"Euclides_0", false);
		}
		
	
	
}
