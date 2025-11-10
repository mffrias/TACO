package genai.chatGPT;
import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class Find_First_Sorted_Test  extends CollectionTestBase {

    @Override
    protected String getClassToCheck() {
        return "genai.chatGPT.Find_First_Sorted";
    }

    private void config() throws VizException {
        // To solve imports
        setConfigKeyRelevantClasses("genai.chatGPT.Find_First_Sorted");

        // Scope
        setConfigKeyUseJavaArithmetic(true);
        setConfigKeyIntBithwidth(2);
        setConfigKeyInferScope(true);
        setConfigKeyObjectScope(0);
        setConfigKeyLoopUnroll(5);
        setConfigKeyTypeScopes("genai.chatGPT.Find_First_Sorted:1");

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
        check (GENERIC_PROPERTIES, "binarySearchFirst(int[],int)", true);
    }

}



