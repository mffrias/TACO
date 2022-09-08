package ar.edu.taco.skunk;


import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TreeSetTest extends CollectionTestBase {

	public TreeSetTest() {
		// TODO Auto-generated constructor stub
	}


	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.skunk.TreeSet";
	}
	
	public void test_TreeSetTest1() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.skunk.TreeSet, ar.edu.taco.skunk.TreeSetEntry");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyCheckArithmeticException(false);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyRemoveQuantifiers(true);
		// Infer-Scope
		setConfigKeyInferScope(true);
		setConfigKeyTypeScopes("ar.edu.taco.skunk.TreeSet:1, ar.edu.taco.skunk.TreeSetEntry:5");
		setConfigKeyLoopUnroll(5);
		// SBP+BOUND
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		// JUNIT
		setConfigKeyGenerateUnitTestCase(true);
		// STRYKER
//		this.setConfigKeyAttemptToCorrectBug(true);
//		this.setConfigKeyMaxStrykerMethodsPerFile(30);

		check(GENERIC_PROPERTIES,"generateInput_0", false);
	}

	
	
	
	
}
