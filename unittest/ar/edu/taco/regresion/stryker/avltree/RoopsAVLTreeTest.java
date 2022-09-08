package ar.edu.taco.regresion.stryker.avltree;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;
/*
 * TODO: all test fail because in the source class the methods are commented. Check with the "Old" instead of "base" version of the source files
 */
public class RoopsAVLTreeTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.avltree.base.AvlTree";
	}

	public void test_findTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.avltree.base.AvlTree, roops.core.objects.avltree.base.AvlNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyInferScope(true);
	    setConfigKeyIntBithwidth(4);
	    setConfigKeyLoopUnroll(4);
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
		setConfigKeyTypeScopes("roops.core.objects.avltree.base.AvlTree:1,roops.core.objects.avltree.base.AvlNode:7");
		check(GENERIC_PROPERTIES,"find_0", false);
	}
	
	
	public void test_findMinTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.avltree.base.AvlTree, roops.core.objects.avltree.base.AvlNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyInferScope(true);
	    setConfigKeyIntBithwidth(4);
	    setConfigKeyLoopUnroll(4);
	    setConfigKeyObjectScope(0);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyMaxStrykerMethodsPerFile(50);
//		setCondigKeyBuildJavaTrace(true);	
		setConfigKeyTypeScopes("roops.core.objects.avltree.base.AvlTree:1,roops.core.objects.avltree.base.AvlNode:7");
		check(GENERIC_PROPERTIES,"findMin_0", false);
	}

	
	
	public void test_findMaxTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.avltree.base.AvlTree, roops.core.objects.avltree.base.AvlNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);// fun_set_size
		setConfigKeyInferScope(true);
	    setConfigKeyIntBithwidth(4);
	    setConfigKeyLoopUnroll(4);
	    setConfigKeyObjectScope(0);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyMaxStrykerMethodsPerFile(50);
//		setCondigKeyBuildJavaTrace(true);	
		setConfigKeyTypeScopes("roops.core.objects.avltree.base.AvlTree:1,roops.core.objects.avltree.base.AvlNode:7");
		check(GENERIC_PROPERTIES,"findMax_0", false);
	}
	
	public void test_privateinsertTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.avltree.base.AvlTree, roops.core.objects.avltree.base.AvlNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyInferScope(true);
	    setConfigKeyIntBithwidth(4);
	    setConfigKeyLoopUnroll(1);
	    setConfigKeyObjectScope(0);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		setConfigKeyMaxStrykerMethodsPerFile(50);
//		setCondigKeyBuildJavaTrace(true);	
		setConfigKeyTypeScopes("roops.core.objects.avltree.base.AvlTree:1,roops.core.objects.avltree.base.AvlNode:5");
		check(GENERIC_PROPERTIES,"insert_0", false);
	}
	

}