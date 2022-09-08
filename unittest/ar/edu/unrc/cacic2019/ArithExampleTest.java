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
package ar.edu.unrc.cacic2019;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class ArithExampleTest extends CollectionTestBase {

	public ArithExampleTest(){};
	
	@Override
	protected String getClassToCheck() {
		return "ar.edu.unrc.cacic2019.ArithExample";
	}
	
	public void test_condNoTrivial_Test() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.unrc.cacic2019.ArithExample");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyNestedLoopUnroll(false);
		setConfigKeyLoopUnroll(3);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyInferScope(false);
		setConfigKeyTypeScopes("ar.edu.unrc.cacic2019.ArithExample:1");
		check(GENERIC_PROPERTIES,"condNoTrivial_0", true);
	}
	
	public void test_propRara_Test() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.unrc.cacic2019.ArithExample");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyNestedLoopUnroll(false);
		setConfigKeyLoopUnroll(3);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyInferScope(false);
		setConfigKeyTypeScopes("ar.edu.unrc.cacic2019.ArithExample:1");
		check(GENERIC_PROPERTIES,"propRara_0", true);
	}

}

	


