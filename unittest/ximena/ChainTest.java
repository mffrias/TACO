package ximena;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class ChainTest extends CollectionTestBase {

    @Override
    protected String getClassToCheck() {
        return "ximena.Chain";
    }



    public void test_walkViaCall() throws VizException {
        setConfigKeyRelevantClasses("ximena.Chain");
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
        setConfigKeyTypeScopes("ximena.Chain:5");
        check(GENERIC_PROPERTIES,"ximena_Chain_walkViaCall(ximena_Chain)",false);
    }

    public void test_test_walkViaField() throws VizException {
        setConfigKeyRelevantClasses("ximena.Chain");
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
        setConfigKeyTypeScopes("ximena.Chain:5");
        check(GENERIC_PROPERTIES,"ximena_Chain_walkViaField(ximena_Chain)",false);
    }


    public void test_testingNameNormalizer() throws VizException {
        setConfigKeyRelevantClasses("ximena.Chain");
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
        setConfigKeyTypeScopes("ximena.Chain:5");
        check(GENERIC_PROPERTIES,"ximena_Chain_testingNameNormalizer(int)",false);
    }

}
