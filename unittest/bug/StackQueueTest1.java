package bug;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class StackQueueTest1 extends CollectionTestBase {

  @Override
    protected String getClassToCheck() {
      //return "bug.Stack";
      //return "bug.Queue";
      return "bug.StackQueue";
      //return "bug.StackQueue,bug.Stack,bug.Queue";
    }

  private void config() throws VizException {
    // To solve imports
    //setConfigKeyRelevantClasses("bug.StackQueue");
    //setConfigKeyRelevantClasses("bug.Stack");
    //setConfigKeyRelevantClasses("bug.Queue,bug");
    setConfigKeyRelevantClasses("bug.StackQueue,bug.Stack,bug.Queue");

    // Scope
    setConfigKeyUseJavaArithmetic(false);
    setConfigKeyIntBithwidth(4);
    setConfigKeyInferScope(true);
    setConfigKeyObjectScope(0);
    setConfigKeyLoopUnroll(5);
    setConfigKeyTypeScopes("bug.StackQueue:1,bug.Stack:1,bug.Queue:1");

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
    //check(GENERIC_PROPERTIES,"getTop()",true);
    //check(GENERIC_PROPERTIES,"getFront()",true);
    //check(GENERIC_PROPERTIES,"stackPlus(bug.Stack)",true);
    check(GENERIC_PROPERTIES,"stackPlus2(bug.Stack)",true);
  }
}
