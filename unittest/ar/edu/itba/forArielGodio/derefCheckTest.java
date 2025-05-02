package ar.edu.itba.forArielGodio;
//package ar.edu.taco.derefCheck;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class derefCheckTest extends CollectionTestBase {

    public derefCheckTest() {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected String getClassToCheck() {
        return "forArielGodio.DerefCheck";
    }

    public void test_derefCheckTest1() throws VizException {
        setConfigKeyRelevantClasses("forArielGodio.DerefCheck");
        setConfigKeyRelevancyAnalysis(true);
        setConfigKeyCheckNullDereference(true);
        setConfigKeyCheckArithmeticException(false);
        setConfigKeyUseJavaArithmetic(false);
        setConfigKeySkolemizeInstanceInvariant(false);
        setConfigKeySkolemizeInstanceAbstraction(false);
        setConfigKeyRemoveQuantifiers(true);
        // Infer-Scope
        setConfigKeyInferScope(true);
        setConfigKeyTypeScopes("forArielGodio.DerefCheck:1");
        setConfigKeyLoopUnroll(1);
        // SBP+BOUND
        setConfigKeyUseJavaSBP(false);
        setConfigKeyUseTightUpperBounds(false);
        // JUNIT
        setConfigKeyGenerateUnitTestCase(true);

        check(GENERIC_PROPERTIES,"accessDerefCheck()", false);
    }
}
