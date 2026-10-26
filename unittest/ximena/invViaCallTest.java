package ximena;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class invViaCallTest extends CollectionTestBase {

    @Override
    protected String getClassToCheck() {
        return "ximena.InvViaCall";
    }



    public void test_breakInvViaCall() throws VizException {
        setConfigKeyRelevantClasses("ximena.InvViaCall");
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
        setConfigKeyTypeScopes("ximena.InvViaCall:1");
        check(GENERIC_PROPERTIES,"ximena_InvViaCall_breakViaCall()",false);
    }

    public void test_breakInvDirect() throws VizException {
        setConfigKeyRelevantClasses("ximena.InvViaCall");
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
        setConfigKeyTypeScopes("ximena.InvViaCall:1");
        check(GENERIC_PROPERTIES,"ximena_InvViaCall_breakDirect()",false);
    }

}
