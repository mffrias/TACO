package ar.edu.taco.regresion.stryker;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class ZuneTest extends CollectionTestBase {

		@Override
		protected String getClassToCheck() {
			return "examples.stryker.Zune";
		}

		public void test_zuneTest() throws VizException {
			setConfigKeyRelevantClasses("examples.stryker.Zune");
			setConfigKeyRelevancyAnalysis(true);
			setConfigKeyCheckNullDereference(true);
			setConfigKeyUseJavaArithmetic(false); 
			setConfigKeyInferScope(true);
		    setConfigKeyIntBithwidth(5);
		    setConfigKeyLoopUnroll(20);
		    setConfigKeyObjectScope(0);
			setConfigKeySkolemizeInstanceInvariant(true);
			setConfigKeySkolemizeInstanceAbstraction(true);
			setConfigKeyGenerateUnitTestCase(true);
			setConfigKeyAttemptToCorrectBug(true);
			setConfigKeyRemoveQuantifiers(true);
			setConfigKeyUseJavaSBP(false);
			setConfigKeyUseTightUpperBounds(false);
			setConfigKeyMaxStrykerMethodsPerFile(1);
			setConfigKeyTypeScopes("examples.stryker.Zune:1");
			check(GENERIC_PROPERTIES,"computeYear_0", false);
		}

		
		
		
	}