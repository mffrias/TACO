package bug;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;
//Reset static indexes
//import ar.edu.taco.simplejml.builtin.JMLAuxiliaryConstantsFactory;
//import ar.edu.taco.simplejml.builtin.AuxiliaryConstantsFactory;
//import ar.edu.taco.engine.StrykerStage;
//import ar.edu.taco.stryker.api.impl.OpenJMLController;
//import ar.edu.taco.jml.MethodCallNonPrimedVariableReplacementVisitor;
//import ar.edu.taco.jml.expression.ESExpressionVisitor;
//import ar.edu.taco.jml.OldExpressionReplacerVisitor;
//import ar.edu.taco.jml.invoke.ActualParameterNormalizerVisitor;
//import ar.edu.taco.jml.loop.WhileBlockVisitor;
//import ar.edu.taco.jml.loop.WhileBlockVisitor;
//import ar.edu.taco.jml.loop.DoWhileBlockVisitor;
//import ar.edu.taco.jml.varnames.VNBlockVisitor;
//import ar.edu.taco.jml.SpecMethodCallRemoverVisitor;
//import ar.edu.taco.simplejml.AssumeSimplifierVisitor;
//import ar.edu.taco.simplejml.BlockStatementsVisitor;
//import ar.edu.taco.simplejml.JmlBaseVisitor;

public class TimeTest extends CollectionTestBase {

  private static boolean ceFound = false;

  @Override
    protected String getClassToCheck() {
      return "bug.Time";
    }

