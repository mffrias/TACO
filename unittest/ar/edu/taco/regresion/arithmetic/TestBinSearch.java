package ar.edu.taco.regresion.arithmetic;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TestBinSearch extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.arithmetic.BinSearch";
	}
	
	public void test_binarySearch() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.arithmetic.BinSearch");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyDisableIntegerLiteralReduction(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyObjectScope(9);
		setConfigKeyLoopUnroll(2);
		
		runAndCheck(GENERIC_PROPERTIES, "binarySearch_0", true);
	}

}
