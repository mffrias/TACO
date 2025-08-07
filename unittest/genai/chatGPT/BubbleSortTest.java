package genai.chatGPT;
import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class BubbleSortTest  extends CollectionTestBase {

    @Override
    protected String getClassToCheck() {
        return "genai.chatGPT.BubbleSort";
    }

    private void config() throws VizException {
        // To solve imports
        setConfigKeyRelevantClasses("genai.chatGPT.BubbleSort");

        // Scope
        setConfigKeyUseJavaArithmetic(true);
        setConfigKeyIntBithwidth(2);
        setConfigKeyInferScope(true);
        setConfigKeyObjectScope(0);
        setConfigKeyLoopUnroll(5);
        setConfigKeyTypeScopes("genai.chatGPT.BubbleSort:1");

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
        check (GENERIC_PROPERTIES, "bubble(int[])", true);
    }

}