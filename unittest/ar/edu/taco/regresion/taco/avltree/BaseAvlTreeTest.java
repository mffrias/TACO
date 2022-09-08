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
package ar.edu.taco.regresion.taco.avltree;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class BaseAvlTreeTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "examples.avltree.base.AvlTree";
	}
	
	public void test_findNode() throws VizException {
		setConfigKeyRelevantClasses("examples.avltree.base.AvlTree,examples.avltree.base.AvlNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyLoopUnroll(1);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		simulate(GENERIC_PROPERTIES,"findNode_0");
	}
	
	public void test_fmax() throws VizException {
		setConfigKeyRelevantClasses("examples.avltree.base.AvlTree,examples.avltree.base.AvlNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyLoopUnroll(1);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		simulate(GENERIC_PROPERTIES,"fmax_0");
	}
	
	public void test_fmin() throws VizException {
		setConfigKeyRelevantClasses("examples.avltree.base.AvlTree,examples.avltree.base.AvlNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyLoopUnroll(1);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		simulate(GENERIC_PROPERTIES,"fmin_0");
	}
}
