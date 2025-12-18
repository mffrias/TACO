package edu.utep.ds2;


import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class AverageTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "edu.utep.ds2.AverageMain";
	}

	public void test_genericMethodTest() throws VizException {
		setConfigKeyRelevantClasses("edu.utep.ds2.AverageMain");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(4);
		setConfigKeyLoopUnroll(3);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		setConfigKeyTypeScopes("edu.utep.ds2.AverageMain:1");
		check(GENERIC_PROPERTIES,"calculateAverage()",false);
//		checkAndRunSpecIfFaulty(GENERIC_PROPERTIES,"calculateAverage()",10);
	}

	public void test_absMethodTest() throws VizException {
		setConfigKeyRelevantClasses("edu.utep.ds2.AverageMain");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(4);
		setConfigKeyLoopUnroll(3);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		setConfigKeyTypeScopes("edu.utep.ds2.AverageMain:1");
		check(GENERIC_PROPERTIES,"abs(int)",false);
//		checkAndRunSpecIfFaulty(GENERIC_PROPERTIES,"calculateAverage(int, int)",10);
	}

	public static void main(String[] args){
		int[] x = new int[3];
		int[][] a = {{1,2,3}, {23}, {1,2,3}};
		int[] b = a[0];
		int[][] c = new int[3][5];
		c[0] = new int[1];
		int[] d = c[0];
		int e = c[2][2];
	}

}