  private void config() throws VizException {
    //Reset static variables
//    JMLAuxiliaryConstantsFactory.resetIndexes();
//    AuxiliaryConstantsFactory.resetIndexes();
//
//    StrykerStage.nonCompilableMutationIndexesFound = 0;
//    OpenJMLController.curJunitIndex = 0;
//    MethodCallNonPrimedVariableReplacementVisitor.newVarIndex = 0;
//    ESExpressionVisitor.variableNameIndex = 0;
//    OldExpressionReplacerVisitor.newParameterFromOldExpressionIndex = 0;
//    ActualParameterNormalizerVisitor.newParameterIndex = 0;
//    WhileBlockVisitor.variantFunctionIndex = 0;
//    WhileBlockVisitor.variableNameIndex = 0;
//    DoWhileBlockVisitor.variableNameIndex = 0;
//    VNBlockVisitor.variableNameIndex = 0;
//    SpecMethodCallRemoverVisitor.newReturnParameterIndex = 0;
//    AssumeSimplifierVisitor.assumeSimplifierVarIndex = 0;
//    BlockStatementsVisitor.variantFunctionIndex = 0;
//    JmlBaseVisitor.variableNameIndex = 0;

    // To solve imports
    setConfigKeyRelevantClasses("bug.Time");

    // Scope
    setConfigKeyUseJavaArithmetic(true);
    setConfigKeyIntBithwidth(4);
    setConfigKeyInferScope(true);
    setConfigKeyObjectScope(0);
    setConfigKeyLoopUnroll(3);
    setConfigKeyTypeScopes("bug.Time:1");

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

//  public void test_genericMethod2Test() throws VizException {
//    config();
//    if (!ceFound)
//      try {
//        check(GENERIC_PROPERTIES,"Time(int, int, int)",true);
//      } catch (Exception e) {
//        throw(e);
//      }
//
//    ceFound = true;
//  }
//
//  public void test_genericMethod3Test() throws VizException {
//    config();
//    if (!ceFound)
//      try {
//        check(GENERIC_PROPERTIES,"setSecond(int)",true);
//      } catch (Exception e) {
//        throw(e);
//      }
//
//    ceFound = true;
//  }
//
//  public void test_genericMethod4Test() throws VizException {
//    config();
//    if (!ceFound)
//      try {
//        check(GENERIC_PROPERTIES,"setMinute(int)",true);
//      } catch (Exception e) {
//        throw(e);
//      }
//
//    ceFound = true;
//  }
//
//  public void test_genericMethod5Test() throws VizException {
//    config();
//    if (!ceFound)
//      try {
//        check(GENERIC_PROPERTIES,"setHour(int)",true);
//      } catch (Exception e) {
//        throw(e);
//      }
//
//    ceFound = true;
//  }
//
//  public void test_genericMethod6Test() throws VizException {
//    config();
//    setConfigKeyTypeScopes("bug.Time:2");
//    if (!ceFound)
//      try {
//        check(GENERIC_PROPERTIES,"getTime()",true);
//      } catch (Exception e) {
//        throw(e);
//      }
//
//    ceFound = true;
//  }
//
//  public void test_genericMethod7Test() throws VizException {
//    config();
//    if (!ceFound)
//      try {
//        check(GENERIC_PROPERTIES,"getSecond()",true);
//      } catch (Exception e) {
//        throw(e);
//      }
//
//    ceFound = true;
//  }
//
//  public void test_genericMethod8Test() throws VizException {
//    config();
//    if (!ceFound)
//      try {
//        check(GENERIC_PROPERTIES,"getMinute()",true);
//      } catch (Exception e) {
//        throw(e);
//      }
//
//    ceFound = true;
//  }
//
//  public void test_genericMethod9Test() throws VizException {
//    config();
//    if (!ceFound)
//      try {
//        check(GENERIC_PROPERTIES,"getHour()",true);
//      } catch (Exception e) {
//        throw(e);
//      }
//
//    ceFound = true;
//  }
//
//  public void test_genericMethod10Test() throws VizException {
//    config();
//    if (!ceFound)
//      try {
//        check(GENERIC_PROPERTIES,"convertToSeconds()",true);
//      } catch (Exception e) {
//        throw(e);
//      }
//
//    ceFound = true;
//  }
//
//  public void test_genericMethod11Test() throws VizException {
//    config();
//    if (!ceFound)
//      try {
//        check(GENERIC_PROPERTIES,"decr()",true);
//      } catch (Exception e) {
//        throw(e);
//      }
//
//    ceFound = true;
//  }
//
//  public void test_genericMethod12Test() throws VizException {
//    config();
//    if (!ceFound)
//      try {
//        check(GENERIC_PROPERTIES,"timer()",true);
//      } catch (Exception e) {
//        throw(e);
//      }
//
//    ceFound = true;
//  }
//
//  public void test_genericMethod13Test() throws VizException {
//    config();
//    if (!ceFound)
//      try {
//        check(GENERIC_PROPERTIES,"timerp(int, int, int)",true);
//      } catch (Exception e) {
//        throw(e);
//      }
//
//    ceFound = true;
//  }
//
//  public void test_genericMethod14Test() throws VizException {
//    config();
//    if (!ceFound)
//      try {
//        check(GENERIC_PROPERTIES,"isTimeZero()",true);
//      } catch (Exception e) {
//        throw(e);
//      }
//
//    ceFound = true;
//  }
//
//  public void test_genericMethod15Test() throws VizException {
//    config();
//    if (!ceFound)
//      try {
//        check(GENERIC_PROPERTIES,"reset()",true);
//      } catch (Exception e) {
//        throw(e);
//      }
//
//    ceFound = true;
//  }
//
  public void test_genericMethod16Test() throws VizException {
    config();
    setConfigKeyTypeScopes("bug.Time:2");
    if (!ceFound)
      try {
        check(GENERIC_PROPERTIES,"bugWithTrue()",true);
      } catch (Exception e) {
        throw(e);
      }

    ceFound = true;
  }

//  public void test_genericMethod17Test() throws VizException {
//    config();
//    setConfigKeyTypeScopes("bug.Time:2");
//    if (!ceFound)
//      try {
//        check(GENERIC_PROPERTIES,"equals(bug.Time)",true);
//      } catch (Exception e) {
//        throw(e);
//      }
//
//    ceFound = true;
//  }
//
// THESE METHODS TAKE TO LONG TO BE VERIFIED
//
  public void test_genericMethod18Test() throws VizException {
    config();
    setConfigKeyTypeScopes("bug.Time:4");
    if (!ceFound)
      try {
        check(GENERIC_PROPERTIES,"trustedDifference(bug.Time, bug.Time)",true);
      } catch (Exception e) {
        throw(e);
      }

    ceFound = true;
  }
//
//
  public void test_genericMethod19Test() throws VizException {
    config();
    setConfigKeyTypeScopes("bug.Time:4");
    if (!ceFound)
      try {
        check(GENERIC_PROPERTIES,"difference(bug.Time, bug.Time)",true);
      } catch (Exception e) {
        throw(e);
      }

    ceFound = true;
  }
//
//
//  public void test_genericMethod20Test() throws VizException {
//    config();
//    setConfigKeyTypeScopes("bug.Time:4");
//    if (!ceFound)
//      try {
//        check(GENERIC_PROPERTIES,"timeOptions(bug.Time, bug.Time, int)",true);
//      } catch (Exception e) {
//        throw(e);
//      }
//
//    ceFound = true;
//  }
}
