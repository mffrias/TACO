/*
 * TACO: Translation of Annotated COde
 * Copyright (c) 2010 Universidad de Buenos Aires
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA,
 * 02110-1301, USA
 */
package ar.edu.taco.regresion.imports;

import ar.edu.taco.infer.InferredScope;
import ar.edu.taco.infer.IntegerOrInfinity;
import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class ClazzC_Test extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.pack1.ClassC";
	}
	
	public void test_show_instance() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.pack1.ClassC,ar.edu.taco.pack2.ClassD");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyLoopUnroll(1);
		setConfigKeyRemoveQuantifiers(true);	
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAbstractSignatureObject(false);
		
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);

		setConfigKeyInferScope(false);
		setConfigKeyIntBithwidth(3);
		setConfigKeyObjectScope(3);
		setConfigKeyTypeScopes("ar.edu.taco.pack1.ClassC:1,ar.edu.taco.pack2.ClassD:1,java.lang.Object:2");
		setConfigKeyInferScope(false);
		
		runAndCheck(GENERIC_PROPERTIES,"show_instance_0", true);
		
	}


}
