package genai.gemini;
import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class FactorialTest  extends CollectionTestBase {

    @Override
    protected String getClassToCheck() {
        return "gemini.Factorial";
    }

    private void config() throws VizException {
        // To solve imports
        setConfigKeyRelevantClasses("gemini.Factorial");

        // Scope
        setConfigKeyUseJavaArithmetic(true);
        setConfigKeyIntBithwidth(2);
        setConfigKeyInferScope(true);
        setConfigKeyObjectScope(0);
        setConfigKeyLoopUnroll(5);
        setConfigKeyTypeScopes("gemini.Factorial:1");

        // Always true
        setConfigKeyRelevancyAnalysis(true);
        setConfigKeyCheckNullDereference(true);
        setConfigKeyGenerateUnitTestCase(true);
        setConfigKeyRemoveQuantifiers(true);

        // Always false
        setConfigKeyUseJavaSBP(false);
        setConfigKeyUseTightUpperBounds(false);
        setConfigKeyAttemptToCorrectBug(false);

        // Ignored
        setConfigKeySkolemizeInstanceInvariant(true);
        setConfigKeySkolemizeInstanceAbstraction(false);
        setConfigKeyMaxStrykerMethodsPerFile(1);
    }

    public void test_genericMethod2Test() throws VizException {
        config();
        check (GENERIC_PROPERTIES, "factorial(int)", true);
    }

}
