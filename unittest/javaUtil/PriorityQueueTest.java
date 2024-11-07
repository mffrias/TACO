package javaUtil;


import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class PriorityQueueTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "javaUtil.PriorityQueue";
	}

	public void test_offerTest() throws VizException {
		setConfigKeyRelevantClasses("javaUtil.PriorityQueue");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyNumericTypeQuantificationRange(0, 4);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(1);
        setConfigKeyLoopUnroll(3);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		setConfigKeyTypeScopes("javaUtil.PriorityQueue:1");
		check(GENERIC_PROPERTIES,"offer(int)",true);
	}

	public void test_indexOfTest() throws VizException {
		setConfigKeyRelevantClasses("javaUtil.PriorityQueue");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyNumericTypeQuantificationRange(0, 4);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(1);
		setConfigKeyLoopUnroll(10);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		setConfigKeyTypeScopes("javaUtil.PriorityQueue:1");
		check(GENERIC_PROPERTIES,"indexOf(int)",true);
	}


	public void test_peekTest() throws VizException {
		setConfigKeyRelevantClasses("javaUtil.PriorityQueue");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyNumericTypeQuantificationRange(0, 4);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(1);
		setConfigKeyLoopUnroll(10);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		setConfigKeyTypeScopes("javaUtil.PriorityQueue:1");
		check(GENERIC_PROPERTIES,"peek()",true);
	}


	public void test_pollTest() throws VizException {
		setConfigKeyRelevantClasses("javaUtil.PriorityQueue");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyNumericTypeQuantificationRange(0, 4);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(1);
		setConfigKeyLoopUnroll(3);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		setConfigKeyTypeScopes("javaUtil.PriorityQueue:1");
		check(GENERIC_PROPERTIES,"poll()",true);
	}
}
