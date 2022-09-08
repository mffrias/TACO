package paperSATinSE;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class BTtest extends CollectionTestBase {

    @Override
    protected String getClassToCheck() {
        return "paperSATinSE.BT";
    }
    
    public void test_getRoot() throws VizException {
        setConfigKeyRelevantClasses("paperSATinSE.BT,paperSATinSE.BTnode");
        setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyCheckArithmeticException(false);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(4);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(false);
        setConfigKeyTypeScopes("paperSATinSE.BT:1,paperSATinSE.BTnode:5");
        check(GENERIC_PROPERTIES, "getRoot_0", true);
    }

}
