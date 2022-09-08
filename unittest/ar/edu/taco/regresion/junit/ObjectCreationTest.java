/**
 * 
 */
package ar.edu.taco.regresion.junit;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

/**
 * @author elgaby
 *
 */
public class ObjectCreationTest extends GenericTestBase {

	/* (non-Javadoc)
	 * @see ar.edu.taco.regresion.GenericTestBase#getClassToCheck()
	 */
	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.junit.House";
	}
	
	public void testObjectsCreation() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.junit.FrameSize, ar.edu.taco.junit.Window");
		
		setConfigKeyObjectScope(10);
		setConfigKeyIntBithwidth(4);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		runAndCheck(GENERIC_PROPERTIES,"createHouse_0", true);
	}
}
