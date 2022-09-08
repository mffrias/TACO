package ar.edu.itba.jpf;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;


public class TreeSetRepOKTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.TreeSet";
	}

	
	public void test_repOk() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.TreeSet, roops.core.objects.TreeSetEntry, roops.core.objects.PairObjectInt");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(false);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyInferScope(true);
	    setConfigKeyIntBithwidth(5);
	    setConfigKeyLoopUnroll(10);
	    setConfigKeyObjectScope(0);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyMaxStrykerMethodsPerFile(50);
//		setCondigKeyBuildJavaTrace(true);	
//		setConfigKeyTypeScopes("roops.core.objects.TreeSet:1,roops.core.objects.TreeSetEntry:10,java.util.Set:1, java.util.HashSet:1, java.util.List:1, java.util.ArrayList:1");
		setConfigKeyTypeScopes("roops.core.objects.TreeSet:1,roops.core.objects.TreeSetEntry:10");
		check(GENERIC_PROPERTIES,"repOk_0", false);
	}
	
}
