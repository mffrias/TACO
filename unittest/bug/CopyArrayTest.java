package bug;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class CopyArrayTest extends CollectionTestBase {

  @Override
    protected String getClassToCheck() {
      return "bug.CopyArray";
    }

  private void config() throws VizException {
    // To solve imports
    setConfigKeyRelevantClasses("bug.CopyArray");

    // Scope
    setConfigKeyUseJavaArithmetic(false);
    setConfigKeyIntBithwidth(4);
    setConfigKeyInferScope(true);
    setConfigKeyObjectScope(0);
    setConfigKeyLoopUnroll(4);
    setConfigKeyTypeScopes("bug.CopyArray:1");

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
    check(GENERIC_PROPERTIES,"copyArray(int[], int, int, int[])",true);
  }
}
