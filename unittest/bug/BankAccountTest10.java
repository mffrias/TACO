package bug;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class BankAccountTest10 extends CollectionTestBase {

  @Override
    protected String getClassToCheck() {
      return "bug.BankAccount";
    }

  private void config() throws VizException {
    // To solve imports
    setConfigKeyRelevantClasses("bug.BankAccount");

    // Scope
    setConfigKeyUseJavaArithmetic(true);
    setConfigKeyIntBithwidth(4);
    setConfigKeyInferScope(true);
    setConfigKeyObjectScope(0);
    setConfigKeyLoopUnroll(5);
    setConfigKeyTypeScopes("bug.BankAccount:1");

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
    check(GENERIC_PROPERTIES,"foreignTransfer(int)",true);
  }
}
