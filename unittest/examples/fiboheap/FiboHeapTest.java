package examples.fiboheap;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class FiboHeapTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "examples.fiboheap.FibHeap";
	}
	
	public void test_showInstance() throws VizException {
		setConfigKeyRelevantClasses("examples.fiboheap.FibHeap,examples.fiboheap.Node");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);

		setConfigKeyLoopUnroll(1);
		setConfigKeyIntBithwidth(6);
		setConfigKeyObjectScope(3);
		setConfigKeyTypeScopes("examples.fiboheap.FibHeap:1,examples.fiboheap.Node:7,int:13");
		runAndCheck(GENERIC_PROPERTIES,"showInstance_0", true);
	}
	

}

