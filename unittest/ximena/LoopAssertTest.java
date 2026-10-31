package ximena;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class LoopAssertTest extends CollectionTestBase {

    @Override
    protected String getClassToCheck() {
        return "ximena.LoopAssert";
    }



    public void test_loop() throws VizException {
        setConfigKeyRelevantClasses("ximena.LoopAssert");
        setConfigKeyRelevancyAnalysis(true);
        setConfigKeyCheckNullDereference(true);
        setConfigKeyUseJavaArithmetic(true);
        setConfigKeyInferScope(true);
        setConfigKeyObjectScope(0);
        setConfigKeyIntBithwidth(6);
        setConfigKeyLoopUnroll(3);
        setConfigKeySkolemizeInstanceInvariant(true);
        setConfigKeySkolemizeInstanceAbstraction(false);
        setConfigKeyGenerateUnitTestCase(true);
        setConfigKeyAttemptToCorrectBug(false);
        setConfigKeyMaxStrykerMethodsPerFile(1);
        setConfigKeyRemoveQuantifiers(true);
        setConfigKeyUseJavaSBP(false);
        setConfigKeyUseTightUpperBounds(false);
        setConfigKeyTypeScopes("ximena.LoopResult:5");
        check(GENERIC_PROPERTIES,"ximena_LoopAssert_loop(int)",false);
    }




}
