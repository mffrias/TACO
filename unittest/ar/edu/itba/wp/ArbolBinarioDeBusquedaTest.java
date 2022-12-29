package ar.edu.itba.wp;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class ArbolBinarioDeBusquedaTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.itba.wp.ArbolBinarioDeBusqueda";
	}

	public void test_mostrarArbolTest() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.itba.wp.ArbolBinarioDeBusqueda,ar.edu.itba.wp.Nodo");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(0);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(4);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(true);
		setConfigKeyMaxStrykerMethodsPerFile(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("ar.edu.itba.wp.ArbolBinarioDeBusqueda:1,ar.edu.itba.wp.Nodo:5");
		check(GENERIC_PROPERTIES,"mostrarArbol()",true);
	}
	
	public void test_findTest() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.itba.wp.ArbolBinarioDeBusqueda,ar.edu.itba.wp.Nodo");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
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
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("ar.edu.itba.wp.ArbolBinarioDeBusqueda:1,ar.edu.itba.wp.Nodo:5");
		check(GENERIC_PROPERTIES,"find(int,ar.edu.itba.wp.ArbolBinarioDeBusqueda,ar.edu.itba.wp.Nodo, int )",true);
	}


}
