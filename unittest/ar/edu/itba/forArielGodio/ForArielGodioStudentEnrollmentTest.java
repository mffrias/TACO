package ar.edu.itba.forArielGodio;


import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class ForArielGodioStudentEnrollmentTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "forArielGodio.StudentEnrollment";
	}

	public void test_genericMethodTest() throws VizException {
		setConfigKeyRelevantClasses("forArielGodio.StudentEnrollment");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(4);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		setConfigKeyTypeScopes("forArielGodio.StudentEnrollment:1");
		check(GENERIC_PROPERTIES,"setTuitionBalance(int)",true);
	}

}
