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
package ar.edu.taco.regresion.examples;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class BinTreeTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "examples.bintree.base.BinTree";
	}
	
	public void test_findTest() throws VizException {
		setConfigKeyRelevantClasses("examples.bintree.base.BinTree,examples.bintree.base.Node");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyLoopUnroll(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		simulate(GENERIC_PROPERTIES,"find_0");
	}
	
	public void test_addTest() throws VizException {
		setConfigKeyRelevantClasses("examples.bintree.base.BinTree,examples.bintree.base.Node");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyLoopUnroll(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		simulate(GENERIC_PROPERTIES,"add_0");
	}
	
	public void test_removeTest() throws VizException {
		setConfigKeyRelevantClasses("examples.bintree.base.BinTree,examples.bintree.base.Node");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyLoopUnroll(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		simulate(GENERIC_PROPERTIES,"remove_0");
	}
}
