package bug;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class PruebaDecreasingTest extends CollectionTestBase {

  @Override
    protected String getClassToCheck() {
      return "bug.PruebaDecreasing";
    }

  private void config() throws VizException {
    // To solve imports
    setConfigKeyRelevantClasses("bug.PruebaDecreasing");

    // Scope
    setConfigKeyUseJavaArithmetic(true);
    setConfigKeyIntBithwidth(2);
    setConfigKeyInferScope(true);
    setConfigKeyObjectScope(0);
    setConfigKeyLoopUnroll(5);
    setConfigKeyTypeScopes("bug.PruebaDecreasing:1");

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
    check(GENERIC_PROPERTIES,"primeList(int)",true);
  }
}