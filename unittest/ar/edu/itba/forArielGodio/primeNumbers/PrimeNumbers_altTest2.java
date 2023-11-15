package ar.edu.itba.forArielGodio.primeNumbers;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class PrimeNumbers_altTest2 extends CollectionTestBase {

  @Override
    protected String getClassToCheck() {
      return "forArielGodio.primeNumbers.PrimeNumbers";
    }

  private void config() throws VizException {
    // To solve imports
    setConfigKeyRelevantClasses("forArielGodio.primeNumbers.PrimeNumbers");

    // Scope
    setConfigKeyUseJavaArithmetic(false);
    setConfigKeyIntBithwidth(5);
    setConfigKeyInferScope(true);
    setConfigKeyObjectScope(0);
    setConfigKeyLoopUnroll(6);
    setConfigKeyTypeScopes("forArielGodio.primeNumbers.PrimeNumbers:1");

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

  public void test_genericMethod1Test() throws VizException {
    config();
    check(GENERIC_PROPERTIES,"primeList(int)",true);
  }
}
