package ximena;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class invCall3Test extends CollectionTestBase {

    @Override
    protected String getClassToCheck() {
        return "ximena.InvCall3";
    }



    public void test_breakThenThrow() throws VizException {
        setConfigKeyRelevantClasses("ximena.InvCall3");
        setConfigKeyRelevancyAnalysis(true);
        setConfigKeyCheckNullDereference(true);
        setConfigKeyUseJavaArithmetic(true);
        setConfigKeyInferScope(true);
        setConfigKeyObjectScope(0);
        setConfigKeyIntBithwidth(6);
        setConfigKeyLoopUnroll(4);
        setConfigKeySkolemizeInstanceInvariant(true);
        setConfigKeySkolemizeInstanceAbstraction(false);
        setConfigKeyGenerateUnitTestCase(true);
        setConfigKeyAttemptToCorrectBug(false);
        setConfigKeyMaxStrykerMethodsPerFile(1);
        setConfigKeyRemoveQuantifiers(true);
        setConfigKeyUseJavaSBP(false);
        setConfigKeyUseTightUpperBounds(false);
        setConfigKeyTypeScopes("ximena.InvViaCall3:1");
        check(GENERIC_PROPERTIES,"ximena_InvCall3_breakThenThrow(int)",false);
    }


}
