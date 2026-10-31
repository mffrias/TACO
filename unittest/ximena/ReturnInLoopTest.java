package ximena;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class ReturnInLoopTest extends CollectionTestBase {

    @Override
    protected String getClassToCheck() {
        return "ximena.ReturnInLoop";
    }



    public void test_firstPositive() throws VizException {
        setConfigKeyRelevantClasses("ximena.ReturnInLoop");
        setConfigKeyRelevancyAnalysis(true);
        setConfigKeyCheckNullDereference(true);
        setConfigKeyUseJavaArithmetic(true);
        setConfigKeyInferScope(true);
        setConfigKeyObjectScope(0);
        setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(1);
        setConfigKeySkolemizeInstanceInvariant(true);
        setConfigKeySkolemizeInstanceAbstraction(false);
        setConfigKeyGenerateUnitTestCase(true);
        setConfigKeyAttemptToCorrectBug(false);
        setConfigKeyMaxStrykerMethodsPerFile(1);
        setConfigKeyRemoveQuantifiers(true);
        setConfigKeyUseJavaSBP(false);
        setConfigKeyUseTightUpperBounds(false);
        setConfigKeyTypeScopes("ximena.ReturnInLoop:5");
        check(GENERIC_PROPERTIES,"ximena_ReturnInLoop_firstPositive(int)",false);
    }

}
